package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;
import java.util.HashSet;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectHavingStep;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuMoveDownReq.MenuMoveDownReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MenuMoveDownReqServerTask extends AbstractServerTask {	
	
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {	
		
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (MenuMoveDownReq)inputMessage);
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

			sendErrorOutputMessage("메뉴 하단 이동이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
		
	}
	public MessageResultRes doWork(String dbcpName, MenuMoveDownReq menuMoveDownReq) throws Exception {
		// FIXME!
		log.info(menuMoveDownReq.toString());
		
		UInteger sourceMenuNo = UInteger.valueOf(menuMoveDownReq.getMenuNo());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			
			
			/** '메뉴 순서' 를 위한 lock */
			Record menuSeqRecord = create.select(SB_SEQ_TB.SQ_VALUE)
			.from(SB_SEQ_TB)
			.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(SequenceType.MENU.getSequenceID())))
			.forUpdate().fetchOne();
			
			if (null == menuSeqRecord) {
				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[")
						.append(SequenceType.MENU.getSequenceID())
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				throw new ServerServiceException(errorMessage);
			}
			
			Record3<UInteger, UByte, UByte> sourceMenuRecord = create.select(SB_SITEMENU_TB.PARENT_NO,
					SB_SITEMENU_TB.DEPTH,
					SB_SITEMENU_TB.ORDER_SQ)
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(sourceMenuNo))
			.fetchOne();
			
			if (null == sourceMenuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("지정한 메뉴[")
						.append(menuMoveDownReq.getMenuNo())
						.append("]가 존재하지 않습니다").toString();				
				
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger sourceParetNo = sourceMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
			UByte sourceDepth = sourceMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);
			UByte sourceOrderSeq = sourceMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);
			
						
			Record4<UInteger, UInteger, UByte, UByte>  targetMenuRecord = null;
			try {
				SelectHavingStep<Record1<UByte>> firstYoungerBrowereQuery = create.select(
						SB_SITEMENU_TB.ORDER_SQ.min().as(SB_SITEMENU_TB.ORDER_SQ))
				.from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.PARENT_NO.eq(sourceParetNo))
				.and(SB_SITEMENU_TB.DEPTH.eq(sourceDepth))
				.and(SB_SITEMENU_TB.ORDER_SQ.gt(sourceOrderSeq));
				
				targetMenuRecord = create.select(
						SB_SITEMENU_TB.MENU_NO, SB_SITEMENU_TB.PARENT_NO, SB_SITEMENU_TB.DEPTH, SB_SITEMENU_TB.ORDER_SQ)
				.from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.ORDER_SQ.eq(firstYoungerBrowereQuery))
				.fetchOne();
			} catch(TooManyRowsException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("지정한 메뉴[")
						.append(menuMoveDownReq.getMenuNo())
						.append("]보다 한칸 낮은 메뉴가 다수 존재합니다").toString();
				
				throw new ServerServiceException(errorMessage);
			}
			
			if (null == targetMenuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("지정한 메뉴[")
						.append(menuMoveDownReq.getMenuNo())
						.append("]보다 한칸 낮은 메뉴가 존재하지 않습니다").toString();				
				
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger targetMenuNo = targetMenuRecord.getValue(SB_SITEMENU_TB.MENU_NO);
			UInteger targetParetNo = targetMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
			UByte targetDepth = targetMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);
			UByte targetOrderSeq = targetMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);
						
			
			int sourceGroupListSize = targetOrderSeq.shortValue() - sourceOrderSeq.shortValue();
			int targetGroupListSize;
			
			Record1<UByte>  firstYoungerBrotherOrderSeqReccordOfTargetMenu = create.select(
					SB_SITEMENU_TB.ORDER_SQ.min().as(SB_SITEMENU_TB.ORDER_SQ))
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.PARENT_NO.eq(targetParetNo))
			.and(SB_SITEMENU_TB.DEPTH.eq(targetDepth))
			.and(SB_SITEMENU_TB.ORDER_SQ.gt(targetOrderSeq))
			.fetchOne();
			
			if (null == firstYoungerBrotherOrderSeqReccordOfTargetMenu || null == firstYoungerBrotherOrderSeqReccordOfTargetMenu.getValue(SB_SITEMENU_TB.ORDER_SQ)) {
				Record1<UByte>  lastOrderSeqMenuRecord = create.select(
						SB_SITEMENU_TB.ORDER_SQ.max().as(SB_SITEMENU_TB.ORDER_SQ))
				.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx"))				
				.fetchOne();
				
				targetGroupListSize = lastOrderSeqMenuRecord.value1().shortValue() - targetOrderSeq.shortValue() + 1;
			} else {
				targetGroupListSize = firstYoungerBrotherOrderSeqReccordOfTargetMenu.getValue(SB_SITEMENU_TB.ORDER_SQ).shortValue() - targetOrderSeq.shortValue();
			}
			
			
			Result<Record1<UInteger>> sourceGroupResult = create.select(SB_SITEMENU_TB.MENU_NO)
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(sourceOrderSeq))
			.and(SB_SITEMENU_TB.ORDER_SQ.lt(UByte.valueOf(sourceOrderSeq.shortValue()+sourceGroupListSize)))
			.fetch();
			
			HashSet<UInteger> sourceGroupMenuNoSet = new HashSet<UInteger>();
			for (Record1<UInteger> sourceGroupRecord : sourceGroupResult) {
				sourceGroupMenuNoSet.add(sourceGroupRecord.getValue(SB_SITEMENU_TB.MENU_NO));
			}
			
			/**
			 * 하단 이동 요청한 메뉴 그룹을 하단 이동 요청한 메뉴 위치로 전부 이동
			 */
			int sourceMenuUpdateCount = create.update(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.add(targetGroupListSize))
			.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(sourceOrderSeq))
			.and(SB_SITEMENU_TB.ORDER_SQ.lt(UByte.valueOf(sourceOrderSeq.shortValue()+sourceGroupListSize)))
			.execute();
			
			if (0 == sourceMenuUpdateCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("메뉴[")
						.append(menuMoveDownReq.getMenuNo())
						.append("] 순서를 한칸 위로 조정하는데  실패하였습니다").toString();
				
				
				throw new ServerServiceException(errorMessage);
			}
			
			/**
			 * 하단 이동 요청한 메뉴보다 한칸 낮은 메뉴 그룹을 하단 이동 요청한 메뉴 위치로 전부 이동
			 */
			int targetMenuUpdateCount = create.update(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.sub(sourceGroupListSize))
			.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(targetOrderSeq))
			.and(SB_SITEMENU_TB.ORDER_SQ.lt(UByte.valueOf(targetOrderSeq.shortValue()+targetGroupListSize)))
			.and(SB_SITEMENU_TB.MENU_NO.notIn(sourceGroupMenuNoSet))
			.execute();
					
			if (0 == targetMenuUpdateCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("메뉴[")
						.append(menuMoveDownReq.getMenuNo())
						.append("]의 한칸 위 메뉴[")
						.append(targetMenuNo)
						.append("] 순서를 조정하는데  실패하였습니다").toString();
				
				
				throw new ServerServiceException(errorMessage);
			}			
			
			
			try {
				conn.commit();
			} catch (Exception e) {
				log.warn("fail to commit");
			}
			
			log.info("메뉴[번호:{}, 순서:{}] <--하단 메뉴 이동에 따른 순서 뒤바뀜--> 위치가 뒤 바뀐 메뉴[번호:{}, 순서:{}]",
					menuMoveDownReq.getMenuNo(),
					sourceOrderSeq,
					targetMenuNo,
					targetOrderSeq);
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(menuMoveDownReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage(new StringBuilder()
					.append("메뉴[")
					.append(menuMoveDownReq.getMenuNo())
					.append("]의 상단 이동 처리가 완료되었습니다").toString());
			
			return messageResultRes;
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
			
			log.warn("unknown error", e);			
			
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
}