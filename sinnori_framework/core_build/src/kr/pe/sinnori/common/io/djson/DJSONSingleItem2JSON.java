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

package kr.pe.sinnori.common.io.djson;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;
import kr.pe.sinnori.common.io.FixedSizeOutputStream;
import kr.pe.sinnori.common.io.djson.header.DJSONHeader;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.message.ItemTypeManger;
import kr.pe.sinnori.common.util.HexUtil;

import org.json.simple.JSONObject;

/**
 * 단일 항목 JSON 변환기 인터페이스<br/>
 * 이 인터페이스는 프로토콜 별로 구현된다.
 * 
 * @author Jonghoon won
 *
 */
public class DJSONSingleItem2JSON implements CommonRootIF, DJSONSingleItem2JSONIF {
	public static final int BYTE_ARRAY_MAX_LENGTH = Integer.MAX_VALUE/8;

	
	private DJSONSingleItemType2JSONIF[] jsonSingleItemType2JSONList = new DJSONSingleItemType2JSONIF[] { 
			new DJSONSingleItemByte2JSON(), new DJSONSingleItemUnsignedByte2JSON(), 
			new DJSONSingleItemShort2JSON(), new DJSONSingleItemUnsignedShort2JSON(),
			new DJSONSingleItemInt2JSON(), new DJSONSingleItemUnsignedInt2JSON(), 
			new DJSONSingleItemLong2JSON(), new DJSONSingleItemUBPascalString2JSON(),
			new DJSONSingleItemUSPascalString2JSON(), new DJSONSingleItemSIPascalString2JSON(), 
			new DJSONSingleItemFixedLengthString2JSON(), new DJSONSingleItemUBVariableLengthBytes2JSON(), 
			new DJSONSingleItemUSVariableLengthBytes2JSON(), new DJSONSingleItemSIVariableLengthBytes2JSON(), 
			new DJSONSingleItemFixedLengthBytes2JSON()
	};
	
	/**
	 * 생성자
	 */
	public DJSONSingleItem2JSON() {
		ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
		
		int itemTypeCnt = itemTypeManger.getItemTypeCnt();
		
		if (itemTypeCnt != jsonSingleItemType2JSONList.length) {
			String errorMessage = 
					String.format("송신 단일 항목 변환기 목록 크기[%d]와 항목 타입 관리자의 크기[%d]가 다릅니다.", 
							jsonSingleItemType2JSONList.length, itemTypeCnt);
			log.fatal(errorMessage);
			
			log.fatal("송신 단일 항목 변환기 목록 크기와 항목 타입 관리자의 크기가 다릅니다.");
			System.exit(1);
		}
		
		for (int i=0; i < jsonSingleItemType2JSONList.length; i++) {
			String itemType = jsonSingleItemType2JSONList[i].getItemType();
			try {
				int itemTypeID = itemTypeManger.getItemTypeID(itemType);
				
				if (itemTypeID != i) {
					String errorMessage = 
							String.format("항목 타입 식별자[%d]와 단일 항목 변환기 구현 클래스의 항목 타입[%s]의 항목 타입 식별자[%d] 가 일치하지 않습니다.", 
									i, itemType, itemTypeID);
					log.fatal(errorMessage);
					System.exit(1);
				}
			} catch (UnknownItemTypeException e) {
				log.fatal("UnknownItemTypeException", e);
				System.exit(1);
			}
		}
	}
	
	@Override
	public void I2S(String itemName, int itemTypeID, Object itemValue,
			int itemSizeForLang, Charset itemCharsetForLang, JSONObject jsonObj)
			throws BodyFormatException, IllegalArgumentException {
		jsonSingleItemType2JSONList[itemTypeID].putValue(itemName, itemValue, itemSizeForLang, itemCharsetForLang, jsonObj);
		
	}

	@Override
	public void S2I(String itemName, int itemTypeID, int itemSizeForLang,
			Charset itemCharsetForLang, HashMap<String, Object> itemValueHash,
			JSONObject jsonObj) throws IllegalArgumentException,
			BodyFormatException {
		itemValueHash.put(itemName,
				jsonSingleItemType2JSONList[itemTypeID].getValue(itemName, itemSizeForLang, itemCharsetForLang, jsonObj));
		
	}
	
	/** DJSON 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemByte2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
			
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < Byte.MIN_VALUE || tValue > Byte.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 byte 타입 항목[%s]의 값[%d]이 byte 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			byte value = (byte) tValue;
			
			return value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			byte value = 0;
			
			if (null != itemValue) {
				value = (Byte) itemValue;
			}
			
			
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "byte";
		}
	}

	/** DJSON 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemUnsignedByte2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned byte 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned byte 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < 0 || tValue > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned byte 타입 항목[%s]의 값[%d]이 unsigned byte 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			short value = (short) tValue;
			
			return value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			short value = 0;
			
			if (null != itemValue) {
				value = (Short) itemValue;
			}
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "unsigned byte";
		}
	}

	/** DJSON 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemShort2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 short 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 short 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < Short.MIN_VALUE || tValue > Short.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 short 타입 항목[%s]의 값[%d]이 short 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			short value = (short) tValue;
			
			return value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			short value = 0;
			
			if (null != itemValue) {
				value = (Short) itemValue;
			}
			
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "short";
		}
	}

	/** DJSON 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemUnsignedShort2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned short 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned short 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < 0 || tValue > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned short 타입 항목[%s]의 값[%d]이 unsigned short 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			int value = (int) tValue;
			
			return value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			int value = 0;	
			
			if (null != itemValue) {
				value = (Integer) itemValue;
			}

			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "unsigned short";
		}
	}

	/** DJSON 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemInt2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 integer 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 integer 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < Integer.MIN_VALUE || tValue > Integer.MAX_VALUE) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 integer 타입 항목[%s]의 값[%d]이 integer 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			int value = (int) tValue;
			
			return value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			int value = 0;
			
			if (null != itemValue) {
				value = (Integer) itemValue;
			}
			
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "integer";
		}
	}

	/** DJSON 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemUnsignedInt2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 unsigned integer 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned integer 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue; 
			if (tValue < 0 || tValue > CommonStaticFinal.MAX_UNSIGNED_INT) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 unsigned integer 타입 항목[%s]의 값[%d]이 unsigned integer 값 범위를 벗어났습니다.", 
								itemName, tValue);
				throw new BodyFormatException(errorMessage);
			}
			
			
			return tValue;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			long value = 0;
			
			if (null != itemValue) {
				value = (Long) itemValue;
			}
			
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "unsigned integer";
		}
	}

	/** DJSON 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemLong2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 long 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			
			if (!(jsonValue instanceof Long)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 long 타입 항목[%s]의 값의 타입[%s]이 Long 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			long tValue = (long)jsonValue;
			
			return tValue;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			long value = 0;
			
			if (null != itemValue) {
				value = (Long) itemValue;
			}
			
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "long";
		}
	}

	/** DJSON 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemUBPascalString2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			return jsonValue;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			String value = CommonStaticFinal.EMPTY_STRING;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			byte[] valueBytes = value.getBytes(DJSONHeader.JSON_STRING_CHARSET);
			if (valueBytes.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
				String errorMessage = 
						String.format("UBPascalString 타입 항목[%s]의 %s 문자셋으로 변환된 바이트 길이[%d]가 unsigned byte 최대값[%d]을 넘었습니다.", 
								itemName, DJSONHeader.JSON_STRING_CHARSET_NAME, valueBytes.length, CommonStaticFinal.MAX_UNSIGNED_BYTE);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "ub pascal string";
		}
	}

	/** DJSON 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemUSPascalString2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 USPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 USPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			return jsonValue;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			String value = CommonStaticFinal.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			byte[] valueBytes = value.getBytes(DJSONHeader.JSON_STRING_CHARSET);
			if (valueBytes.length > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
				String errorMessage = 
						String.format("USPascalString 타입 항목[%s]의 %s 문자셋으로 변환된 바이트 길이[%d]가 unsigned short 최대값[%d]을 넘었습니다.", 
								itemName, DJSONHeader.JSON_STRING_CHARSET_NAME, valueBytes.length, CommonStaticFinal.MAX_UNSIGNED_SHORT);
				throw new IllegalArgumentException(errorMessage);
			}
			
			jsonObj.put(itemName, value);
		}

		@Override
		public String getItemType() {
			return "us pascal string";
		}
	}

	/** DJSON 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemSIPascalString2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 SIPascalString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 SIPascalString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			
			
			return jsonValue;
			
		}

		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			String value = CommonStaticFinal.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			jsonObj.put(itemName, value);
		}
		
		@Override
		public String getItemType() {
			return "si pascal string";
		}
	}

	/** DJSON 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemFixedLengthString2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 FixedLengthString 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 FixedLengthString 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] valueBytes = ((String)jsonValue).getBytes(itemCharsetForLang);
			if (valueBytes.length > itemSizeForLang) {
				String errorMessage = 
						String.format("FixedLengthString 타입 항목[%s]의 %s 문자셋으로 변환된 바이트 길이[%d]가 지정된 고정크기[%d] 보다 큽니다.", 
								itemName, itemCharsetForLang.name(), valueBytes.length, itemSizeForLang);
				throw new BodyFormatException(errorMessage);
			}
			
			return jsonValue;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			String value = CommonStaticFinal.EMPTY_STRING;;
			
			if (null != itemValue) {
				value = (String) itemValue;
			}
			
			ByteBuffer outputBuffer = ByteBuffer.allocate(itemSizeForLang);
			
			/** 고정 크기 출력 스트림 */
			FixedSizeOutputStream fsos = 
					new FixedSizeOutputStream(outputBuffer, itemCharsetForLang, CharsetUtil.createCharsetEncoder(itemCharsetForLang));
			
			try {
				fsos.putString(itemSizeForLang, value);
			} catch (BufferOverflowException e) {
				/** dead code area */
				e.printStackTrace();
				System.exit(1);
			} catch (NoMoreDataPacketBufferException e) {
				/** dead code area */
				e.printStackTrace();
				System.exit(1);
			}
			
			outputBuffer.flip();
			
			jsonObj.put(itemName, new String(outputBuffer.array(), itemCharsetForLang));

		}
		
		@Override
		public String getItemType() {
			return "fixed length string";
		}
	}

	

	/** DJSON 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemUBVariableLengthBytes2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 UBVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 UBVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.hexToByteArray(tValue);
					
					if (value.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
						String errorMessage = 
								String.format("UBVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned byte 최대값을 넘었습니다.", 
										itemName, value.length);
						throw new BodyFormatException(errorMessage);
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 UBVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			byte value[] = new byte[0];
			
			if (null != itemValue) {
				value = (byte[]) itemValue;
				
				if (value.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
					String errorMessage = 
							String.format("UBVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned byte 최대값을 넘었습니다.", 
									itemName, value.length);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			String tValue = HexUtil.byteArrayAllToHex(value);
			
			
			jsonObj.put(itemName, tValue);
		}
		
		@Override
		public String getItemType() {
			return "ub variable length byte[]";
		}
	}

	/** DJSON 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemUSVariableLengthBytes2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 USVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 USVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.hexToByteArray(tValue);
					
					if (value.length > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
						String errorMessage = 
								String.format("USVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned short 최대값을 넘었습니다.", 
										itemName, value.length);
						throw new BodyFormatException(errorMessage);
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 USVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			byte value[] = new byte[0];
			
			if (null != itemValue) {
				value = (byte[]) itemValue;
				
				if (value.length > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
					String errorMessage = 
							String.format("USVariableLengthBytes 타입 항목[%s]의 길이[%d]가 unsigned short 최대값을 넘었습니다.", 
									itemName, value.length);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			String tValue = HexUtil.byteArrayAllToHex(value);
			
			
			jsonObj.put(itemName, tValue);
		}
		
		@Override
		public String getItemType() {
			return "us variable length byte[]";
		}
	}
	
	/** DJSON 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemSIVariableLengthBytes2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {
				
			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 SIVariableLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 SIVariableLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.hexToByteArray(tValue);
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 SIVariableLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			byte value[] = new byte[0];
			
			if (null != itemValue) {
				value = (byte[]) itemValue;
				
				if (value.length > BYTE_ARRAY_MAX_LENGTH) {
					String errorMessage = 
							String.format("DJSON 메시지 프로토콜은 SIVariableLengthBytes 타입, 즉 byte[] 크기[%d]는 최대 처리 가능한 길이[%d]로 제한됩니다. ", 
									itemName, value.length, BYTE_ARRAY_MAX_LENGTH);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			String tValue = HexUtil.byteArrayAllToHex(value);
			
			
			jsonObj.put(itemName, tValue);
		}
		
		@Override
		public String getItemType() {
			return "si variable length byte[]";
		}
	}
	
	/** DJSON 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class DJSONSingleItemFixedLengthBytes2JSON implements DJSONSingleItemType2JSONIF {
		@Override
		public Object getValue(String itemName, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj) throws IllegalArgumentException, BodyFormatException {

			Object jsonValue = jsonObj.get(itemName);
			if (null == jsonValue) {
				String errorMessage = 
						String.format("JSON Object[%s]에 FixedLengthBytes 타입 항목[%s]이 존재하지 않습니다.", 
								jsonObj.toJSONString(), itemName);
				throw new BodyFormatException(errorMessage);
			}
			if (!(jsonValue instanceof String)) {
				String errorMessage = 
						String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값의 타입[%s]이 String 이 아닙니다.", 
								itemName, jsonValue.getClass().getName());
				throw new BodyFormatException(errorMessage);
			}
			
			byte[] value = null;
			String tValue = (String)jsonValue;
			
			if (tValue.isEmpty()) {
				value = new byte[0];
			} else {
				try {
					value = HexUtil.hexToByteArray(tValue);
					
					if (value.length != itemSizeForLang) {
						throw new IllegalArgumentException(
								String.format(
										"파라미터로 넘어온 바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
										value.length, itemSizeForLang));
					}
				} catch(NumberFormatException e) {
					String errorMessage = 
							String.format("JSON Object 로 부터 얻은 FixedLengthBytes 타입 항목[%s]의 값[%s]이 hex 문자열이 아닙니다.", 
									itemName, tValue);
					throw new BodyFormatException(errorMessage);
				}
			}
			
			return value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void putValue(String itemName, Object itemValue, int itemSizeForLang,
				Charset itemCharsetForLang, JSONObject jsonObj)
				throws IllegalArgumentException {
			
			byte value[] = new byte[0];
			
			if (null != itemValue) {
				value = (byte[]) itemValue;
				
				if (value.length != itemSizeForLang) {
					throw new IllegalArgumentException(
							String.format(
									"파라미터로 넘어온 바이트 배열의 크기[%d]가 메시지 정보에서 지정한 크기[%d]와 다릅니다. 고정 크기 바이트 배열에서는 일치해야 합니다.",
									value.length, itemSizeForLang));
				}
				
				if (value.length > BYTE_ARRAY_MAX_LENGTH) {
					String errorMessage = 
							String.format("DJSON 메시지 프로토콜은 SIVariableLengthBytes 타입, 즉 byte[] 크기[%d]는 최대 처리 가능한 길이[%d]로 제한됩니다. ", 
									itemName, value.length, BYTE_ARRAY_MAX_LENGTH);
					throw new IllegalArgumentException(errorMessage);
				}
			}
			
			String tValue = HexUtil.byteArrayAllToHex(value);
			
			
			jsonObj.put(itemName, tValue);	
		}
		
		@Override
		public String getItemType() {
			return "fixed length byte[]";
		}
	}

	

}