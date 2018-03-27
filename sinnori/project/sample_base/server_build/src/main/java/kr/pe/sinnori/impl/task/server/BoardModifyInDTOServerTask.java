package kr.pe.sinnori.impl.task.server;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardModifyInDTO.BoardModifyInDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.mybatis.MybatisSqlSessionFactoryManger;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardModifyInDTOServerTask extends AbstractServerTask {
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		// FIXME!
		log.info(inputMessage.toString());		
		
		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		BoardModifyInDTO inObj = (BoardModifyInDTO)inputMessage;
		
		MessageResult messageResultOutObj = new MessageResult();
		messageResultOutObj.setIsSuccess(false);
		messageResultOutObj.setTaskMessageID(inObj.getMessageID());
		
		try {
			ValueChecker.checkValidSubject(inObj.getSubject());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		try {
			ValueChecker.checkValidContent(inObj.getContent());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		try {
			ValueChecker.checkValidWriterId(inObj.getUserId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		
		try {
			ValueChecker.checkValidIP(inObj.getIp());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultOutObj.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		}
		
		SqlSession session = sqlSessionFactory.openSession(false);	
		try {		
			HashMap<String, Object> boardModifyHash = session.selectOne("getBoardModifyInLock", inObj);
			if (null == boardModifyHash) {
				session.rollback();
				String errorMessage = String.format("게시판[%d]에서 글[%d]이 존재하지 않습니다.", 
						inObj.getBoardId(), inObj.getBoardNo());
				messageResultOutObj.setResultMessage(errorMessage);
				
				log.warn("{}, userId={}, ip={}", errorMessage, inObj.getUserId(), inObj.getUserId());
			} else {
				// FIXME!
				log.info("boardModifyHash={}", boardModifyHash.toString());
				
				if (inObj.getBoardId() != (Long)boardModifyHash.get("boardId")) {
					session.commit();
					String errorMessage = String.format("게시판 식별자[%d]와 파라미터로 넘어온 게시판 식별자[%d]가 상이합니다.", 
							(Long)boardModifyHash.get("boardId"), inObj.getBoardId());
					messageResultOutObj.setResultMessage(errorMessage);
					
					log.warn("{}, userId={}, ip={}", errorMessage, inObj.getUserId(), inObj.getIp());
				} else {
						
					if (inObj.getUserId().equals((String)boardModifyHash.get("writerId"))) {
						Long attachId = (Long)boardModifyHash.get("attachId");
						if (null != attachId && attachId != inObj.getAttachId()) {
							session.commit();
							String errorMessage = String.format("기 등록된 게시판 업로드 식별자[%d]와 파라미터 게시판 업로드 식별자가[%s]가 상이합니다.", 
									attachId, inObj.getAttachId());
							log.warn("{}, userId={}, ip={}", errorMessage, inObj.getUserId(), inObj.getIp());
							
							messageResultOutObj.setResultMessage(errorMessage);
						} else {
							int resultOfUpdate = session.update("updateBoard", inObj);						
							if (resultOfUpdate > 0) {
								session.commit();
								messageResultOutObj.setIsSuccess(true);
								messageResultOutObj.setResultMessage("게시판 글 수정이 성공하였습니다.");
							} else {
								session.rollback();
								messageResultOutObj.setResultMessage("1.게시판 글 수정이 실패하였습니다.");
							}
						}					
						
					} else {
						session.commit();
						
						String errorMessage = String.format("게시판 작성자[%s]와 로그인 아이디[%s]가 상이합니다.", 
								(String)boardModifyHash.get("writerId"), inObj.getUserId());
						log.warn("{}, ip={}", errorMessage, inObj.getIp());
						
						messageResultOutObj.setResultMessage(errorMessage);					
					}
				}			
			}			
			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);			
			
			messageResultOutObj.setResultMessage("2.게시판 글 수정이 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
		
		toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
		return;
		
	}
}