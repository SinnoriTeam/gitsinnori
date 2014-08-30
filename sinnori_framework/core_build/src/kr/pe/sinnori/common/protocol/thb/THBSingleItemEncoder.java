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
package kr.pe.sinnori.common.protocol.thb;

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.util.Arrays;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.OutputStreamIF;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * THB 단일 항목 인코더
 * @author "Jonghoon Won"
 *
 */
public class THBSingleItemEncoder implements SingleItemEncoderIF, CommonRootIF {
	
	private interface THBTypeSingleItemEncoderIF {
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws Exception;
	}
	
	private final THBTypeSingleItemEncoderIF[] dhbTypeSingleItemEncoderList = new THBTypeSingleItemEncoderIF[] { 
			new THBByteSingleItemEncoder(), new THBUnsignedByteSingleItemEncoder(), 
			new THBShortSingleItemEncoder(), new THBUnsignedShortSingleItemEncoder(),
			new THBIntSingleItemEncoder(), new THBUnsignedIntSingleItemEncoder(), 
			new THBLongSingleItemEncoder(), new THBUBPascalStringSingleItemEncoder(),
			new THBUSPascalStringSingleItemEncoder(), new THBSIPascalStringSingleItemEncoder(), 
			new THBFixedLengthStringSingleItemEncoder(), new THBUBVariableLengthBytesSingleItemEncoder(), 
			new THBUSVariableLengthBytesSingleItemEncoder(), new THBSIVariableLengthBytesSingleItemEncoder(), 
			new THBFixedLengthBytesSingleItemEncoder()
	};

		
	/** THB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBByteSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, NoMoreDataPacketBufferException {
			
			byte value = 0;
				
			if (null != itemValue) {
				value = (Byte) itemValue;
			}	
			sw.putByte(value);
		}
	}

	/** THB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedByteSingleItemEncoder implements THBTypeSingleItemEncoderIF {

		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			short value = 0;
			
			if (null != itemValue) {
				value = (Short) itemValue;
			}
			
			
			sw.putUnsignedByte(value);
		}
	}

	/** THB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBShortSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, NoMoreDataPacketBufferException {
			short value = 0;
			
			if (null != itemValue) {
				value = (Short) itemValue;
			}
			
			sw.putShort(value);
		}
	}

	/** THB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedShortSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			int value = 0;	
			
			if (null != itemValue) {
				value = (Integer) itemValue;
			}

			sw.putUnsignedShort(value);
		}
	}

	/** THB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBIntSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, NoMoreDataPacketBufferException {
			int value = 0;
			
			if (null != itemValue) {
				value = (Integer) itemValue;
			}
			
			sw.putInt(value);
		}
	}

	/** THB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedIntSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			long value = 0;
			
			if (null != itemValue) {
				value = (Long) itemValue;
			}
			
			sw.putUnsignedInt(value);
		}
	}

	/** THB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBLongSingleItemEncoder implements THBTypeSingleItemEncoderIF {		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, NoMoreDataPacketBufferException {
			long value = 0;
			
			if (null != itemValue) {
				value = (Long) itemValue;
			}
			
			sw.putLong(value);
		}
	}

	/** THB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBPascalStringSingleItemEncoder implements THBTypeSingleItemEncoderIF {		
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinalVars.EMPTY_STRING;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			sw.putUBPascalString(value);
		}
	}

	/** THB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSPascalStringSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			sw.putUSPascalString(value);
		}
	}

	/** THB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIPascalStringSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			sw.putSIPascalString(value);
		}
	}

	/** THB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthStringSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			String value = CommonStaticFinalVars.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			if (null == itemCharsetForLang) {
				sw.putString(itemSizeForLang, value);
			} else {
				sw.putString(itemSizeForLang, value,
						CharsetUtil.createCharsetEncoder(itemCharsetForLang));
			}

		}
	}

	

	/** THB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBVariableLengthBytesSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				sw.putUnsignedByte((short)0);
			} else {
				byte value[] = (byte[]) itemValue;
				/*
				if (realValue.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
					throw new IllegalArgumentException(String.format(
							"파라미터 바이트 배열 길이[%d]는 unsigned byte 최대값[%d]을 넘을 수 없습니다.", realValue.length, CommonStaticFinal.MAX_UNSIGNED_BYTE));
				}
				*/
				sw.putUnsignedByte(value.length);
				sw.putBytes(value);
			}
		}
	}

	/** THB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSVariableLengthBytesSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				sw.putUnsignedShort(0);
			} else {
				byte value[] = (byte[]) itemValue;
				sw.putUnsignedShort(value.length);
				sw.putBytes(value);
			}
		}
	}
	
	/** THB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIVariableLengthBytesSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {
				sw.putInt(0);
			} else {

				byte value[] = (byte[]) itemValue;
				sw.putInt(value.length);
				sw.putBytes(value);
			}
		}
	}
	
	/** THB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthBytesSingleItemEncoder implements THBTypeSingleItemEncoderIF {
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, OutputStreamIF sw)
				throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
			if (null == itemValue) {

				byte value[] = new byte[itemSizeForLang];
				Arrays.fill((byte[]) value, (byte) 0);
				sw.putBytes((byte[]) value, 0, itemSizeForLang);
			} else {
				byte value[] = (byte[]) itemValue;	

				if (value.length != itemSizeForLang) {
					throw new IllegalArgumentException(
							String.format(
									"파라미터로 넘어온 바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
									value.length, itemSizeForLang));
				}
				
				
				sw.putBytes(value);
			}
		}
	}

	@Override
	public void putValueToMiddleWriteObj(String path, String itemName,
			int itemTypeID, String itemTypeName, Object itemValue,
			int itemSizeForLang, String itemCharset,
			Charset charsetOfProject, Object middleWriteObj) throws Exception {
		if (!(middleWriteObj instanceof OutputStreamIF)) {
			String errorMessage = String.format(
					"중간 다리역활 출력 객체[%s] 타입이 OutputStreamIF 이 아닙니다.",
					middleWriteObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		Charset itemCharsetForLang = null;
		if (null == itemCharset) {
			itemCharsetForLang = charsetOfProject;
		} else {
			itemCharsetForLang = Charset.forName(itemCharset);
		}
		
		OutputStreamIF sw = (OutputStreamIF)middleWriteObj;		
		try {
			dhbTypeSingleItemEncoderList[itemTypeID].putValue(itemName, itemValue, itemSizeForLang, itemCharsetForLang, sw);
		} catch(IllegalArgumentException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("잘못된 파라미티터 에러::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSizeForLang);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(itemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.info(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch(BufferOverflowException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("BufferOverflowException::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSizeForLang);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(itemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.info(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch(NoMoreDataPacketBufferException e) {
			StringBuffer errorMessageBuilder = new StringBuffer("NoMoreDataPacketBufferException::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSizeForLang);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(itemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.warn(errorMessage);
			throw new NoMoreDataPacketBufferException(errorMessage);
		} catch(OutOfMemoryError e) {
			throw e;
		} catch(Exception e) {
			StringBuffer errorMessageBuilder = new StringBuffer("알수없는에러::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSizeForLang);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(itemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		}
	}

	@Override
	public Object getMiddleWriteObjFromArrayObj(String path, Object arrayObj, int inx) throws BodyFormatException {
		return arrayObj;
	}
	
	@Override
	public Object getArrayObjFromMiddleWriteObj(String path, String arrayName,
			int arrayCntValue, Object middleWriteObj) throws BodyFormatException {
		return middleWriteObj;
	}
}
