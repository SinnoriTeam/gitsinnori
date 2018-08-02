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

    private static final long serialVersionUID = 534333164;

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
0 : 공지, 1:자유, 2:FAQ
     */
    public final TableField<SbBoardInfoTbRecord, UByte> BOARD_ID = createField("board_id", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "게시판 식별자,\n0 : 공지, 1:자유, 2:FAQ");

    /**
     * The column <code>sb_db.sb_board_info_tb.board_name</code>. 게시판 이름
     */
    public final TableField<SbBoardInfoTbRecord, String> BOARD_NAME = createField("board_name", org.jooq.impl.SQLDataType.VARCHAR(30), this, "게시판 이름");

    /**
     * The column <code>sb_db.sb_board_info_tb.board_info</code>. 게시판 설명
     */
    public final TableField<SbBoardInfoTbRecord, String> BOARD_INFO = createField("board_info", org.jooq.impl.SQLDataType.CLOB, this, "게시판 설명");

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
