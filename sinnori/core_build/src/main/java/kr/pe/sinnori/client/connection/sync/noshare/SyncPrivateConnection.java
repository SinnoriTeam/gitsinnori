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
package kr.pe.sinnori.client.connection.sync.noshare;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;


public class SyncPrivateConnection extends AbstractConnection {
	private SocketResoruceIF syncPrivateSocketResoruce = null;
	
	private static final int MAILBOX_ID = 1;
	
	/** 메일 식별자 */
	private int mailID = 0;	
	
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	
	
	
	public SyncPrivateConnection(String projectName,  
			String host, int port,
			long socketTimeOut,
			ClientMessageUtilityIF clientMessageUtility,
			SocketResoruceIF syncPrivateSocketResoruce) throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(projectName, host, port, socketTimeOut, clientMessageUtility);
		
		this.syncPrivateSocketResoruce = syncPrivateSocketResoruce;
		
		doConnect();
		
		//log.info(String.format("project[%s] NoShareSyncConnection[%d] 생성자 end", projectName, serverSC.hashCode()));
	}
	
	
	
	
	public boolean isInQueue() {
		// return lastCaller.equals("");
		return isQueueIn;
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
		// log.info("put NoShareSyncConnection[{}] in the connection queue", monitor.hashCode());
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
		isQueueIn = false;
		// log.info("get NoShareSyncConnection[{}] from the connection queue", monitor.hashCode());
	}
	

	@Override
	protected void openSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSelectableChannel = serverSC.configureBlocking(false);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);		

		/*StringBuilder infoBuilder = null;
		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(projectName);
		infoBuilder.append("] sync private connection[");
		infoBuilder.append(serverSC.hashCode());
		infoBuilder.append("]");
		log.info(infoBuilder.toString());*/
		
		log.info("projectName[{}] sync private connection[{}] created", projectName, serverSC.hashCode());
		
	}

	@Override
	protected void doConnect() throws IOException {
		Selector connectionEventOnlySelector = Selector.open();

		try {
			serverSC.register(connectionEventOnlySelector, SelectionKey.OP_CONNECT);

			InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
			if (! serverSC.connect(remoteAddr)) {
				@SuppressWarnings("unused")
				int numberOfKeys = connectionEventOnlySelector.select(socketTimeOut);

				// log.info("numberOfKeys={}", numberOfKeys);

				Iterator<SelectionKey> selectionKeyIterator = connectionEventOnlySelector.selectedKeys().iterator();
				if (!selectionKeyIterator.hasNext()) {

					String errorMessage = String.format("1.the socket[sc hascode=%d] timeout", serverSC.hashCode());
					throw new SocketTimeoutException(errorMessage);
				}

				SelectionKey selectionKey = selectionKeyIterator.next();
				selectionKey.cancel();

				if (!serverSC.finishConnect()) {
					String errorMessage = String.format("the socket[sc hascode=%d] has an error pending",
							serverSC.hashCode());
					throw new SocketTimeoutException(errorMessage);
				}
			}
		} finally {
			connectionEventOnlySelector.close();
		}

		log.info("projectName[{}] sync private connection[{}] connected", projectName, serverSC.hashCode());
	}
	
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws InterruptedException, NoMoreDataPacketBufferException,  
			DynamicClassCallException, ServerTaskException, AccessDeniedException, BodyFormatException, IOException {
		long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();
		
		// String messageID = inObj.getMessageID();
		// LetterFromServer letterFromServer = null;
		

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		inObj.messageHeaderInfo.mailboxID = MAILBOX_ID;
		inObj.messageHeaderInfo.mailID = this.mailID;
		
		
		AbstractMessage outObj = null;
		
		Selector ioEventOnlySelector = Selector.open();
		
		try {
			serverSC.register(ioEventOnlySelector, SelectionKey.OP_WRITE);
			
			List<WrapBuffer> warpBufferList = null;
			try {
				warpBufferList = clientMessageUtility.buildReadableWrapBufferList(classLoader, inObj);
				
				int indexOfWorkingBuffer = 0;
				int warpBufferListSize = warpBufferList.size();
				
				
				// WrapBuffer workingWrapBuffer = null;
				// ByteBuffer workingByteBuffer = null;
				WrapBuffer workingWrapBuffer = warpBufferList.get(indexOfWorkingBuffer);
				ByteBuffer workingByteBuffer = workingWrapBuffer.getByteBuffer();
				
				boolean loop = true;
				do {
					int numberOfKeys =  ioEventOnlySelector.select(socketTimeOut);
					if (0 == numberOfKeys) {
						String errorMessage = new StringBuilder("this sync private connection[")
								.append(serverSC.hashCode())
								.append("] timeout").toString();
						throw new SocketTimeoutException(errorMessage);
					}
					
					ioEventOnlySelector.selectedKeys().clear();
					
					serverSC.write(workingByteBuffer);
					
					if (! workingByteBuffer.hasRemaining()) {
						if ((indexOfWorkingBuffer+1) == warpBufferListSize) {
							loop = false;
							break;
						}
						indexOfWorkingBuffer++;
						workingWrapBuffer = warpBufferList.get(indexOfWorkingBuffer);
						workingByteBuffer = workingWrapBuffer.getByteBuffer();
					}
				} while (loop);
				
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to write a input message in this sync private connection[")
						.append(serverSC.hashCode())
						.append("], errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				log.warn("this input message[sc={}][{}] has dropped becase of write failure", 
						serverSC.hashCode(), inObj.toString());
				
				throw new IOException(errorMessage);
			} finally {
				if (null != warpBufferList) {
					clientMessageUtility.releaseWrapBufferList(warpBufferList);
				}
			}
			
			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}
			
			SocketOutputStream socketOutputStream = syncPrivateSocketResoruce.getSocketOutputStream();
			serverSC.keyFor(ioEventOnlySelector).interestOps(SelectionKey.OP_READ);			
			
			boolean loop = true;
			do {
				int numberOfKeys =  ioEventOnlySelector.select(socketTimeOut);
				if (0 == numberOfKeys) {
					String errorMessage = new StringBuilder("this sync private connection[")
							.append(serverSC.hashCode())
							.append("] timeout").toString();
					throw new SocketTimeoutException(errorMessage);
				}
				
				ioEventOnlySelector.selectedKeys().clear();
				
				try {
					int numRead = socketOutputStream.read(serverSC);
					
					if (numRead == -1) {
						String errorMessage = new StringBuilder("this socket channel[")
								.append(serverSC.hashCode())
								.append("] has reached end-of-stream").toString();
						throw new IOException(errorMessage);
					}
					
					setFinalReadTime();					
					
					ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = clientMessageUtility
							.getWrapReadableMiddleObjectList(socketOutputStream);
					
					
					if (wrapReadableMiddleObjectList.size() ==  1) {
						WrapReadableMiddleObject wrapReadableMiddleObject = wrapReadableMiddleObjectList.get(0);
						outObj = clientMessageUtility.buildOutputMessage(classLoader, wrapReadableMiddleObject);
						loop = false;
						break;
					} else if (wrapReadableMiddleObjectList.size() > 1) {
						String errorMessage = new StringBuilder("this sync private connection[")
								.append(serverSC.hashCode())
								.append("] has a one more message").toString();
						throw new SocketException(errorMessage);
					}

				} catch (Exception e) {
					String errorMessage = new StringBuilder("fail to read a output message")							
							.append(" in this sync private connection[")
							.append(serverSC.hashCode())
							.append("], errmsg=")
							.append(e.getMessage()).toString();
					log.warn(errorMessage, e);
					log.warn("this input message[sc={}][{}] has dropped becase of read failure", 
							serverSC.hashCode(), inObj.toString());
					
					throw new IOException(errorMessage);
				}
			} while (loop);		
			
		} finally {
			try {
				ioEventOnlySelector.close();
			} catch(IOException e) {
			}
		}		
		
		if (outObj instanceof SelfExnRes) {
			SelfExnRes selfExnRes = (SelfExnRes) outObj;
			log.warn(selfExnRes.toString());
			SelfExn.ErrorType.throwSelfExnException(selfExnRes);
		}		
		
		endTime = System.nanoTime();
		log.debug("시간차[{}]", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

		return outObj;
	}	
	
	public void sendAsynInputMessage(AbstractMessage inObj) 
			throws NotSupportedException {		
		throw new NotSupportedException("this synchronous connection doesn't support this method 'sendAsynInputMessage'");
	}
	
	@Override
	public void finalize() {
		try {
			close();
		} catch (IOException e) {
			
		}
		
		if (! isQueueIn) {
			log.warn("큐로 복귀 못한 동기 비공유 연결[{}]", hashCode());
		}
	}


	protected void doReleaseSocketResources() {
		syncPrivateSocketResoruce.releaseSocketResources();
	}



}