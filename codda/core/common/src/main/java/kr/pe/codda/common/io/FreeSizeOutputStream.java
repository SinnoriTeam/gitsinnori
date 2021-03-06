/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.codda.common.io;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;
import java.util.Arrays;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CharsetEncoderException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.BufferOverflowExceptionWithMessage;

/**
 * 가변 크기를 갖는 출력 이진 스트림<br/>
 * 참고) 스트림은 데이터 패킷 버퍼 큐 관리자가 관리하는 랩 버퍼들로 구현된다.
 * 
 * @author Won Jonghoon
 *
 */
public final class FreeSizeOutputStream implements BinaryOutputStreamIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(FreeSizeOutputStream.class);

	private final ArrayDeque<WrapBuffer> outputStreamWrapBufferQueue = new ArrayDeque<WrapBuffer>();;
	private ByteOrder streamByteOrder = null;
	private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder = null;
	private int dataPacketBufferMaxCount;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;	

	private ByteBuffer workBuffer = null;
	private long numberOfWrittenBytes = 0;
	private long outputStreamMaxSize = 0;

	public FreeSizeOutputStream(int dataPacketBufferMaxCount, CharsetEncoder streamCharsetEncoder,
			DataPacketBufferPoolIF dataPacketBufferPool) throws NoMoreDataPacketBufferException {
		if (dataPacketBufferMaxCount <= 0) {
			String errorMessage = String.format(
					"the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero",
					dataPacketBufferMaxCount);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("the parameter streamCharsetEncoder is null");
		}
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}

		this.dataPacketBufferMaxCount = dataPacketBufferMaxCount;
		this.streamByteOrder = dataPacketBufferPool.getByteOrder();
		this.streamCharset = streamCharsetEncoder.charset();
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.dataPacketBufferPool = dataPacketBufferPool;

		// outputStreamWrapBufferList = new ArrayList<WrapBuffer>();
		WrapBuffer wrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
		workBuffer = wrapBuffer.getByteBuffer();
		outputStreamWrapBufferQueue.add(wrapBuffer);

		outputStreamMaxSize = dataPacketBufferMaxCount * dataPacketBufferPool.getDataPacketBufferSize();
	}

	/**
	 * 랩 버퍼 확보 실패시 이전에 등록한 랩 버퍼 목록을 해제해 주는 메소드
	 */
	private void freeDataPacketBufferList() {
		if (null != outputStreamWrapBufferQueue) {
			while (! outputStreamWrapBufferQueue.isEmpty()) {
				WrapBuffer outputStreamWrapBuffer = outputStreamWrapBufferQueue.removeFirst();
				dataPacketBufferPool.putDataPacketBuffer(outputStreamWrapBuffer);
			}
		}
	}

	/**
	 * 스트림에 랩 버퍼를 추가하여 확장한다.
	 * 
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	private void addBuffer() throws NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {
		/** WARNING! 신규 버퍼를 추가할때 작업 버퍼에 남은 용량은 없어야 한다, 이 규칙에 대한 방어 코드 */
		if (workBuffer.hasRemaining()) {
			String errorMessage = "you want to add a new buffer but the working buffer has a remaing data";
			log.error(errorMessage);
			System.exit(1);
		}

		if (outputStreamWrapBufferQueue.size() == dataPacketBufferMaxCount) {
			String errorMessage = String.format(
					"this output stream is full. maximum number of data packet buffers=[%d]", dataPacketBufferMaxCount);
			// log.warn();
			throw new BufferOverflowExceptionWithMessage(errorMessage);
		}

		/** 새로운 바디 버퍼 받아 오기 */
		WrapBuffer newWrapBuffer = null;
		try {
			newWrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
		} catch (NoMoreDataPacketBufferException e) {
			// freeDataPacketBufferList();
			throw e;
		}

		workBuffer = newWrapBuffer.getByteBuffer();
		outputStreamWrapBufferQueue.add(newWrapBuffer);
	}

	private void doPutBytes(ByteBuffer src) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		do {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(src.get());
			numberOfWrittenBytes++;
		} while (src.hasRemaining());
	}

	private void doPutBytes(byte[] src, int offset, int length) throws BufferOverflowException,
			BufferOverflowExceptionWithMessage, IllegalArgumentException, NoMoreDataPacketBufferException {
		int numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		if (0 == numberOfBytesRemainingInWorkBuffer) {
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		}

		do {
			if (numberOfBytesRemainingInWorkBuffer >= length) {
				workBuffer.put(src, offset, length);
				numberOfWrittenBytes += length;
				break;
			}

			workBuffer.put(src, offset, numberOfBytesRemainingInWorkBuffer);
			numberOfWrittenBytes += numberOfBytesRemainingInWorkBuffer;

			offset += numberOfBytesRemainingInWorkBuffer;
			length -= numberOfBytesRemainingInWorkBuffer;
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		} while (0 != length);
	}

	private void doPutUnsignedByte(byte value)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException {
		if (!workBuffer.hasRemaining()) {
			addBuffer();
		}
		workBuffer.put(value);
		numberOfWrittenBytes++;
	}

	private void doPutUnsignedShort(short value)
			throws BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException {
		
		byte t2 = (byte)(value & 0xff);
		byte t1 = (byte)((value & 0xff00) >> 8);
		
		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
		} else {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
		}
	}

	private void throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(long numberOfBytesRequired)
			throws BufferOverflowExceptionWithMessage {		
		long numberOfBytesRemaining = remaining();
		if (numberOfBytesRemaining < numberOfBytesRequired) {
			throw new BufferOverflowExceptionWithMessage(
					String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
							numberOfBytesRemaining, numberOfBytesRequired));
		}
	}

	@Override
	public void putByte(byte value)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		if (!workBuffer.hasRemaining()) {
			addBuffer();
		}
		workBuffer.put(value);
		numberOfWrittenBytes++;
	}

	@Override
	public void putUnsignedByte(short value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		doPutUnsignedByte((byte) value);

	}

	@Override
	public void putUnsignedByte(int value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		doPutUnsignedByte((byte) value);
	}

	@Override
	public void putUnsignedByte(long value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		doPutUnsignedByte((byte) value);
	}

	@Override
	public void putShort(short value)
			throws BufferOverflowException, BufferOverflowExceptionWithMessage, NoMoreDataPacketBufferException {

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);
		
		byte t2 = (byte)(value & 0xff);
		byte t1 = (byte)((value & 0xff00) >> 8);
		
		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
		} else {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
		}
	}

	@Override
	public void putUnsignedShort(int value) throws BufferOverflowException, BufferOverflowExceptionWithMessage,
			IllegalArgumentException, NoMoreDataPacketBufferException {

		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		doPutUnsignedShort((short) value);
	}

	@Override
	public void putUnsignedShort(long value) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {

		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		doPutUnsignedShort((short) value);
	}

	@Override
	public void putInt(int value)
			throws BufferOverflowException, NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);

		byte t4 = (byte)(value & 0xff);
		byte t3 = (byte)((value & 0xff00) >> 8);
		byte t2 = (byte)((value & 0xff0000) >> 16);
		byte t1 = (byte)((value & 0xff000000) >> 24);
		
		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t3);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t4);
			numberOfWrittenBytes++;
		} else {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t4);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t3);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
		}
	}

	@Override
	public void putUnsignedInt(long value) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}
	
		if (value > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned integer 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_INTEGER_MAX));
		}
	
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);
	
		byte t4 = (byte)(value & 0xffL);
		byte t3 = (byte)((value & 0xff00L) >> 8);
		byte t2 = (byte)((value & 0xff0000L) >> 16);
		byte t1 = (byte)((value & 0xff000000L) >> 24);
		
		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t3);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t4);
			numberOfWrittenBytes++;
		} else {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t4);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t3);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
		}
	}

	@Override
	public void putLong(long value)
			throws BufferOverflowException, NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(8);
	
		byte t8 = (byte)(value & 0xffL);
		byte t7 = (byte)((value & 0xff00L) >> 8);
		byte t6 = (byte)((value & 0xff0000L) >> 16);
		byte t5 = (byte)((value & 0xff000000L) >> 24);
		byte t4 = (byte)((value & 0xff00000000L) >> 32);
		byte t3 = (byte)((value & 0xff0000000000L) >> 40);
		byte t2 = (byte)((value & 0xff000000000000L) >> 48);
		byte t1 = (byte)((value & 0xff00000000000000L) >> 56);
		
		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t3);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t4);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t5);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t6);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t7);
			numberOfWrittenBytes++;
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t8);
			numberOfWrittenBytes++;
		} else {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t8);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t7);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t6);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t5);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t4);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t3);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t2);
			numberOfWrittenBytes++;
			
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(t1);
			numberOfWrittenBytes++;
		}
	}

	
	@Override
	public void putFixedLengthString(int fixedLength, String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		putFixedLengthString(fixedLength, src, streamCharsetEncoder);
	}

	@Override
	public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException,
			BufferOverflowExceptionWithMessage, CharsetEncoderException {
		if (fixedLength < 0) {
			throw new IllegalArgumentException(
					String.format("the parameter fixedLength[%d] is less than zero", fixedLength));
		}
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharsetEncoder == null) {
			throw new IllegalArgumentException("the parameter wantedCharsetEncoder is null");
		}

		if (0 == fixedLength) {
			return;
		}	

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(fixedLength);
		
		byte strBytes[] = new byte[fixedLength];		
		Arrays.fill(strBytes, CommonStaticFinalVars.ZERO_BYTE);
		ByteBuffer strByteBuffer = ByteBuffer.wrap(strBytes);
		
		CharBuffer strCharBuffer = CharBuffer.wrap(src);
		try {
			wantedCharsetEncoder.encode(strCharBuffer, strByteBuffer, true);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharsetEncoder.charset().name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		// log.info("strBufer=[%s]", strBufer.toString());
		
		doPutBytes(strBytes, 0, strBytes.length);
		
		/*byte strBytes[] = src.getBytes(wantedCharsetEncoder.charset());
		
		
		if (fixedLength > strBytes.length) {
			// Arrays.fill(dstBytes, CommonStaticFinalVars.ZERO_BYTE);
			doPutBytes(strBytes, 0, strBytes.length);
			byte dstBytes[] = new byte[fixedLength - strBytes.length];
			Arrays.fill(dstBytes, CommonStaticFinalVars.ZERO_BYTE);
			doPutBytes(dstBytes, 0, dstBytes.length);
		} else {
			doPutBytes(strBytes, 0, fixedLength);
		}*/	
	}

	@Override
	public void putStringAll(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		putStringAll(src, streamCharset);
	}
	
	@Override
	public void putStringAll(String src, Charset wantedCharset) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		
		
		/*CharBuffer srcCharBuffer = CharBuffer.wrap(src);
		ByteBuffer srcByteBuffer = null;
		try {
			srcByteBuffer = streamCharsetEncoder.encode(srcCharBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new CharsetEncoderException("fail to call streamCharsetEncoder.encode::"+e.getMessage());
		}
		

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(srcByteBuffer.remaining());

		doPutBytes(srcByteBuffer);*/
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length);
		
		doPutBytes(strBytes, 0, strBytes.length);
	}

	@Override
	public void putPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		putUBPascalString(src, streamCharset);
	}
	
	@Override
	public void putPascalString(String src, Charset wantedCharset) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		putUBPascalString(src, wantedCharset);
	}

	@Override
	public void putUBPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		putUBPascalString(src, streamCharset);
	}

	@Override
	public void putUBPascalString(String src, Charset wantedCharset) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
	
		/*CharBuffer srcCharBuffer = CharBuffer.wrap(src);
		ByteBuffer srcByteBuffer = null;
		try {
			srcByteBuffer = streamCharsetEncoder.encode(srcCharBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new CharsetEncoderException("fail to call streamCharsetEncoder.encode::"+e.getMessage());
		}		
	
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((srcByteBuffer.remaining() + 1));
	
		putUnsignedByte(srcByteBuffer.remaining());
		putBytes(srcByteBuffer);*/
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		if (strBytes.length > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned byte max[%d]", 
					strBytes.length, wantedCharset.name(),
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX);
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length+1);
		
		doPutUnsignedByte((byte)strBytes.length);
		doPutBytes(strBytes, 0, strBytes.length);
	
	}

	@Override
	public void putUSPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		putUSPascalString(src, streamCharset);
	}

	@Override
	public void putUSPascalString(String src, Charset wantedCharset) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
	
		/*CharBuffer srcCharBuffer = CharBuffer.wrap(src);
		ByteBuffer srcByteBuffer = null;
		try {
			srcByteBuffer = streamCharsetEncoder.encode(srcCharBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new CharsetEncoderException("fail to call streamCharsetEncoder.encode::"+e.getMessage());
		}	
	
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((srcByteBuffer.remaining() + 2));
	
		putUnsignedShort(srcByteBuffer.remaining());
		putBytes(srcByteBuffer);*/
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		if (strBytes.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {			
			String errorMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned short max[%d]", 
					strBytes.length, wantedCharset.name(),
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX);
			throw new IllegalArgumentException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length+2);
		
		doPutUnsignedShort((short)strBytes.length);
		doPutBytes(strBytes, 0, strBytes.length);
	}

	@Override
	public void putSIPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		putSIPascalString(src, streamCharset);
	}
	
	@Override
	public void putSIPascalString(String src, Charset wantedCharset) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		/*CharBuffer srcCharBuffer = CharBuffer.wrap(src);
		ByteBuffer srcByteBuffer = null;
		try {
			srcByteBuffer = streamCharsetEncoder.encode(srcCharBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new CharsetEncoderException("fail to call streamCharsetEncoder.encode::"+e.getMessage());
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((srcByteBuffer.remaining() + 4));

		putInt(srcByteBuffer.remaining());
		putBytes(srcByteBuffer);*/
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length+4);
		
		putInt(strBytes.length);
		doPutBytes(strBytes, 0, strBytes.length);
	}

	@Override
	public void putBytes(byte[] src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}		
	
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(src.length);
	
		doPutBytes(src, 0, src.length);
	}

	@Override
	public void putBytes(byte[] src, int offset, int length) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
	
		if (offset < 0) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] is less than zero", offset));
		}
		
		if (offset >= src.length) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] is greater than or equal to array.length[%d]", offset, src.length));
		}
	
		if (length < 0) {
			throw new IllegalArgumentException(String.format("the parameter length[%d] is less than zero", length));
		}
		
		if (0 == length) {
			return;
		}
		
		long sumOfOffsetAndLength = ((long)offset + length);
		if (sumOfOffsetAndLength > src.length) {
			throw new IllegalArgumentException(String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]", 
					sumOfOffsetAndLength, offset, length, src.length));
		}
		
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);		
	
		doPutBytes(src, offset, length);
	}

	@Override
	public void putBytes(ByteBuffer src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, BufferOverflowExceptionWithMessage {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		int numberOfBytesReamaining = src.remaining();
		if (0 == numberOfBytesReamaining) {
			return;
		}
	
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(numberOfBytesReamaining);
		
		/**
		 * slice 메소드를 통해 원본 버퍼의 속성과 별개의 속성을 갖는 <br/>
		 * 그리고 실제 필요한 영역만을 가지는 바이트 버퍼를 만든다.
		 */
		ByteBuffer sliceBuffer = src.slice();
	
		doPutBytes(sliceBuffer);
	
	}

	@Override
	public void skip(int n) throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException,
			BufferOverflowExceptionWithMessage {
		skip((long)n);
	}
	
	public void skip(long n) throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException,
		BufferOverflowExceptionWithMessage {
		if (n < 0) {
			throw new IllegalArgumentException(String.format("the parameter n[%d] is less than zero", n));
		}
		if (0 == n)
			return;

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(n);		

		int numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		if (0 == numberOfBytesRemainingInWorkBuffer) {
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		}

		do {
			if (n <= numberOfBytesRemainingInWorkBuffer) {
				workBuffer.position(workBuffer.position() + (int)n);
				numberOfWrittenBytes += n;
				break;
			}

			int limitOfWorkBuffer = workBuffer.limit();
			workBuffer.position(limitOfWorkBuffer);
			numberOfWrittenBytes += numberOfBytesRemainingInWorkBuffer;

			n -= numberOfBytesRemainingInWorkBuffer;
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		} while (n != 0);
	}

	@Override
	public Charset getCharset() {
		return streamCharset;
	}

	public long size() {
		return numberOfWrittenBytes;
	}

	public long remaining() {
		return (outputStreamMaxSize - numberOfWrittenBytes);
	}

	public long getNumberOfWrittenBytes() {
		long numberOfWrittenBytes = 0;

		for (WrapBuffer buffer : outputStreamWrapBufferQueue) {
			// numberOfWrittenBytes += buffer.getByteBuffer().position();
			
			ByteBuffer dupByteBuffer = buffer.getByteBuffer().duplicate();
			dupByteBuffer.flip();			
			numberOfWrittenBytes += dupByteBuffer.remaining();
		}

		return numberOfWrittenBytes;
	}

	public ArrayDeque<WrapBuffer> getReadableWrapBufferQueue() {
		changeReadableWrapBufferList();

		return outputStreamWrapBufferQueue;
	}
	
	public void changeReadableWrapBufferList() {
		/** flip all buffer */
		for (WrapBuffer outputStreamWrapBuffer : outputStreamWrapBufferQueue) {
			outputStreamWrapBuffer.getByteBuffer().flip();
		}
	}
	
	
	public ArrayDeque<WrapBuffer> getOutputStreamWrapBufferList() {
		return outputStreamWrapBufferQueue;
	}	

	@Override
	public void close() {
		freeDataPacketBufferList();
	}
}
