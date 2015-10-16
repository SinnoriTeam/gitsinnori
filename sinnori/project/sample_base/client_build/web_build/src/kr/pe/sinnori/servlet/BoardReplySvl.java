/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.weblib.AbstractAuthServlet;
import kr.pe.sinnori.impl.message.BoardReplyRequest.BoardReplyRequest;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

/**
 * 게시판 댓글 등록 처리
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BoardReplySvl extends AbstractAuthServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = null;		
		
		String parmPageMode = req.getParameter("pageMode");
		if (null == parmPageMode) {
			goPage = "/menu/board/BoardReply01.jsp";
			String errorMessage = "페이지 모드를 넣어주세요.";
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}
		
		if (!parmPageMode.equals("view") && !parmPageMode.equals("proc")) {
			goPage = "/menu/board/BoardReply01.jsp";
			String errorMessage = new StringBuilder("페이지 모드는 2가지(view, proc) 입니다.")
			.append(CommonStaticFinalVars.NEWLINE)
			.append("페이지 모드 값[").append(parmPageMode).append("]이 잘못 되었습니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}		
		
		if (parmPageMode.equals("view")) {
			goPage = "/menu/board/BoardReply01.jsp";
			
			String parmBoardId = req.getParameter("boardId");
			if (null == parmBoardId) {
				String errorMessage = "게시판 식별자를 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			long parentBoardNo = 0L;
			String parmParentBoardNo = req.getParameter("parentBoardNo");
			
			if (null == parmParentBoardNo) {
				String errorMessage = "부모 게시판 번호 값을 넣어 주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}		
			
			try {
				parentBoardNo = Long.parseLong(parmParentBoardNo);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 부모 게시판 번호 값[")
				.append(parmParentBoardNo).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}			
			
			if (parentBoardNo <= 0) {
				String errorMessage = new StringBuilder("부모 게시판 번호 값[")
				.append(parmParentBoardNo).append("]은 0 보다 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}	
			
			req.setAttribute("parmBoardId", parmBoardId);
			req.setAttribute("parmParentBoardNo", parmParentBoardNo);	
			req.setAttribute("errorMessage", "");
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		} else {			
			
			goPage = "/menu/board/BoardReply02.jsp";
			
			long boardId = 2L;
			long parentBoardNo = 0L;
			
			String parmBoardId = req.getParameter("boardId");
			if (null == parmBoardId) {
				String errorMessage = "게시판 식별자를 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			
			try {
				boardId = Long.parseLong(parmBoardId);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자 값[")
				.append(parmBoardId).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			if (boardId <= 0) {
				String errorMessage = new StringBuilder("게시판 식별자 값[")
				.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			String parmParentBoardNo = req.getParameter("parentBoardNo");
			
			if (null == parmParentBoardNo) {
				String errorMessage = "부모 게시판 번호 값을 넣어 주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}	
			
			try {
				parentBoardNo = Long.parseLong(parmParentBoardNo);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 부모 게시판 번호 값[")
				.append(parmParentBoardNo).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}			
			
			if (parentBoardNo <= 0) {
				String errorMessage = new StringBuilder("부모 게시판 번호 값[")
				.append(parmParentBoardNo).append("]은 0 보다 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}	
			
			String parmSubject = req.getParameter("subject");
			if (null == parmSubject) {
				String errorMessage = "제목 값을 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			
			String parmContent = req.getParameter("content");		
			if (null == parmContent) {
				String errorMessage = "글 내용 값을 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			String parmAttachId = req.getParameter("attachId");
			if (null == parmAttachId) {
				String errorMessage = "업로드 식별자를 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			long attachId = 0L;
			try {
				attachId = Long.parseLong(parmAttachId);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 업로드 식별자 값[")
				.append(parmAttachId).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			if (attachId < 0) {
				String errorMessage = new StringBuilder("업로드 식별자 값[")
				.append(parmAttachId).append("]은 0 보다 작거나 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
			
			if (attachId > CommonStaticFinalVars.MAX_UNSIGNED_INTEGER) {
				String errorMessage = new StringBuilder("업로드 식별자 값[")
				.append(parmAttachId).append("]은 ")
				.append(CommonStaticFinalVars.MAX_UNSIGNED_INTEGER)
				.append(" 값 보다 작거나 같아야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printWebLayoutControlJspPage(req, res, goPage);
				return;
			}
				
			String errorMessage = "";
			String userId = getUserId(req);
			BoardReplyRequest inObj = new BoardReplyRequest();
			inObj.setBoardId(boardId);
			inObj.setParentBoardNo(parentBoardNo);
			inObj.setSubject(parmSubject);
			inObj.setContent(parmContent);
			inObj.setAttachId(attachId);
			inObj.setUserId(userId);
			inObj.setIp(req.getRemoteAddr());
			
			ClientProject clientProject = ClientProjectManager.getInstance().getMainClientProject();
			AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(inObj);
			if (messageFromServer instanceof MessageResult) {
				MessageResult outObj = (MessageResult)messageFromServer;
				
				
				req.setAttribute("messageResultOutObj", outObj);
			} else {				
				errorMessage = "게시판 댓글 등록이 실패하였습니다.";
				
				if (messageFromServer instanceof SelfExn) {
					log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				} else {
					log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				}
			}			
			
			req.setAttribute("parmBoardId", parmBoardId);
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
		}
	}
}