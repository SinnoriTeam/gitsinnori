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

package kr.pe.codda.server.threadpool.outputmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.asyn.ToLetter;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.threadpool.ThreadPoolIF;
import kr.pe.codda.server.threadpool.IEOServerThreadPoolSetManagerIF;

/**
 * 서버 출력 메시지 소켓 쓰기 담당 쓰레드 폴
 * 
 * @author Won Jonghoon
 */
public class OutputMessageWriterPool implements ThreadPoolIF, OutputMessageWriterPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(OutputMessageWriterPool.class);
	private final Object monitor = new Object();
	private final List<OutputMessageWriterIF> pool = new ArrayList<OutputMessageWriterIF>();
	
	private int poolMaxSize;
	private String projectName = null;
	private int outputMessageQueueSize;
	private DataPacketBufferPoolIF dataPacketBufferPool;

	public OutputMessageWriterPool(int poolSize, int poolMaxSize,
			String projectName, 
			int outputMessageQueueSize,
			DataPacketBufferPoolIF dataPacketBufferPool,
			IEOServerThreadPoolSetManagerIF ieoThreadPoolManager) {
		if (poolSize <= 0) {
			String errorMessage = String.format("the parameter poolSize[%d] is less than or equal to zero", poolSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (poolMaxSize <= 0) {
			String errorMessage = String.format("the parameter poolMaxSize[%d] is less than or equal to zero", poolMaxSize); 
			throw new IllegalArgumentException(errorMessage);
		}

		if (poolSize > poolMaxSize) {
			String errorMessage = String.format("the parameter poolSize[%d] is greater than the parameter poolMaxSize[%d]", poolSize, poolMaxSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == projectName) {
			throw new IllegalArgumentException("the parameter projectName is null");
		}
		
		if (outputMessageQueueSize <= 0) {
			String errorMessage = String.format("the parameter outputMessageQueueSize[%d] is less than or equal to zero", outputMessageQueueSize); 
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}
		
		if (null == ieoThreadPoolManager) {
			throw new IllegalArgumentException("the parameter ieoThreadPoolManager is null");
		}		

		this.poolMaxSize = poolMaxSize;
		this.projectName = projectName;		
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.dataPacketBufferPool = dataPacketBufferPool;
		
		ieoThreadPoolManager.setOutputMessageWriterPool(this);
		
		for (int i = 0; i < poolSize; i++) {
			try {
				addTask();
			} catch (IllegalStateException | NotSupportedException e) {
				log.error(e.getMessage(), e);
				System.exit(1);
			}
		}
	}

	@Override
	public void addTask() throws IllegalStateException, NotSupportedException {
		ArrayBlockingQueue<ToLetter> outputMessageQueue = new ArrayBlockingQueue<ToLetter>(outputMessageQueueSize);
		synchronized (monitor) {
			int size = pool.size();
			
			if (size >= poolMaxSize) {
				String errorMessage = new StringBuilder("can't add a OutputMessageWriter in the project[")
						.append(projectName)
						.append("] becase the number of OutputMessageWriter is maximum[")
						.append(poolMaxSize)
						.append("]").toString();
				
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			try {
				OutputMessageWriterIF handler = new OutputMessageWriter(projectName, size,
						outputMessageQueue,   
						dataPacketBufferPool);
				
				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("failed to add a OutputMessageWriter in the project[")
						.append(projectName)
						.append("], errmsg=")
						.append(e.getMessage()).toString(); 
				
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}

	@Override
	public OutputMessageWriterIF getOutputMessageWriterWithMinimumNumberOfSockets() {
		if (pool.isEmpty()) {
			throw new NoSuchElementException("OutputMessageWriterPool empty");
		}		
		
		int min = Integer.MAX_VALUE;
		OutputMessageWriterIF minOutputMessageWriter = null;
	
		for (OutputMessageWriterIF handler : pool) {
			int numberOfSocket = handler.getNumberOfConnection();
			if (numberOfSocket < min) {
				min = numberOfSocket;
				minOutputMessageWriter = handler;
			}
		}
		
		return minOutputMessageWriter;
	}
	
	@Override
	public int getPoolSize() {
		return pool.size();
	}

	@Override
	public void startAll() {
		for (OutputMessageWriterIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		for (OutputMessageWriterIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.interrupt();
		}
	}
}