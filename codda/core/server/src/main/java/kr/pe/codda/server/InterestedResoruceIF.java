package kr.pe.codda.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface InterestedResoruceIF {
	public SelectionKey keyFor(Selector ioEventSelector);
	public void onRead(SelectionKey selectedKey) throws InterruptedException;
	public void onWrite(SelectionKey selectedKey) throws InterruptedException;
	public void close() throws IOException;
	public void releaseResources();
	public int hashCode();
}
