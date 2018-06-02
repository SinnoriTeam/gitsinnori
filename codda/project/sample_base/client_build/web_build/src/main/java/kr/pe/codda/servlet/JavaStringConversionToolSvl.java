package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;
import kr.pe.codda.weblib.sitemenu.SiteTopMenuType;

@SuppressWarnings("serial")
public class JavaStringConversionToolSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				SiteTopMenuType.TEST_EXAMPLE);
		
		String parmRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		if (null == parmRequestType) {
			firstPage(req, res);			
			return;
		}
		
		if (parmRequestType.equals("view")) {
			firstPage(req, res);
			return;
		} else if (parmRequestType.equals("proc")) {		
			processPage(req, res);
			return;
		} else {
			String errorMessage = "파라미터 '요청종류'의 값이 잘못되었습니다";
			String debugMessage = new StringBuilder("the web parameter \"")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE)
					.append("\"")
					.append("'s value[")
					.append(parmRequestType)			
					.append("] is not a elment of request type set[view, proc]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}
	
	private void firstPage(HttpServletRequest req, HttpServletResponse res) {
		printJspPage(req, res, "/menu/testcode/JavaStringConversionTool01.jsp");
	}
	
	private void processPage(HttpServletRequest req, HttpServletResponse res) {
		String parmSourceString = req.getParameter("sourceString");
		if (null == parmSourceString) {
			String errorMessage = "자바 문자열로 변환을 원하는 문자열을 넣어 주세요";
			String debugMessage = "the web parameter 'sourceString' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		}
		
		String[] sourceLines = parmSourceString.split("(\r\n|\r|\n|\n\r)");
		
		StringBuilder targetStringBuilder = new StringBuilder();
		
		targetStringBuilder.append("StringBuilder stringBuilder = new StringBuilder();");
		targetStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		
		for (String sourceLine : sourceLines) {
			targetStringBuilder.append("stringBuilder.append(\"");
			targetStringBuilder.append(StringEscapeUtils.escapeJava(sourceLine));
			targetStringBuilder.append("\");");
			targetStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			targetStringBuilder.append("stringBuilder.append(");
			targetStringBuilder.append("System.getProperty(\"line.separator\")");
			targetStringBuilder.append(");");
			targetStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		}
		
		targetStringBuilder.append("return stringBuilder.toString();");		
		
		req.setAttribute("sourceString", parmSourceString);
		req.setAttribute("targetString", targetStringBuilder.toString());
		printJspPage(req, res, "/menu/testcode/JavaStringConversionTool02.jsp");
	}
}