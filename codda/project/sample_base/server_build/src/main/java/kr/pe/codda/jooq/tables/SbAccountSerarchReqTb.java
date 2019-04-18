/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.jooq.Indexes;
import kr.pe.codda.jooq.Keys;
import kr.pe.codda.jooq.SbDb;
import kr.pe.codda.jooq.tables.records.SbAccountSerarchReqTbRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
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
public class SbAccountSerarchReqTb extends TableImpl<SbAccountSerarchReqTbRecord> {

    private static final long serialVersionUID = 1467273307;

    /**
     * The reference instance of <code>sb_db.sb_account_serarch_req_tb</code>
     */
    public static final SbAccountSerarchReqTb SB_ACCOUNT_SERARCH_REQ_TB = new SbAccountSerarchReqTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbAccountSerarchReqTbRecord> getRecordType() {
        return SbAccountSerarchReqTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_account_serarch_req_tb.user_id</code>. 사용자 아이디
     */
    public final TableField<SbAccountSerarchReqTbRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "사용자 아이디");

    /**
     * The column <code>sb_db.sb_account_serarch_req_tb.fail_cnt</code>. 비밀 인증 값 실패 횟수, 0 부터 시작 비밀번호 틀렸을 때 1 증가
     */
    public final TableField<SbAccountSerarchReqTbRecord, UByte> FAIL_CNT = createField("fail_cnt", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "비밀 인증 값 실패 횟수, 0 부터 시작 비밀번호 틀렸을 때 1 증가");

    /**
     * The column <code>sb_db.sb_account_serarch_req_tb.retry_cnt</code>. 비밀번호 찾기 재시도 횟수, 1부터 시작하며 재시도할때 마다 1씩 증가한다
     */
    public final TableField<SbAccountSerarchReqTbRecord, UByte> RETRY_CNT = createField("retry_cnt", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "비밀번호 찾기 재시도 횟수, 1부터 시작하며 재시도할때 마다 1씩 증가한다");

    /**
     * The column <code>sb_db.sb_account_serarch_req_tb.last_secret_auth_value</code>. 마지막 비밀 인증 값, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다
     */
    public final TableField<SbAccountSerarchReqTbRecord, String> LAST_SECRET_AUTH_VALUE = createField("last_secret_auth_value", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "마지막 비밀 인증 값, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다");

    /**
     * The column <code>sb_db.sb_account_serarch_req_tb.last_req_dt</code>. 마지막 비밀번호 찾기 요청일, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다
     */
    public final TableField<SbAccountSerarchReqTbRecord, Timestamp> LAST_REQ_DT = createField("last_req_dt", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "마지막 비밀번호 찾기 요청일, 비밀번호 찾기 요청은 최대 횟수까지 가능하며 그때마다 '비밀 인증 값' 과 '비밀 번호 찾기 요청일' 이 변경된다");

    /**
     * The column <code>sb_db.sb_account_serarch_req_tb.is_finished</code>. 종결여부,  'N':미결, 'Y':종결, 24시간 동안은 유지하여 하루당 메일 보내는 횟수를 제한하기 위함이며 배치에서 종결 상태로 24시간이 지난 레코드 일괄 삭제하도록한다.
     */
    public final TableField<SbAccountSerarchReqTbRecord, String> IS_FINISHED = createField("is_finished", org.jooq.impl.SQLDataType.CHAR(1), this, "종결여부,  'N':미결, 'Y':종결, 24시간 동안은 유지하여 하루당 메일 보내는 횟수를 제한하기 위함이며 배치에서 종결 상태로 24시간이 지난 레코드 일괄 삭제하도록한다.");

    /**
     * Create a <code>sb_db.sb_account_serarch_req_tb</code> table reference
     */
    public SbAccountSerarchReqTb() {
        this(DSL.name("sb_account_serarch_req_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_account_serarch_req_tb</code> table reference
     */
    public SbAccountSerarchReqTb(String alias) {
        this(DSL.name(alias), SB_ACCOUNT_SERARCH_REQ_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_account_serarch_req_tb</code> table reference
     */
    public SbAccountSerarchReqTb(Name alias) {
        this(alias, SB_ACCOUNT_SERARCH_REQ_TB);
    }

    private SbAccountSerarchReqTb(Name alias, Table<SbAccountSerarchReqTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbAccountSerarchReqTb(Name alias, Table<SbAccountSerarchReqTbRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return SbDb.SB_DB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SB_ACCOUNT_SERARCH_REQ_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbAccountSerarchReqTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_ACCOUNT_SERARCH_REQ_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbAccountSerarchReqTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbAccountSerarchReqTbRecord>>asList(Keys.KEY_SB_ACCOUNT_SERARCH_REQ_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<SbAccountSerarchReqTbRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SbAccountSerarchReqTbRecord, ?>>asList(Keys.PWD_SEARCH_REQ_FK1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchReqTb as(String alias) {
        return new SbAccountSerarchReqTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbAccountSerarchReqTb as(Name alias) {
        return new SbAccountSerarchReqTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbAccountSerarchReqTb rename(String name) {
        return new SbAccountSerarchReqTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbAccountSerarchReqTb rename(Name name) {
        return new SbAccountSerarchReqTb(name, null);
    }
}