package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardListReq.BoardListReq;
import kr.pe.sinnori.impl.message.BoardListRes.BoardListRes;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;

@SuppressWarnings("serial")
public class BoardListSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.COMMUNITY);
		
		String goPage = "/menu/board/BoardList01.jsp";
		
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) parmBoardId = "2";
		
		
		short boardId = 2;		
		try {
			boardId = Short.parseShort(parmBoardId);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자(boardId) 값[")
			.append(parmBoardId).append("]이 잘못되었습니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		
		if (boardId <= 0) {
			String errorMessage = new StringBuilder("게시판 식별자(boardId) 값[")
			.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		int pageNo = 1;
		
		String parmPageNo = req.getParameter("pageNo");
		if (null == parmPageNo) {
			parmPageNo = "1";
		}
		
		try {
			pageNo = Integer.parseInt(parmPageNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "parameter pageNo type is a not integer";
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}	
		
		
		if (pageNo <= 0) {
			String errorMessage = "parameter pageNo is less than or equal to zero";
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		// int pageSize = 20;
		String errorMessage = "";
		
		BoardListReq inObj = new BoardListReq();
		inObj.setBoardId(boardId);
		inObj.setStartNo((pageNo - 1) * WebCommonStaticFinalVars.WEBSITE_BOARD_PAGESIZE);
		inObj.setPageSize(WebCommonStaticFinalVars.WEBSITE_BOARD_PAGESIZE);
				
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(inObj);
		
		if (messageFromServer instanceof BoardListRes) {
			BoardListRes boardListRes = (BoardListRes)messageFromServer;
			req.setAttribute("boardListRes", boardListRes);
		} else {			
			if (messageFromServer instanceof MessageResultRes) {				
				errorMessage = ((MessageResultRes)messageFromServer).getResultMessage();
				
				log.warn("입력 메시지[{}]의 응답 메시지로 MessageResult 메시지 도착, 응답 메시지=[{}], userId={}, ip={}", 
						inObj.toString(), messageFromServer.toString(), getUserId(req), req.getRemoteAddr());
			} else {
				errorMessage = "게시판 목록 메시지를 얻는데 실패하였습니다.";
				
				if (messageFromServer instanceof SelfExnRes) {
					log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				} else {
					log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				}
			}
				
		}
		
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("errorMessage", errorMessage);	
		printJspPage(req, res, goPage);
	}

}
