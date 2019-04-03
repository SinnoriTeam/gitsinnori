package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

@SuppressWarnings("serial")
public abstract class AbstractLoginServlet extends AbstractSessionKeyServlet {	

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		AccessedUserInformation accessedUserformation = getAccessedUserInformation(req);
		
		if (! accessedUserformation.isLoginedIn()) {
			String requestURI = req.getRequestURI();

			ServerSessionkeyIF webServerSessionkey  = null;
			try {
				ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
				webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();			
			} catch (SymmetricException e) {
				log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]", e.getMessage());
				
				String errorMessage = "ServerSessionkeyManger instance init error";
				String debugMessage = new StringBuilder("ServerSessionkeyManger instance init error, errormessage=[")
						.append(e.getMessage())
						.append("]").toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			req.setAttribute("successURL", requestURI);
			req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
					webServerSessionkey.getModulusHexStrForWeb());
			printJspPage(req, res, JDF_MEMBER_LOGIN_INPUT_PAGE);
			return;
		} 
		
		super.performPreTask(req, res);		
	}
}
