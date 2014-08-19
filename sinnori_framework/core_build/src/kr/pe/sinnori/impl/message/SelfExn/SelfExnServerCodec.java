package kr.pe.sinnori.impl.message.SelfExn;

import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;

public final class SelfExnServerCodec implements MessageCodecIF {

	@Override
	public MessageDecoder getMessageDecoder() throws DynamicClassCallException {
		throw new DynamicClassCallException("SelfExn 메시지는 클라이언트에서 서버로 전달하지 않는 메시지 입니다.");
	}

	@Override
	public MessageEncoder getMessageEncoder() throws DynamicClassCallException {
		return CommonStaticFinalVars.SELFEXN_ENCODER;
	}
	
}
