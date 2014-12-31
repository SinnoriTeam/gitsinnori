/**
 * 
 * @(#) .java
 * Copyright 1999-2000 by  LG-EDS Systems, Inc.,
 * Information Technology Group, Application Architecture Team, 
 * Application Intrastructure Part.
 * 236-1, Hyosung-2dong, Kyeyang-gu, Inchun, 407-042, KOREA.
 * All rights reserved.
 *  
 * NOTICE !      You can copy or redistribute this code freely, 
 * but you should not remove the information about the copyright notice 
 * and the author.
 *  
 * @author  WonYoung Lee, wyounglee@lgeds.lg.co.kr.
 */

package kr.pe.sinnori.common.weblib;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * <pre>
 * 암호화 페이지를 보여주고자 할때 상속 받아야할 추상화 클래스로 암호화에 필요한 사용자 session key 와 iv 를 요구한다.
 * iv 값은 대칭키 특성상 동일 원문에 대한 암호문이 같지 않도록 도와주는 랜덤 값이다.
 * 
 * 복사자&수정자 : Won Jonghoon
 * 복사&수정 내용 : 응용에 따라 약간 수정
 * </pre>
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractSessionKeyServlet extends AbstractServlet {	

	
	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		// HttpSession session = req.getSession();
		
		String parmSessionKeyBase64 = req.getParameter("sessionkeyBase64");
		String parmIVBase64 = req.getParameter("ivBase64");
		
		
		log.info(String.format("parm parmSessionKeyBase64=[%s]", parmSessionKeyBase64));
		log.info(String.format("parm parmIVBase64=[%s]", parmIVBase64));
		
		if (null == parmSessionKeyBase64 || null == parmIVBase64) {
			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager
					.getInstance();
			String modulusHex = sessionKeyServerManger.getModulusHexStrForWeb();
			
			
			String requestURI = req.getRequestURI();
			
			StringBuilder pageStrBuilder = new StringBuilder("<!DOCTYPE html><html><head>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<title>");
			pageStrBuilder.append(WebCommonStaticFinalVars.WEBSITE_TITLE);
			pageStrBuilder.append("</title>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/jsbn.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/jsbn2.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/prng4.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/rng.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/rsa.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/jsbn/rsa2.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/rollups/sha256.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/rollups/aes.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/components/core-min.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("<script type=\"text/javascript\" src=\"/js/cryptoJS/components/cipher-core-min.js\"></script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("<script type=\"text/javascript\">");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\tfunction goURL(targeturl) {");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\tif (typeof(sessionStorage) == 'undefined' ) {");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\talert('당신의 브라우저는 HTML5 sessionStorage 를 지원하지 않습니다. 브라우저를 업그레이드하세요.');");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\treturn;");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t}");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\tvar webUserPrivateKeyBase64 = sessionStorage.getItem('");
			pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME);
			pageStrBuilder.append("');");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			// FIXME! 웹 유저 비밀키(=대칭키)
			// pageStrBuilder.append("\t\talert(sinnoriPrivateKey);");
			// pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\tif (null == webUserPrivateKeyBase64 || '' == webUserPrivateKeyBase64) {");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\ttry {");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t\tvar webUserPrivateKey = CryptoJS.lib.WordArray.random(");
			pageStrBuilder.append(WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE);
			pageStrBuilder.append(");");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			
			pageStrBuilder.append("\t\t\t\tsessionStorage.setItem('");
			pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME);
			pageStrBuilder.append("', CryptoJS.enc.Base64.stringify(webUserPrivateKey)); // key-value 형식으로 저장");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			
					
			pageStrBuilder.append("\t\t\t\tvar rsa = new RSAKey();");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t\trsa.setPublic(\"");
			pageStrBuilder.append(modulusHex);
			pageStrBuilder.append("\", \"10001\");");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t\tvar sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(webUserPrivateKey));");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t\tsessionStorage.setItem('");
			pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME);
			pageStrBuilder.append("', CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex))); // key-value 형식으로 저장");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t} catch (e) {");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			
			
			pageStrBuilder.append("\t\t\t\t\tsessionStorage.removeItem('");
			pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME);
			pageStrBuilder.append("');");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t\t\tsessionStorage.removeItem('");
			pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME);
			pageStrBuilder.append("');");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t\t\talert(e);");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t\t\t\treturn;");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			
			
			pageStrBuilder.append("\t\t\t}");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\t}");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\tvar g = document.gofrm;");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("\t\tg.action = targeturl;");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);

			pageStrBuilder.append("\t\tg.sessionkeyBase64.value = sessionStorage.getItem('");
			pageStrBuilder.append(WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME);
			pageStrBuilder.append("');");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\tvar iv = CryptoJS.lib.WordArray.random(");
			pageStrBuilder.append(WebCommonStaticFinalVars.WEBSITE_IV_SIZE);
			pageStrBuilder.append(");");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("\t\tg.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			// FIXME!
			pageStrBuilder.append("\t\tg.submit();");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("\t}");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("</script>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			pageStrBuilder.append("</head>");
			
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("<body onload=\"goURL('");
			pageStrBuilder.append(requestURI);
			pageStrBuilder.append("')\">");
			
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("<form name=gofrm method='post'>");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("<input type=hidden name=\"sessionkeyBase64\" />");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			pageStrBuilder.append("<input type=hidden name=\"ivBase64\" />");
			pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			
			
			Enumeration<String> parmEnum = req.getParameterNames();
			while(parmEnum.hasMoreElements()) {
				String parmName = parmEnum.nextElement();
				String parmValue = req.getParameter(parmName);
				
				pageStrBuilder.append("<input type=hidden name=\"");
				pageStrBuilder.append(StringEscapeUtils.escapeHtml4(parmName));
				
				
				pageStrBuilder.append("\" value=\"");
				pageStrBuilder.append(StringEscapeUtils.escapeHtml4(parmValue));
				pageStrBuilder.append("\" />");
				pageStrBuilder.append(CommonStaticFinalVars.NEWLINE);
			}
			
			pageStrBuilder.append("</body>");
			pageStrBuilder.append("</html>");

			// log.info(pageStrBuilder.toString());
			
			
			java.io.PrintWriter out = res.getWriter();
			out.write(pageStrBuilder.toString());
			out.close();
			return;
		}
		
		SymmetricKey  webUserSymmetricKey = null;
		try {
			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager.getInstance();
			webUserSymmetricKey = sessionKeyServerManger.getSymmetricKey(WebCommonStaticFinalVars.WEBSITE_JAVA_SYMMETRIC_KEY_ALGORITHM_NAME, CommonType.SymmetricKeyEncoding.BASE64, parmSessionKeyBase64, parmIVBase64);
		} catch(IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			
			String errorMessage = e.getMessage();
			
			
			printMessagePage(req, res, errorMessage, null);
			return;
		} catch(SymmetricException e) {
			log.warn("SymmetricException", e);
			
			String errorMessage = e.getMessage();
			
			printMessagePage(req, res, errorMessage, null);
			return;
		}			
		
		req.setAttribute("parmIVBase64", parmIVBase64);
		req.setAttribute("webUserSymmetricKey", webUserSymmetricKey);
		performTask(req,res);
	}
}