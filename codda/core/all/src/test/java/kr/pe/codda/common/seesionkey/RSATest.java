package kr.pe.codda.common.seesionkey;

import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientRSA;
import kr.pe.codda.common.sessionkey.ServerRSA;

public class RSATest extends AbstractJunitTest {
	
	
	@Test
	public void testRSAThreadSafe() {
		ServerRSA serverRSA = null;
		ClientRSA clientRSA = null;
		try {
			serverRSA = new ServerRSA();
			clientRSA = new ClientRSA(serverRSA.getDupPublicKeyBytes());
		} catch (SymmetricException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		
		
		int threadID = 0;
		RSATestThread rasTestThreadList[]  = {
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA),
				new RSATestThread(threadID++, serverRSA, clientRSA)
		};
		for (RSATestThread rasTestThread : rasTestThreadList) {
			rasTestThread.start();
		}
		
		try {
			Thread.sleep(1000L*60*2);
		} catch (InterruptedException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		for (RSATestThread rasTestThread : rasTestThreadList) {
			rasTestThread.interrupt();
		}
		
		while (! isAllTerminated(rasTestThreadList)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
				fail(e.getMessage());
			}
		}
		for (RSATestThread rasTestThread : rasTestThreadList) {
			if (rasTestThread.isError()) {
				fail(rasTestThread.getErrorMessage());
			}
		}		
	}
	
	private boolean isAllTerminated(RSATestThread rasTestThreadList[]) {
		for (RSATestThread rasTestThread : rasTestThreadList) {
			if (!rasTestThread.isTerminated()) {
				return false;
			}
		}
		return true;
	}
}
