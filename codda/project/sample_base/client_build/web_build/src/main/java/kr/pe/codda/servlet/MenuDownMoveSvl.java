package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuDownMoveReq.MenuDownMoveReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class MenuDownMoveSvl extends AbstractAdminLoginServlet {
	
	private static final long serialVersionUID = -1955352172747013425L;

	private void printErrorMessageCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, "/jsp/menu/errorMessageCallback.jsp");
	}
	
	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String paramMenuNo = req.getParameter("menuNo");
		if (null == paramMenuNo) {
			String errorMessage = "파라미터 '메뉴번호'(=menuNo) 값을 넣어주세요";			
			log.warn(errorMessage);
			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		long nativeMenuNo;
		
		try {
			nativeMenuNo = Long.parseLong(paramMenuNo);
		} catch(NumberFormatException e) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(paramMenuNo)
					.append("])의 값이 long 타입 정수가 아닙니다").toString();
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (nativeMenuNo < 0) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(paramMenuNo)
					.append("])의 값이 음수입니다").toString();
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (nativeMenuNo > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(paramMenuNo)
					.append("])의 값이 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("] 보다 큽니다").toString();
			
			log.warn(errorMessage);
			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		MenuDownMoveReq menuDownMoveReq = new MenuDownMoveReq();
		menuDownMoveReq.setMenuNo(nativeMenuNo);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(menuDownMoveReq);
		
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			if (! messageResultRes.getIsSuccess()) {
				printErrorMessageCallBackPage(req, res, messageResultRes.getResultMessage());
				return;
			}
			
			printJspPage(req, res, "/jsp/menu/moveMenuDownOkCallBack.jsp");
			return;
		} else {
			String errorMessage = "메뉴 하단 이동이 실패하였습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(menuDownMoveReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.warn(debugMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
	}

}
