package kr.pe.sinnori.client.connection.asyn.threadpool.executor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.asyn.IOEAsynConnectionIF;
import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public class ClientExecutor extends Thread implements ClientExecutorIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ClientExecutor.class);

	private String projectName = null;
	private int index;

	private ArrayBlockingQueue<WrapReadableMiddleObject> outputMessageQueue;

	private ClientMessageUtilityIF clientMessageUtility = null;

	private final ConcurrentHashMap<IOEAsynConnectionIF, IOEAsynConnectionIF> ioeAsynConnectionHash = 
			new ConcurrentHashMap<IOEAsynConnectionIF, IOEAsynConnectionIF>();

	public ClientExecutor(String projectName, int index, 
			ArrayBlockingQueue<WrapReadableMiddleObject> outputMessageQueue,
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
				WrapReadableMiddleObject wrapReadableMiddleObject = outputMessageQueue.take();

				SocketChannel fromSC = wrapReadableMiddleObject.getFromSC();
				// WrapReadableMiddleObject wrapReadableMiddleObject = fromLetter.getWrapReadableMiddleObject();
				String messageID = wrapReadableMiddleObject.getMessageID();

				AbstractClientTask clientTask = clientMessageUtility.getClientTask(messageID);

				clientTask.execute(index, projectName, fromSC, wrapReadableMiddleObject, clientMessageUtility);
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
	public void registerAsynConnection(IOEAsynConnectionIF asynConnection) {
		// log.info("add asynConnection[{}]", asynConnection.hashCode());
		ioeAsynConnectionHash.put(asynConnection, asynConnection);
		
		log.debug("{} ClientExecutor[{}] new AsynConnection[{}][{}] added", projectName, index, asynConnection.hashCode());
	}

	@Override
	public int getNumberOfConnection() {
		return ioeAsynConnectionHash.size();
	}

	@Override
	public void removeAsynConnection(IOEAsynConnectionIF asynConnection) {
		ioeAsynConnectionHash.remove(asynConnection);
	}

	@Override
	public void putAsynOutputMessage(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException {
		
		
		outputMessageQueue.put(wrapReadableMiddleObject);
	}	

	public void finalize() {
		log.warn("{} ClientExecutor[{}] finalize", projectName, index);
	}
}
