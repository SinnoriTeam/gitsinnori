package kr.pe.sinnori.impl.task.server;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardListInDTO.BoardListInDTO;
import kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.MybatisSqlSessionFactoryManger;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardListInDTOServerTask extends AbstractServerTask {
	

	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		// FIXME!
		log.info(inputMessage.toString());		
		
		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		BoardListInDTO inObj = (BoardListInDTO)inputMessage;
		
		/*long boardID = inObj.getBoardId();
		
		if (boardID <=0) {
			String errorMessage = new StringBuilder("게시판 식별자(boardId) 값[")
			.append(boardID).append("]은 0 보다 커야합니다.").toString();
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage(errorMessage);
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}		*/
		
		SqlSession session = sqlSessionFactory.openSession(false);		
		// session.commit(false);
		
		// log.info("", session.);
		
		BoardListOutDTO outObj = null;
		
		try {			
			outObj = session.selectOne("getBoardListMap", inObj);
			session.commit();
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID(inObj.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("알 수 없는 이유로 게시판 조회가 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
		
		java.util.List<BoardListOutDTO.Board> boardList = outObj.getBoardList();
		
		if (null == boardList) {
			outObj.setCnt(0);				
		} else {
			outObj.setCnt(boardList.size());
		}
		
		//  log.info(outObj.toString());
		
		toLetterCarrier.addSyncOutputMessage(outObj);
	}
}