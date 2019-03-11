package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbUserActionHistoryTb.SB_USER_ACTION_HISTORY_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDeleteReq.BoardDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

/**
 * 게시글 삭제 서버 타스크, WARNING! 게시글 삭제는 오직 작성자 본인만이 가능하며 관리자라도 할 수 없다 
 * 
 * @author Won Jonghoon
 *
 */
public class BoardDeleteReqServerTask extends AbstractServerTask {
	public BoardDeleteReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardDeleteReq)inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());
			
			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=")
					.append(e.getMessage())
					.append(", inObj=")
					.append(inputMessage.toString()).toString();
			
			log.warn(errorMessage, e);
						
			sendErrorOutputMessage("게시글 가져오는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public MessageResultRes doWork(String dbcpName, BoardDeleteReq boardDeleteReq)
			throws Exception {

		if (null == boardDeleteReq.getRequestedUserID()) {
			String errorMessage = "요청한 사용자 아이디를 넣어주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		
		if (boardDeleteReq.getBoardNo() < 0 || boardDeleteReq.getBoardNo() > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = "게시판 번호가 unsigned integer type 의 최대값(=4294967295) 보다 큽니다";
			throw new ServerServiceException(errorMessage);
		}
		
		String requestedUserID = boardDeleteReq.getRequestedUserID();
		UByte boardID = UByte.valueOf(boardDeleteReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardDeleteReq.getBoardNo());
		String boardName = null;

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
						
			ServerDBUtil.checkUserAccessRights(conn, create, log, "게시글 삭제 서비스", PermissionType.MEMBER, requestedUserID);
			
			Record2<String, Byte> boardInforRecord = create
					.select(SB_BOARD_INFO_TB.BOARD_NAME,
							SB_BOARD_INFO_TB.LIST_TYPE)
					.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).fetchOne();

			if (null == boardInforRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
						.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);			
			byte boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
			
			
			BoardListType boardListType = null;
			try {
				boardListType = BoardListType.valueOf(boardListTypeValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = e.getMessage();
				throw new ServerServiceException(errorMessage);
			}
			
			
			Record1<UInteger> 
			boardRecordForGroupNo = create.select(SB_BOARD_TB.GROUP_NO)
					.from(SB_BOARD_TB)					
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
					.fetchOne();

			if (null == boardRecordForGroupNo) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "1.해당 게시글이 존재 하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger groupNo = boardRecordForGroupNo.value1();
			
			/** 최상위 그룹 레코드에 대한 락 걸기 */
			Record1<UInteger> rootBoardRecord = create.select(SB_BOARD_TB.BOARD_NO)
				.from(SB_BOARD_TB)
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_TB.BOARD_NO.eq(groupNo)).forUpdate().fetchOne();
			
			if (null == rootBoardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder().append("그룹 최상위 글[boardID=")
						.append(boardID.shortValue())
						.append(", boardNo=").append(groupNo.longValue())
						.append("] 이 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			/** 최상위 그룹 레코드에 대한 락 획득후 게시판 상태 얻기 */
			Record3<UInteger, String, String>  boardRecord = create.select(SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.BOARD_ST,
					SB_BOARD_HISTORY_TB.REGISTRANT_ID)
			.from(SB_BOARD_TB)
			.innerJoin(SB_BOARD_HISTORY_TB)
			.on(SB_BOARD_TB.BOARD_ID.eq(SB_BOARD_HISTORY_TB.BOARD_ID))
			.and(SB_BOARD_TB.BOARD_NO.eq(SB_BOARD_HISTORY_TB.BOARD_NO))
			.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0)))
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
			.fetchOne();
			
			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "2.해당 게시글이 존재 하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger parentNo = boardRecord.getValue(SB_BOARD_TB.PARENT_NO);
			String boardState = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);
			String firstWriterID = boardRecord.getValue(SB_BOARD_HISTORY_TB.REGISTRANT_ID);
			
			
			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(boardState, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("게시글의 상태 값[")
						.append(boardState)
						.append("]이 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			if (BoardStateType.DELETE.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "해당 게시글은 삭제된 글입니다";
				throw new ServerServiceException(errorMessage);
			} else if (BoardStateType.BLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";
				throw new ServerServiceException(errorMessage);
			} else if (BoardStateType.TREEBLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "해당 게시글 트리는 관리자에 의해 차된되었습니다";
				throw new ServerServiceException(errorMessage);
			}			
			/*
			 * Record1<String> firstWriterBoardRecord =
			 * create.select(SB_BOARD_HISTORY_TB.REGISTRANT_ID) .from(SB_BOARD_HISTORY_TB)
			 * .join(SB_MEMBER_TB)
			 * .on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.REGISTRANT_ID))
			 * .where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
			 * .and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
			 * .and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0))) .fetchOne();
			 * 
			 * if (null == firstWriterBoardRecord) { try { conn.rollback(); } catch
			 * (Exception e) { log.warn("fail to rollback"); }
			 * 
			 * String errorMessage = new
			 * StringBuilder("해당 게시글의 최초 작성자 정보가 존재 하지 않습니다").toString(); throw new
			 * ServerServiceException(errorMessage); }
			 * 
			 * String writerID =
			 * firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.REGISTRANT_ID);
			 */
			
			if (! requestedUserID.equals(firstWriterID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "타인 글은 삭제 할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			
			create.update(SB_BOARD_TB)
			.set(SB_BOARD_TB.BOARD_ST, BoardStateType.DELETE.getValue())
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
			.execute();
			
			if (BoardListType.TREE.equals(boardListType)) {
				// 계층형 목록일때 삭제시 카운트 1 감소
				create.update(SB_BOARD_INFO_TB)
					.set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.sub(1))
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
				.execute();
			} else {
				// 그룹 루트만으로 이루어진 목록일때 그룹 루트에 대한 차단시에만 카운트 1 감소
				if (0L == parentNo.longValue()) {					 
					create.update(SB_BOARD_INFO_TB)
					.set(SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.CNT.sub(1))
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
					.execute();
				}
			}			 
			
			create.insertInto(SB_USER_ACTION_HISTORY_TB)
			.set(SB_USER_ACTION_HISTORY_TB.USER_ID, requestedUserID)
			.set(SB_USER_ACTION_HISTORY_TB.INPUT_MESSAGE_ID, boardDeleteReq.getMessageID())
			.set(SB_USER_ACTION_HISTORY_TB.INPUT_MESSAGE, boardDeleteReq.toString())
			.set(SB_USER_ACTION_HISTORY_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
			.execute();
			
			conn.commit();			

			
		} catch (ServerServiceException e) {
			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(boardDeleteReq.getMessageID());
		messageResultRes.setIsSuccess(true);		
		messageResultRes.setResultMessage(new StringBuilder()
				.append(boardName)
				.append(" 게시판[")
				.append(boardID)
				.append("]의 글[")
				.append(boardNo.longValue())
				.append("] 삭제가 완료되었습니다").toString());
		
		return messageResultRes;
	}
}
