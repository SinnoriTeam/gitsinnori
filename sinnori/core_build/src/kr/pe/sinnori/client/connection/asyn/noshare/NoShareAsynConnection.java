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
package kr.pe.sinnori.client.connection.asyn.noshare;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.SyncOutputMessageQueueQueueMangerIF;
import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.share.mailbox.PrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;

/**
 * 클라이언트 비공유 방식의 비동기 연결 클래스.<br/>
 * 참고) 비공유 방식은 큐로 관리 되기에 비공유 방식의 동기 연결 클래스는 큐 인터페이스를 구현하고 있다<br/>
 * 참고)  소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareAsynConnection extends AbstractAsynConnection {
	/** 개인 메일함 번호. 참고) 메일함을 가상으로 운영하여 전체적으로 메일함 운영의 틀을 유지한다. */
	// private static final int mailboxID = 1;
		
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	private PrivateMailbox mailbox = null;

	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param socketTimeOut 소켓 타임 아웃
	 * @param whetherToAutoConnect 자동 연결 여부
	 * @param finishConnectMaxCall 연결 확립 최대 시도 횟수
	 * @param finishConnectWaittingTime 연결 확립 재 시도 간격
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param asynOutputMessageQueue 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 큐
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param outputMessageQueueQueueManger 출력 메시지 큐를 원소로 가지는 큐 관리자
	 * @param outputMessageReaderPool 서버에 접속한 소켓 채널을 균등하게 소켓 읽기 담당 쓰레드에 등록하기 위한 인터페이스
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws InterruptedException 쓰레드 인터럽트
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 * @throws NoMoreOutputMessageQueueException  출력 메시지 큐 부족시 실패시 던지는 예외
	 */
	public NoShareAsynConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			int finishConnectMaxCall,
			long finishConnectWaittingTime,
			ClientProjectConfig clientProjectConfig,
			LinkedBlockingQueue<ReceivedLetter> asynOutputMessageQueue,
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			MessageProtocolIF messageProtocol,
			OutputMessageReaderPoolIF outputMessageReaderPool,
			SyncOutputMessageQueueQueueMangerIF outputMessageQueueQueueManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws InterruptedException, 
			NoMoreDataPacketBufferException, NoMoreOutputMessageQueueException {		
		super(index, socketTimeOut, whetherToAutoConnect, 
				finishConnectMaxCall, finishConnectWaittingTime, 
				clientProjectConfig, asynOutputMessageQueue, inputMessageQueue, 
				messageProtocol, outputMessageReaderPool, 
				dataPacketBufferQueueManager, clientObjectCacheManager);

		// this.messageManger = messageManger;
		// this.outputMessageQueue = outputMessageQueue;
		mailbox = new PrivateMailbox(this, 1, inputMessageQueue, outputMessageQueueQueueManger);
		
		
		try {
			reopenSocketChannel();
		} catch (IOException e) {
			String errorMessage = String.format("project[%s] NoShareAsynConnection[%d], fail to config a socket channel", clientProjectConfig.getProjectName(), index);
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		/**
		 * 연결 종류별로 설정이 모두 다르다 따라서 설정 변수 "소켓 자동접속 여부"에 따른 서버 연결은 연결별 설정후 수행해야 한다.
		 */
		if (whetherToAutoConnect) {
			try {
				serverOpen();
			} catch (ServerNotReadyException e) {
				log.warn(String.format("project[%s] NoShareAsynConnection[%d] fail to connect server", clientProjectConfig.getProjectName(), index), e);
				// System.exit(1);
			}
		}
		
		
		log.info(String.format("project[%s] NoShareAsynConnection[%d] 생성자 end", clientProjectConfig.getProjectName(), index));
	}
	
	/**
	 * 연결 클래스가 큐로 관리될때 큐 속에 있는지 여부 반환 메소드
	 * 
	 * @return 연결 클래스가 큐로 관리될때 큐 속에 있는지 여부
	 */
	public boolean isInQueue() {
		// return lastCaller.equals("");
		return isQueueIn;
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	public void queueIn() {
		isQueueIn = true;
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	public void queueOut() {
		isQueueIn = false;
	}
	
	/**
	 * 비공유 + 비동기 연결 전용 소켓 채널 열기
	 * @throws IOException 소켓 채널을 개방할때 혹은 비공유 + 비동기 에 맞도록 설정할때 에러 발생시 던지는 예외 
	 */
	private void reopenSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSC.configureBlocking(false);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		// serverSC.setOption(StandardSocketOptions.SO_SNDBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF, clientProjectConfig.getDataPacketBufferSize()*2);
		//SO_SNDBUF
		
		StringBuilder infoBuilder = null;
		
		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(clientProjectConfig.getProjectName());
		infoBuilder.append("] asyn+noshare connection[");
		infoBuilder.append(index);
		infoBuilder.append("]");
		log.info(infoBuilder.append(" (re)open new serverSC=").append(serverSC.hashCode()).toString());
	}
	
	@Override
	public void serverOpen() throws ServerNotReadyException {
		/**
		 * <pre>
		 * 서버와 연결하는 시간보다 연결 이후 시간이 훨씬 길기때문에,
		 * 연결시 비용이 더 들어가도 연결후 비용을 더 줄일 수 만 있다면 좋을 것이다.
		 * 아래와 같이 재연결을 판단하는 if 문을 2번 사용하여 이를 달성한다.
		 * 그러면 소켓 채널이 서버와 연결된 후에는 동기화 비용 없어지고
		 * 연결 할때만 if 문 중복 비용만 더 추가될 것이다.
		 * </pre>
		 */
		
		try {
			synchronized (monitor) {
				// (재)연결 판단 로직, 2번이상 SocketChannel.open() 호출하는것을 막는 역활을 한다.
				if (serverSC.isConnected()) {
					/*StringBuilder infoBuilder = null;
					
					infoBuilder = new StringBuilder("projectName[");
					infoBuilder.append(clientProjectConfig.getProjectName());
					infoBuilder.append("] asyn connection[");
					infoBuilder.append(index);
					infoBuilder.append("] serverSC[");
					infoBuilder.append(serverSC.hashCode());
					infoBuilder.append("]");
					log.info(new StringBuilder(infoBuilder.toString()).append(" connected").toString());*/
					return;
				} 
				
				
				//log.info(new StringBuilder(info).append(" before connect").toString());
				
				finalReadTime = new java.util.Date();
				InetSocketAddress remoteAddr = new InetSocketAddress(
						clientProjectConfig.getServerHost(),
						clientProjectConfig.getServerPort());
				
				if (!serverSC.isOpen()) {
					reopenSocketChannel();
				}
				serverSC.connect(remoteAddr);
				finishConnect();
				initSocketResource();
				afterConnectionWork();
				
				
				StringBuilder infoBuilder = null;
				
				infoBuilder = new StringBuilder("projectName[");
				infoBuilder.append(clientProjectConfig.getProjectName());
				infoBuilder.append("] asyn connection[");
				infoBuilder.append(index);
				infoBuilder.append("] serverSC[");
				infoBuilder.append(serverSC.hashCode());
				infoBuilder.append("]");
				log.info(new StringBuilder(infoBuilder.toString()).append(" connect").toString());
				//log.info(new StringBuilder(info).append(" after connect").toString());
			}

		} catch (ConnectException e) {
			String errorMessage = String.format(
					"ServerNotReadyException::%s conn[%d] index[%02d], host[%s], port[%d]", 
					clientProjectConfig.getProjectName(),
					serverSC.hashCode(),
					index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort());
			
			log.warn(errorMessage, e);
			
			throw new ServerNotReadyException(errorMessage);
		} catch (UnknownHostException e) {
			String errorMessage = String.format(
					"ServerNotReadyException::%s conn[%d] index[%02d], host[%s], port[%d]", 
					clientProjectConfig.getProjectName(),
					serverSC.hashCode(),
					index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort());
			
			log.warn(errorMessage, e);
			
			throw new ServerNotReadyException(errorMessage);
		} catch (ClosedChannelException e) {
			String errorMessage = String.format(
					"ServerNotReadyException::%s conn[%d] index[%02d], host[%s], port[%d]", 
					clientProjectConfig.getProjectName(),
					serverSC.hashCode(),
					index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort());
			
			log.warn(errorMessage, e);
			
			throw new ServerNotReadyException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String.format(
					"ServerNotReadyException::%s conn[%d] index[%02d], host[%s], port[%d]", 
					clientProjectConfig.getProjectName(),
					serverSC.hashCode(),
					index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort());
			
			log.warn(errorMessage, e);
			
			serverClose();
			
			throw new ServerNotReadyException(errorMessage);
		} catch (ServerNotReadyException e) {
			String errorMessage = String.format(
					"ServerNotReadyException::%s conn[%d] index[%02d], host[%s], port[%d]", 
					clientProjectConfig.getProjectName(),
					serverSC.hashCode(),
					index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort());
			
			log.warn(errorMessage, e);
			
			serverClose();
			
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format(
					"unknown exception::%s conn[%d] index[%02d], host[%s], port[%d]", 
					clientProjectConfig.getProjectName(),
					serverSC.hashCode(),
					index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort());
			log.warn(errorMessage, e);
			
			serverClose();
			
			throw new ServerNotReadyException(errorMessage);
		}

		// log.info("projectName[%s%02d] call serverOpen end", projectName,
		// index);
	}
	

	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, NotLoginException {

		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		// log.info("inputMessage=[%s]", inputMessage.toString());
		
		serverOpen();
				
		boolean isInterrupted = false;
		
		ClassLoader classLoader = inObj.getClass().getClassLoader();
		
		LetterToServer letterToServer = null;
		
		
		ReceivedLetter receivedLetter = null;
		
		/**
		 * <pre>
		 * 공유+비동기 연결 객체를 직접 받을 수 없지만 동시 사용이 가능 하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * 비공유+비동기 연결 객체는 원칙적으로 공유할 수 없지만 직접 받을 수 있기때문에 동시 사용 가능이 가능하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * </pre>
		 */
		synchronized (mailbox) {
			try {
				mailbox.setActive();	
				
				inObj.messageHeaderInfo.mailboxID = mailbox.getMailboxID();
				inObj.messageHeaderInfo.mailID = mailbox.getMailID();
				
				// FIXME!
				//log.info("inObj={}", inObj.toString());
				
				letterToServer = getLetterToServer(classLoader, inObj);
				
				try {
					mailbox.putSyncInputMessage(letterToServer);
				} catch (InterruptedException e) {
					isInterrupted = true;
					try {
						mailbox.putSyncInputMessage(letterToServer);
					} catch (InterruptedException e1) {
						log.error("인터럽트 받아 후속 처리중 발생", e);
						System.exit(1);
					}
				}

				// AbstractMessage workOutObj = null;
				
				try {				
					receivedLetter = mailbox.takeSyncOutputMessage();
				} catch(InterruptedException e) {
					/** 인터럽트 발생시 메소드 끝가지 로직 수행후 인터럽트 상태를 복귀 시켜 최종 인터럽트 처리를 마무리 하도록 유도 */					
					if (isInterrupted) {
						log.error("인터럽트 받아 후속 처리중 발생", e);
						System.exit(1);
					} else {
						try {
							receivedLetter = mailbox.takeSyncOutputMessage();
						} catch(InterruptedException e1) {
							log.error("인터럽트 받아 후속 처리중 발생", e1);
							System.exit(1);
						}
						isInterrupted = true;
					}
				}
			} finally {
				mailbox.setDisable();

				if (isInterrupted)
					Thread.currentThread().interrupt();
			}
		}
		
		/*endTime = new java.util.Date().getTime();
		log.info(String.format("1.시간차=[%d]", (endTime - startTime)));*/
		
		/*String messageID = letterFromServer.getMessageID();
		int mailboxID = letterFromServer.getMailboxID();
		int mailID = letterFromServer.getMailID();
		Object middleReadObj = letterFromServer.getMiddleReadObj();*/
		
		AbstractMessage outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);
		
		endTime = new java.util.Date().getTime();
		log.info(String.format("2.시간차=[%d]", (endTime - startTime)));
		
		/*if (outObj instanceof SelfExn) {
			SelfExn selfExnOutObj = (SelfExn)outObj;
			log.warn(selfExnOutObj.getReport());
			selfExnOutObj.toException();
		}*/

		return outObj;
	}
	
	
	@Override
	public void sendAsynInputMessage(AbstractMessage inObj) 
			throws ServerNotReadyException, SocketTimeoutException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, NotSupportedException {
		//long startTime = new java.util.Date().getTime();
		
		// log.info("inputMessage=[%s]", inputMessage.toString());
		
		serverOpen();
		
		
		boolean isInterrupted = false;
		
		ClassLoader classLoader = inObj.getClass().getClassLoader();
		LetterToServer letterToServer = null;
		
		/**
		 * <pre>
		 * 공유+비동기 연결 객체를 직접 받을 수 없지만 동시 사용이 가능 하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * 비공유+비동기 연결 객체는 원칙적으로 공유할 수 없지만 직접 받을 수 있기때문에 동시 사용 가능이 가능하므로 synchronized (mailbox) 를 걸어주어야 한다.
		 * </pre>
		 */
		synchronized (mailbox) {
			try {
				mailbox.setActive();
				
				inObj.messageHeaderInfo.mailboxID = mailbox.getMailboxID();
				inObj.messageHeaderInfo.mailID = mailbox.getMailID();
				letterToServer = getLetterToServer(classLoader, inObj);
				
				try {
					mailbox.putAsynInputMessage(letterToServer);
				} catch (InterruptedException e) {
					isInterrupted = true;
					try {
						mailbox.putAsynInputMessage(letterToServer);
					} catch (InterruptedException e1) {
						log.error("인터럽트 받아 후속 처리중 발생", e);
						System.exit(1);
					}
				}
				
			} finally {
				mailbox.setDisable();
	
				if (isInterrupted)
					Thread.currentThread().interrupt();
			}
		}

		/*long endTime = new java.util.Date().getTime();
		log.info(String.format("sendOnlyInputMessage 시간차=[%d]", (endTime - startTime)));*/
	}

	@Override
	protected void afterConnectionWork() {
		outputMessageReaderPool.addNewServer(this);
	}
	

	@Override
	public void putToOutputMessageQueue(ReceivedLetter receivedLetter) {
		if (receivedLetter.getMailboxID() == CommonStaticFinalVars.ASYN_MAILBOX_ID) {
			/** 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 */
			boolean result = false;
			try {
				result = asynOutputMessageQueue.offer(receivedLetter, socketTimeOut, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				/**
				 * 인터럽트 발생시 메소드 끝가지 로직 수행후 인터럽트 상태를 복귀 시켜 최종 인터럽트 처리를 마무리 하도록 유도
				 */				
				try {
					result = asynOutputMessageQueue.offer(receivedLetter, socketTimeOut, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e1) {
					log.error("인터럽트 받아 후속 처리중 발생", e1);
					System.exit(1);
				}
				Thread.currentThread().interrupt();
			}
			
			if (!result) {
				String errorMsg = String
						.format("서버 공용 출력 메시지 큐 응답 시간[%d]이 초과되었습니다. serverConnection=[%s], receivedLetter=[%s]",
								socketTimeOut, this.getSimpleConnectionInfo(),
								receivedLetter.toString());
				log.warn(errorMsg);
			}
		} else {
			if (isInQueue()) {
				String errorMessage = String
						.format("연결 클래스가 사용중이 아닙니다. 연결 클래스 큐 대기중, serverConnection=[%s], receivedLetter=[%s]",
								this.getSimpleConnectionInfo(), receivedLetter.toString());

				log.warn(errorMessage);
				return;
			}

			mailbox.putToSyncOutputMessageQueue(receivedLetter);	
		}
	}
	
	
	@Override
	public void finalize() {
		// MessageInputStreamResource messageInputStreamResource = getMessageInputStreamResource();
		messageInputStreamResource.destory();
		log.warn(String.format("NoShareAsynConnection 소멸::[%s]", toString()));
	}
}