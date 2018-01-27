package kr.pe.sinnori.common.protocol.dhb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManager;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnDecoder;
import kr.pe.sinnori.impl.message.SelfExn.SelfExnEncoder;

public class DHBMessageProtocolTest {
	
	Logger log = null;

	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString,
				mainProjectName, logType);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH, sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		// SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString,
		// mainProjectName, logType);

		log = LoggerFactory.getLogger(DHBMessageProtocolTest.class);
	}
	
	
	@After
	public void finish() {
		System.gc();
	}
	
	@Test
	public void testM2S_basic() {
		int messageIDFixedSize = 25;
		int dataPacketBufferMaxCntPerMessage = 10;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = CharsetUtil.createCharsetEncoder(streamCharset);
		CharsetDecoder streamCharsetDecoder = CharsetUtil.createCharsetDecoder(streamCharset);
		ByteOrder streamByteOrder = ByteOrder.LITTLE_ENDIAN;
		DataPacketBufferPoolManagerIF dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 4096;
		int dataPacketBufferPoolSize = 100;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		DHBMessageProtocol dhbMessageProtocol = 
				new DHBMessageProtocol(messageIDFixedSize, 
						dataPacketBufferMaxCntPerMessage,
						streamCharsetEncoder,
						streamCharsetDecoder,
						dataPacketBufferPoolManager);
		
		SelfExnEncoder selfExnEncoder = new SelfExnEncoder();
		SelfExnDecoder selfExnDecoder = new SelfExnDecoder();
		DHBSingleItemDecoder dhbSingleItemDecoder = new DHBSingleItemDecoder(streamCharsetDecoder);
		
		
		// log.info("1");		
		long beforeTime = 0;
		long afterTime = 0;
		
		
		int retryCount = 1;
		
		int firstIndex = -1;
		int differentCount = 0;
		
		StringBuilder testStringBuilder = new StringBuilder();
		
		for (int i=0; i < 2500; i++) {
			testStringBuilder.append("한글");
		}
		
		SelfExn selfExnReq = new SelfExn();
		selfExnReq.setErrorPlace("sever");
		selfExnReq.setErrorGubun("B");
		selfExnReq.setErrorMessageID("Echo");
		selfExnReq.setErrorMessage(testStringBuilder.toString());	
		
		selfExnReq.messageHeaderInfo.mailboxID = 1;
		selfExnReq.messageHeaderInfo.mailID = 3;
		
		beforeTime= new Date().getTime();
		
		for (int i=0; i < retryCount; i++) {			
			long beforeLocalTime= new Date().getTime();			
			
			List<WrapBuffer> wrapBufferListOfInputMessage = null;
			try {
				wrapBufferListOfInputMessage = dhbMessageProtocol.M2S(selfExnReq, selfExnEncoder);
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			//log.info("2");
			
			for (WrapBuffer inputMessageWrapBuffer : wrapBufferListOfInputMessage) {
				if (inputMessageWrapBuffer.isInQueue()) {
					fail("bad wrap buffer where is in of queue");
				}
				
				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				inputMessageByteBuffer.position(inputMessageByteBuffer.limit());
			}
			
			//log.info("3");
			
			SocketOutputStream sos = null;
			try {
				sos = new SocketOutputStream(wrapBufferListOfInputMessage, streamCharsetDecoder, dataPacketBufferMaxCntPerMessage, dataPacketBufferPoolManager);
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			// log.info("sos.size={}", sos.size());
			
			//log.info("4");
			
			ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = null;
			try {
				wrapReadableMiddleObjectList = dhbMessageProtocol.S2MList(sos);
			} catch (Exception e) {
				String errorMessage = "error::"+e.getMessage();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			//log.info("5");
			
			for (WrapReadableMiddleObject wrapReadableMiddleObject :  wrapReadableMiddleObjectList) {
				Object readablemiddleObj = wrapReadableMiddleObject.getReadableMiddleObject();				
				
				try {
					AbstractMessage resObj = selfExnDecoder.decode(dhbSingleItemDecoder, readablemiddleObj);
					resObj.messageHeaderInfo.mailboxID = wrapReadableMiddleObject.getMailboxID();
					resObj.messageHeaderInfo.mailID = wrapReadableMiddleObject.getMailID();
					
					/*if (! (resObj instanceof SelfExn)) {
						fail("resObj is not a instance of SelfExn class");
					}*/
					
					SelfExn selfExnRes = (SelfExn)resObj;
					
					assertEquals("SelfExn 입력과 출력 메시지 비교", selfExnReq.toString(), selfExnRes.toString());
				} catch (Exception e) {
					String errorMessage = "error::"+e.getMessage();
					log.warn(errorMessage, e);
					fail(errorMessage);
				}
			}
			
			long afterLocalTime= new Date().getTime();
			if ((-1 == firstIndex) && (afterLocalTime == beforeLocalTime)) {
				firstIndex = i;
			}
			
			if (afterLocalTime != beforeLocalTime) {
				// log.info("case[{}]::afterLocalTime != beforeLocalTime", i);
				differentCount++;
			}
		}
		
		afterTime= new Date().getTime();
		
		log.info("{} 번 시간차={} ms, 평균={} ms, firstIndex={}, differentCount={}", retryCount, (afterTime-beforeTime), (double)(afterTime-beforeTime)/retryCount, firstIndex, differentCount);
	}
}

