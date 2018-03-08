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

package kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriter;
import kr.pe.sinnori.client.connection.asyn.threadpool.inputmessage.handler.InputMessageWriterIF;
import kr.pe.sinnori.common.asyn.ToLetter;
import kr.pe.sinnori.common.threadpool.AbstractThreadPool;

/**
 * 클라이언트 입력 메시지 소켓 쓰기 담당 쓰레드 폴.
 * 
 * @author Won Jonghoon
 */
public class InputMessageWriterPool extends AbstractThreadPool implements InputMessageWriterPoolIF {
	private String projectName = null;
	private int inputMessageQueueSize;
	private ClientMessageUtilityIF clientMessageUtility = null;
	private long socketTimeOut;

	public InputMessageWriterPool(String projectName, int size,
			int inputMessageQueueSize, ClientMessageUtilityIF clientMessageUtility,
			long socketTimeOut) {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("%s 파라미터 size 는 0보다 커야 합니다.", projectName));
		}

		this.projectName = projectName;
		this.inputMessageQueueSize = inputMessageQueueSize;
		this.clientMessageUtility = clientMessageUtility;
		this.socketTimeOut = socketTimeOut;

		for (int i = 0; i < size; i++) {
			addHandler();
		}
	}

	@Override
	public void addHandler() throws IllegalStateException {
		ArrayBlockingQueue<ToLetter> inputMessageQueue = new ArrayBlockingQueue<ToLetter>(inputMessageQueueSize);

		synchronized (monitor) {
			int size = pool.size();

			try {
				Thread handler = new InputMessageWriter(projectName, size, inputMessageQueue,
						clientMessageUtility, socketTimeOut);

				pool.add(handler);
			} catch (Exception e) {
				String errorMessage = String.format("%s InputMessageWriter[%d] 등록 실패", projectName, size);
				log.warn(errorMessage, e);
				throw new IllegalStateException(errorMessage);
			}

		}
	}

	@Override
	public InputMessageWriterIF getInputMessageWriterWithMinimumNumberOfConnetion() {
		Iterator<Thread> poolIter = pool.iterator();
		if (! poolIter.hasNext()) {
			throw new NoSuchElementException("InputMessageWriterPool empty");
		}
		
		int min = Integer.MAX_VALUE;
		InputMessageWriterIF minInputMessageWriter = (InputMessageWriterIF) poolIter.next();
		min = minInputMessageWriter.getNumberOfAsynConnection();

		while (poolIter.hasNext()) {
			InputMessageWriterIF inputMessageWriter = (InputMessageWriterIF) poolIter.next();
			int numberOfAsynConnection = inputMessageWriter.getNumberOfAsynConnection();
			if (numberOfAsynConnection < min) {
				minInputMessageWriter = inputMessageWriter;
				min = numberOfAsynConnection;
			}
		}

		return minInputMessageWriter;
	}

}
