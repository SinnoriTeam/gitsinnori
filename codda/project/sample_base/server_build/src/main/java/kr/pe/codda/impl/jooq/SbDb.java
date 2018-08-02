/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb;
import kr.pe.codda.impl.jooq.tables.SbBoardInfoTb;
import kr.pe.codda.impl.jooq.tables.SbBoardTb;
import kr.pe.codda.impl.jooq.tables.SbBoardVoteTb;
import kr.pe.codda.impl.jooq.tables.SbGroupInfoTb;
import kr.pe.codda.impl.jooq.tables.SbGroupTb;
import kr.pe.codda.impl.jooq.tables.SbMemberTb;
import kr.pe.codda.impl.jooq.tables.SbSeqTb;
import kr.pe.codda.impl.jooq.tables.SbSitemenuTb;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class SbDb extends SchemaImpl {

    private static final long serialVersionUID = -2034966400;

    /**
     * The reference instance of <code>sb_db</code>
     */
    public static final SbDb SB_DB = new SbDb();

    /**
     * The table <code>sb_db.sb_board_filelist_tb</code>.
     */
    public final SbBoardFilelistTb SB_BOARD_FILELIST_TB = kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;

    /**
     * The table <code>sb_db.sb_board_info_tb</code>.
     */
    public final SbBoardInfoTb SB_BOARD_INFO_TB = kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

    /**
     * The table <code>sb_db.sb_board_tb</code>.
     */
    public final SbBoardTb SB_BOARD_TB = kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

    /**
     * The table <code>sb_db.sb_board_vote_tb</code>.
     */
    public final SbBoardVoteTb SB_BOARD_VOTE_TB = kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;

    /**
     * The table <code>sb_db.sb_group_info_tb</code>.
     */
    public final SbGroupInfoTb SB_GROUP_INFO_TB = kr.pe.codda.impl.jooq.tables.SbGroupInfoTb.SB_GROUP_INFO_TB;

    /**
     * The table <code>sb_db.sb_group_tb</code>.
     */
    public final SbGroupTb SB_GROUP_TB = kr.pe.codda.impl.jooq.tables.SbGroupTb.SB_GROUP_TB;

    /**
     * The table <code>sb_db.sb_member_tb</code>.
     */
    public final SbMemberTb SB_MEMBER_TB = kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

    /**
     * The table <code>sb_db.sb_seq_tb</code>.
     */
    public final SbSeqTb SB_SEQ_TB = kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

    /**
     * The table <code>sb_db.sb_sitemenu_tb</code>.
     */
    public final SbSitemenuTb SB_SITEMENU_TB = kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

    /**
     * No further instances allowed
     */
    private SbDb() {
        super("sb_db", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            SbBoardFilelistTb.SB_BOARD_FILELIST_TB,
            SbBoardInfoTb.SB_BOARD_INFO_TB,
            SbBoardTb.SB_BOARD_TB,
            SbBoardVoteTb.SB_BOARD_VOTE_TB,
            SbGroupInfoTb.SB_GROUP_INFO_TB,
            SbGroupTb.SB_GROUP_TB,
            SbMemberTb.SB_MEMBER_TB,
            SbSeqTb.SB_SEQ_TB,
            SbSitemenuTb.SB_SITEMENU_TB);
    }
}
