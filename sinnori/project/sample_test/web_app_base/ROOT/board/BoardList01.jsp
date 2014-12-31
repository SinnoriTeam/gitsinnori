<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="modulusHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="boardListOutDTO" class="kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:include page="/common/crypto_common.jsp" flush="false">
	<jsp:param name="modulusHex" value="<%=modulusHex%>" />
</jsp:include><%
%>
<style>
<!--
table {
	border:solid 1px;
	border-color:black;
	border-collapse:collapse;
}
thead {
	height : 30px;
	text-align:center;
}
tbody {
	height : 20px;
	text-align:center;
}
-->
</style>
<script type="text/javascript">
	function goWrite() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goWriteForm;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
	}


	function goDetail(boardNo) {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goDetailForm;
		g.boardNo.value = boardNo;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
	}


	function goPage(pageNo) {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.gofrm;
		g.pageNo.value = pageNo;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}
</script>
<form name=goWriteForm method="post" action="/servlet/BoardWrite">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageMode" value="view" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>


<form name=goDetailForm method="post" action="/servlet/BoardDetail">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageMode" value="view" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="boardNo" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>

<form name=gofrm method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="pageNo" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>

<h1>자유 게시판</h1>
<br/>
<form name=frm onsubmit="return false">
<div style="width:600px; margin:0 auto; pading:10px;">
	<div style="clear:both; height:40px;">
		<div style="float:left; width:200px;">
		<input type=button onClick="goWrite()" value="글 작성하기" />
		</div><%
	if (null == errorMessage || errorMessage.equals("")) {
		long total = boardListOutDTO.getTotal();
		int pageSize = boardListOutDTO.getPageSize();
		long totalPage = (total + pageSize - 1) / pageSize;

		if (totalPage > 1) {

			long pageNo = boardListOutDTO.getStartNo() / pageSize+1;
			int pageListSize = 2;

			long startPageNo = 1 + pageListSize*(long)((pageNo -1 ) / pageListSize);
%><div style="text-align:right; float:right; width:400px;">페이지 이동 : <%

			if (startPageNo > 1) {
%><a href="#" onClick="goPage('<%=startPageNo-1%>')">prev</a> &nbsp;<%
			}

			for (int i=0; i < pageListSize; i++) {
				long workPageNo = startPageNo + i;
				if (workPageNo > totalPage) break;

				if (workPageNo == pageNo) {
%><b><%=workPageNo%></b>&nbsp;<%	
				} else {
%><a href="#" onClick="goPage('<%=workPageNo%>')"><%=workPageNo%></a>&nbsp;<%	
				}
		
			}

			if (startPageNo+pageListSize <= totalPage) {
%>&nbsp;<a href="#" onClick="goPage('<%=startPageNo+pageListSize%>')">next</a><%
			}
%></div><%
		}
	}
%>
	</div>
	<div style="clear:both; height:100%">
<table border="1">
<thead>
<tr>
	<th style="width:30px;">번호</th>
	<th style="width:300px;">제목</th>
	<th style="width:90px;">작성자</th>
	<th style="width:40px;">조회수</th>
	<th style="width:40px;">추천수</th>
	<th>마지막<br/>수정일</th>
</tr>
</thead>
<tbody><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
<tr>
	<td colspan="8"><%= errorMessage %></td>
</tr><%
	} else {
		java.util.List<BoardListOutDTO.Board> boardList = boardListOutDTO.getBoardList();

		if (null == boardList) {
%>
	<td colspan="8">&nbsp;</td><%
		} else {

			for (BoardListOutDTO.Board board : boardList) {
				int depth = board.getDepth();
%>
<tr>
	<td><%=board.getBoardNo() %></td>
	<td align="left">&nbsp;&nbsp;<%
	if (depth > 0) {
		for (int i=0; i < depth; i++) {
			out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		out.print("ㄴ");
	}
%><a href="#" onClick="goDetail('<%=board.getBoardNo() %>')"><%=escapeHtml(board.getSubject(), WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER) %></a></td>
	<td><%=escapeHtml(board.getNickname(), WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER) %></td>
	<td><%=board.getViewCount() %></td>
	<td><%=board.getVotes() %></td>
	<td><%=board.getModifiedDate().toString() %></td>
</tr><%
			}
		}
	}
%>
</tbody>
</table>
	</div>
<br/>
	<div style="clear:both; height:30px;">
		<div style="float:left; width:200px;">
		<input type=button onClick="goWrite()" value="글 작성하기" />
		</div><%
	if (null == errorMessage || errorMessage.equals("")) {
		long total = boardListOutDTO.getTotal();
		int pageSize = boardListOutDTO.getPageSize();
		long totalPage = (total + pageSize - 1) / pageSize;

		if (totalPage > 1) {
			long pageNo = boardListOutDTO.getStartNo() / pageSize+1;
			int pageListSize = 2;
			long startPageNo = 1 + pageListSize*(long)((pageNo -1 ) / pageListSize);
%><div style="text-align:right; float:right; width:400px;">페이지 이동 : <%

			if (startPageNo > 1) {
%><a href="#" onClick="goPage('<%=startPageNo-1%>')">prev</a>&nbsp;<%
			}

			for (int i=0; i < pageListSize; i++) {
				long workPageNo = startPageNo + i;
				if (workPageNo > totalPage) break;

				if (workPageNo == pageNo) {
%><b><%=workPageNo%></b>&nbsp;<%	
				} else {
%><a href="#" onClick="goPage('<%=workPageNo%>')"><%=workPageNo%></a>&nbsp;<%	
				}
		
			}

			if (startPageNo+pageListSize <= totalPage) {
%>&nbsp;<a href="#" onClick="goPage('<%=startPageNo+pageListSize%>')">next</a><%
			}
%></div><%
		}
	}
%>
	</div>
</div>
</form>
