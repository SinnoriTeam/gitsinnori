package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.weblib.jdf.AbstractLoginServlet;

@SuppressWarnings("serial")
public class JDFLoginTestSvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage="/menu/testcode/JDFLoginTest01.jsp";
		
		/*String parmSessionKey = req.getParameter("sessionkey");
		String parmIV = req.getParameter("iv");
		
		SymmetricKey  webUserSymmetricKey = null;
		ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager.getInstance();
		try {
			
			webUserSymmetricKey = sessionKeyServerManger.getSymmetricKey("AES", CommonType.SymmetricKeyEncoding.BASE64, parmSessionKey, parmIV);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage);
			
			printMessagePage(req, res, errorMessage, errorMessage);
			return;
		} catch(SymmetricException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage);
			printMessagePage(req, res, errorMessage, errorMessage);
			return;
		}
		
		req.setAttribute("webUserSymmetricKey", webUserSymmetricKey);
		req.setAttribute("pageIV", parmIV);*/
		
		printWebLayoutControlJspPage(req, res, goPage);			
	}

}
