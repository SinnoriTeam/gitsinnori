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
package kr.pe.sinnori.common.message.info;

import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;

/**
 * 단일 항목 정보 클래스
 * 
 * @author Won Jonghoon
 * 
 */
public class SingleItemInfo extends AbstractItemInfo {
	private Logger logger =  Logger.getGlobal();
	
	private String itemName;
	private String firstUpperItemName;
	private String itemType;
	private String itemTypeForJavaLang;
	private String itemTypeForJavaLangClassCasting;
	private int itemTypeID;
	private String itemDefaultValue;
	private Object itemDefaultValueForLang=null;
	private String itemSize;
	private int itemSizeForLang = -1;
	private String itemCharset;
	
	// private boolean itemUseYN=false;

	
	
	private static final ItemTypeManger itemTypeIDManger = ItemTypeManger.getInstance(); 
	

	/**
	 * 단일 항목 정보 클래스 생성자
	 * 
	 * @param itemName
	 *            항목 이름
	 * @param itemType
	 *            항목 타입
	 * @param itemDefaultValue
	 *            디폴트 값
	 * @param itemSize
	 *            항목 타입 부가 정보중 하나인 크기
	 * @param itemCharset
	 *            항목 타입 부가 정보중 하나인 문자셋
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 값이 들어올 경우 던지는 예외
	 */
	public SingleItemInfo(String itemName, String itemType,
			String itemDefaultValue, String itemSize, String itemCharset)
			throws IllegalArgumentException {
		this.itemName = itemName;
		this.firstUpperItemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1); 
		this.itemType = itemType;
		this.itemDefaultValue = itemDefaultValue;
		this.itemSize = itemSize;
		this.itemCharset = itemCharset;

		
		try {
			itemTypeID = itemTypeIDManger.getItemTypeID(itemType);
		} catch (UnknownItemTypeException e) {
			// log.error("UnknownItemTypeException", e);
			e.printStackTrace();
			System.exit(1);
		}
				
		if (itemType.equals("byte")) {
			itemTypeForJavaLang = "byte";
			itemTypeForJavaLangClassCasting = "Byte";
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Byte.parseByte(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 byte 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					// log.warn(errorMessage, nfe);
					logger.log(Level.WARNING, errorMessage);
					nfe.printStackTrace();
					
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("unsigned byte")) {
			itemTypeForJavaLang = "short";
			itemTypeForJavaLangClassCasting = "Short";
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Short.parseShort(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 unsigned byte 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					// log.warn(errorMessage, nfe);
					logger.log(Level.WARNING, errorMessage);
					nfe.printStackTrace();
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("short")) {
			itemTypeForJavaLang = "short";
			itemTypeForJavaLangClassCasting = "Short";
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Short.parseShort(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 short 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					// log.warn(errorMessage, nfe);
					logger.log(Level.WARNING, errorMessage);
					nfe.printStackTrace();
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("unsigned short")) {
			itemTypeForJavaLang = "int";
			itemTypeForJavaLangClassCasting = "Integer";
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Integer.parseInt(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 unsigned short 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					// log.warn(errorMessage, nfe);
					logger.log(Level.WARNING, errorMessage);
					nfe.printStackTrace();
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("integer")) {
			itemTypeForJavaLang = "int";
			itemTypeForJavaLangClassCasting = "Integer";
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Integer.parseInt(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 integer 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					// log.warn(errorMessage, nfe);
					logger.log(Level.WARNING, errorMessage);
					nfe.printStackTrace();
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("unsigned integer")) {
			itemTypeForJavaLang = "long";
			itemTypeForJavaLangClassCasting = "Long";
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Long.parseLong(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 unsigned integer 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					// log.warn(errorMessage, nfe);
					logger.log(Level.WARNING, errorMessage);
					nfe.printStackTrace();
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("long")) {
			itemTypeForJavaLang = "long";
			itemTypeForJavaLangClassCasting = "Long";
			
			if (null != itemDefaultValue) {
				try {
					itemDefaultValueForLang = Long.parseLong(itemDefaultValue);
				} catch(NumberFormatException nfe) {
					String errorMessage = String.format("타입 long 항목[%s]의 디폴트 값[%s] 지정이 잘못되었습니다.", itemName, itemDefaultValue);
					
					// log.warn(errorMessage, nfe);
					logger.log(Level.WARNING, errorMessage);
					nfe.printStackTrace();
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (itemType.equals("ub pascal string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemType.equals("us pascal string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemType.equals("si pascal string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		} else if (itemType.equals("fixed length string")) {
			itemTypeForJavaLang = "String";
			itemTypeForJavaLangClassCasting = "String";
			
			if (null != itemDefaultValue) {
				itemDefaultValueForLang = itemDefaultValue;
			}
		
		} else if (itemType.equals("ub variable length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemType.equals("us variable length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemType.equals("si variable length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 variable length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemType.equals("fixed length byte[]")) {
			itemTypeForJavaLang = "byte[]";
			itemTypeForJavaLangClassCasting = "byte[]";
			
			if (null != itemDefaultValue) {
				String errorMessage = "타입 fixed length byte[]은  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}
		} else if (itemType.equals("java sql date")) {
			itemTypeForJavaLang = "java.sql.Date";
			itemTypeForJavaLangClassCasting = "java.sql.Date";
			
			/*if (null != itemDefaultValue) {
				String errorMessage = "타입 java.sql.date 는  디폴트 값을 지정할 수 없습니다.";
				throw new IllegalArgumentException(errorMessage);
				
			}*/
		} else if (itemType.equals("java sql timestamp")) {
			itemTypeForJavaLang = "java.sql.Timestamp";
			itemTypeForJavaLangClassCasting = "java.sql.Timestamp";
		} else if (itemType.equals("boolean")) {
			itemTypeForJavaLang = "boolean";
			itemTypeForJavaLangClassCasting = "java.lang.Boolean";
			
		} else {
			/** XSD 에서 항목 타입을 제약한다. 따라서 이곳 로직은 들어 올 수 없다. */
			String errorMessage = String.format("unkown type[%s]", itemType);
			// log.warn(errorMessage);
			logger.log(Level.WARNING, errorMessage);
			
			// System.exit(1);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null != itemSize) {
			try {
				itemSizeForLang = Integer.parseInt(itemSize);
			} catch (NumberFormatException num_e) {
				/** SAX 파싱 과정에서 검사를 해서 이곳 로직을 들어 올 수 없다. */
				String errorMessage = String.format(
						"타입 부가 정보인 크기[%s]가 숫자가 아닙니다.", itemSize);
				// log.warn(errorMessage);
				logger.log(Level.WARNING, errorMessage);
				num_e.printStackTrace();
				throw new IllegalArgumentException(errorMessage);
			}
		}		
	}
	
	public String getFirstUpperItemName() {
		return firstUpperItemName;
	}

	
	public String getItemTypeForJavaLang() {
		return itemTypeForJavaLang;
	}
	
	public String getItemTypeForJavaLangClassCasting() {
		return itemTypeForJavaLangClassCasting;
	}
	
	
	/**
	 * @see ItemTypeManger
	 * @return 항목 타입 식별자
	 */
	public int getItemTypeID() {
		return itemTypeID;
	}

	/**
	 * 정수형 항목 크기를 반환한다.
	 * 
	 * @return 정수형 항목 크기
	 */
	public int getItemSizeForLang() {
		return itemSizeForLang;
	}

	/**
	 * 타입 부과 정보인 문자셋을 반환한다.
	 * 
	 * @return 타입 부가 정보인 문자셋
	 */
	public String getItemCharset() {
		return itemCharset;
	}

	/**
	 * 항목 타입을 반환한다.
	 * 
	 * @return 항목 타입
	 */
	public String getItemType() {
		return itemType;
	}

	/**
	 * 타입 부과 정보인 크기를 반환한다.
	 * 
	 * @return 타입 부과 정보인 크기
	 */
	public String getItemSize() {
		return itemSize;
	}

	/**
	 * 디폴트 값을 반환한다.
	 * 
	 * @return 디폴트 값
	 */
	public String getItemDefaultValue() {
		return itemDefaultValue;
	}
	
	public Object getItemDefaultValueForLang() {
		return itemDefaultValueForLang;
	}

	

	/******************* AbstractItemInfo start ***********************/
	@Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public CommonType.LOGICAL_ITEM_GUBUN getLogicalItemGubun() {
		return CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM;
	}

	/******************* AbstractItemInfo end ***********************/

	

	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{ itemName=[");
		strBuff.append(itemName);
		strBuff.append("], itemType=[");
		strBuff.append(itemType);
		strBuff.append("], itemDefaultValue=[");
		strBuff.append(itemDefaultValue);
		strBuff.append("], itemSize=[");
		strBuff.append(itemSize);
		strBuff.append("], itemCharset=[");
		strBuff.append(itemCharset);
		strBuff.append("] }");

		return strBuff.toString();
	}
}
