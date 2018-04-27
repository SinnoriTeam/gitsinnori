package kr.pe.sinnori.server.threadpool.executor;

import java.nio.channels.SocketChannel;

import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public interface ServerExecutorIF {
	public void addNewSocket(SocketChannel newSC);
	public int getNumberOfConnection();
	public void removeSocket(SocketChannel sc);	
	public void putIntoQueue(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException;
	public boolean isAlive();
	public void start();
	public void interrupt();
}
