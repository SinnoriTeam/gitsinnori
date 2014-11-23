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
package kr.pe.sinnori.impl.message.BoardUploadFileOutDTO;

import java.nio.charset.Charset;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * BoardUploadFileOutDTO 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardUploadFileOutDTODecoder extends MessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardUploadFileOutDTO 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 BoardUploadFileOutDTO 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		BoardUploadFileOutDTO boardUploadFileOutDTO = new BoardUploadFileOutDTO();
		String sigleItemPath0 = "BoardUploadFileOutDTO";

		boardUploadFileOutDTO.setAttachId((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "attachId" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileOutDTO.setOwnerId((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "ownerId" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileOutDTO.setIp((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "ip" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileOutDTO.setRegisterDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "registerDate" // itemName
		, 16 // itemTypeID
		, "java sql timestamp" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileOutDTO.setModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "modifiedDate" // itemName
		, 16 // itemTypeID
		, "java sql timestamp" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		boardUploadFileOutDTO.setAttachFileCnt((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "attachFileCnt" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		int attachFileListSize = boardUploadFileOutDTO.getAttachFileCnt();
		Object attachFileMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "attachFile", attachFileListSize, middleReadObj);
		java.util.List<BoardUploadFileOutDTO.AttachFile> attachFileList = new java.util.ArrayList<BoardUploadFileOutDTO.AttachFile>();
		for (int i=0; i < attachFileListSize; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("AttachFile[").append(i).append("]").toString();
			Object attachFileMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, attachFileMiddleReadArray, i);
			BoardUploadFileOutDTO.AttachFile attachFile = new BoardUploadFileOutDTO.AttachFile();
			attachFileList.add(attachFile);

			attachFile.setAttachSeq((Short)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "attachSeq" // itemName
			, 1 // itemTypeID
			, "unsigned byte" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, attachFileMiddleReadObj));

			attachFile.setAttachFileName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "attachFileName" // itemName
			, 8 // itemTypeID
			, "us pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, attachFileMiddleReadObj));

			attachFile.setSystemFileName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "systemFileName" // itemName
			, 8 // itemTypeID
			, "us pascal string" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, attachFileMiddleReadObj));
		}
		boardUploadFileOutDTO.setAttachFileList(attachFileList);
		return boardUploadFileOutDTO;
	}
}