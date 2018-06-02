package kr.pe.codda.client.connection.asyn.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;

public class ClientExecutor extends Thread implements ClientExecutorIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ClientExecutor.class);

	private String projectName = null;
	private int index;

	private ArrayBlockingQueue<ReadableMiddleObjectWrapper> outputMessageQueue = null;

	private ClientMessageUtilityIF clientMessageUtility = null;

	private final ConcurrentHashMap<AsynConnectionIF, AsynConnectionIF> ioeAsynConnectionHash = 
			new ConcurrentHashMap<AsynConnectionIF, AsynConnectionIF>();

	public ClientExecutor(String projectName, int index, 
			ArrayBlockingQueue<ReadableMiddleObjectWrapper> outputMessageQueue,
			ClientMessageUtilityIF clientMessageUtility) {
		this.projectName = projectName;
		this.index = index;
		this.outputMessageQueue = outputMessageQueue;
		this.clientMessageUtility = clientMessageUtility;
	}

	public void run() {
		log.info("{} ClientExecutor[{}] start", projectName, index);

		try {
			while (!Thread.currentThread().isInterrupted()) {
				ReadableMiddleObjectWrapper readableMiddleObjectWrapper = outputMessageQueue.take();

				Object eventHandler = readableMiddleObjectWrapper.getEventHandler();
				
				AsynConnectionIF asynConnection = (AsynConnectionIF)eventHandler;
				String messageID = readableMiddleObjectWrapper.getMessageID();

				AbstractClientTask clientTask = clientMessageUtility.getClientTask(messageID);

				clientTask.execute(index, projectName, asynConnection, readableMiddleObjectWrapper, clientMessageUtility);
			}
			log.warn("{} ClientExecutor[{}] loop exit", projectName, index);
		} catch (InterruptedException e) {
			log.warn("{} ClientExecutor[{}] stop", projectName, index);
		} catch (Exception e) {
			String errorMessage = new StringBuilder(projectName).append(" ClientExecutor[").append(index)
					.append("] unknown error::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
		}
	}

	@Override
	public void registerAsynConnection(AsynConnectionIF asynConnection) {
		// log.info("add asynConnection[{}]", asynConnection.hashCode());
		ioeAsynConnectionHash.put(asynConnection, asynConnection);
		
		log.debug("{} ClientExecutor[{}] new AsynConnection[{}][{}] added", projectName, index, asynConnection.hashCode());
	}

	@Override
	public int getNumberOfConnection() {
		return ioeAsynConnectionHash.size();
	}

	@Override
	public void removeAsynConnection(AsynConnectionIF asynConnection) {
		ioeAsynConnectionHash.remove(asynConnection);
	}

	@Override
	public void putAsynOutputMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper) throws InterruptedException {
		
		
		outputMessageQueue.put(readableMiddleObjectWrapper);
	}	

	public void finalize() {
		log.warn("{} ClientExecutor[{}] finalize", projectName, index);
	}
}