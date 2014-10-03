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

package kr.pe.sinnori.server.executor;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.ServerObjectCacheManagerIF;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * <pre>
 * 로그인을 요구하지 않는 서버 비지니스 로직 부모 클래스. 
 * 메시지는 자신만의 서버 비지니스를 갖는다. 
 * 개발자는 이 클래스를 상속 받은 메시지별 비지니스 로직을 개발하며, 
 * 이렇게 개발된 비지니스 로직 모듈은 동적으로 로딩된다.
 * </pre> 
 * 
 * @author Jonghoon Won
 * 
 */
public abstract class AbstractServerTask implements CommonRootIF {
	protected final ClassLoader classLoader = this.getClass().getClassLoader();
	private java.util.Hashtable<String, MessageEncoder> encoderHash = new java.util.Hashtable<String, MessageEncoder>(); 
	private java.util.Hashtable<String, MessageDecoder> decoderHash = new java.util.Hashtable<String, MessageDecoder>(1);
	
	
	/**
	 * <pre>
	 * 파일 관련 2개의 메시지 UpFileInfo, DownFileInfo 들을 제외한 메시지에 호출되는 메소드로,
	 * 클라이언트에서 보낸 입력 메시지 내용에 따라 비지니스 로직을 수행 후 
	 * 결과로 생긴 출력 메시지들을 편지에 넣어 반환한다.
	 * 참고) 서버가 다루는 편지는 메시지와 소켓 채널 묶음이다. 
	 *       이렇게 소켓 채널과 메시지를 묶은 이유는 
	 *       소켓 채널을 통해서만 서버와의 데이터 교환을 할 수 있기때문이다. 
	 * </pre>
	 *       
	 * @param serverProjectConfig 프로젝트의 서버 환경 변수
	 * @param serverSession 서버 비지니스 로직 섹션
	 * @param clientResourceManager 클라이언트 자원 관리자
	 * @throws MessageItemException 메시지 항목 값을 얻을때 혹은 항목 값을 설정할때 항목 관련 에러 발생시 던지는 예외
	 */
	
	public void execute(int index, 
			ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			Charset projectCharset,
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue,
			MessageProtocolIF messageProtocol,
			SocketChannel fromSC,
			ClientResource clientResource,
			ReceivedLetter receivedLetter, 
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		// FIXME!
		// log.info("inputMessage=[%s]", inputMessage.toString());
		
		String messageIDFromClient = receivedLetter.getMessageID();
		
		// Charset projectCharset = serverProjectConfig.getCharset();
		
		
		
		MessageDecoder  messageDecoder  = decoderHash.get(messageIDFromClient);
		
		if (null == messageDecoder) {
			MessageCodecIF messageCodec = null;
			
			try {
				messageCodec = serverObjectCacheManager.getServerCodec(classLoader, messageIDFromClient);
			} catch (DynamicClassCallException e) {
				log.warn(e.getMessage());
				
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
				
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage(e.getMessage());
				
				ArrayList<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
					System.exit(1);
				}
				
				putToOutputMessageQueue(fromSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
				return;
			} catch(Exception e) {
				log.warn(e.getMessage(), e);
				
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
				// selfExnOutObj.setError("S", messageID, new DynamicClassCallException("알수 없는 에러 발생::"+e.getMessage()));
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage("메시지 서버 코덱을 얻을때 알수 없는 에러 발생::"+e.getMessage());
				
				ArrayList<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
					
					putToOutputMessageQueue(fromSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
				}
				return;
			}
			
			
			try {
				messageDecoder = messageCodec.getMessageDecoder();
			} catch(DynamicClassCallException e) {
				log.warn(e.getMessage());
				
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
				// selfExnOutObj.setError("S", messageID, new DynamicClassCallException(e.getMessage()));
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage(e.getMessage());
				
				ArrayList<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
					putToOutputMessageQueue(fromSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
				}
				return;
			} catch(Exception e) {
				log.warn(e.getMessage());
				
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
				selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDFromClient);
				selfExnOutObj.setErrorMessage("메시지 디코더를 얻을때 알수 없는 에러 발생::"+e.getMessage());
				
				ArrayList<WrapBuffer> wrapBufferList = null;
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
					putToOutputMessageQueue(fromSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
				}
				return;
			}
			
			decoderHash.put(messageIDFromClient, messageDecoder);
			
			log.info("classLoader[{}], serverTask[{}], create new messageDecoder", classLoader.hashCode(), messageIDFromClient);
		}
		
		AbstractMessage  messageFromClient = null;
		
		try {
			messageFromClient = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), projectCharset, receivedLetter.getMiddleReadObj());
			messageFromClient.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
			messageFromClient.messageHeaderInfo.mailID = receivedLetter.getMailID();
		} catch (BodyFormatException e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(BodyFormatException.class);
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(e.getMessage());
			
			ArrayList<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				
				putToOutputMessageQueue(fromSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
			}
			return;
		} catch (OutOfMemoryError e) {
			log.warn(e.getMessage());
			
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(BodyFormatException.class);
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(new StringBuilder("OutOfMemoryError::").append(e.getMessage()).toString());
			
			ArrayList<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				
				putToOutputMessageQueue(fromSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
			}			
			return;
		} catch (Exception e) {
			log.warn(e.getMessage());
			
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
			selfExnOutObj.messageHeaderInfo.mailID = receivedLetter.getMailID();
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(BodyFormatException.class);
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(new StringBuilder("메시지를 디코딩하여 추출할때 알수 없는 에러 발생::").append(e.getMessage()).toString());
			
			ArrayList<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				
				putToOutputMessageQueue(fromSC, receivedLetter, selfExnOutObj, wrapBufferList, ouputMessageQueue);
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
			}
			return;
		}
					
		// messageProtocol, projectCharset
		LetterSender letterSender = new LetterSender(this, clientResource, messageFromClient, projectCharset, ouputMessageQueue, messageProtocol, serverObjectCacheManager);
		long firstErraseTime = new java.util.Date().getTime();
		try {			
			doTask(serverProjectConfig, loginManager, letterSender, messageFromClient);			
		} catch (Exception e) {
			// FIXME!
			log.warn("unknown error", e);
			
			String errorMessgae = e.getMessage();
			if (null == errorMessgae) {
				errorMessgae = "서비 비지니스 로직 실행시 에러 발생";
			} else {
				errorMessgae = new StringBuilder("서비 비지니스 로직 실행시 에러 발생::").append(errorMessgae).toString();
			}
			
			log.warn(String.format("%s Executor[%d], fromSC[%d], messageFromClient[%s], %s", 
					serverProjectConfig.getProjectName(), index, 
					fromSC.hashCode(), messageFromClient.toString(), errorMessgae), e);
			
			
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageFromClient.messageHeaderInfo;
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(ServerTaskException.class);
			selfExnOutObj.setErrorMessageID(messageIDFromClient);
			selfExnOutObj.setErrorMessage(errorMessgae);
			
			ArrayList<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				
				putToOutputMessageQueue(fromSC, messageFromClient, selfExnOutObj, wrapBufferList, ouputMessageQueue);
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, fromSC={}, SelfExn={}", fromSC.hashCode(), selfExnOutObj.toString());
			}
			
			/**
			 * FIXME! 서버 타스크 수행중 받은 편지들 로그 남기기, 삭제할 필요는 없어 삭제는 하지 않음. 
			 */
			letterSender.writeLogAll("서버 타스크 수행중 에러");
			return;
		}
		
		long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
		
		letterSender.directSendLetterToClientList();
	}
	
	public ArrayList<WrapBuffer> getMessageStream(String messageIDFromClient, 
			SocketChannel toSC, 
			AbstractMessage  messageToClient,
			Charset projectCharset,
			MessageProtocolIF messageProtocol,			
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		String messageIDToClient = messageToClient.getMessageID();
		
		ArrayList<WrapBuffer> wrapBufferList = null;		
		
		MessageEncoder messageEncoder = encoderHash.get(messageIDToClient);

		if (null == messageEncoder) {
			MessageCodecIF messageCodec = null;
			try {
				messageCodec = serverObjectCacheManager.getServerCodec(classLoader, messageIDToClient);				
			} catch (DynamicClassCallException e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());
				
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			} catch (Exception e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());
				
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			}
			
			try {
				messageEncoder = messageCodec.getMessageEncoder();
			} catch (DynamicClassCallException e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			} catch (Exception e) {
				SelfExn selfExnOutObj = new SelfExn();
				selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
				selfExnOutObj.setErrorWhere("S");
				selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
				selfExnOutObj.setErrorMessageID(messageIDToClient);
				selfExnOutObj.setErrorMessage(e.getMessage());
				
				try {
					wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
				} catch(Exception e1) {
					log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
					return null;
				}
				return wrapBufferList;
			}
			
			encoderHash.put(messageIDToClient, messageEncoder);
			
			log.info("classLoader[{}], serverTask[{}], create new messageEncoder of messageIDToClient={}", classLoader.hashCode(), messageIDFromClient, messageIDToClient);
		}
		
		try {
			wrapBufferList = messageProtocol.M2S(messageToClient, messageEncoder, projectCharset);
		} catch (NoMoreDataPacketBufferException e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(NoMoreDataPacketBufferException.class);
			selfExnOutObj.setErrorMessageID(messageIDToClient);
			selfExnOutObj.setErrorMessage(e.getMessage());
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
				return null;
			}
			return wrapBufferList;
		} catch (DynamicClassCallException e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(DynamicClassCallException.class);
			selfExnOutObj.setErrorMessageID(messageIDToClient);
			selfExnOutObj.setErrorMessage(e.getMessage());
			
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
				return null;
			}
			return wrapBufferList;
		} catch (BodyFormatException e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(BodyFormatException.class);
			selfExnOutObj.setErrorMessageID(messageIDToClient);
			selfExnOutObj.setErrorMessage(e.getMessage());
			
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);				
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
				return null;
			}
			return wrapBufferList;
		} catch (Exception e) {
			SelfExn selfExnOutObj = new SelfExn();
			selfExnOutObj.messageHeaderInfo = messageToClient.messageHeaderInfo;
			selfExnOutObj.setErrorWhere("S");
			selfExnOutObj.setErrorGubun(BodyFormatException.class);
			selfExnOutObj.setErrorMessageID(messageIDToClient);
			selfExnOutObj.setErrorMessage("unknown error::"+e.getMessage());
			
			try {
				wrapBufferList = messageProtocol.M2S(selfExnOutObj, CommonStaticFinalVars.SELFEXN_ENCODER, projectCharset);
			} catch(Exception e1) {
				log.error("시스템 내부 메시지 SelfExn 스트림 만들기 실패, toSC={}, SelfExn={}", toSC.hashCode(), selfExnOutObj.toString());
				return null;
			}
			return wrapBufferList;
		}
		
		return wrapBufferList;
	}
	
	
	private void putToOutputMessageQueue(SocketChannel clientSC, 
			AbstractMessage  messageFromClient,
			AbstractMessage wrapBufferMessage, ArrayList<WrapBuffer> wrapBufferList, 
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue) {
		
		// wrapBufferMessage.messageHeaderInfo = messageFromClient.messageHeaderInfo;
		
		LetterToClient letterToClient = new LetterToClient(clientSC,
				wrapBufferMessage,
				wrapBufferList);  
		try {
			ouputMessageQueue.put(letterToClient);
		} catch (InterruptedException e) {
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, clientSC hashCode=[{}], messageFromClient=[{}], 전달 못한 송신 메시지=[{}]", 
					clientSC.hashCode(), messageFromClient.toString(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}
		
	private void putToOutputMessageQueue(SocketChannel clientSC, 
			ReceivedLetter  receivedLetter,
			AbstractMessage wrapBufferMessage, ArrayList<WrapBuffer> wrapBufferList, 
			LinkedBlockingQueue<LetterToClient> ouputMessageQueue) {
		
		/*wrapBufferMessage.messageHeaderInfo.mailboxID = receivedLetter.getMailboxID();
		wrapBufferMessage.messageHeaderInfo.mailID = receivedLetter.getMailID();*/
		
		LetterToClient letterToClient = new LetterToClient(clientSC,
				wrapBufferMessage,
				wrapBufferList);  
		try {
			ouputMessageQueue.put(letterToClient);
		} catch (InterruptedException e) {
			try {
				ouputMessageQueue.put(letterToClient);
			} catch (InterruptedException e1) {
				log.error("재시도 과정에서 인터럽트 발생하여 종료, clientSC hashCode=[{}], 입력 메시지[{}] 추출 실패, 전달 못한 송신 메시지=[{}]", 
					clientSC.hashCode(), receivedLetter.toString(), wrapBufferMessage.toString());
				Thread.interrupted();
			}
		}
	}
	
	
	/**
	 * 출력메시지 직접 전송하는 개발자가 직접 작성해야할 비지니스 로직 
	 * @param serverProjectConfig 프로젝트의 서버 환경 변수
	 * @param loginManager 로그인 관리자
	 * @param letterSender 클라이언트로 보내는 편지 배달부
	 * @param messageFromClient 입력 메시지	  
	 * @param clientResourceManager 클라이언트 자원 관리자
	 * @throws Exception 에러 발생시 던지는 예외
	 */
	abstract public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender,
			AbstractMessage messageFromClient) throws Exception;
}