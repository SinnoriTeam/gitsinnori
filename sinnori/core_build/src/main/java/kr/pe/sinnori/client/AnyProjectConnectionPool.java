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

package kr.pe.sinnori.client;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ClientMessageUtility;
import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.ConnectionFixedParameter;
import kr.pe.sinnori.client.connection.ConnectionPoolIF;
import kr.pe.sinnori.client.connection.ConnectionPoolSupporter;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceFactory;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceFactoryIF;
import kr.pe.sinnori.client.connection.asyn.AsynSocketResourceIF;
import kr.pe.sinnori.client.connection.asyn.noshare.AsynPrivateConnection;
import kr.pe.sinnori.client.connection.asyn.noshare.AsynPrivateConnectionPool;
import kr.pe.sinnori.client.connection.asyn.noshare.AsynPrivateConnectionPoolParameter;
import kr.pe.sinnori.client.connection.asyn.share.AsynPrivateMailboxPoolFactory;
import kr.pe.sinnori.client.connection.asyn.share.AsynPrivateMailboxPoolFactoryIF;
import kr.pe.sinnori.client.connection.asyn.share.AsynPrivateMailboxPoolIF;
import kr.pe.sinnori.client.connection.asyn.share.AsynPublicConnection;
import kr.pe.sinnori.client.connection.asyn.share.AsynPublicConnectionPool;
import kr.pe.sinnori.client.connection.asyn.share.AsynPublicConnectionPoolParameter;
import kr.pe.sinnori.client.connection.asyn.threadpool.IEOClientThreadPoolSetManager;
import kr.pe.sinnori.client.connection.asyn.threadpool.IEOClientThreadPoolSetManagerIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.executor.ClientExecutorPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.InputMessageWriterPool;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPool;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateConnection;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateConnectionPool;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateConnectionPoolParameter;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateSocketResource;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateSocketResourceFactory;
import kr.pe.sinnori.client.connection.sync.noshare.SyncPrivateSocketResourceFactoryIF;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPool;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStreamFactory;
import kr.pe.sinnori.common.io.SocketOutputStreamFactoryIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.protocol.djson.DJSONMessageProtocol;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;
import kr.pe.sinnori.common.type.ConnectionType;

/**
 * <pre>
 * 클라리언트 프로젝트 클래스. 프로젝트 소속 클라이언트용 서버접속 API 동작 환경 조성과 자원을 전담하는 클래스.
 * -  자원 목록 -
 * (1) 데이터 패킷 버퍼 큐
 * (2) 바디 버퍼 큐
 * (3) 비동기 입출력 자원
 *     (3-1) 입력 메시지 큐
 *     (3-2) 출력 메시지 큐를 원소로 하는 큐
 *     (3-3) 서버에서 보내는 불특정 다수 메시지를 받는 큐
 *     (3-4) 입력 메시지 쓰기 담당 쓰레드 폴
 *     (3-5) 출력 메시지 쓰기 담당 쓰레드 폴
 * (4) 메시지 정보 해쉬
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class AnyProjectConnectionPool implements AnyProjectConnectionPoolIF {
	private Logger log = LoggerFactory.getLogger(AnyProjectConnectionPool.class);

	private ProjectPartConfiguration projectPartConfiguration = null;

	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private IEOClientThreadPoolSetManagerIF ieoClientThreadPoolSetManager = null;

	/** 비동기 방식에서 사용되는 변수 시작 */
	private AsynSocketResourceFactoryIF asynSocketResourceFactory = null;
	private AsynPrivateMailboxPoolFactoryIF asynPrivateMailboxPoolFactory = null;
	/** 비동기 방식에서 사용되는 변수 종료 */

	/** 동기 방식에서 사용되는 변수 시작 */
	private SyncPrivateSocketResourceFactoryIF syncPrivateSocketResourceFactory = null;
	/** 동기 방식에서 사용되는 변수 종료 */

	/** 프로젝트의 연결 클래스 폴 */
	private ConnectionPoolIF connectionPool = null;	
	private ConnectionFixedParameter connectionFixedParameter = null; 
	
	private ConnectionPoolSupporter connectionPoolSupporter = null;
	private InputMessageWriterPool inputMessageWriterPool = null;
	private OutputMessageReaderPool outputMessageReaderPool = null;
	private ClientExecutorPool clientExecutorPool = null;

	public AnyProjectConnectionPool(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException, ConnectionPoolException {
		this.projectPartConfiguration = projectPartConfiguration;

		CharsetEncoder charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(projectPartConfiguration.getCharset());
		CharsetDecoder charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(projectPartConfiguration.getCharset());
		
		MessageProtocolIF messageProtocol = null;

		boolean isDirect = false;
		this.dataPacketBufferPool = new DataPacketBufferPool(isDirect, projectPartConfiguration.getByteOrder(),
				projectPartConfiguration.getDataPacketBufferSize(),
				projectPartConfiguration.getDataPacketBufferPoolSize());

		switch (projectPartConfiguration.getMessageProtocolType()) {
		case DHB: {
			messageProtocol = new DHBMessageProtocol(
					projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), charsetEncoderOfProject,
					charsetDecoderOfProject, dataPacketBufferPool);

			break;
		}
		case DJSON: {
			messageProtocol = new DJSONMessageProtocol(projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
					charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPool);
			break;
		}
		case THB: {
			messageProtocol = new THBMessageProtocol(projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), charsetEncoderOfProject,
					charsetDecoderOfProject, dataPacketBufferPool);
			break;
		}
		default: {
			log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.", projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getMessageProtocolType().toString()));
			System.exit(1);
		}
		}

		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(
				projectPartConfiguration.getFirstPrefixDynamicClassFullName());

		ClientObjectCacheManagerIF clientObjectCacheManager = new ClientObjectCacheManager(ioPartDynamicClassNameUtil);

		SocketOutputStreamFactoryIF socketOutputStreamFactory = new SocketOutputStreamFactory(charsetDecoderOfProject,
				projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), dataPacketBufferPool);

		ClientMessageUtilityIF clientMessageUtility = new ClientMessageUtility(messageProtocol, clientObjectCacheManager,
				dataPacketBufferPool);
		
		connectionPoolSupporter = new ConnectionPoolSupporter(1000L * 60 * 10);
		
		connectionFixedParameter 
			= new ConnectionFixedParameter(projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getServerHost(),
					projectPartConfiguration.getServerPort(),
					projectPartConfiguration.getClientSocketTimeout(),
					clientMessageUtility);
		
		if (projectPartConfiguration.getConnectionType().equals(ConnectionType.SYNC_PRIVATE)) {
			syncPrivateSocketResourceFactory = new SyncPrivateSocketResourceFactory(socketOutputStreamFactory);
			
			SyncPrivateConnectionPoolParameter syncPrivateConnectionPoolParameter 
				= new SyncPrivateConnectionPoolParameter(projectPartConfiguration.getClientConnectionCount(),
						projectPartConfiguration.getClientConnectionMaxCount(),
						connectionPoolSupporter, syncPrivateSocketResourceFactory);

			connectionPool = new SyncPrivateConnectionPool(syncPrivateConnectionPoolParameter,					
					connectionFixedParameter);
		} else {
			inputMessageWriterPool = new InputMessageWriterPool(
					projectPartConfiguration.getClientAsynInputMessageWriterPoolSize(),
					projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getClientAsynInputMessageQueueSize(), clientMessageUtility);

			outputMessageReaderPool = new OutputMessageReaderPool(					
					projectPartConfiguration.getClientAsynOutputMessageReaderPoolSize(),
					projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getClientWakeupIntervalOfSelectorForReadEventOnly(), messageProtocol);

			clientExecutorPool = new ClientExecutorPool(
					projectPartConfiguration.getClientAsynExecutorPoolSize(), 
					projectPartConfiguration.getProjectName(),
					projectPartConfiguration.getClientAsynOutputMessageQueueSize(), clientMessageUtility);

			ieoClientThreadPoolSetManager = new IEOClientThreadPoolSetManager(
					inputMessageWriterPool, outputMessageReaderPool, clientExecutorPool);

			asynSocketResourceFactory = new AsynSocketResourceFactory(socketOutputStreamFactory,
					ieoClientThreadPoolSetManager);

			inputMessageWriterPool.startAll();
			clientExecutorPool.startAll();
			outputMessageReaderPool.startAll();

			if (projectPartConfiguration.getConnectionType().equals(ConnectionType.ASYN_PUBLIC)) {
				asynPrivateMailboxPoolFactory = new AsynPrivateMailboxPoolFactory(
						projectPartConfiguration.getClientAsynPirvateMailboxCntPerPublicConnection(),
						projectPartConfiguration.getClientSocketTimeout());

				AsynPublicConnectionPoolParameter asynPublicConnectionPoolParameter =
						new AsynPublicConnectionPoolParameter(projectPartConfiguration.getClientConnectionCount(),
								projectPartConfiguration.getClientConnectionMaxCount(),
								connectionPoolSupporter, asynSocketResourceFactory, asynPrivateMailboxPoolFactory);
				
				connectionPool = new AsynPublicConnectionPool(asynPublicConnectionPoolParameter,
						connectionFixedParameter);
			} else {
				AsynPrivateConnectionPoolParameter  asynPrivateConnectionPoolParameter 
				= new AsynPrivateConnectionPoolParameter(projectPartConfiguration.getClientConnectionCount(),
						projectPartConfiguration.getClientConnectionMaxCount(),
						connectionPoolSupporter, asynSocketResourceFactory); 
				
				connectionPool = new AsynPrivateConnectionPool(asynPrivateConnectionPoolParameter,
						connectionFixedParameter);
			}

		}

		/*
		 * clientProjectMonitor = new ClientProjectMonitor( clientMonitorTimeInterval,
		 * clientMonitorReceptionTimeout); clientProjectMonitor.start();
		 */
	}

	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, ServerTaskException,
			AccessDeniedException, InterruptedException, ConnectionPoolException, IOException {
		long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();

		AbstractMessage outObj = null;
		AbstractConnection conn = connectionPool.getConnection();
		try {
			outObj = conn.sendSyncInputMessage(inputMessage);
		} catch (BodyFormatException e) {
			throw e;
		} catch (IOException e) {
			// log.warn("IOException", e);
			try {
				conn.close();
			} catch (IOException e1) {
			}

			throw e;
		} finally {
			connectionPool.release(conn);
		}

		endTime = System.nanoTime();
		log.debug("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));

		return outObj;
	}

	public void sendAsynInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, ConnectionPoolException, SocketTimeoutException, NotSupportedException,
			NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException, DynamicClassCallException {
		long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();

		AbstractConnection conn = connectionPool.getConnection();
		try {
			conn.sendAsynInputMessage(inputMessage);
		} finally {
			connectionPool.release(conn);
		}

		endTime = System.nanoTime();
		log.debug("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
	}

	public AbstractConnection createConnection(String host, int port)
			throws NoMoreDataPacketBufferException, InterruptedException, IOException {
		AbstractConnection conn = null;

		if (projectPartConfiguration.getConnectionType().equals(ConnectionType.SYNC_PRIVATE)) {
			SyncPrivateSocketResource syncPrivateSocketResource = syncPrivateSocketResourceFactory.makeNewSyncPrivateSocketResource();

			conn = new SyncPrivateConnection(connectionFixedParameter,
					syncPrivateSocketResource);
		} else {
			AsynSocketResourceIF asynSocketResource = asynSocketResourceFactory.makeNewAsynSocketResource();

			if (projectPartConfiguration.getConnectionType().equals(ConnectionType.ASYN_PUBLIC)) {
				
				AsynPrivateMailboxPoolIF asynPrivateMailboxPool
					=	asynPrivateMailboxPoolFactory.makeNewAsynPrivateMailboxPool();	
				
				conn = new AsynPublicConnection(connectionFixedParameter,						
						asynSocketResource,
						asynPrivateMailboxPool);
			} else {				
				conn = new AsynPrivateConnection(connectionFixedParameter, 
						asynSocketResource);
			}
		}

		return conn;
	}
	
	/**
	 * this method for junit test
	 * @return the connection poll defined as specified in the configuration file
	 */
	public ConnectionPoolIF getconnectionPool() {
		return connectionPool;
	}
	
	/**
	 * this method for junit test
	 * @return the client input/executor/output thread pool set manager defined as specified in the configuration file
	 */
	public IEOClientThreadPoolSetManagerIF getIEOClientThreadPoolSetManager() throws NotSupportedException {
		if (null == ieoClientThreadPoolSetManager) {
			String errorMessage = new StringBuilder("this project[")
					.append(projectPartConfiguration.getProjectName())
					.append("]'s connection type[")
					.append(projectPartConfiguration.getConnectionType())
					.append("] doesn't support this method").toString();
			throw new NotSupportedException(errorMessage);
		}
		return ieoClientThreadPoolSetManager;
	}

}
