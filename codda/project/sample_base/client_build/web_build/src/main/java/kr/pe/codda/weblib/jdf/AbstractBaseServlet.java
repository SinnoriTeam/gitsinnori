package kr.pe.codda.weblib.jdf;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

@SuppressWarnings("serial")
public abstract class AbstractBaseServlet extends HttpServlet {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractBaseServlet.class);

	/**
	 * 어드민의 로그인 여부를 반환한다.
	 * @param req HttpServletRequest 객체
	 * @return 로그인 여부
	 */		
	public boolean isAdminLogin(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 어드민의 로그인 여부를 반환한다.
	 * @param httpSession HttpSession 객체
	 * @return 로그인 여부
	 */		
	public boolean isAdminLogin(HttpSession httpSession) {
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	
	
	
	/**
	 * 어드민의 로그인 아이디를 반환한다. 단 로그인을 안했을 경우 손님을 뜻하는 guest 아이디로 고정된다.
	 * @param req HttpServletRequest 객체
	 * @return 로그인 아이디
	 */
	public String getLoginedAdminID(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		
		Object loginUserIDValue = httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID);
		if (null == loginUserIDValue) {
			return "guest";
		}
		
		String loginUserID = (String) loginUserIDValue;
		if (loginUserID.equals("")) {
			loginUserID = "guest";
		}
		
		return loginUserID;
	}
	
	public boolean isUserLogin(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USERID);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	public boolean isUserLogin(HttpSession httpSession) {
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USERID);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	public String getLoginedUserID(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		
		Object loginUserIDValue = httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USERID);
		if (null == loginUserIDValue) {
			return "guest";
		}
		
		String loginUserID = (String) loginUserIDValue;
		if (loginUserID.equals("")) {
			loginUserID = "guest";
		}
		
		return loginUserID;
	}
	
	/**
	 * 파일명이 겹치지 않기 위해서 DB 를 이용한 시퀀스 값인 업로드 파일 이름 순번을 받아 업로드 파일의 시스템 절대 경로 파일명을 반환한다.
	 * @param uploadFileNameSeq 파일명이 겹치지 않기 위해서 DB 를 이용한 시퀀스 값인 업로드 파일 이름 순번
	 * @return 업로드 파일의 시스템 절대 경로 파일명
	 */
	public String getAttachSystemFullFileName(long uploadFileNameSeq) {
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		
		String mainProjectName = runningProjectConfiguration.getMainProjectName();
		String sinnoriInstalledPathString = runningProjectConfiguration.getInstalledPathString();
		String webUploadPathString = WebRootBuildSystemPathSupporter.getWebUploadPathString(sinnoriInstalledPathString, mainProjectName);
		
		StringBuilder attachSystemFullFileNameBuilder = new StringBuilder(webUploadPathString);
		attachSystemFullFileNameBuilder.append(File.separator);
		attachSystemFullFileNameBuilder.append(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_PREFIX);
		attachSystemFullFileNameBuilder.append("_");
		attachSystemFullFileNameBuilder.append(uploadFileNameSeq);
		attachSystemFullFileNameBuilder.append(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_SUFFIX);
		
		return attachSystemFullFileNameBuilder.toString();
	}
}
