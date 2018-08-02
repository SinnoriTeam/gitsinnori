package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class MenuManagementSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -5023286397753637436L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ArraySiteMenuReq menuListReq = new ArraySiteMenuReq();
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(menuListReq);
		
		if (outputMessage instanceof ArraySiteMenuRes) {
			ArraySiteMenuRes menuListRes = (ArraySiteMenuRes)outputMessage;
			
			req.setAttribute("menuListRes", menuListRes);
			printJspPage(req, res, "/jsp/menu/menuManagement.jsp");
			return;
		} else if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			String errorMessage = "메뉴 목록 조회가 실패하였습니다";
			String debugMessage = messageResultRes.toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		} else {
			String errorMessage = "메뉴 목록 조회가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(menuListReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
	}

}
