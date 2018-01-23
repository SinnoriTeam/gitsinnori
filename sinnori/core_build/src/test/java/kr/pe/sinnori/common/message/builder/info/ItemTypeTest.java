package kr.pe.sinnori.common.message.builder.info;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class ItemTypeTest {
	
	Logger log = null;

	final String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	final String mainProjectName = "sample_base";

	@Before
	public void setup() {
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

		log = LoggerFactory.getLogger(ItemTypeTest.class);
	}

	@Test
	public void test_ordinal와itemTypeID같은지검사() {
		SingleItemType singleItemType = SingleItemType.UNSIGNED_SHORT;
		
		assertEquals("ItemTypeID 를 순차적으로 잘 정의했는지  테스트", singleItemType.ordinal(), singleItemType.getItemTypeID());
	}
	
	
	@Test
	public void testGetItemTypeID_ItemTypeID를순차적으로잘정의했는지검사() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType singleItemType = singleItemTypes[i];
			assertEquals("ItemTypeID 를 순차적으로 잘 정의했는지  테스트", i, singleItemType.getItemTypeID());
		}
	}
	
	
	@Test
	public void test() {
		SingleItemType singleItemType = SingleItemType.UNSIGNED_SHORT;
		
		log.info("singleItemType.name={}", singleItemType.name());
	}
}
