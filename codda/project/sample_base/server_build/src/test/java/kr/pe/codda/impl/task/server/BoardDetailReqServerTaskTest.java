package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardDetailReqServerTaskTest extends AbstractJunitTest {	
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);
		
		String userID = "test01";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디1";
		String pwdHint = "힌트 그것이 알고싶다";
		String pwdAnswer = "힌트답변 말이여 방구여";
		String ip = "127.0.0.1";
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (ServerServiceException e) {
			String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
					.append(userID)
					.append("] 입니다").toString();
			String actualErrorMessag = e.getMessage();
			
			log.warn(actualErrorMessag, e);
			
			assertEquals(expectedErrorMessage, actualErrorMessag);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to create a test ID");
		}
	}
	
	
	@Test
	public void testDoService_ok() {
		String userID = "test01";
		final short boardID = 3; 
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("테스트 주제1234");
		boardWriteReq.setContents("내용::그림 하나를 그리다, 하하호호");		
		boardWriteReq.setRequestedUserID(userID);
		boardWriteReq.setIp("172.16.0.1");
		
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
			attachedFile.setAttachedFileName("반짝반짝작은별01.jpg");
			
			attachedFileList.add(attachedFile);
		}
		
		boardWriteReq.setNewAttachedFileCnt((short)attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME, boardWriteReq);
			log.info(boardWriteRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
				
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID(userID);
		
		
		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(TEST_DBCP_NAME, boardDetailReq);
			
			assertEquals(boardWriteReq.getSubject(), boardDetailRes.getSubject());
			assertEquals(boardWriteReq.getContents(), boardDetailRes.getContents());
			assertEquals(boardWriteReq.getRequestedUserID(), boardDetailRes.getFirstWriterID());
			
			
			assertEquals(boardWriteReq.getNewAttachedFileList().get(0).getAttachedFileName(), 
					boardDetailRes.getAttachedFileList().get(0).getAttachedFileName());			
			
			log.info(boardDetailRes.toString());
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}
