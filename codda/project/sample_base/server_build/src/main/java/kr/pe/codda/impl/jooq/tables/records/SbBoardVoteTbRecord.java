/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.tables.SbBoardVoteTb;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
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
public class SbBoardVoteTbRecord extends UpdatableRecordImpl<SbBoardVoteTbRecord> implements Record4<UInteger, String, String, Timestamp> {

    private static final long serialVersionUID = -1435265986;

    /**
     * Setter for <code>SB_DB.SB_BOARD_VOTE_TB.board_no</code>.
     */
    public void setBoardNo(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>SB_DB.SB_BOARD_VOTE_TB.board_no</code>.
     */
    public UInteger getBoardNo() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>SB_DB.SB_BOARD_VOTE_TB.user_id</code>.
     */
    public void setUserId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>SB_DB.SB_BOARD_VOTE_TB.user_id</code>.
     */
    public String getUserId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>SB_DB.SB_BOARD_VOTE_TB.ip</code>.
     */
    public void setIp(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>SB_DB.SB_BOARD_VOTE_TB.ip</code>.
     */
    public String getIp() {
        return (String) get(2);
    }

    /**
     * Setter for <code>SB_DB.SB_BOARD_VOTE_TB.reg_dt</code>.
     */
    public void setRegDt(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>SB_DB.SB_BOARD_VOTE_TB.reg_dt</code>.
     */
    public Timestamp getRegDt() {
        return (Timestamp) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<UInteger, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<UInteger, String, String, Timestamp> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<UInteger, String, String, Timestamp> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.IP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.REG_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component1() {
        return getBoardNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getBoardNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value1(UInteger value) {
        setBoardNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value2(String value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value3(String value) {
        setIp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value4(Timestamp value) {
        setRegDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord values(UInteger value1, String value2, String value3, Timestamp value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbBoardVoteTbRecord
     */
    public SbBoardVoteTbRecord() {
        super(SbBoardVoteTb.SB_BOARD_VOTE_TB);
    }

    /**
     * Create a detached, initialised SbBoardVoteTbRecord
     */
    public SbBoardVoteTbRecord(UInteger boardNo, String userId, String ip, Timestamp regDt) {
        super(SbBoardVoteTb.SB_BOARD_VOTE_TB);

        set(0, boardNo);
        set(1, userId);
        set(2, ip);
        set(3, regDt);
    }
}