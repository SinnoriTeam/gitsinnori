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
package kr.pe.sinnori.impl.message.SelfExn;

import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * SelfExn 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class SelfExnEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		SelfExn selfExn = (SelfExn)messageObj;
		encodeBody(selfExn, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(SelfExn selfExn, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		LinkedList<String> pathStack = new LinkedList<String>();
		pathStack.push("SelfExn");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "errorPlace"
			, SingleItemType.UB_PASCAL_STRING // itemType
			, selfExn.getErrorPlace() // itemValue
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "errorGubun"
			, SingleItemType.UB_PASCAL_STRING // itemType
			, selfExn.getErrorGubun() // itemValue
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "errorMessageID"
			, SingleItemType.UB_PASCAL_STRING // itemType
			, selfExn.getErrorMessageID() // itemValue
			, -1 // itemSize
			, "ISO-8859-1" // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "errorMessage"
			, SingleItemType.US_PASCAL_STRING // itemType
			, selfExn.getErrorMessage() // itemValue
			, -1 // itemSize
			, "utf8" // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}