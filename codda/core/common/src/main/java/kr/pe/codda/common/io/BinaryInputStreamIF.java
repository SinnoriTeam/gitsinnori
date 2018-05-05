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

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import kr.pe.codda.common.exception.CharsetDecoderException;
import kr.pe.codda.common.exception.BufferUnderflowExceptionWithMessage;

/**
 * 이진 스트림에서 각각의 데이터 타입별 읽기 기능 제공자 인터페이스
 * 
 * @see FixedSizeInputStream
 * @see FreeSizeInputStream
 * @author Won Jonghoon
 * 
 */
public interface BinaryInputStreamIF {
	/**
	 * @return byte 데이터
	 * @throws BufferUnderflowExceptionWithMessage
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public byte getByte() throws BufferUnderflowExceptionWithMessage;

	/**
	 * unsigned byte 데이터를 읽어 반환한다.
	 * 
	 * @return unsigned byte 데이터
	 * @throws BufferUnderflowExceptionWithMessage
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public short getUnsignedByte() throws BufferUnderflowExceptionWithMessage;

	/**
	 * short 데이터를 읽어 반환한다.
	 * 
	 * @return short 데이터
	 * @throws BufferUnderflowExceptionWithMessage
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public short getShort() throws BufferUnderflowExceptionWithMessage;

	/**
	 * unsigned short 데이터를 읽어 반환한다.
	 * 
	 * @return unsigned short 데이터
	 * @throws BufferUnderflowExceptionWithMessage
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public int getUnsignedShort() throws BufferUnderflowExceptionWithMessage;

	/**
	 * integer 데이터를 읽어 반환한다.
	 * 
	 * @return integer 데이터
	 * @throws BufferUnderflowExceptionWithMessage
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public int getInt() throws BufferUnderflowExceptionWithMessage;

	/**
	 * unsigned integer 데이터를 읽어 반환한다.
	 * 
	 * @return unsigned integer 데이터
	 * @throws BufferUnderflowExceptionWithMessage
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public long getUnsignedInt() throws BufferUnderflowExceptionWithMessage;

	/**
	 * long 데이터를 읽어 반환한다.
	 * 
	 * @return long 데이터
	 * @throws BufferUnderflowExceptionWithMessage
	 *             버퍼 크기를 넘어서는 읽기 시도시 발생
	 */
	public long getLong() throws BufferUnderflowExceptionWithMessage;

	/**
	 * 이진 스트림에서 지정된 길이의 데이터를 지정된 문자셋으로 읽어서 얻은 문자열을 반환한다.
	 * 
	 * @param length
	 *            지정된 길이, 단위 byte
	 * @param wantedCharsetDecoder
	 *            지정된 문자셋 디코더
	 * @return 이진 스트림에서 지정된 길이의 데이터를 지정된 문자셋으로 읽어서 얻은 문자열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getFixedLengthString(final int fixedLength, final CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException;

	/**
	 * 이진 스트림에서 지정된 길이의 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열을 반환한다.
	 * 
	 * @param length
	 *            지정된 길이, 단위 byte
	 * @return 이진 스트림에서 지정된 길이의 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getFixedLengthString(final int fixedLength) throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;

	/**
	 * 남아 있는 모든 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열을 반환한다.
	 * 
	 * @return 남아 있는 모든 데이터를 스트림 고유 문자셋으로 읽어서 얻은 문자열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getStringAll() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getStringAll(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	/**
	 * 문자열 길이 타입을 unsigned byte 로 하는 원조 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getPascalString() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	/**
	 * 문자열 길이 타입을 integer 로 하는 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getSIPascalString() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getSIPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	/**
	 * 문자열 길이 타입을 unsigned short 로 하는 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getUSPascalString() throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException, CharsetDecoderException;
	
	public String getUSPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	/**
	 * 문자열 길이 타입을 unsigned byte 로 하는 파스칼 문자열을 반환한다.
	 * 
	 * @return 문장열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 */
	public String getUBPascalString() throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;
	
	public String getUBPascalString(Charset wantedCharset) throws BufferUnderflowExceptionWithMessage,
	IllegalArgumentException, CharsetDecoderException;

	/**
	 * 이진 스트림에서 지정된 크기 만큼 읽어서 바이트 배열의 지정된 위치에 저장한다.
	 * 
	 * @param dstBuffer
	 *            목적지 바이트 배열
	 * @param offset
	 *            목저지 바이트 배열내에 데이터가 저장될 시작 위치
	 * @param length
	 *            길이
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생한다.
	 */
	public void getBytes(byte[] dst, int offset, int length)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException;

	/**
	 * 이진 스트림에서 목적지 바이트 배열의 크기 만큼 읽어서 목적지 바이트 배열로 데이터를 복사한다.
	 * 
	 * @param dstBuffer
	 *            목적지 바이트 배열
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생한다.
	 */
	public void getBytes(byte[] dst) throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException;

	
	public byte[] getBytes(int length) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException;

	/**
	 * 지정된 크기 만큼 읽을 위치를 이동시킨다.
	 * 
	 * @param n
	 *            건너 뛰기를 원하는 길이
	 * @throws BufferUnderflowExceptionWithMessage
	 *             이진 스트림은 버퍼로 구현되는데 버퍼 크기를 넘어서는 읽기 시도시 발생
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 발생한다.
	 */
	public void skip(int n) throws BufferUnderflowExceptionWithMessage,
			IllegalArgumentException;

	/**
	 * @return 스트림 문자셋
	 */
	public Charset getCharset();
	
	/**
	 * 스트림의 바이트 순서를 반환한다.
	 * 
	 * @return 바이트 순서
	 */
	public ByteOrder getByteOrder();

	/**
	 * 남아 있는 바이트 수를 반환한다.
	 * 
	 * @return 남아 있는 바이트 수
	 */
	public long available();

	/**
	 * 스트림 안에서의 위치를 반환한다.
	 * 
	 * @return 스트림 안에서의 위치
	 */
	// public long position();
	
	
	
	/**
	 * 현재 작업 커서 이후로 검색할 바이트 배열과 일치하는 위치를 반환한다.
	 * @param searchBytes 검색할 바이트 배열
	 * @return 현재 작업 커서 이후의 검색할 바이트 배열과 일치하는 첫번째 위치, 못찾았거나 혹은 스트림이 닫혔다면 -1을 반환한다.
	 */
	public long indexOf(byte[] searchBytes);
	
	public void close();
}
