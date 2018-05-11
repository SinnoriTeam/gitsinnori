<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="plainText" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="algorithm" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="privateKey" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="iv" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="encryptedBytesHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="plainTextHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="decryptedBytesHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="decryptedPlainText" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="isSame" class="java.lang.String" scope="request" /><%
%><!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title><%=WebCommonStaticFinalVars.WEBSITE_TITLE%></title>
<meta name="Author" content="SinnoriTeam - website / Design by Ian Smith - N-vent Design Services LLC - www.n-vent.com" />
<meta name="distribution" content="global" />
<meta name="rating" content="general" />
<meta name="Keywords" content="" />
<meta name="ICBM" content=""/> <!-- see geourl.org -->
<meta name="DC.title" content="Your Company"/>
<link rel="shortcut icon" href="favicon.ico"/> <!-- see favicon.com -->
<link rel="stylesheet" type="text/css" href="/css/style.css" />
<script type="text/javascript">
    function goURL(bodyurl) {		
		top.document.location.href = bodyurl;		
    }
</script>
</head>
<body>
<form name="directgofrm" method="post">
<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>"/>
</form>
<!-- The ultra77 template is designed and released by Ian Smith - N-vent Design Services LLC - www.n-vent.com. Feel free to use this, but please don't sell it and kindly leave the credits intact. Muchas Gracias! -->
<div id="wrapper">
<a name="top"></a>
<!-- header -->
<div id="header">
	<div id="pagedescription"><h1>Sinnori Framework::공사중</h1><br /><h2> Sinnori Framework is an open software<br/> that help to create a server/client application.</h2><%
	if (! isLogin(request)) {
%><a href="/servlet/Login?topmenu=<%=getCurrentTopMenuIndex(request)%>">login</a><%		
	} else {
%><a href="/menu/member/logout.jsp?topmenu=<%=getCurrentTopMenuIndex(request)%>">logout</a><%
	}
%>
	
	</div>
	<div id="branding"><p><span class="templogo"><!-- your logo here -->Sinnori Framework</span><br />of the developer, by the developer, for the developer</p></div>
</div>

<!-- top menu -->
<div id="menu">
	<ul><%= buildTopMenuPartString(request) %></ul>
</div> <!-- end top menu -->
<!-- bodywrap -->
<div id="bodytop">&nbsp;</div>
<div id="bodywrap">
	<div id="contentbody">
		<table>
			<tr><td colspan="2"><h3>CryptoJS 대칭키 테스트 결과 페이지</h3></td></tr>
			<tr>
				<td>원문</td>
				<td><%=HtmlStringUtil.toHtml4BRString(plainText)%></td> 
			</tr>
			<tr>
				<td>선택한 대칭키 알고리즘</td>
				<td><%=HtmlStringUtil.toHtml4BRString(algorithm)%></td> 
			</tr>
			<tr>
				<td>개인키</td>
				<td><%=privateKey%></td> 
			</tr>
			<tr>
				<td>iv</td>
				<td><%=iv%></td> 
			</tr>
			<tr>
				<td>javascirpt 암호문</td>
				<td><%=encryptedBytesHex%></td> 
			</tr>
			<tr>
				<td>원문 Hex</td>
				<td><%=plainTextHex%></td> 
			</tr>
			<tr>
				<td>server 복호문 Hex</td>
				<td><%=decryptedBytesHex%></td> 
			</tr>
			<tr>
				<td>server 복호문</td>
				<td><%=HtmlStringUtil.toHtml4BRString(decryptedPlainText)%></td> 
			</tr>
			<tr>
				<td>비교결과</td>
				<td><%=HtmlStringUtil.toHtml4BRString(isSame)%></td> 
			</tr>
		</table>
	</div>
</div> <!-- end bodywrap -->
<div id="bodybottom">&nbsp;</div>


<!-- footer -->
<div id="footer">
<p><jsp:include page="/footer.html"  flush="false" />. Design by <a href="http://www.n-vent.com" title="The ultra77 template is designed and released by N-vent Design Services LLC">N-vent</a></p>
<ul>
<li><a href="http://www.oswd.org" title="Open Source Web Design">Open Source Web Design</a></li>

</ul>
</div> <!-- end footer -->

<!-- side menu  --><%= buildLeftMenuPartString(request) %><!-- end side menu -->

</div> <!-- end wrapper -->
</body>
</html>