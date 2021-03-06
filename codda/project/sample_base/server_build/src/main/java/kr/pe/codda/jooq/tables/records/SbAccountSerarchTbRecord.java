/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import kr.pe.codda.jooq.tables.SbAccountSerarchTb;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UByte;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SbAccountSerarchTbRecord extends UpdatableRecordImpl<SbAccountSerarchTbRecord> implements Record6<String, UByte, UByte, String, Timestamp, String> {

    private static final long serialVersionUID = 1128453224;

    /**
     * Setter for <code>sb_db.sb_account_serarch_tb.user_id</code>. 사용자 아이디
     */
    public void setUserId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>sb_db.sb_account_serarch_tb.user_id</code>. 사용자 아이디
     */
    public String getUserId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>sb_db.sb_account_serarch_tb.fail_cnt</code>. 비밀 인증 값 실패 횟수, 0 부터 시작 비밀번호 틀렸을 때 1 증가
     */
    public void setFailCnt(UByte value) {
        set(1, value);
    }

    /**
     * Getter for <code>sb_db.sb_account_serarch_tb.fail_cnt</code>. 비밀 인증 값 실패 횟수, 0 부터 시작 비밀번호 틀렸을 때 1 증가
     */
    public UByte getFailCnt() {
        return (UByte) get(1);
    }

    /**
     * Setter for <code>sb_db.sb_account_serarch_tb.retry_cnt</code>. 비밀번호 찾기 재시도 횟수, 1부터 시작하며 재시도할때 마다 1씩 증가한다
     */
    public void setRetryCnt(UByte value) {
        set(2, value);
    }

    /**
     * Getter for <code>sb_db.sb_account_serarch_tb.retry_cnt</code>. 비밀번호 찾기 재시도 횟수, 1부터 시작하며 재시도할때 마다 1씩 증가한다
     */
    public UByte getRetryCnt() {
        return (UByte) get(2);
    }

    /**
     * Setter for <code>sb_db.sb_account_serarch_tb.last_secret_auth_value</code>. 마지막 비밀 인증 값, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다
     */
    public void setLastSecretAuthValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>sb_db.sb_account_serarch_tb.last_secret_auth_value</code>. 마지막 비밀 인증 값, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다
     */
    public String getLastSecretAuthValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>sb_db.sb_account_serarch_tb.last_req_dt</code>. 마지막 비밀번호 찾기 요청일, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다
     */
    public void setLastReqDt(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>sb_db.sb_account_serarch_tb.last_req_dt</code>. 마지막 비밀번호 찾기 요청일, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다
     */
    public Timestamp getLastReqDt() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>sb_db.sb_account_serarch_tb.is_finished</code>. 종결여부,  'N':미결, 'Y':종결, 24시간 동안은 유지하여 하루당 메일 보내는 횟수를 제한하기 위함이며 배치에서 종결 상태로 24시간이 지난 레코드 일괄 삭제하도록한다.
     */
    public void setIsFinished(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>sb_db.sb_account_serarch_tb.is_finished</code>. 종결여부,  'N':미결, 'Y':종결, 24시간 동안은 유지하여 하루당 메일 보내는 횟수를 제한하기 위함이며 배치에서 종결 상태로 24시간이 지난 레코드 일괄 삭제하도록한다.
     */
    public String getIsFinished() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, UByte, UByte, String, Timestamp, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, UByte, UByte, String, Timestamp, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field2() {
        return SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.FAIL_CNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field3() {
        return SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.RETRY_CNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.LAST_REQ_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.IS_FINISHED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte component2() {
        return getFailCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte component3() {
        return getRetryCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getLastSecretAuthValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component5() {
        return getLastReqDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getIsFinished();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte value2() {
        return getFailCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte value3() {
        return getRetryCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getLastSecretAuthValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getLastReqDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getIsFinished();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchTbRecord value1(String value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchTbRecord value2(UByte value) {
        setFailCnt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchTbRecord value3(UByte value) {
        setRetryCnt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchTbRecord value4(String value) {
        setLastSecretAuthValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchTbRecord value5(Timestamp value) {
        setLastReqDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchTbRecord value6(String value) {
        setIsFinished(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchTbRecord values(String value1, UByte value2, UByte value3, String value4, Timestamp value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbAccountSerarchTbRecord
     */
    public SbAccountSerarchTbRecord() {
        super(SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB);
    }

    /**
     * Create a detached, initialised SbAccountSerarchTbRecord
     */
    public SbAccountSerarchTbRecord(String userId, UByte failCnt, UByte retryCnt, String lastSecretAuthValue, Timestamp lastReqDt, String isFinished) {
        super(SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB);

        set(0, userId);
        set(1, failCnt);
        set(2, retryCnt);
        set(3, lastSecretAuthValue);
        set(4, lastReqDt);
        set(5, isFinished);
    }
}
