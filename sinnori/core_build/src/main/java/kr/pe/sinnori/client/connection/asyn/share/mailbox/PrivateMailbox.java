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
package kr.pe.sinnori.client.connection.asyn.share.mailbox;

import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.ClientOutputMessageQueueQueueMangerIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.share.ShareAsynConnection;
import kr.pe.sinnori.client.io.ClientOutputMessageQueueWrapper;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.protocol.ReceivedLetter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 클라이언트 메일함 클래스. 비동기 소켓 채널을 쓰레드간에 공유하며 메시지 송수신을 위한 클래스. 
 * - 메일함 이용 메시지 교환 과정 -
 * (1) 입력 메시지 전송 과정
 *     입력 메시지를 보내고자 하는 쓰레드 -> 입력 메시지 큐 -> 입력 메시지 소켓 쓰기 담당 쓰레드
 * (2) 출력 메시지 전송 과정 
 *     (2-1) 소켓 채널 1:1 대응 비동기 연결 클래스 찾기
 *     (2-2) 비동기 연결 클래스에서 출력 메시지의 메일 식별자와 1:1 대응 메일함 찾기
 *     출력 메시지 소켓 읽기 담당 쓰레드 ->  메일함 출력 메시지 큐 -> 출력 메시지를 받고자 하는 쓰레드
 *     
 * 가정1) 메시지 송수신을 시도하는 쓰레드끼리는 메일함을 공유하지 않는다.
 * 가정2) 출력 메시지의 메일함 식별자와 메일 식별자값은 입력 메시지의 메일함 식별자와 메일 식별자값이다. 즉 복사된 값이다.
 * 가정3) 메일함의 가지는 입력 메시지큐, 출력 메시지큐 모두 쓰레드 세이프 하다.
 * 참고) 가정1, 가정2, 가정3이 지켜진다면 비동기 소켓 채널을 비록 공유할지라도 
 *       메시지 송수신 쓰레드는 자신이 보낸 입력 메시지에 대한 출력 메시지를 얻게 된다.
 *       단 보안 공격에 취약하다.
 * </pre>
 * 
 * @see ShareAsynConnection
 * @author Won Jonghoon
 * 
 */
public class PrivateMailbox {
	private Logger log = LoggerFactory.getLogger(PrivateMailbox.class);
	
	private final Object monitor = new Object();
	
	/** 메일함 식별자 */
	private int mailboxID;
	/** 입력 메시지 큐 */
	private LinkedBlockingQueue<LetterToServer> inputMessageQueue = null;
	private ClientOutputMessageQueueQueueMangerIF syncOutputMessageQueueQueueManager = null;
	/** 출력 메시지 큐 */
	private ClientOutputMessageQueueWrapper wrapOutputMessageQueue = null;
	private LinkedBlockingQueue<ReceivedLetter> syncOutputMessageQueue = null;
	/** 메일함이 속한 비동기 연결 방식의 소켓 채널을 쓰레드간에 공유할려는 연결 클래스 */
	private AbstractAsynConnection serverConnection = null;
	/** 메일함 사용 여부 */
	private boolean isActive = false;

	/**
	 * 메일 식별자. 입력 메시지에 대한 출력 메시지가 맞는지 검사하기 위한 장치로 출력 메시지를 다 받았다면 증가한다. 주의점)
	 * 서버에서는 입력 메시지에 대한 출력 메시지 발생시 반듯이 입력메시지의 메일 식별자를 출력 메시지에 넣어야 한다. Integer
	 * 최소값에서 시작해서 최대값까지 무한 순환한다.
	 */
	private int mailID = Integer.MIN_VALUE;

	/**
	 * 출력메시지를 출력메시지 큐에 넣거나 가져올때 무한적으로 기다릴 수는 없어 제한한 시간.
	 */
	private long socketTimeOut;

	/**
	 * 개인 메일함 생성자
	 * 
	 * @param serverConnection
	 *            개인 메일함이 속한 비동기 연결 방식의 소켓 채널을 쓰레드간에 공유할려는 연결 클래스
	 * @param mailboxID
	 *            개인 메일함 식별자
	 * @param inputMessageQueue
	 *            입력 메시지 큐
	 * @param outputMessageQueueQueueManager 출력 메시지 큐를 원소로 가지는 큐 관리자
	 * @throws NoMoreOutputMessageQueueException 출력 메시지 큐 부족시 던지는 예외
	 */
	public PrivateMailbox(AbstractAsynConnection serverConnection,
			int mailboxID,
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			ClientOutputMessageQueueQueueMangerIF syncOutputMessageQueueQueueManger
			) throws NoMoreOutputMessageQueueException {
		// this.mailboxMonitor = mailboxMonitor;
		this.serverConnection = serverConnection;
		this.mailboxID = mailboxID;
		this.inputMessageQueue = inputMessageQueue;
		this.syncOutputMessageQueueQueueManager = syncOutputMessageQueueQueueManger;
		this.wrapOutputMessageQueue = syncOutputMessageQueueQueueManger.pollOutputMessageQueue();
		this.syncOutputMessageQueue = wrapOutputMessageQueue.getOutputMessageQueue();
		socketTimeOut = serverConnection.getSocketTimeOut();
	}

	/**
	 * 메일함 식별자를 반환한다.
	 * 
	 * @return 메일함 식별자
	 */
	public int getMailboxID() {
		return mailboxID;
	}
	
	public int getMailID() {
		return mailID;
	}

	/**
	 * 입력 메시지를 메일함의 식별자와 메일 식별자를 갖도록 한후 메일함이 가진 입력 메시지 큐에 넣는다.
	 * 
	 * @param letterToServer
	 *            입력 메시지와 연결 클래스를 담은 편지
	 * @throws InterruptedException 인터럽트가 발생하여 던지는 예외
	 */
	public void putSyncInputMessage(LetterToServer letterToServer)
			throws InterruptedException, SocketTimeoutException {
		/*AbstractMessage inObj = letterToServer.getInputMessage();
		inObj.messageHeaderInfo.mailboxID = this.mailboxID;
		inObj.messageHeaderInfo.mailID = this.mailID;*/
		
		
		// letterToServer.setMailBox(mailboxID, mailID);
		// inputMessageQueue.put(letterToServer);
		
		boolean result = inputMessageQueue.offer(letterToServer, socketTimeOut, TimeUnit.MILLISECONDS);
		if (!result) {
			String errorMsg = String
					.format("메일 박스[%d]에서 입력 메시지 큐 응답 시간[%d]이 초과되었습니다. serverConnection=[%s], letterToServer=[%s]",
							mailboxID, socketTimeOut, serverConnection.getSimpleConnectionInfo(),
							letterToServer.toString());
			log.warn(errorMsg);
			throw new SocketTimeoutException(errorMsg);
		}
	}
	
	public void putAsynInputMessage(LetterToServer letterToServer) throws InterruptedException, SocketTimeoutException {
		// letterToServer.setMailBox(CommonStaticFinalVars.ASYN_MAILBOX_ID, mailID);
		boolean result = inputMessageQueue.offer(letterToServer, socketTimeOut, TimeUnit.MILLISECONDS);
		if (!result) {
			String errorMsg = String
					.format("메일 박스[%d]에서 입력 메시지 큐 응답 시간[%d]이 초과되었습니다. serverConnection=[%s], letterToServer=[%s]",
							mailboxID, socketTimeOut, serverConnection.getSimpleConnectionInfo(),
							letterToServer.toString());
			log.warn(errorMsg);
			throw new SocketTimeoutException(errorMsg);
		}
	}
	

	/**
	 * <pre>
	 * 
	 *  출력 메시지 큐에 설정 파일에서 지정된 소켓 타임아웃 시간만큼 대기하여 출력 메시지를 넣는다.
	 *  만약 소켓 타임 아웃 시간에도 큐에 못 넣을 경우 처리를 종료하여 결과적으로 출력 메시지는 폐기된다.
	 *  참고) 비동기 입출력용 출력 메시지 소켓 읽기 쓰레드에서 이 메소드를 호출한다. 
	 *       정상적으로 큐에 출력 메시지를 넣게 되면 
	 *       입력 메시지를 보내고 출력 메시지 큐로 부터 출력 메시지를 기다리는 
	 *       쓰레드가 깨어 나서 이후 처리를 하게 된다.
	 * </pre>
	 * @see AbstractAsynConnection#putToOutputMessageQueue(OutputMessage)
	 * @param outObj
	 *            출력 메시지
	 */
	public void putToSyncOutputMessageQueue(ReceivedLetter receivedLetter) {
		if (!isActive) {
			String errorMessage = String
					.format("메일함이 사용중이 아닙니다. 출력 메시지를 버립니다. %s, receivedLetter=[%s]",
							serverConnection.getSimpleConnectionInfo(),
							receivedLetter.toString());

			log.warn(errorMessage);
			return;
		}
		
		int fromMailID = receivedLetter.getMailID();

		if (mailID != fromMailID) {
			String errorMessage = String
					.format("메일식별자 불일치 에러. 출력 메시지를 버립니다. %s, mailbox'mailID=[%d], receivedLetter=[%s]",
							serverConnection.getSimpleConnectionInfo(), this.mailID,
							receivedLetter.toString());
			log.warn(errorMessage);
			return;
		}

		boolean result = false;
		
		result = syncOutputMessageQueue.offer(receivedLetter);
		
		
		if (!result) {
			StringBuilder errorMessageStringBuilder = new StringBuilder("출력 메시지 큐가 꽉 차 있어 출력 메시지[");
			errorMessageStringBuilder.append(receivedLetter.toString());
			errorMessageStringBuilder.append("] 를 버립니다. ");
			errorMessageStringBuilder.append(serverConnection.getSimpleConnectionInfo());
			
			log.warn(errorMessageStringBuilder.toString());
			return;
		}
	}

	/**
	 * 지정된 소켓 타임 아웃 시간 동안 메일함의 출력 메시지 큐에서 얻은 출력 메시지를 반환한다.
	 * 
	 * @return 메일함이 가지는 출력 메시지 큐에서 얻은 출력 메시지
	 * @throws SocketTimeoutException
	 *             환경 변수 소켓 타임 아웃(=client.socket_timeout.value) 시간안에 출력 메시지를 얻지
	 *             못했을때 발생
	 * @throws InterruptedException 인터럽트 발생시 던지는 예외
	 */
	public ReceivedLetter getSyncOutputMessage() throws SocketTimeoutException, InterruptedException {
		long firstElapsedTime = new java.util.Date().getTime();
		long lastElapsedTime = firstElapsedTime;
		long elapsedTimeDifference = 0L;
		ReceivedLetter receivedLetter = null;		
		do {
			receivedLetter = syncOutputMessageQueue.poll(socketTimeOut - elapsedTimeDifference,
					TimeUnit.MILLISECONDS);
			if (null == receivedLetter) {
				String errorMessage = String
						.format("1.서버 응답 시간[%d]이 초과되었습니다. %s, mailboxID=[%d], mailID=[%d]",
								socketTimeOut, serverConnection.getSimpleConnectionInfo(),
								mailboxID, mailID);
				log.warn(errorMessage);
				throw new SocketTimeoutException(errorMessage);
			}
			
			if (receivedLetter.getMailID() == mailID) {
				return receivedLetter;
			}
			
			lastElapsedTime = new java.util.Date().getTime();
			elapsedTimeDifference = lastElapsedTime - firstElapsedTime; 
			if (elapsedTimeDifference >= socketTimeOut) {
				String errorMessage = String
						.format("2.서버 응답 시간[%d]이 초과되었습니다. %s, mailboxID=[%d], mailID=[%d]",
								socketTimeOut, serverConnection.getSimpleConnectionInfo(),
								mailboxID, mailID);
				log.warn(errorMessage);
				throw new SocketTimeoutException(errorMessage);
			}
			
			log.warn(String.format(
					"%s 연결 객체의 메일 박스[%d]를 통해 보낸 입력 메시지의 메일 식별자[%d]와 전달 받은 출력 메시지[%s]의 메일 식별자가 다릅니다.",
					serverConnection.getSimpleConnectionInfo(), mailboxID,
					mailID,
					receivedLetter.toString()));
			
						
		} while (true);		
	}

	/**
	 * 개인 메일함 큐에서 나와 있는 상태. 메일함 사용중임을 표시함.
	 */
	public void setActive() {
		isActive = true;
	}

	/**
	 * 개인 메일함 큐에 들어간 상태. 메일함 비 사용중임을 표시함.
	 */
	public void setDisable() {
		isActive = false;

		if (Integer.MAX_VALUE == mailID)
			mailID = Integer.MIN_VALUE;
		else
			mailID++;

		int inx = 0;
		while (!syncOutputMessageQueue.isEmpty()) {
			log.info(String.format("mailbox[%d]'s outputMessageQueue not empty, letter[%d]=[%s]",
					mailboxID, inx++, syncOutputMessageQueue.poll().toString()));
		}
	}

	/**
	 * 메일함이 메일함 큐에 있는지 여부를 반환, 즉 사용중 여부를 반환
	 * 
	 * @return 메일함이 메일함 큐에 있는지 여부, 즉 사용중 여부
	 */
	public boolean isActive() {
		return isActive;
	}
	
	public int hashCode() {
		return monitor.hashCode();
	}

	@Override
	protected void finalize() throws Throwable {
		String errorMsg = String
				.format("회수못한 mailbox. %s, mailboxID=[%d], mailID=[%d]",
						serverConnection.getSimpleConnectionInfo(), mailboxID, mailID);

		log.warn(errorMsg);
		
		syncOutputMessageQueueQueueManager.putOutputMessageQueue(wrapOutputMessageQueue);
		super.finalize();
	}
}
