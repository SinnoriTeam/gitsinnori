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


package kr.pe.sinnori.common.message.builder.info;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.UnknownItemTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <pre>
 * 신놀이 메시지를 구성하는 항목 타입을 관리 클래스. 
 * 자바 문자열로 변환은 org.apache.commons.lang.StringEscapeUtils 를 이용하는데,
 * 한글의 경우 유니코드 문자열로 변환을 하기때문에 읽기 곤란한 점이 있다. 
 * 하지만 한글은 주석에서만 있으므로 나중 영문으로 변경하면 회피할 수 있는 문제이므로 그대로 넘어가기로 함.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class ItemValueTypeManger {
	private final Logger log = LoggerFactory.getLogger(ItemValueTypeManger.class);
	
	private String messageXSLStr = null;
	
	private LinkedHashMap<String, Integer> itemValueTypeToIDHash  = new LinkedHashMap<String, Integer>();
	private HashMap<Integer, String> itemIDToItemValueTypeHash  = new HashMap<Integer, String>();
		
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class ItemValueTypeMangerHolder {
		static final ItemValueTypeManger singleton = new ItemValueTypeManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static ItemValueTypeManger getInstance() {
		return ItemValueTypeMangerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private ItemValueTypeManger() {
		
		int id=0;
		String itemValueType = "byte";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "unsigned byte";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "short";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "unsigned short";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "integer";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "unsigned integer";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "long";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "ub pascal string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "us pascal string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "si pascal string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "fixed length string";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		itemValueType = "ub variable length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "us variable length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "si variable length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "fixed length byte[]";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "java sql date";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "java sql timestamp";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		itemValueType = "boolean";
		itemValueTypeToIDHash.put(itemValueType, id);
		itemIDToItemValueTypeHash.put(id, itemValueType);
		id++;
		
		
		/** 신규 타입 추가시 구현 언어인 자바 타입등을 정의한 SingleItemInfo 에도 추가를 해 주어야 한다. */
		
		StringBuilder mesgXSLStringBuilder = new StringBuilder();
		mesgXSLStringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		mesgXSLStringBuilder.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t<xs:group name=\"itemgroup\">\n");
		mesgXSLStringBuilder.append("\t\t<xs:choice>\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:element name=\"singleitem\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:element name=\"desc\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"type\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		
		Iterator<String> itemValueTypeIter = itemValueTypeToIDHash.keySet().iterator();

		while (itemValueTypeIter.hasNext()) {
			mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"");
			mesgXSLStringBuilder.append(itemValueTypeIter.next());
			mesgXSLStringBuilder.append("\" />\n");
		}
		
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"size\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"charset\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"defaultValue\" use=\"optional\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:element>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:element name=\"array\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cnttype\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"reference\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"direct\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cntvalue\" use=\"required\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:element>\n");
		mesgXSLStringBuilder.append("\t\t</xs:choice>\n");
		mesgXSLStringBuilder.append("\t</xs:group>\n");
		mesgXSLStringBuilder.append("\n");
		mesgXSLStringBuilder.append("\t<xs:element name=\"");
		mesgXSLStringBuilder.append(CommonStaticFinalVars.MESSAGE_INFO_XML_FILE_ROOT_TAG);
		mesgXSLStringBuilder.append("\">\n");
		mesgXSLStringBuilder.append("\t\t<xs:complexType>\n");
		mesgXSLStringBuilder.append("\t\t\t<xs:sequence>\n");
		
		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"messageID\" minOccurs=\"1\" maxOccurs=\"1\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:pattern value=\"[a-zA-Z][a-zA-Z1-9]+\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:element>\n");
		
		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"direction\" minOccurs=\"1\" maxOccurs=\"1\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t<xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t\t<xs:pattern value=\"[a-zA-Z][a-zA-Z1-9_]+\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t\t</xs:restriction>\n");
		mesgXSLStringBuilder.append("\t\t\t\t\t</xs:simpleType>\n");
		mesgXSLStringBuilder.append("\t\t\t\t</xs:element>\n");
		
		mesgXSLStringBuilder.append("\t\t\t\t<xs:element name=\"desc\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"1\" />\n");
		mesgXSLStringBuilder.append("\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		mesgXSLStringBuilder.append("\t\t\t</xs:sequence>\n");
		mesgXSLStringBuilder.append("\t\t</xs:complexType>\n");
		mesgXSLStringBuilder.append("\t</xs:element>\n");
		mesgXSLStringBuilder.append("</xs:schema>\n");
		
		messageXSLStr = mesgXSLStringBuilder.toString();
		
		FileWriter fw = null;
		BufferedWriter bw  = null;
		try {
			File f = File.createTempFile("SinnoriMsgXSL", ".tmp");
			
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			
			bw.write(messageXSLStr);
			
			
			log.info("the sinnori message information .xsl temporary file[{}] was created successfully", f.getAbsolutePath());
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getMessageXSLStr() {
		return messageXSLStr;
	}
	
	public ByteArrayInputStream getMesgXSLInputSream() {
		ByteArrayInputStream xslByteArrayInputStream = new ByteArrayInputStream(messageXSLStr.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET));
		return xslByteArrayInputStream;
	}
	
	public int getItemValueTypeID(String itemValueType) throws UnknownItemTypeException {
		Integer itemValueTypeID = itemValueTypeToIDHash.get(itemValueType);
		if (null == itemValueTypeID) {
			String errorMessage = new StringBuilder("the parameter itemValueType[")
			.append(itemValueType).append("] is not an element of item value type set")
			.append(getUnmodifiableItemValueTypeSet().toString()).toString();
			UnknownItemTypeException e = new UnknownItemTypeException(errorMessage);			
			throw e;
		}		
		return itemValueTypeID.intValue();
	}
	
	public String getItemValueType(int itemValueTypeID) throws UnknownItemTypeException {
		String itemValueType = itemIDToItemValueTypeHash.get(itemValueTypeID);
		if (null == itemValueType) {
			String errorMessage = String.format("unknown message item type id[%d]", itemValueTypeID);
			UnknownItemTypeException e = new UnknownItemTypeException(errorMessage);
			// log.warn(errorMessage, e);
			e.printStackTrace();
			throw e;
		}		
		return itemValueType;
	}
	
	public int getItemValueTypeCnt() {
		return itemValueTypeToIDHash.size();
	}
	
	public Set<String> getUnmodifiableItemValueTypeSet() {
		Set<String> itemValueTypeSet = Collections.unmodifiableSet(itemValueTypeToIDHash.keySet());
		return itemValueTypeSet;
	}
}
