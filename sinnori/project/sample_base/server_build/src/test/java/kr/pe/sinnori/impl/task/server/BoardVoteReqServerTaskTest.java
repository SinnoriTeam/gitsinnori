package kr.pe.sinnori.impl.task.server;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.impl.message.BoardVoteReq.BoardVoteReq;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardVoteReqServerTaskTest extends AbstractJunitTest {
	@Test
	public void testDoTask() {
		PersonalLoginManagerIF personalLoginManagerMock = Mockito.mock(PersonalLoginManagerIF.class);				
		ToLetterCarrier toLetterCarrierMock = Mockito.mock(ToLetterCarrier.class);
		
		
		BoardVoteReq inObj = new BoardVoteReq();
		inObj.setBoardId(BoardType.FREE.getValue());
		inObj.setBoardNo(7);
		inObj.setUserId("test02");
		inObj.setIp("127.0.0.1");		
		
		BoardVoteReqServerTask boardVoteReqServerTask= new BoardVoteReqServerTask();
		
		try {
			boardVoteReqServerTask.doTask(mainProjectName, 
					personalLoginManagerMock, toLetterCarrierMock, inObj);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
}