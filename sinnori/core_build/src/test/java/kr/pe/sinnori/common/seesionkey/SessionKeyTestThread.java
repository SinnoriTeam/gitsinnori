package kr.pe.sinnori.common.seesionkey;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyIF;
import kr.pe.sinnori.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSymmetricKeyIF;

public class SessionKeyTestThread extends Thread {
	private Logger log = LoggerFactory.getLogger(SessionKeyTestThread.class);
	private int threadID = -1;
	
	private ServerSessionkeyIF mainProjectServerSessionkey = null;
	private ClientSessionKeyIF mainProjectClientSessionKey = null;
	
	
	private boolean isTerminated=false;
	private String errorMessage = null;
	
	public SessionKeyTestThread(int threadID, ServerSessionkeyIF mainProjectServerSessionkey, ClientSessionKeyIF mainProjectClientSessionKey) {
		this.threadID = threadID;
		
		this.mainProjectServerSessionkey = mainProjectServerSessionkey;
		this.mainProjectClientSessionKey = mainProjectClientSessionKey;
	}
	
	
	public void run() {
		Random random = new Random();
		random.setSeed(new Date().getTime());
		
		// log.info("threadID[{}] start", threadID);
		
		try {
			while (!Thread.currentThread().isInterrupted()) {
				// log.info("threadID[{}] running 111", threadID);
				
				String plainTextOfClient = new StringBuilder("hello한글").append(random.nextLong()).toString();
				
				byte [] plainTextBytesOfClient  = plainTextOfClient.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);				
				
								
				byte sessionKeyBytes[] = mainProjectClientSessionKey.getDupSessionKeyBytes();
				byte ivBytes[] = mainProjectClientSessionKey.getDupIVBytes();				
				ClientSymmetricKeyIF  clientSymmetricKey = mainProjectClientSessionKey.getClientSymmetricKey();
				
				ServerSymmetricKeyIF serverSymmetricKey = mainProjectServerSessionkey
				.getNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
				
				
				byte encryptedBytesOfClient[] = clientSymmetricKey.encrypt(plainTextBytesOfClient);
				byte decryptedBytesOfServer[] = serverSymmetricKey.decrypt(encryptedBytesOfClient);
				
				String plainTextOfServer = new StringBuilder("hello한글그림").append(random.nextLong()).toString();
				
				byte [] plainTextBytesOfServer  = plainTextOfServer.getBytes(CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);				
				byte encryptedBytesOfServer[] = serverSymmetricKey.encrypt(plainTextBytesOfServer);
			
				byte decryptedBytesOfClient[] = clientSymmetricKey.decrypt(encryptedBytesOfServer);				
				
				String decryptedTextOfServer = new String(decryptedBytesOfServer, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
				String decryptedTextOfClient = new String(decryptedBytesOfClient, CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
				
				if (!decryptedTextOfServer.equals(plainTextOfClient)) {
					errorMessage = String.format("In the SessionKeyTestThread[%d] the plain text[%s] of client is not same to the decrypted text[%s] of server", 
							threadID, plainTextOfClient, decryptedTextOfServer);
					log.warn(errorMessage);
					break;
				}
				
				if (!decryptedTextOfClient.equals(plainTextOfServer)) {
					errorMessage = String.format("In the SessionKeyTestThread[%d] the plain text[%s] of server is not same to the decrypted text[%s] of cleint", 
							threadID, plainTextOfServer, decryptedTextOfClient);
					log.warn(errorMessage);
					break;
				}
				
				// log.info("threadID[{}] running 222", threadID);
				
				Thread.sleep(random.nextInt(5)+5);
				
				// log.info("threadID[{}] running 333", threadID);
			}		
			
			log.info("the SessionKeyTestThread[{}] loop exist", threadID);
		} catch (InterruptedException e) {
			log.info("the SessionKeyTestThread[{}] was interrupted", threadID);
		} catch (Exception e) {
			errorMessage = String.format("the SessionKeyTestThread[%d]]'s error message=%s", threadID, e.getMessage());
			log.warn(errorMessage, e);
		} finally {
			isTerminated = true;
		}		
	}
	
	public boolean isTerminated() {
		return isTerminated;
	}
	
	public boolean isError() {
		return (errorMessage != null) ? true : false;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
