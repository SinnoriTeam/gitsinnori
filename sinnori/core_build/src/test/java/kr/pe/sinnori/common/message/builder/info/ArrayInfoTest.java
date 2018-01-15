package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.fail;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.SinnoriLogbackManger;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.message.builder.info.ArrayInfo;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayInfoTest {
	private Logger log = LoggerFactory
			.getLogger(ArrayInfoTest.class);
	
	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);		
		

		SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);
	}

	@Test
	public void testConstructor_배열크기직접입력방식_문자인배열크기() {
		String testTitle = "배열 크기 직접 입력 방식_문자인 배열 크기";	
				
		String arrayName = "member";
		String arrayCntType = "direct";
		String arrayCntValue = "hello";
		
		String expectedMessage  = new StringBuilder("fail to parses the string argument(=this array item[")
		.append(arrayName).append("]'s attribute 'cntvalue' value[")
		.append(arrayCntValue)
		.append("]) as a signed decimal integer").toString();
		
		@SuppressWarnings("unused")
		ArrayInfo arrayInfo = null;
		try {
			arrayInfo = new ArrayInfo(arrayName, arrayCntType,
				arrayCntValue);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage)) return;
		}
		
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}

	
	@Test
	public void testConstructor_배열크기직접입력방식_0보다작은배열크기() {
		String testTitle = "배열 크기 직접 입력 방식_0보다 작은 배열 크기";	
				
		String arrayName = "member";
		String arrayCntType = "direct";
		String arrayCntValue = "-2";
		
		String expectedMessage  = new StringBuilder("this array item[")
		.append(arrayName).append("]'s attribute 'cntvalue' value[")
		.append(arrayCntValue)
		.append("] is less than or equals to zero").toString();
		
		@SuppressWarnings("unused")
		ArrayInfo arrayInfo = null;
		try {
			arrayInfo = new ArrayInfo(arrayName, arrayCntType,
				arrayCntValue);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage)) return;
		}
		
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}
	
	@Test
	public void testConstructor_잘못된배열크기방식지정() {
		String testTitle = "잘못된배열크기방식지정";	
				
		String arrayName = "member";
		String arrayCntType = "direct2";
		String arrayCntValue = "-2";
		
		String expectedMessage  = new StringBuilder("this array item[")
		.append(arrayName).append("]'s attribute 'cnttype' value[")
		.append(arrayCntType)
		.append("] is not an element of direction set[direct, reference]").toString();
		
		@SuppressWarnings("unused")
		ArrayInfo arrayInfo = null;
		try {
			arrayInfo = new ArrayInfo(arrayName, arrayCntType,
				arrayCntValue);
		} catch (IllegalArgumentException e) {
			log.warn(e.toString(), e);
			String errorMessage = e.getMessage();
			if (errorMessage.equals(expectedMessage)) return;
		}
		
		fail(new StringBuilder("'").append(testTitle).append("' test failed").toString());
	}
}
