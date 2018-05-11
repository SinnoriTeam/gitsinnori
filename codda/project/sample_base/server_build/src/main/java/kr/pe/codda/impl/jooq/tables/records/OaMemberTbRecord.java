/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.tables.OaMemberTb;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


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
public class OaMemberTbRecord extends UpdatableRecordImpl<OaMemberTbRecord> implements Record5<String, String, String, String, Timestamp> {

    private static final long serialVersionUID = -336144409;

    /**
     * Setter for <code>SB_DB.OA_MEMBER_TB.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>SB_DB.OA_MEMBER_TB.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>SB_DB.OA_MEMBER_TB.pwd</code>.
     */
    public void setPwd(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>SB_DB.OA_MEMBER_TB.pwd</code>.
     */
    public String getPwd() {
        return (String) get(1);
    }

    /**
     * Setter for <code>SB_DB.OA_MEMBER_TB.email</code>.
     */
    public void setEmail(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>SB_DB.OA_MEMBER_TB.email</code>.
     */
    public String getEmail() {
        return (String) get(2);
    }

    /**
     * Setter for <code>SB_DB.OA_MEMBER_TB.phone</code>.
     */
    public void setPhone(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>SB_DB.OA_MEMBER_TB.phone</code>.
     */
    public String getPhone() {
        return (String) get(3);
    }

    /**
     * Setter for <code>SB_DB.OA_MEMBER_TB.regdate</code>.
     */
    public void setRegdate(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>SB_DB.OA_MEMBER_TB.regdate</code>.
     */
    public Timestamp getRegdate() {
        return (Timestamp) get(4);
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
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, String, String, String, Timestamp> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, String, String, String, Timestamp> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return OaMemberTb.OA_MEMBER_TB.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return OaMemberTb.OA_MEMBER_TB.PWD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return OaMemberTb.OA_MEMBER_TB.EMAIL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return OaMemberTb.OA_MEMBER_TB.PHONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return OaMemberTb.OA_MEMBER_TB.REGDATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getPwd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getPhone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component5() {
        return getRegdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getPwd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getPhone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getRegdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OaMemberTbRecord value1(String value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OaMemberTbRecord value2(String value) {
        setPwd(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OaMemberTbRecord value3(String value) {
        setEmail(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OaMemberTbRecord value4(String value) {
        setPhone(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OaMemberTbRecord value5(Timestamp value) {
        setRegdate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OaMemberTbRecord values(String value1, String value2, String value3, String value4, Timestamp value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OaMemberTbRecord
     */
    public OaMemberTbRecord() {
        super(OaMemberTb.OA_MEMBER_TB);
    }

    /**
     * Create a detached, initialised OaMemberTbRecord
     */
    public OaMemberTbRecord(String id, String pwd, String email, String phone, Timestamp regdate) {
        super(OaMemberTb.OA_MEMBER_TB);

        set(0, id);
        set(1, pwd);
        set(2, email);
        set(3, phone);
        set(4, regdate);
    }
}