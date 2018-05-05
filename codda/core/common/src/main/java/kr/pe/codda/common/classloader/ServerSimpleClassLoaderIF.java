package kr.pe.codda.common.classloader;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.protocol.MessageCodecIF;

public interface ServerSimpleClassLoaderIF {
	public MessageCodecIF getMessageCodec(String messageID) throws DynamicClassCallException;
}
