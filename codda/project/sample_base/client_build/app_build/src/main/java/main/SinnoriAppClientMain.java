package main;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class SinnoriAppClientMain {

	public static void main(String[] args) {
		InternalLogger log = InternalLoggerFactory.getInstance("kr.pe.sinnori");

		log.info("start");

		/*ConnectionPoolManager connectionPoolManager = ConnectionPoolManager.getInstance();

		AnyProjectConnectionPoolIF mainProjectConnectionPool = connectionPoolManager.getMainProjectConnectionPool();

		java.util.Random random = new java.util.Random();

		long beforeTime = 0;
		long afterTime = 0;

		int retryCount = 1000000;

		beforeTime = System.currentTimeMillis();

		for (int i = 0; i < retryCount; i++) {
			Echo echoInObj = new Echo();
			echoInObj.setRandomInt(random.nextInt());
			echoInObj.setStartTime(new java.util.Date().getTime());

			AbstractMessage messageFromServer = null;
			try {
				messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(echoInObj);

				if (messageFromServer instanceof Echo) {
					Echo echoOutObj = (Echo) messageFromServer;
					if ((echoInObj.getRandomInt() == echoOutObj.getRandomInt())
							&& (echoInObj.getStartTime() == echoOutObj.getStartTime())) {
						// log.info("성공::echo 메시지 입력/출력 동일함");
					} else {
						log.info("실패::echo 메시지 입력/출력 다름");
					}
				} else {
					log.warn("messageFromServer={}", messageFromServer.toString());
				}
			} catch (Exception e) {
				log.warn("SocketTimeoutException", e);
			}
		}

		afterTime = System.currentTimeMillis();

		log.info("loop count[{}], elapsed time[{} ms], average time[{} ms]", retryCount, (afterTime - beforeTime),
				(double) (afterTime - beforeTime) / retryCount);*/

	}
}