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

package kr.pe.codda.server.threadpool.inputmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.threadpool.ThreadPoolIF;
import kr.pe.codda.server.SocketResourceManagerIF;
import kr.pe.codda.server.threadpool.IEOServerThreadPoolSetManagerIF;

/**
 * 입력 메시지 소켓 읽기 담당 쓰레드 폴
 * 
 * @author Won Jonghoon
 * 
 */
public class InputMessageReaderPool implements ThreadPoolIF, InputMessageReaderPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(InputMessageReaderPool.class);
	private final Object monitor = new Object();
	private final List<InputMessageReaderIF> pool = new ArrayList<InputMessageReaderIF>();
	
	
	private int poolMaxSize;
	private String projectName = null;	
	private long wakeupIntervalOfSelectorFoReadEventOnly;
	private MessageProtocolIF messageProtocol;
	private SocketResourceManagerIF socketResourceManager;
	
	public InputMessageReaderPool( 
			int poolSize, 
			int poolMaxSize, 
			String projectName,
			long wakeupIntervalOfSelectorFoReadEventOnly,
			MessageProtocolIF messageProtocol,
			SocketResourceManagerIF socketResourceManager,
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
		
		if (wakeupIntervalOfSelectorFoReadEventOnly < 0) {
			String errorMessage = String.format("the parameter wakeupIntervalOfSelectorFoReadEventOnly[%d] is less than zero", wakeupIntervalOfSelectorFoReadEventOnly); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		
		if (null == socketResourceManager) {
			throw new IllegalArgumentException("the parameter socketResourceManager is null");
		}
		
		if (null == ieoThreadPoolManager) {
			throw new IllegalArgumentException("the parameter ieoThreadPoolManager is null");
		}
		
		this.poolMaxSize = poolMaxSize;
		this.projectName = projectName;
		this.wakeupIntervalOfSelectorFoReadEventOnly = wakeupIntervalOfSelectorFoReadEventOnly;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;	
		
		// ieoThreadPoolManager.setInputMessageReaderPool(this);

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
		synchronized (monitor) {
			int size = pool.size();
			
			if (size >= poolMaxSize) {
				String errorMessage = new StringBuilder("can't add a InputMessageReader in the project[")
						.append(projectName)
						.append("] becase the number of InputMessageReader is maximum[")
						.append(poolMaxSize)
						.append("]").toString();
				
				log.warn(errorMessage);
				throw new IllegalStateException(errorMessage);
			}
			
			try {
				InputMessageReaderIF handler = new InputMessageReader(projectName, 
						size, 
						wakeupIntervalOfSelectorFoReadEventOnly, 
						messageProtocol,  
						socketResourceManager);
				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("failed to add a InputMessageReader in the project[")
						.append(projectName)
						.append("], errmsg=")
						.append(e.getMessage()).toString();  
				
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}
		}
	}	
	
	@Override
	public InputMessageReaderIF getInputMessageReaderWithMinimumNumberOfSockets() {
		if (pool.isEmpty()) {
			throw new NoSuchElementException("InputMessageReaderPool empty");
		}
		
		int min = Integer.MAX_VALUE;
		InputMessageReaderIF minInputMessageReader = null;
				
		for (InputMessageReaderIF handler : pool) {
			int numberOfSocket = handler.getNumberOfConnection();
			if (numberOfSocket < min) {
				min = numberOfSocket;
				minInputMessageReader = handler;
			}
		}
		
		return minInputMessageReader;
	}
	
	@Override
	public int getPoolSize() {
		return pool.size();
	}

	@Override
	public void startAll() {
		for (InputMessageReaderIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.start();
		}
	}

	@Override
	public void stopAll() {
		for (InputMessageReaderIF handler: pool) {			
			if (handler.isAlive()) continue;
			handler.interrupt();
		}
	}
	
	public String getPoolState() {
		StringBuilder pollStateStringBuilder = new StringBuilder();
		
		int total = 0;
		
		for (InputMessageReaderIF handler : pool) {
			int numberOfSocket = handler.getNumberOfConnection();
			
			pollStateStringBuilder.append(", ");
			pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);			
			pollStateStringBuilder.append("InputMessageReader[");			
			pollStateStringBuilder.append(handler.getIndex());
			pollStateStringBuilder.append("].numberOfSocket=");
			pollStateStringBuilder.append(numberOfSocket);
			total += numberOfSocket;
		}
		pollStateStringBuilder.append(", ");
		pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);		
		pollStateStringBuilder.append("InputMessageReader.sumOfSocket=");
		pollStateStringBuilder.append(total);
		return pollStateStringBuilder.toString();
	}
}