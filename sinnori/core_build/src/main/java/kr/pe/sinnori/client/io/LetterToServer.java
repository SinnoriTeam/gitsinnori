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

package kr.pe.sinnori.client.io;

import java.util.List;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.common.io.WrapBuffer;

/**
 * 서버로 보내는 입력 메시지와 연결 클래스를 담은 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class LetterToServer {
	private AbstractAsynConnection serverConnection;
	private String messageID = null;
	private int mailboxID;
	private int mailID;
	private List<WrapBuffer> wrapBufferList = null;

	/**
	 * 생성자
	 * 
	 * @param serverConnection
	 *            비동기 방식의 소켓 채널을 갖는 연결 클래스
	 * @param intputMessage
	 *            입력 메시지
	 */
	public LetterToServer(AbstractAsynConnection serverConnection, String messageID, int mailboxID, int mailID, List<WrapBuffer> wrapBufferList) {
		this.serverConnection = serverConnection;
		this.messageID = messageID;
		this.mailboxID = mailboxID;
		this.mailID = mailID;
		this.wrapBufferList = wrapBufferList;
	}

	/**
	 * 비동기 방식의 소켓 채널을 갖는 연결 클래스를 반환한다.
	 * 
	 * @return 비동기 방식의 소켓 채널을 갖는 연결 클래스
	 */
	
	public AbstractAsynConnection getServerConnection() {
		return serverConnection;
	}
	
	public String getMessageID() {
		return messageID;
	}

	public int getMailboxID() {
		return mailboxID;
	}
	
	public int getMailID() {
		return mailID;
	}
	
	public void setMailBox(int mailboxID, int mailID) {
		this.mailboxID = mailboxID;
		this.mailID = mailID;
	}
	
	public List<WrapBuffer> getWrapBufferList() {
		return wrapBufferList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LetterToServer [serverConnection=");
		builder.append(serverConnection.getSimpleConnectionInfo());
		builder.append(", messageID=");
		builder.append(messageID);
		builder.append(", mailboxID=");
		builder.append(mailboxID);
		builder.append(", mailID=");
		builder.append(mailID);
		builder.append(", wrapBufferList size=");
		builder.append(wrapBufferList.size());
		builder.append("]");
		return builder.toString();
	}
}
