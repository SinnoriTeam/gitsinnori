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
package kr.pe.sinnori.client.connection.asyn.share;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.ConnectionPoolManagerIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResource;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.mailbox.AsynPrivateMailboxMapper;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.handler.ClientExecutorIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.handler.OutputMessageReaderIF;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * 클라이언트 공유 방식의 비동기 연결 클래스 {@link ShareAsynConnection} 를 원소로 가지는 폴 관리자 클래스<br/>
 * 다른 쓰레드간에 연결 클래스를 공유하기 위해서 목록으로 관리되며 순차적으로 순환 할당한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ShareAsynConnectionPool implements ConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(ShareAsynConnectionPool.class);
	
	private final Object monitor = new Object();

	private String projectName = null;
	private String host = null;
	private int port;
	private int connectionPoolSize;
	private int connectionPoolMaxSize;
	private long socketTimeOut;
	private InputMessageWriterPoolIF inputMessageWriterPool = null;
	private OutputMessageReaderPoolIF outputMessageReaderPool = null;
	private ClientExecutorPoolIF clientExecutorPool = null;
	private int numberOfAsynPrivateMailboxPerConnection;
	private int dataPacketBufferMaxCntPerMessage;
	private CharsetDecoder streamCharsetDecoder = null;
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ClientObjectCacheManagerIF clientObjectCacheManager = null;

	/**
	 * 공유방식으로 비동기 방식의 소켓 채널을 소유한 연결 클래스 목록. 쓰레드 공유하기 위해서 순차적으로 할당한다.
	 */
	private LinkedList<ShareAsynConnection> connectionList = null;
	private transient int numberOfConnection = 0;
	private ConnectionPoolManagerIF poolManager = null;
	private int currentWorkingIndex = -1;

	public ShareAsynConnectionPool(String projectName, String host, int port, int connectionPoolSize,
			int connectionPoolMaxSize, long socketTimeOut, InputMessageWriterPoolIF inputMessageWriterPool,
			OutputMessageReaderPoolIF outputMessageReaderPool, ClientExecutorPoolIF clientExecutorPool,
			int numberOfAsynPrivateMailboxPerConnection, int dataPacketBufferMaxCntPerMessage,
			CharsetDecoder streamCharsetDecoder, MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool, ClientObjectCacheManagerIF clientObjectCacheManager)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		this.projectName = projectName;
		this.host = host;
		this.port = port;
		this.connectionPoolSize = connectionPoolSize;
		this.connectionPoolMaxSize = connectionPoolMaxSize;
		this.socketTimeOut = socketTimeOut;
		this.inputMessageWriterPool = inputMessageWriterPool;
		this.outputMessageReaderPool = outputMessageReaderPool;
		this.clientExecutorPool = clientExecutorPool;
		this.numberOfAsynPrivateMailboxPerConnection = numberOfAsynPrivateMailboxPerConnection;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.clientObjectCacheManager = clientObjectCacheManager;

		connectionList = new LinkedList<ShareAsynConnection>();
		try {
			for (int i = 0; i < connectionPoolSize; i++) {
				addConnection();
			}

		} catch (IOException e) {
			while (!connectionList.isEmpty()) {
				try {
					connectionList.removeFirst().close();
				} catch (IOException e1) {
				}
			}
			throw e;
		}

		// log.info("connectionList size=[%d]", connectionList.size());
	}

	public void addConnection()
			throws InterruptedException, NoMoreDataPacketBufferException, IOException, ConnectionPoolException {
		synchronized (monitor) {
			if (numberOfConnection >= connectionPoolMaxSize) {
				throw new ConnectionPoolException("fail to add a connection because this connection pool is full");
			}

			OutputMessageReaderIF outputMessageReader = outputMessageReaderPool.getNextOutputMessageReader();
			InputMessageWriterIF inputMessageWriter = inputMessageWriterPool.getNextInputMessageWriter();
			ClientExecutorIF clientExecutor = clientExecutorPool.getNextClientExecutor();

			SocketOutputStream socketOutputStream = new SocketOutputStream(streamCharsetDecoder,
					dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);

			ShareAsynConnection serverConnection = null;

			AsynPrivateMailboxMapper asynPrivateMailboxMapper = new AsynPrivateMailboxMapper(
					numberOfAsynPrivateMailboxPerConnection, socketTimeOut);

			AsynSocketResourceIF asynSocketResource = new AsynSocketResource(socketOutputStream, inputMessageWriter,
					outputMessageReader, clientExecutor);

			serverConnection = new ShareAsynConnection(projectName, host, port, socketTimeOut, asynPrivateMailboxMapper,
					asynSocketResource, messageProtocol, clientObjectCacheManager);

			connectionList.add(serverConnection);
			
			numberOfConnection++;			
			connectionPoolSize = Math.max(numberOfConnection, connectionPoolSize);
		}

	}

	public AbstractConnection getConnection() throws InterruptedException, SocketTimeoutException, ConnectionPoolException {
		boolean loop = false;
		ShareAsynConnection conn = null;
		
		synchronized (monitor) {			
			do {
				if (connectionList.isEmpty()) {
					throw new ConnectionPoolException("check server alive");
				}
				
				currentWorkingIndex = (currentWorkingIndex + 1) % connectionList.size();
				
				conn = connectionList.get(currentWorkingIndex);

				if (conn.isConnected()) {
					loop = false;
				} else {
					loop = true;
					
					String reasonForLoss = new StringBuilder("다음 차례의 비동기 공유 연결[")
							.append(conn.hashCode()).append("]이 닫혀있어 폐기").toString();

					numberOfConnection--;

					log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);
					
					poolManager.notice(reasonForLoss);
					
					connectionList.remove(conn);
				}
			} while (loop);

			return conn;
		}
	}

	public void release(AbstractConnection conn) {
		
		if (null == conn) {
			String errorMessage = "the parameter conn is null";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!(conn instanceof ShareAsynConnection)) {
			String errorMessage = "the parameter conn is not instace of ShareAsynConnection class";
			log.warn(errorMessage, new Throwable());
			throw new IllegalArgumentException(errorMessage);
		}
		
		synchronized (monitor) {
			if (!conn.isConnected()) {
				String reasonForLoss = new StringBuilder("반환된 비동기 공유 연결[")
						.append(conn.hashCode()).append("]이 닫혀있어 폐기").toString();

				numberOfConnection--;

				log.warn("{}, 총 연결수[{}]", reasonForLoss, numberOfConnection);
				
				poolManager.notice(reasonForLoss);
				
				connectionList.remove(conn);
			}
		}
	}
	
	@Override
	public boolean whetherConnectionIsMissing() {
		return (numberOfConnection != connectionPoolSize);
	}

	@Override
	public void registerPoolManager(ConnectionPoolManagerIF poolManager) {
		this.poolManager = poolManager;
	}

}
