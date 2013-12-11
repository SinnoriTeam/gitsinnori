<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>신놀이 프레임워크</h1>
<ol>
<li>
	<dl>
			<dt>신놀이 프레임워크란?</dt>
			<dd> 신놀이 프레임워크는 자바언어로 구현한 개발 프레임워크로 <br/>
			아래 2가지 요소(신놀이 서버, 클라이언트 서버 접속 라이브러리)로 구성되어 있습니다.<br/><br/>
			신놀이 프레임워크로 무엇을 할 수 있는가? <br/>
			파일 송수신 서버와 클라이언트, 채팅 서버와 클라이언트,<br/> 
			웹 애플리케이션 서버 개발이 가능할것입니다.<br/>
			서버와 클라이언 사이에 메시지 교환을 통한 할 수 있는 일이 <br/>
			바로 신놀이 프레임워크로 할 수 있는 일이기에 가능합니다.<br/> 
			하지만 신놀이 프레임워크는 아주 많이 부족합니다.<br/> 
			2013년 12월 11일 현재까지 혼자 프로젝트를 했왔기때문입니다.<br/>
			이제 신놀이 프레임워크를 이용하여 파일 송수신 서버와 클라이언트, <br/>
			즉 기능이 제한적이지만 Ftp 짝퉁 서버와 클라이언트를 만들었기에 신놀이 프로젝트를 공개합니다.<br/><br/>
			
			앞으로 저는 Ftp 짝퉁 서버와 클라이언트에 이어받기 기능 추가와 <br/>
			파일 전송시 파일 데이터 요청후 그 처리 결과를 응답으로 받아야하는 2단계를 파일 처리 1단계로 바꿀예정입니다.<br/>
			이 일이 끝나면 오픈 프로젝트 Netty 를 신놀이에 흡수하는데 주력할 것입니다.<br/>
			비록 기능, 속도, 안정성 그리고 소프트웨어 설계면에서 형평없지만 신놀이 프레임워크는<br/> 
			오픈 소스 진영의 2개의 훌륭한 프로젝트 Netty 와 Protocol Buffers 를 합친 모습을 가지고 있습니다.<br/>
			이 2 가지 조합이 바로 서버와 클라이언트 사이에 메시지 교환이기때문입니다.<br/><br/>
			
			Netty 는 TCP/IP,  UDP 등 매우 확장성 있는 네트워크 프레임워크로 이미 실전에서도 검증 받았기때문에<br/>
			신놀이는 반듯이 Netty 의 좋은점을 흡수해야 합니다.<br/> 
			그것은 곧 프로젝트의 성장이기도 하지만 저 개인의 성장이기도 합니다.<br/>
			저는 Netty 가 부럽기에 질투합니다.<br/><br/>
			
			Protocol Buffers 의 .proto 파일은 신놀이 에서 .xml 파일로 입/출력 메시지로 부릅니다.<br/>
			Protocol Buffers 는 다양한 언어를 지원하다 보니, <br/>
			Protocol Buffers 샘플 소스에는 입/출력 메시지 수정시 동적으로 이를 불러들이는 설명이 없더군요.<br/>
			개발시 빈번한 입/출력 메시지 수정이 있기때문에 특히 서버에서는 서버 멈추고 기동하는 시간이 길기때문에<br/> 
			많은 개발자들이 동시에 개발을 하기때문에 입/출력 메시지 수정시 이를 동적으로 반영해 주는 기능 여부가 중요합니다.<br/>
			Protocol Buffers .proto 를 자바빈즈를 따르는 클래스로 만들어 주던데, <br/>
			Protocol Buffers 자체 라이브러리가 없어도 동적 클래스 로딩 처리를 해 주면 되지만, <br/>
			해쉬를 기반으로 하는 신놀이 입/출력 메시지 객체는 리플렉션을 기반으로 하는 자바빈즈 보다 <br/>
			항목 접근 속도가 우수 하다고 감히 말씀드립니다.<br/>&nbsp;<br/>
			</dd>
	</dl>
	<ol>
	<li>
	<dl>
		<dt>신놀이 서버</dt>
		<dd>한빛미디어 김성박/송지훈님의 "자바 I/O & NIO 네트워크 프로그래밍" 에서 소개한 AdvancedChatServer 기반으로 만들었습니다.</dd>
	</dl>
	</li>
	<li>
	<dl>
		<dt>클라이언트 서버 접속 라이브러리</dt>
		<dd>신놀이 서버에 연결하여 메세지를 주고 받을 수 있는 라이브러리로 구성된다.<br/>
		2013년 현재 자바 신놀이 클라이언트 라이브러리만 제공된다.<br/>
		연결 폴의 종류는 3가지이며 그중 하나는 다른 사용자와 소캣 자원을 공유하는 방법을 제공하는것이 특색이다.</dd>
	</dl>
		<ol>
			<li><dl>
		<dt>자바 신놀이 클라이언트 라이브러리</dt>
		<dd>자바언어로 만든 비동기 통신방식으로 신놀이 서버와 연결하는 신놀이 클라이언트 라이브러리이다.</dd>
	</dl></li>
		</ol>
	</li>
	</ol>
	
</li>
<br/>
<li>
	<dl>
			<dt>신놀이 프레임 워크 주요 기능</dt>
			<dd>3가지 주요 기능이 있습니다.
				<ol>
				<li>
					<dl>
					<dt>메시지 교환</dt>
					<dd>서버와 클라이언트간의  메시지 교환 기능</dd>
					</dl>
				</li>
				<li>
					<dl>
					<dt>서버 중지 없는 메시지 운영</dt>
					<dd>서버 중지 없이 서버 운영중에 메시지를 신규 추가할 수 있고 수정된 메시지를 반영할 수 있는 기능</dd>
					</dl>
				</li>
				<li>
					<dl>
					<dt>서버 중지 없는 비지니스 로직 운영</dt>
					<dd>서버 중지 없이 서버 운영중에 신규 비지니스 로직을 추가할 수 있고 수정된 비지니스 로직을 반영할 수 있는 기능</dd>
					</dl>
				</li>
				</ol>
			</dd>			
	</dl>	
</li>
<br/>
<li>
	<dl>
		<dt>개발한것을 아파치 라이센스 2.0 으로 공개하는 특별한 이유?</dt>
		<dd>개인적으로 자유 소프트웨어 진영에 공헌을 하고 싶고<br/>
		상용으로도 유용하게 사용했으면 하기에 아파치 라이센스 2.0 을 선택하였습니다.
		</dd>
	</dl>
</li>
<br/>
<li>
	<dl>
		<dt>개발을 하게된 동기?</dt>
		<dd>	신놀이 프레임 워크의 시작은 윷놀이 서버/클라이언트 게임을 만들고자 시작되었습니다. <br/>
		처음엔 공개된 프레임 워크를 통하면 쉽게 만들수있겠지라는 안일한 생각을 가지고 시작했습니다.<br/>
		하지만 그일은 결코 쉽지 않았습니다.<br/>
		남의 소스 읽기는 지루하고 어렵고 샘플이라는것은 응용에 적용하기엔 너무 간단했습니다.<br/>
		그 과정에서 무엇이 빠르고 쉬운 개발이냐에 대한 고민을  가지게 되었고, <br/>
		불평만 하는것이 아닌 실질적인 해결을 해 보고 싶어 개발하게 되었습니다. 
		</dd>
	</dl>
</li>
</ol>
