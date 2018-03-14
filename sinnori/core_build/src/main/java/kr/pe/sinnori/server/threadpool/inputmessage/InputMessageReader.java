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

package kr.pe.sinnori.server.threadpool.inputmessage;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.asyn.FromLetter;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.server.SocketResource;
import kr.pe.sinnori.server.SocketResourceManagerIF;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorIF;

public class InputMessageReader extends Thread implements InputMessageReaderIF {
	private Logger log = LoggerFactory.getLogger(InputMessageReader.class);

	// private final Object monitor = new Object();

	private String projectName = null;
	private int index;
	private long wakeupIntervalOfSelectorForReadEventOnley;
	private MessageProtocolIF messageProtocol = null;

	private SocketResourceManagerIF socketResourceManager = null;

	// private final ArrayDeque<SocketChannel> notRegistedSocketChannelList = new
	// ArrayDeque<SocketChannel>();
	private final Set<SocketChannel> notRegistedSocketChannelList = Collections
			.synchronizedSet(new HashSet<SocketChannel>());

	private Selector selectorForReadEventOnly = null;

	public InputMessageReader(String projectName, int index, long wakeupIntervalOfSelectorForReadEventOnley,
			MessageProtocolIF messageProtocol,
			SocketResourceManagerIF socketResourceManager) {
		this.index = index;
		this.wakeupIntervalOfSelectorForReadEventOnley = wakeupIntervalOfSelectorForReadEventOnley;
		this.projectName = projectName;
		this.messageProtocol = messageProtocol;
		this.socketResourceManager = socketResourceManager;

		try {
			selectorForReadEventOnly = Selector.open();
		} catch (IOException ioe) {
			log.error(String.format("RequetProcessor[%d] selector open fail", index), ioe);
			System.exit(1);
		}

	}

	@Override
	public void addNewSocket(SocketChannel newSC) throws InterruptedException {
		// clientResourceManager.addNewSocketChannel(newSocketChannelToRegisterWithReadOnlySelector);
		// synchronized (monitor) {
		notRegistedSocketChannelList.add(newSC);
		// }

		// waitingSCQueue.put(sc);

		if (getState().equals(Thread.State.NEW)) {
			return;
		}
		
		boolean loop = false;
		do {
			selectorForReadEventOnly.wakeup();

			try {
				Thread.sleep(wakeupIntervalOfSelectorForReadEventOnley);
			} catch (InterruptedException e) {
				log.info("give up the test checking whether the new socket[{}] is registered with the Selector because the socket has occurred", newSC.hashCode());
				throw e;
			}
			if (! newSC.isOpen()) {
				log.info("give up the test checking whether the new socket[{}] is registered with the Selector because the socket is not open", newSC.hashCode());
				return;
			}
			
			if (! newSC.isConnected()) {
				log.info("give up the test checking whether the new socket[{}] is registered with the Selector because the socket is not connected", newSC.hashCode());
				return;
			}
			
			loop = ! newSC.isRegistered();
			
		} while (loop);
		
		log.debug("{} InputMessageReader[{}] new newSC[{}] added", projectName, index, newSC.hashCode());
	}

	@Override
	public int getNumberOfSocket() {
		return (notRegistedSocketChannelList.size() + selectorForReadEventOnly.keys().size());
	}

	/**
	 * 미 등록된 소켓 채널들을 selector 에 등록한다.
	 */
	private void processNewConnection() {
		Iterator<SocketChannel> notRegistedSocketChannelIterator = notRegistedSocketChannelList.iterator();

		while (notRegistedSocketChannelIterator.hasNext()) {
			SocketChannel notRegistedSocketChannel = notRegistedSocketChannelIterator.next();
			notRegistedSocketChannelIterator.remove();

			try {
				notRegistedSocketChannel.register(selectorForReadEventOnly, SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				log.warn("{} InputMessageReader[{}] socket channel[{}] fail to register selector", projectName, index,
						notRegistedSocketChannel.hashCode());

				SocketResource failedSocketResource = socketResourceManager.getSocketResource(notRegistedSocketChannel);
				if (null == failedSocketResource) {
					log.warn("this scToAsynConnectionHash contains no mapping for the key[{}] that is the socket channel failed to be registered with the given selector", notRegistedSocketChannel.hashCode());
				} else {
					failedSocketResource.close();
					socketResourceManager.remove(notRegistedSocketChannel);
				}
				
			}
		}
	}

	@Override
	public void run() {
		log.info(String.format("%s InputMessageReader[%d] start", projectName, index));

		int numRead = 0;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				processNewConnection();
				int selectionKeyCount = selectorForReadEventOnly.select();

				if (selectionKeyCount > 0) {
					Set<SelectionKey> selectedKeySet = selectorForReadEventOnly.selectedKeys();
					Iterator<SelectionKey> selectionKeyIterator = selectedKeySet.iterator();
					while (selectionKeyIterator.hasNext()) {
						SelectionKey selectedKey = selectionKeyIterator.next();
						selectionKeyIterator.remove();
						SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();
						// ByteBuffer lastInputStreamBuffer = null;
						SocketResource fromSocketResource = socketResourceManager
								.getSocketResource(selectedSocketChannel);

						if (null == fromSocketResource) {
							log.warn(String.format(
									"%s InputMessageReader[%d] socket channel[%d] is no match for ClientResource",
									projectName, index, selectedSocketChannel.hashCode()));
							continue;
						}

						SocketOutputStream fromSocketOutputStream = fromSocketResource.getSocketOutputStream();
						ServerExecutorIF fromExecutor = fromSocketResource.getExecutor();

						try {
							/*
							 * lastInputStreamBuffer = clientSocketInputStream.getLastDataPacketBuffer();
							 * 
							 * do { numRead = readableSocketChannel.read(lastInputStreamBuffer); if (numRead
							 * < 1) break;
							 * 
							 * if (!lastInputStreamBuffer.hasRemaining()) { if
							 * (!clientSocketInputStream.canNextDataPacketBuffer()) break;
							 * lastInputStreamBuffer = clientSocketInputStream.nextDataPacketBuffer(); } }
							 * while(true);
							 */

							numRead = fromSocketOutputStream.read(selectedSocketChannel);

							if (numRead == -1) {								
								log.warn("{} InputMessageReader[{}] this socket channel[{}] has reached end-of-stream",
												projectName, index, selectedSocketChannel.hashCode());
								closeClient(selectedKey, fromSocketResource);
								continue;
							}
							
							fromSocketResource.setFinalReadTime();

							List<WrapReadableMiddleObject> wrapReadableMiddleObjectList = messageProtocol
									.S2MList(fromSocketOutputStream);
							
							Iterator<WrapReadableMiddleObject> wrapReadableMiddleObjectIterator =
									wrapReadableMiddleObjectList.iterator();
							
							try {
								while (wrapReadableMiddleObjectIterator.hasNext()) {
									WrapReadableMiddleObject wrapReadableMiddleObject = wrapReadableMiddleObjectIterator.next();
									
									fromExecutor
											.putIntoQueue(new FromLetter(selectedSocketChannel, wrapReadableMiddleObject));
									
									
								}
							} catch(InterruptedException e) {
								while (wrapReadableMiddleObjectIterator.hasNext()) {
									WrapReadableMiddleObject wrapReadableMiddleObject = wrapReadableMiddleObjectIterator.next();
									
									log.info("drop the input message[{}] becase of InterruptedException", wrapReadableMiddleObject.toString());
									
									wrapReadableMiddleObject.closeReadableMiddleObject();								
								}
								throw e;
							}

						} catch (NoMoreDataPacketBufferException e) {
							String errorMessage = String.format(
									"%s InputMessageReader[%d] NoMoreDataPacketBufferException::%s", projectName, index,
									e.getMessage());
							log.warn(errorMessage, e);
							closeClient(selectedKey, fromSocketResource);
							continue;						
						} catch (IOException e) {
							String errorMessage = String.format("%s InputMessageReader[%d] IOException::%s",
									projectName, index, e.getMessage());
							log.warn(errorMessage, e);
							closeClient(selectedKey, fromSocketResource);
							continue;
						}
					}
				}
			}

			log.warn("{} InputMessageReader[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} InputMessageReader[{}] stop", projectName, index);			
		} catch (Exception e) {
			String errorMessage = String.format("%s InputMessageReader[%d] unknown error", projectName, index); 
			log.warn(errorMessage, e);
		}
	}

	private void closeClient(SelectionKey selectedKey, SocketResource fromSocketResource) {
		SocketChannel selectedSocketChannel = (SocketChannel) selectedKey.channel();

		log.info("close the socket[{}]", selectedSocketChannel.hashCode());

		selectedKey.cancel();

		try {
			selectedSocketChannel.close();
		} catch (IOException e) {
			log.warn("fail to close the socket[{}]", selectedSocketChannel.hashCode());
		}

		fromSocketResource.close();

		socketResourceManager.remove(selectedSocketChannel);
	}
}