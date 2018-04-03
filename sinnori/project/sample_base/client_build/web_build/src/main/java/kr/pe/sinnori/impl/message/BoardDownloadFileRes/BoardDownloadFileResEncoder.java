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
package kr.pe.sinnori.impl.message.BoardDownloadFileRes;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * BoardDownloadFileRes 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class BoardDownloadFileResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		BoardDownloadFileRes boardDownloadFileRes = (BoardDownloadFileRes)messageObj;
		encodeBody(boardDownloadFileRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(BoardDownloadFileRes boardDownloadFileRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("BoardDownloadFileRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "ownerId"
			, kr.pe.sinnori.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, boardDownloadFileRes.getOwnerId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachId"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDownloadFileRes.getAttachId() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachSeq"
			, kr.pe.sinnori.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, boardDownloadFileRes.getAttachSeq() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "attachFiledName"
			, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardDownloadFileRes.getAttachFiledName() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "systemFileName"
			, kr.pe.sinnori.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, boardDownloadFileRes.getSystemFileName() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}