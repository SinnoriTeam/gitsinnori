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

package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.io.DataPacketBufferPool;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStreamFactory;
import kr.pe.sinnori.common.io.SocketOutputStreamFactoryIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManager;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;
import kr.pe.sinnori.server.threadpool.accept.processor.AcceptProcessorPool;
import kr.pe.sinnori.server.threadpool.accept.selector.AcceptSelector;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorPool;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderPool;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterPool;


public class AnyProjectServer {
	private InternalLogger log = InternalLoggerFactory.getInstance(AnyProjectServer.class);
	
	private ProjectPartConfiguration projectPartConfiguration = null;
	 
	
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	/** 접속 승인 큐 */
	private ArrayBlockingQueue<SocketChannel> acceptQueue = null;

	/** 클라이언트 접속 승인 쓰레드 */
	private AcceptSelector acceptSelector = null;
	/** 클라이 언트 등록 담당 쓰레드 폴 */
	private AcceptProcessorPool acceptProcessorPool = null;
	/** 입력 메시지 소켓 읽기 담당 쓰레드 폴 */
	private InputMessageReaderPool inputMessageReaderPool = null;
	/** 비지니스 로직 처리 담당 쓰레드 폴 */
	private ServerExecutorPool executorPool = null;
	/** 출력 메시지 소켓 쓰기 담당 쓰레드 폴 */
	private OutputMessageWriterPool outputMessageWriterPool = null;	
	
	private ServerObjectCacheManager serverObjectCacheManager = null;
	
	private ServerProjectMonitor serverProjectMonitor = null;
	
	public AnyProjectServer(ProjectPartConfiguration projectPartConfiguration)
			throws NoMoreDataPacketBufferException, SinnoriConfigurationException {
		this.projectPartConfiguration = projectPartConfiguration;		
		
		CharsetEncoder charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(projectPartConfiguration.getCharset());
		CharsetDecoder charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(projectPartConfiguration.getCharset());
		
		boolean isDirect = false;
		this.dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
				projectPartConfiguration.getByteOrder()
						, projectPartConfiguration.getDataPacketBufferSize()
						, projectPartConfiguration.getDataPacketBufferPoolSize());
		

		MessageProtocolIF messageProtocol = null;
		switch (projectPartConfiguration.getMessageProtocolType()) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol( 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPool);
	
				break;
			}
			/*case DJSON: {
				messageProtocol = new DJSONMessageProtocol(
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPool);
				break;
			}*/
			case THB: {
				messageProtocol = new THBMessageProtocol( 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPool);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						projectPartConfiguration.getProjectName(), projectPartConfiguration
								.getMessageProtocolType().toString()));
				System.exit(1);
			}
		}
		
		acceptQueue = new ArrayBlockingQueue<SocketChannel>(
				projectPartConfiguration.getServerAcceptQueueSize());
		
		IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = new IEOServerThreadPoolSetManager();
		
		SocketOutputStreamFactoryIF socketOutputStreamFactory = 
				new SocketOutputStreamFactory(charsetDecoderOfProject,
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						dataPacketBufferPool);
		
		
		SocketResourceManagerIF socketResourceManager = 
				new SocketResourceManager(socketOutputStreamFactory, ieoThreadPoolManager);

		acceptSelector = new AcceptSelector(
				projectPartConfiguration.getProjectName(), 
				projectPartConfiguration.getServerHost(),
				projectPartConfiguration.getServerPort(),  
				projectPartConfiguration.getServerMaxClients(), acceptQueue, socketResourceManager);

		
		acceptProcessorPool = new AcceptProcessorPool(
				projectPartConfiguration.getServerAcceptProcessorSize(), 
				projectPartConfiguration.getServerAcceptProcessorMaxSize(),
				projectPartConfiguration.getProjectName(),
				acceptQueue,
				socketResourceManager);
		
		inputMessageReaderPool = new InputMessageReaderPool(
				projectPartConfiguration.getServerInputMessageReaderPoolSize(), 
				projectPartConfiguration.getServerInputMessageReaderPoolMaxSize(),
				projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerWakeupIntervalOfSelectorForReadEventOnly(),
				messageProtocol, 
				socketResourceManager, ieoThreadPoolManager);		
		
		serverObjectCacheManager = createNewServerObjectCacheManager();		

		executorPool = new ServerExecutorPool(				 
				projectPartConfiguration.getServerExecutorPoolSize(),
				projectPartConfiguration.getServerExecutorPoolMaxSize(),
				projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerInputMessageQueueSize(), 
				messageProtocol, 
				socketResourceManager,
				serverObjectCacheManager,
				ieoThreadPoolManager);

		outputMessageWriterPool = new OutputMessageWriterPool(
				projectPartConfiguration.getServerOutputMessageWriterPoolSize(), 
				projectPartConfiguration.getServerOutputMessageWriterPoolMaxSize(),
				projectPartConfiguration.getProjectName(),
				projectPartConfiguration.getServerOutputMessageQueueSize(), 
				dataPacketBufferPool, ieoThreadPoolManager);

		serverProjectMonitor = new ServerProjectMonitor(
				projectPartConfiguration.getServerMonitorTimeInterval(), socketResourceManager, 
				dataPacketBufferPool,
				inputMessageReaderPool);
		serverProjectMonitor.start();

	}

	private ServerObjectCacheManager createNewServerObjectCacheManager() throws SinnoriConfigurationException {
		
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(projectPartConfiguration
				.getFirstPrefixDynamicClassFullName());
		
		ServerClassLoaderBuilder serverSimpleClassLoaderBuilder = 
				new ServerClassLoaderBuilder(ioPartDynamicClassNameUtil);
		
		return new ServerObjectCacheManager(serverSimpleClassLoaderBuilder, ioPartDynamicClassNameUtil);
	}
	
	/**
	 * 서버 시작
	 */	
	synchronized public void startServer() {		
		// serverProjectMonitor.start();
		outputMessageWriterPool.startAll();
		executorPool.startAll();
		inputMessageReaderPool.startAll();
		acceptProcessorPool.startAll();
		if (!acceptSelector.isAlive()) {
			acceptSelector.start();
		}
	}

	/**
	 * 서버 종료
	 */
	synchronized public void stopServer() {
		// serverProjectMonitor.interrupt();

		if (! acceptSelector.isInterrupted()) {
			acceptSelector.interrupt();
		}
	
		acceptProcessorPool.stopAll();
		inputMessageReaderPool.stopAll();

		
		executorPool.stopAll();		

		outputMessageWriterPool.stopAll();
	}	

	/**
	 * 서버 프로젝트 모니터
	 * 
	 * @author Won Jonghoon
	 * 
	 */
	private class ServerProjectMonitor extends Thread {		
		private long serverMonitorTimeInterval;
		private SocketResourceManagerIF socketResourceManager = null;
		private DataPacketBufferPoolIF dataPacketBufferPool = null;
		private InputMessageReaderPool inputMessageReaderPool = null;
		
		
		public ServerProjectMonitor(long serverMonitorTimeInterval, 
				SocketResourceManagerIF socketResourceManager,
				DataPacketBufferPoolIF dataPacketBufferPool,
				InputMessageReaderPool inputMessageReaderPool) {
			this.serverMonitorTimeInterval = serverMonitorTimeInterval;
			this.socketResourceManager = socketResourceManager;
			this.dataPacketBufferPool = dataPacketBufferPool;
			this.inputMessageReaderPool = inputMessageReaderPool;
		}

		@Override
		public void run() {
			log.info("ServerProjectMonitor start");
			
			try {
				while (!Thread.currentThread().isInterrupted()) {
					log.info("the number of socketResources[{}]", socketResourceManager.getNumberOfSocketResources());
					log.info("the size[{}] of DataPacketBufferPool[{}]", dataPacketBufferPool.size(), dataPacketBufferPool.getDataPacketBufferPoolSize());					
					log.info("the sum[{}] of inputMessageReaderPool's socket", inputMessageReaderPool.getSumOfInputMessageReaderPoolSocket());
					
					Thread.sleep(serverMonitorTimeInterval);
				}
			} catch(InterruptedException e) {
				log.info("ServerProjectMonitor::interrupr");
			} catch(Exception e) {
				log.info("ServerProjectMonitor::unknow error", e);
			}

			log.info("ServerProjectMonitor end");
		}
	}

}
