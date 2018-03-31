package kr.pe.sinnori.impl.task.server;

import static kr.pe.sinnori.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.sinnori.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.JooqSqlUtil;
import kr.pe.sinnori.server.lib.MemberStateType;
import kr.pe.sinnori.server.lib.MembershipType;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class MemberRegisterReqServerTask extends AbstractServerTask {
	
	private void sendErrorOutputtMessageForCommit(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}
	
	private void sendErrorOutputMessageForRollback(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
		if (null != conn) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}
		}		
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}
	
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);		
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}
	
	private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, Connection conn,
			ToLetterCarrier toLetterCarrier) throws InterruptedException {		
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	private String getDecryptedString(byte[] cipherBytes, ServerSymmetricKeyIF serverSymmetricKey)
			throws InterruptedException, IllegalArgumentException, SymmetricException {		
		byte[] valueBytes = serverSymmetricKey.decrypt(cipherBytes);
		String decryptedString = new String(valueBytes, CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
		return decryptedString;
	}
	
	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		MemberRegisterReq inObj = (MemberRegisterReq) inputMessage;
		
		doWork(projectName, personalLoginManager, toLetterCarrier, inObj);		
	}	
	
	public void doWork(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			MemberRegisterReq memberRegisterReq) throws Exception {
		// FIXME!
		log.info(memberRegisterReq.toString());

		String idCipherBase64 = memberRegisterReq.getIdCipherBase64();		
		String pwdCipherBase64 = memberRegisterReq.getPwdCipherBase64();
		String nicknameCipherBase64 = memberRegisterReq.getNicknameCipherBase64();
		String hintCipherBase64 = memberRegisterReq.getHintCipherBase64();
		String answerCipherBase64 = memberRegisterReq.getAnswerCipherBase64();
		String sessionKeyBase64 = memberRegisterReq.getSessionKeyBase64();
		String ivBase64 = memberRegisterReq.getIvBase64();
		
		if (null == idCipherBase64) {
			sendErrorOutputMessage("아이디를 입력해 주세요", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		if (null == pwdCipherBase64) {
			sendErrorOutputMessage("비밀번호를 입력해 주세요", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		if (null == nicknameCipherBase64) {
			sendErrorOutputMessage("별명을 입력해 주세요", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		if (null == hintCipherBase64) {
			sendErrorOutputMessage("비밀번호 분실 힌트를 입력해 주세요", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		if (null == answerCipherBase64) {
			sendErrorOutputMessage("비밀번호 분실 답변을 입력해 주세요", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		if (null == sessionKeyBase64) {
			sendErrorOutputMessage("세션키를 입력해 주세요", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		if (null == ivBase64) {
			sendErrorOutputMessage("세션키 소금값을 입력해 주세요", toLetterCarrier, memberRegisterReq);
			return;
		}
		

		byte[] idCipherBytes = null;		
		byte[] pwdCipherBytes = null;
		byte[] nicknameCipherBytes = null;
		byte[] hintCipherBytes = null;
		byte[] answerCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

		try {
			idCipherBytes = Base64.decodeBase64(idCipherBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("아이디 암호문은 base64 인코딩되지 않았습니다", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		try {
			pwdCipherBytes = Base64.decodeBase64(pwdCipherBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("비밀번호 암호문은 base64 인코딩되지 않았습니다", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		try {
			nicknameCipherBytes = Base64.decodeBase64(nicknameCipherBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("별명은 base64 인코딩되지 않았습니다", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		try {
			hintCipherBytes = Base64.decodeBase64(hintCipherBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("비밀번호 분실 힌트는 base64 인코딩되지 않았습니다", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		try {
			answerCipherBytes = Base64.decodeBase64(answerCipherBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("비밀번호 분실 답변은 base64 인코딩되지 않았습니다", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		try {
			sessionKeyBytes = Base64.decodeBase64(sessionKeyBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("세션키는 base64 인코딩되지 않았습니다", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		try {
			ivBytes = Base64.decodeBase64(ivBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("세션키 소금값은 base64 인코딩되지 않았습니다", toLetterCarrier, memberRegisterReq);
			return;
		}
		
		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance()
					.getMainProjectServerSessionkey();
			serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format("잘못된 파라미터로 인한 대칭키 생성 실패, sessionKeyMemberRegisterReq=[%s]", memberRegisterReq.toString());
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = String.format("알수 없는 이유로 대칭키 생성 실패, sessionKeyMemberRegisterReq=[%s]", memberRegisterReq.toString());
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		}

		String userID = null;
		String nickname = null;
		String pwdHint = null;
		String pwdAnswer = null;

		try {
			userID = getDecryptedString(idCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(memberRegisterReq.toString(), e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		}

		try {
			ValueChecker.checkValidUserId(userID);
		} catch (IllegalArgumentException e) {
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, memberRegisterReq);
			return;
		}

		
		try {
			nickname = getDecryptedString(nicknameCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "별명에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "별명에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(memberRegisterReq.toString(), e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		}

		try {
			ValueChecker.checkValidNickname(nickname);
		} catch (IllegalArgumentException e) {
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, memberRegisterReq);
			return;
		}

		try {
			pwdHint = getDecryptedString(hintCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "비밀번호 분실 힌트에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "비밀번호 분실 힌트에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(memberRegisterReq.toString(), e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		}

		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
		} catch (RuntimeException e) {
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, memberRegisterReq);
			return;
		}

		try {
			pwdAnswer = getDecryptedString(answerCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "비밀번호 분실 답변에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "비밀번호 분실 답변에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(memberRegisterReq.toString(), e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		}

		try {
			ValueChecker.checkValidPwdAnswer(pwdAnswer);
		} catch (RuntimeException e) {
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, memberRegisterReq);
			return;
		}

		/**
		 * 비밀번호외 항목들은 철저히 보안을 유지 해야 하므로 찍지 않음. 필요시 특정 아이디에 한에서만 찍도록 해야함.
		 */
		log.info("회원가입 아이디[{}] 처리전", userID);
		
		byte[] passwordBytes = null;
		try {
			passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = "비밀번호에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "비밀번호에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(memberRegisterReq.toString(), e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, memberRegisterReq);
			return;
		}
					
		try {
			ValueChecker.checkValidPwd(passwordBytes);
		} catch(IllegalArgumentException e) {
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, memberRegisterReq);
			return;
		}

		Random random = new Random();
		byte[] pwdSaltBytes = new byte[8];
		random.nextBytes(pwdSaltBytes);

		ByteBuffer passwordByteBuffer = ByteBuffer.allocate(pwdSaltBytes.length + passwordBytes.length);
		passwordByteBuffer.put(pwdSaltBytes);
		passwordByteBuffer.put(passwordBytes);

		// FIXME!
		// log.info(HexUtil.getAllHexStringFromByteBuffer(passwordByteBuffer));

		MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.SINNORI_PASSWORD_ALGORITHM_NAME);

		md.update(passwordByteBuffer.array());

		byte passwordMDBytes[] = md.digest();
		
		/** 복호환 비밀번호 초기화 */
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);

		String pwdBase64 = Base64.encodeBase64String(passwordMDBytes);
		String pwdSaltBase64 = Base64.encodeBase64String(pwdSaltBytes);
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			boolean isSameIDMember = create.fetchExists(create.select().from(SB_MEMBER_TB)
					.where(SB_MEMBER_TB.USER_ID.eq(userID)));
			
			/*int countOfSameIDMember = create.selectCount()
				.from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
			.fetchOne(0, Integer.class);*/
			
			// if (0 != countOfSameIDMember) {
			if (isSameIDMember) {
				String errorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				sendErrorOutputtMessageForCommit(errorMessage, conn, toLetterCarrier, memberRegisterReq);
				return;
			}
			
			boolean isSameNicknameMember = create.fetchExists(create.select()
					.from(SB_MEMBER_TB)
					.where(SB_MEMBER_TB.NICKNAME.eq(nickname)));
			
			/*int countOfSameNicknameMember = create.selectCount()
					.from(SB_MEMBER_TB)
					.where(SB_MEMBER_TB.NICKNAME.eq(nickname))
			.fetchOne(0, Integer.class);
			
			if (0 != countOfSameNicknameMember) {*/
			if (isSameNicknameMember) {
				String errorMessage = new StringBuilder("기존 회원과 중복되는 별명[")
						.append(nickname)
						.append("] 입니다").toString();
				sendErrorOutputtMessageForCommit(errorMessage, conn, toLetterCarrier, memberRegisterReq);
				return;
			}
			
		/*	insert into SB_MEMBER_TB  
			(`user_id`,
			`nickname`,
			`pwd_base64`,
			`pwd_salt_base64`,
			`member_gb`,
			`member_st`,
			`pwd_hint`,
			`pwd_answer`,
			`pwd_fail_cnt`,
			`reg_dt`,
			`mod_dt`) 
			values(#{userId}, #{nickname}, #{pwdBase64}, #{pwdSaltBase64}, 1, 0, #{pwdHint}, #{pwdAnswer}, 0, sysdate(), reg_dt)*/
			
			int resultOfInsert = create.insertInto(SB_MEMBER_TB)
			.set(SB_MEMBER_TB.USER_ID, userID)
			.set(SB_MEMBER_TB.NICKNAME, nickname)
			.set(SB_MEMBER_TB.PWD_BASE64, pwdBase64)
			.set(SB_MEMBER_TB.PWD_SALT_BASE64, pwdSaltBase64)
			.set(SB_MEMBER_TB.MEMBER_GB, MembershipType.USER.getValue())
			.set(SB_MEMBER_TB.MEMBER_ST, MemberStateType.OK.getValue())
			.set(SB_MEMBER_TB.PWD_HINT, pwdHint)
			.set(SB_MEMBER_TB.PWD_ANSWER, pwdAnswer)
			.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
			.set(SB_MEMBER_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
			.set(SB_MEMBER_TB.MOD_DT, SB_MEMBER_TB.REG_DT).execute();
			
			if (0 == resultOfInsert) {
				String errorMessage = "1.회원 가입 실패하였습니다";
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, memberRegisterReq);
				return;
			}
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(memberRegisterReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage("회원 가입 성공하였습니다");
			
			sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			return;		
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error, inObj=")
					.append(memberRegisterReq.toString()).toString();
			log.warn(errorMessage, e);
			
			sendErrorOutputMessageForRollback("2.회원 가입 실패하였습니다", conn, toLetterCarrier, memberRegisterReq);
			return;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}

		/*HashMap<String, Object> inserMemberHashMap = new HashMap<String, Object>();
		inserMemberHashMap.put("userId", userId);
		inserMemberHashMap.put("nickname", nickname);
		inserMemberHashMap.put("pwdBase64", pwdBase64);
		inserMemberHashMap.put("pwdSaltBase64", pwdSaltBase64);
		inserMemberHashMap.put("pwdHint", pwdHint);
		inserMemberHashMap.put("pwdAnswer", pwdAnswer);

		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			HashMap<String, Object> memberByIDHash = session.selectOne("getMemberByID", userId);
			if (null != memberByIDHash) {
				// outObj.setTaskResult("N");
				messageResultRes.setResultMessage("기존 회원과 중복되는 아이디[" + userId + "]로는 회원 가입할 수 없습니다.");
			} else {
				HashMap<String, Object> memberByNickNameHash = session.selectOne("getMemeberByNickname", nickname);
				if (null != memberByNickNameHash) {
					messageResultRes.setResultMessage("기존 회원과 중복되는 별명[" + nickname + "]로는 회원 가입할 수 없습니다.");
				} else {
					int resultOfInsert = session.insert("kr.pr.sinnori.testweb.insertMember", inserMemberHashMap);
					if (resultOfInsert > 0) {
						messageResultRes.setIsSuccess(true);
						messageResultRes.setResultMessage("회원 가입이 성공하였습니다.");
					} else {
						messageResultRes.setResultMessage("1.회원 가입이 실패하였습니다.");
					}
				}
			}

			session.commit();
		} catch (Exception e) {
			session.rollback();
			log.warn("회원 가입시 알 수 없는 에러 발생", e);
			messageResultRes.setResultMessage("2.회원 가입이 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultRes);
			return;
		} finally {
			session.close();
		}

		toLetterCarrier.addSyncOutputMessage(messageResultRes);*/
	}
}
