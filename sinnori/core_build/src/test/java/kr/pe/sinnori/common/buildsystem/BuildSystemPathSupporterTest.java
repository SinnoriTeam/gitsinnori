package kr.pe.sinnori.common.buildsystem;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;

public class BuildSystemPathSupporterTest extends AbstractJunitTest {
	
	
	@Test
	public void testGetProjectBasePathString() {
		/*java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd:HHmmss");
		sdf.format(new java.util.Date());*/
		
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectBasePathString(sinnoriInstalledPathString);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetProjectPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getProjectPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetSinnoriConfigPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\config";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getSinnoriConfigPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetSinnoriConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\config\\sinnori.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetLogbackConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\logback.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		String returnedValue = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetMessageInfoPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\message_info";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getMessageInfoFilesPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetWebClientAntPropertiesFilePath() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build\\webAnt.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}	
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntPropertiesFilePath(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	
	@Test
	public void testGetSessionKeyRSAKeypairPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\rsa_keypair";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getSessionKeyRSAKeypairPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetServerBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetServerBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build\\build.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetAPPINFPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\server_build\\APP-INF";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getServerAPPINFPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetDBCPConnPoolConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\resources\\dbcp\\dbcp.sample_base_db.properties";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getDBCPConfigFilePathString(sinnoriInstalledPathString, mainProjectName, "sample_base_db");
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetClientBuildBasePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getClientBuildBasePathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetAppClientBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\app_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetAppClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\app_build\\build.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getAppClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetWebClientBuildPathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		// FIXME!
		log.info(expectedValue);
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientBuildPathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
		
	@Test
	public void testGetWebClientBuildConfigFilePathString() {
		String mainProjectName = "sample_base";
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String expectedValue = "D:\\gitsinnori\\sinnori\\project\\sample_base\\client_build\\web_build\\build.xml";
		
		if (!(new File(sinnoriInstalledPathString)).exists()) {
			fail("the sinnori installed path doesn't exist");
		}
		if (!(new File(expectedValue)).exists()) {
			fail("the file(=the variable expectedValue) doesn't exist");
		}
		
		
		String returnedValue = BuildSystemPathSupporter
				.getWebClientAntBuildXMLFilePathString(sinnoriInstalledPathString, mainProjectName);
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
}
