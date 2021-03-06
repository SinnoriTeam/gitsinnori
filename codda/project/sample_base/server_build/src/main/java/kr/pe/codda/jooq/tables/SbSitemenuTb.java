/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.jooq.Indexes;
import kr.pe.codda.jooq.Keys;
import kr.pe.codda.jooq.SbDb;
import kr.pe.codda.jooq.tables.records.SbSitemenuTbRecord;

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
public class SbSitemenuTb extends TableImpl<SbSitemenuTbRecord> {

    private static final long serialVersionUID = 1997246755;

    /**
     * The reference instance of <code>sb_db.sb_sitemenu_tb</code>
     */
    public static final SbSitemenuTb SB_SITEMENU_TB = new SbSitemenuTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbSitemenuTbRecord> getRecordType() {
        return SbSitemenuTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_sitemenu_tb.menu_no</code>. 메뉴 번호,  1부터 시작된다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 메뉴 번호를 얻어옴.
     */
    public final TableField<SbSitemenuTbRecord, UInteger> MENU_NO = createField("menu_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "메뉴 번호,  1부터 시작된다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 메뉴 번호를 얻어옴.");

    /**
     * The column <code>sb_db.sb_sitemenu_tb.parent_no</code>. 부모 메뉴 번호,  메뉴 번호는 1부터 시작되며 부모가 없는 경우 부모 메뉴 번호 값은  0 값을 갖는다.
     */
    public final TableField<SbSitemenuTbRecord, UInteger> PARENT_NO = createField("parent_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "부모 메뉴 번호,  메뉴 번호는 1부터 시작되며 부모가 없는 경우 부모 메뉴 번호 값은  0 값을 갖는다.");

    /**
     * The column <code>sb_db.sb_sitemenu_tb.depth</code>. 트리 깊이,  0 부터 시작하며 부모보다 + 1 이 크다
     */
    public final TableField<SbSitemenuTbRecord, UByte> DEPTH = createField("depth", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "트리 깊이,  0 부터 시작하며 부모보다 + 1 이 크다");

    /**
     * The column <code>sb_db.sb_sitemenu_tb.order_sq</code>. 전체 메뉴 순서
     */
    public final TableField<SbSitemenuTbRecord, UByte> ORDER_SQ = createField("order_sq", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "전체 메뉴 순서");

    /**
     * The column <code>sb_db.sb_sitemenu_tb.menu_nm</code>. 메뉴 이름
     */
    public final TableField<SbSitemenuTbRecord, String> MENU_NM = createField("menu_nm", org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "메뉴 이름");

    /**
     * The column <code>sb_db.sb_sitemenu_tb.link_url</code>. 메뉴에 대응되는 링크 주소
     */
    public final TableField<SbSitemenuTbRecord, String> LINK_URL = createField("link_url", org.jooq.impl.SQLDataType.VARCHAR(2048).nullable(false), this, "메뉴에 대응되는 링크 주소");

    /**
     * Create a <code>sb_db.sb_sitemenu_tb</code> table reference
     */
    public SbSitemenuTb() {
        this(DSL.name("sb_sitemenu_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_sitemenu_tb</code> table reference
     */
    public SbSitemenuTb(String alias) {
        this(DSL.name(alias), SB_SITEMENU_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_sitemenu_tb</code> table reference
     */
    public SbSitemenuTb(Name alias) {
        this(alias, SB_SITEMENU_TB);
    }

    private SbSitemenuTb(Name alias, Table<SbSitemenuTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbSitemenuTb(Name alias, Table<SbSitemenuTbRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.SB_SITEMENU_TB_PRIMARY, Indexes.SB_SITEMENU_TB_SB_SITEMENU_IDX1, Indexes.SB_SITEMENU_TB_SB_SITEMENU_IDX2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbSitemenuTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_SITEMENU_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbSitemenuTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbSitemenuTbRecord>>asList(Keys.KEY_SB_SITEMENU_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbSitemenuTb as(String alias) {
        return new SbSitemenuTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbSitemenuTb as(Name alias) {
        return new SbSitemenuTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbSitemenuTb rename(String name) {
        return new SbSitemenuTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbSitemenuTb rename(Name name) {
        return new SbSitemenuTb(name, null);
    }
}
