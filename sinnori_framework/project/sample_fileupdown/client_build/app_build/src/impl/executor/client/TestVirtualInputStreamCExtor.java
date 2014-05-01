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


package impl.executor.client;

/**
 * 가상적으로 입력 메시지들을 넣은 거대 스트림으로부터 랜덤하게 데이터를 순차적으로 읽으면서 출력 메시지를 바르게 추출하는지 검사하기 위한 로직이다.
 * 
 * @author Jonghoon Won
 *
 */
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.io.MessageProtocolIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.ItemGroupDataOfArray;
import kr.pe.sinnori.util.AbstractClientExecutor;

public final class TestVirtualInputStreamCExtor extends AbstractClientExecutor {
	private ArrayList<InputMessage> orgInputMessageList = new ArrayList<InputMessage>();  
	private ByteBuffer baseBuffer = ByteBuffer.allocate(1*1024*1024);
	private int mailID = 1;

	@Override
	protected void doTask(ClientProjectConfigIF clientProjectConfig, MessageMangerIF messageManger,
			ClientProjectIF clientProject) throws SocketTimeoutException,
			ServerNotReadyException, DynamicClassCallException,
			NoMoreDataPacketBufferException, BodyFormatException,
			MessageInfoNotFoundException, InterruptedException, MessageItemException {

		
		DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = (DataPacketBufferQueueManagerIF)clientProject;
		
		
		MessageProtocolIF messageProtocol = clientProject.getMessageProtocol();
		
		
		
		SocketInputStream socketInputStream = 
				new SocketInputStream(dataPacketBufferQueueManager);
		
		java.util.Random random = new java.util.Random();
		
		baseBuffer.order(clientProjectConfig.getByteOrder());
				
		addAllDataTypeInObj(10, clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("1.baseBuffer=[%s]", baseBuffer.toString()));
		
		
		// log.info(String.format("1.baseBuffer=[%s]", HexUtil.byteBufferToHex(baseBuffer, 0, 523)));
		
		addEchoInObj(clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("2.baseBuffer=[%s]", baseBuffer.toString()));
		
		addEchoInObj(clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("3.baseBuffer=[%s]", baseBuffer.toString()));
		
		addAllDataTypeInObj(10, clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("4.baseBuffer=[%s]", baseBuffer.toString()));
		
		addAllDataTypeInObj(10, clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		
		log.info(String.format("5.baseBuffer=[%s]", baseBuffer.toString()));
		
		addAllDataTypeInObj(10, clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		
		addAllDataTypeInObj(30000, clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		addEchoInObj(clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		addEchoInObj(clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		addAllDataTypeInObj(30000,clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		addEchoInObj(clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		addAllDataTypeInObj(30000,clientProjectConfig, messageManger, random, messageProtocol, dataPacketBufferQueueManager);
		
		baseBuffer.flip();
		
		/*// FIXME!, THB 64 bytes 에서만 유효, DHB Header 96 bytes, 조작할 메시지 시작 옵셋 739, 조작할 위치 79, 
		int t1 = baseBuffer.getInt((643+64+66+13));
		log.info(String.format("t1=[%d]", t1));
		
		byte b1 = baseBuffer.get((643+64+66+13+4));
		log.info(String.format("b1=[%x]", b1));
		
		
		int t2 = baseBuffer.getInt((1134+64+66+13));
		log.info(String.format("t2=[%d]", t2));
		
		// FIXME!
		log.info(String.format("baseBuffer=[%s]", baseBuffer.toString()));*/
		
		try {
			while (baseBuffer.hasRemaining()) {
				int len = random.nextInt(3000) + 30;
				len = Math.min(len, baseBuffer.remaining());
				ByteBuffer sourceBuffer = ByteBuffer.allocate(len);
				baseBuffer.get(sourceBuffer.array());
								
				ByteBuffer scOwnLastBuffer = socketInputStream.getLastDataPacketBuffer();
				
				while(sourceBuffer.hasRemaining()) {
					if (!scOwnLastBuffer.hasRemaining()) {
						scOwnLastBuffer = socketInputStream.nextDataPacketBuffer();
					}
						
					scOwnLastBuffer.put(sourceBuffer.get());
				}
				
				// log.info(String.format("1.len=[%d], baseBuffer.position=[%d], socketInputStream.position=[%d]", len, baseBuffer.position(), socketInputStream.position()));
				
				
				// log.info(String.format("2.len=[%d], baseBuffer.position=[%d], socketInputStream.position=[%d]", len, baseBuffer.position(), socketInputStream.position()));

				ArrayList<AbstractMessage> readInputMessageList = null;
				
				readInputMessageList = messageProtocol.S2MList(InputMessage.class, clientProjectConfig.getCharset(), socketInputStream, messageManger);
				
				int readInputMessageListSize = readInputMessageList.size();
				for (int i=0; i< readInputMessageListSize; i++) {
					InputMessage inObj = (InputMessage)readInputMessageList.get(i);
					log.info(String.format("inObj[%d] %s", i, inObj.getMessageID()));
					log.info(inObj.toString());
				}
			}
		} catch (HeaderFormatException e) {
			e.printStackTrace();
		}
	}
	
	private void addEchoInObj(ClientProjectConfigIF clientProjectConfig, 
			MessageMangerIF messageManger,			
			java.util.Random random, 
			MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws MessageInfoNotFoundException, BodyFormatException, NoMoreDataPacketBufferException, MessageItemException {
		
		
		
		InputMessage echoInObj = messageManger.createInputMessage("Echo");

		echoInObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		echoInObj.messageHeaderInfo.mailID = mailID++;

		echoInObj.setAttribute("randomInt", random.nextInt());
		echoInObj.setAttribute("startTime", new java.util.Date().getTime());
		
		orgInputMessageList.add(echoInObj);
		
		
		
		ArrayList<WrapBuffer> warpBufferList = messageProtocol.M2S(echoInObj, clientProjectConfig.getCharset());
		
		int warpBufferListSize = warpBufferList.size();
		for (int i=0; i < warpBufferListSize; i++) {
			WrapBuffer wrapBuffer = warpBufferList.get(i);
			ByteBuffer workByteBuffer = wrapBuffer.getByteBuffer();
			baseBuffer.put(workByteBuffer);
			// log.debug(oneBuffer.toString());
			dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
		}
	}
	
	private void addAllDataTypeInObj(int lenOfBytesVar2, ClientProjectConfigIF clientProjectConfig, MessageMangerIF messageManger,
			java.util.Random random, 
			MessageProtocolIF messageProtocol, DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws MessageInfoNotFoundException, BodyFormatException, NoMoreDataPacketBufferException, MessageItemException {
		
		
		InputMessage allDataTypeInObj = messageManger.createInputMessage("AllDataType");
		allDataTypeInObj.messageHeaderInfo.mailboxID = CommonStaticFinal.SERVER_MAILBOX_ID;
		allDataTypeInObj.messageHeaderInfo.mailID = mailID++;
		
		allDataTypeInObj.setAttribute("byteVar1", Byte.MAX_VALUE);
		allDataTypeInObj.setAttribute("byteVar2", Byte.MIN_VALUE);
		allDataTypeInObj.setAttribute("byteVar3", (byte) 0x60);

		allDataTypeInObj.setAttribute("unsignedByteVar1", (short) 0);
		allDataTypeInObj.setAttribute("unsignedByteVar2", (short) 0xff);
		allDataTypeInObj.setAttribute("unsignedByteVar3", (short) 0x65);

		allDataTypeInObj.setAttribute("shortVar1", Short.MAX_VALUE);
		allDataTypeInObj.setAttribute("shortVar2", Short.MIN_VALUE);
		allDataTypeInObj.setAttribute("shortVar3", (short) 30);

		allDataTypeInObj.setAttribute("unsignedShortVar1", (int) 0);
		allDataTypeInObj.setAttribute("unsignedShortVar2", (int) 0xffff);
		allDataTypeInObj.setAttribute("unsignedShortVar3",
				(int) (Short.MAX_VALUE + 1000));

		allDataTypeInObj.setAttribute("intVar1", Integer.MAX_VALUE);
		allDataTypeInObj.setAttribute("intVar2", Integer.MIN_VALUE);
		allDataTypeInObj.setAttribute("intVar3", random.nextInt());

		allDataTypeInObj.setAttribute("unsignedIntVar1", (long) 0);
		allDataTypeInObj.setAttribute("unsignedIntVar2", (long) 0x7fffffff);
		allDataTypeInObj.setAttribute("unsignedIntVar3", Integer.MAX_VALUE
				+ (long) 1000);

		allDataTypeInObj.setAttribute("longVar1", Long.MAX_VALUE);
		allDataTypeInObj.setAttribute("longVar2", Long.MIN_VALUE);
		allDataTypeInObj.setAttribute("longVar3", random.nextLong());

		// FIXME!
		allDataTypeInObj.setAttribute("strVar1", "testHH");
		allDataTypeInObj.setAttribute("strVar2", "1234");
		allDataTypeInObj.setAttribute("strVar3", "uiop");

		byte[] bytesVar1 = new byte[]{-30, 76};
		allDataTypeInObj.setAttribute("bytesVar1", bytesVar1);
		// allDataTypeInObj.setAttribute("bytesVar2", new byte[] { 1, 2, 3, 4, 5, 6, 7,
		// 8,
		// 9, 10, 11 });
		allDataTypeInObj.setAttribute("bytesVar2", new byte[lenOfBytesVar2]);

		int memberListCnt = 2;
		allDataTypeInObj.setAttribute("cnt", memberListCnt);

		ArrayData memberListForArrayDataList = (ArrayData) allDataTypeInObj
				.getAttribute("memberList");

		ItemGroupDataIF memberList[] = new ItemGroupDataIF[memberListCnt];

		ArrayData itemListForArrayDataList[] = new ArrayData[memberListCnt];
		ItemGroupDataIF itemList[][] = new ItemGroupDataIF[memberListCnt][];
		// int itemListCnt[]=new int[memberListCnt];

		memberList[0] = memberListForArrayDataList.get(0);
		memberList[0].setAttribute("memberID", "test01ID");
		memberList[0].setAttribute("memberName", "test01");
		memberList[0].setAttribute("cnt", 2);

		// System.out.println(memberList[0].toString());

		itemListForArrayDataList[0] = (ArrayData) memberList[0]
				.getAttribute("itemList");

		itemList[0] = new ItemGroupDataOfArray[2];

		itemList[0][0] = itemListForArrayDataList[0].get(0);
		itemList[0][0].setAttribute("itemID", "1");
		itemList[0][0].setAttribute("itemName", "최강의검");

		// System.out.println(memberList[0].toString());

		itemList[0][0].setAttribute("itemCnt", 1);

		// System.out.println(memberList[0].toString());

		itemList[0][1] = itemListForArrayDataList[0].get(1);
		itemList[0][1].setAttribute("itemID", "2");
		itemList[0][1].setAttribute("itemName", "살살검");
		itemList[0][1].setAttribute("itemCnt", 2);

		// System.out.println(memberList[0].toString());

		memberList[1] = memberListForArrayDataList.get(1);

		// System.out.println(memberList[1].toString());

		memberList[1].setAttribute("memberID", "test02ID");
		memberList[1].setAttribute("memberName", "test02");
		memberList[1].setAttribute("cnt", 1);

		// System.out.println(memberList[1].toString());

		itemListForArrayDataList[1] = (ArrayData) memberList[1]
				.getAttribute("itemList");

		// System.out.println(itemListForArrayDataList[1].toString());

		itemList[1] = new ItemGroupDataOfArray[1];
		itemList[1][0] = itemListForArrayDataList[1].get(0);
		itemList[1][0].setAttribute("itemID", "3");
		itemList[1][0].setAttribute("itemName", "안좋은검");
		itemList[1][0].setAttribute("itemCnt", 65000);
		
		
		
		orgInputMessageList.add(allDataTypeInObj);
		
		ArrayList<WrapBuffer> warpBufferList = messageProtocol.M2S(allDataTypeInObj, clientProjectConfig.getCharset());
		
		int warpBufferListSize = warpBufferList.size();
		for (int i=0; i < warpBufferListSize; i++) {
			WrapBuffer wrapBuffer = warpBufferList.get(i);
			ByteBuffer workByteBuffer = wrapBuffer.getByteBuffer();
			baseBuffer.put(workByteBuffer);
			// log.debug(oneBuffer.toString());
			dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
		}
		
		// FIXME! THB 프로토콜에서만 유효
		if (5 == mailID) {
			baseBuffer.putInt((643+64+66+13), -1);
		} else if (6 == mailID) {
			baseBuffer.putInt((1134+64+66+13), 30000);
		}
	}
}