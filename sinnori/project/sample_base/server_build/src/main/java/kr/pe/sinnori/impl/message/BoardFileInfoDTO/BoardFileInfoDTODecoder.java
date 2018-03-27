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
package kr.pe.sinnori.impl.message.BoardFileInfoDTO;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * BoardFileInfoDTO 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class BoardFileInfoDTODecoder extends AbstractMessageDecoder {
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		BoardFileInfoDTO boardFileInfoDTO = new BoardFileInfoDTO();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardFileInfoDTO");

		boardFileInfoDTO.setAttachId((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "attachId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardFileInfoDTO.setOwnerId((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ownerId" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardFileInfoDTO.setIp((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "ip" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardFileInfoDTO.setRegisterDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "registerDate" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		boardFileInfoDTO.setModifiedDate((java.sql.Timestamp)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "modifiedDate" // itemName
			, kr.pe.sinnori.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return boardFileInfoDTO;
	}
}