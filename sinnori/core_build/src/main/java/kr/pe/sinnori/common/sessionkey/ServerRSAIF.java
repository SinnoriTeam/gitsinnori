package kr.pe.sinnori.common.sessionkey;

import kr.pe.sinnori.common.exception.SymmetricException;

public interface ServerRSAIF {
	public byte[] getDupPublicKeyBytes();
	public byte[] getClientSymmetricKeyBytes(byte[] sessionKeyBytes) throws SymmetricException;
	public String getModulusHexStrForWeb();
}
