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
package kr.pe.sinnori.impl.message.SyncUploadReq;

import java.nio.charset.Charset;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * SyncUploadReq 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class SyncUploadReqDecoder extends AbstractMessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 SyncUploadReq 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 SyncUploadReq 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		SyncUploadReq syncUploadReq = new SyncUploadReq();
		String sigleItemPath0 = "SyncUploadReq";

		syncUploadReq.setClientSourceFileID((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "clientSourceFileID" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		syncUploadReq.setServerTargetFileID((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "serverTargetFileID" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		syncUploadReq.setFileBlockNo((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "fileBlockNo" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		syncUploadReq.setFileData((byte[])
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "fileData" // itemName
		, 13 // itemTypeID
		, "si variable length byte[]" // itemTypeName
		, -1 // itemSize
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));
		return syncUploadReq;
	}
}