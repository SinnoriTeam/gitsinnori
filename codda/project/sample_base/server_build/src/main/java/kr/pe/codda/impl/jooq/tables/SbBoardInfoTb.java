/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.Indexes;
import kr.pe.codda.impl.jooq.Keys;
import kr.pe.codda.impl.jooq.SbDb;
import kr.pe.codda.impl.jooq.tables.records.SbBoardInfoTbRecord;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;


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
public class SbBoardInfoTb extends TableImpl<SbBoardInfoTbRecord> {

    private static final long serialVersionUID = -467000463;

    /**
     * The reference instance of <code>sb_db.sb_board_info_tb</code>
     */
    public static final SbBoardInfoTb SB_BOARD_INFO_TB = new SbBoardInfoTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbBoardInfoTbRecord> getRecordType() {
        return SbBoardInfoTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_board_info_tb.board_id</code>. 게시판 식별자,
1 : 공지, 2:자유, 3:이슈
     */
    public final TableField<SbBoardInfoTbRecord, UByte> BOARD_ID = createField("board_id", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "게시판 식별자,\n1 : 공지, 2:자유, 3:이슈");

    /**
     * The column <code>sb_db.sb_board_info_tb.board_name</code>. 게시판 이름
     */
    public final TableField<SbBoardInfoTbRecord, String> BOARD_NAME = createField("board_name", org.jooq.impl.SQLDataType.VARCHAR(30), this, "게시판 이름");

    /**
     * The column <code>sb_db.sb_board_info_tb.list_type</code>. 게시판 목록 유형, 0:그룹 루트, 1; 계층
     */
    public final TableField<SbBoardInfoTbRecord, Byte> LIST_TYPE = createField("list_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "게시판 목록 유형, 0:그룹 루트, 1; 계층");

    /**
     * The column <code>sb_db.sb_board_info_tb.reply_policy_type</code>. 댓글 정책 유형, 0:댓글없음, 1:본문글에만, 2:본문및 댓글 모두
     */
    public final TableField<SbBoardInfoTbRecord, Byte> REPLY_POLICY_TYPE = createField("reply_policy_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "댓글 정책 유형, 0:댓글없음, 1:본문글에만, 2:본문및 댓글 모두");

    /**
     * The column <code>sb_db.sb_board_info_tb.write_permission_type</code>. 본문 쓰기 권한 유형, 0:어드민, 1:일반인, 2:손님
     */
    public final TableField<SbBoardInfoTbRecord, Byte> WRITE_PERMISSION_TYPE = createField("write_permission_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "본문 쓰기 권한 유형, 0:어드민, 1:일반인, 2:손님");

    /**
     * The column <code>sb_db.sb_board_info_tb.reply_permission_type</code>. 댓글 쓰기 권한 유형, 0:어드민, 1:일반인, 2:손님, 주) '댓글 쓰기 권한 유형'은 '상세 유형'에서 '0 댓글없음' 이 아닌 경우만 유효하다
     */
    public final TableField<SbBoardInfoTbRecord, Byte> REPLY_PERMISSION_TYPE = createField("reply_permission_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "댓글 쓰기 권한 유형, 0:어드민, 1:일반인, 2:손님, 주) '댓글 쓰기 권한 유형'은 '상세 유형'에서 '0 댓글없음' 이 아닌 경우만 유효하다");

    /**
     * The column <code>sb_db.sb_board_info_tb.cnt</code>. 게시판 목록 개수
     */
    public final TableField<SbBoardInfoTbRecord, Long> CNT = createField("cnt", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.BIGINT)), this, "게시판 목록 개수");

    /**
     * The column <code>sb_db.sb_board_info_tb.total</code>. 게시판 글 전체 개수
     */
    public final TableField<SbBoardInfoTbRecord, Long> TOTAL = createField("total", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.BIGINT)), this, "게시판 글 전체 개수");

    /**
     * The column <code>sb_db.sb_board_info_tb.next_board_no</code>. 다음 게시판 번호, 본문의 부모 게시판 번호가 0 으로 예약되어 있어 1부터 시작
     */
    public final TableField<SbBoardInfoTbRecord, UInteger> NEXT_BOARD_NO = createField("next_board_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "다음 게시판 번호, 본문의 부모 게시판 번호가 0 으로 예약되어 있어 1부터 시작");

    /**
     * Create a <code>sb_db.sb_board_info_tb</code> table reference
     */
    public SbBoardInfoTb() {
        this(DSL.name("sb_board_info_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_board_info_tb</code> table reference
     */
    public SbBoardInfoTb(String alias) {
        this(DSL.name(alias), SB_BOARD_INFO_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_board_info_tb</code> table reference
     */
    public SbBoardInfoTb(Name alias) {
        this(alias, SB_BOARD_INFO_TB);
    }

    private SbBoardInfoTb(Name alias, Table<SbBoardInfoTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbBoardInfoTb(Name alias, Table<SbBoardInfoTbRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.SB_BOARD_INFO_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbBoardInfoTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_BOARD_INFO_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbBoardInfoTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbBoardInfoTbRecord>>asList(Keys.KEY_SB_BOARD_INFO_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardInfoTb as(String alias) {
        return new SbBoardInfoTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardInfoTb as(Name alias) {
        return new SbBoardInfoTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbBoardInfoTb rename(String name) {
        return new SbBoardInfoTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbBoardInfoTb rename(Name name) {
        return new SbBoardInfoTb(name, null);
    }
}
