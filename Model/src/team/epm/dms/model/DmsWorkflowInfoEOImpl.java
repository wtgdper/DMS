package team.epm.dms.model;

import oracle.jbo.Key;
import oracle.jbo.RowIterator;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon Aug 03 16:39:39 CST 2015
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class DmsWorkflowInfoEOImpl extends EntityImpl {
    private static EntityDefImpl mDefinitionObject;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Id {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getId();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setId((String)value);
            }
        }
        ,
        Locale {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getLocale();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setLocale((String)value);
            }
        }
        ,
        WfName {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getWfName();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setWfName((String)value);
            }
        }
        ,
        Seq {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getSeq();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setSeq((Number)value);
            }
        }
        ,
        WfCom {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getWfCom();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setWfCom((String)value);
            }
        }
        ,
        CreatedAt {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getCreatedAt();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setCreatedAt((Date)value);
            }
        }
        ,
        UpdatedAt {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getUpdatedAt();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setUpdatedAt((Date)value);
            }
        }
        ,
        CreatedBy {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getCreatedBy();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setCreatedBy((String)value);
            }
        }
        ,
        UpdatedBy {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getUpdatedBy();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setUpdatedBy((String)value);
            }
        }
        ,
        WfStatus {
            public Object get(DmsWorkflowInfoEOImpl obj) {
                return obj.getWfStatus();
            }

            public void put(DmsWorkflowInfoEOImpl obj, Object value) {
                obj.setWfStatus((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(DmsWorkflowInfoEOImpl object);

        public abstract void put(DmsWorkflowInfoEOImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static final int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int ID = AttributesEnum.Id.index();
    public static final int LOCALE = AttributesEnum.Locale.index();
    public static final int WFNAME = AttributesEnum.WfName.index();
    public static final int SEQ = AttributesEnum.Seq.index();
    public static final int WFCOM = AttributesEnum.WfCom.index();
    public static final int CREATEDAT = AttributesEnum.CreatedAt.index();
    public static final int UPDATEDAT = AttributesEnum.UpdatedAt.index();
    public static final int CREATEDBY = AttributesEnum.CreatedBy.index();
    public static final int UPDATEDBY = AttributesEnum.UpdatedBy.index();
    public static final int WFSTATUS = AttributesEnum.WfStatus.index();

    /**
     * This is the default constructor (do not remove).
     */
    public DmsWorkflowInfoEOImpl() {
    }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        if (mDefinitionObject == null) {
            mDefinitionObject = EntityDefImpl.findDefObject("team.epm.dms.model.DmsWorkflowInfoEO");
        }
        return mDefinitionObject;
    }

    /**
     * Gets the attribute value for Id, using the alias name Id.
     * @return the Id
     */
    public String getId() {
        return (String)getAttributeInternal(ID);
    }

    /**
     * Sets <code>value</code> as the attribute value for Id.
     * @param value value to set the Id
     */
    public void setId(String value) {
        setAttributeInternal(ID, value);
    }

    /**
     * Gets the attribute value for Locale, using the alias name Locale.
     * @return the Locale
     */
    public String getLocale() {
        return (String)getAttributeInternal(LOCALE);
    }

    /**
     * Sets <code>value</code> as the attribute value for Locale.
     * @param value value to set the Locale
     */
    public void setLocale(String value) {
        setAttributeInternal(LOCALE, value);
    }

    /**
     * Gets the attribute value for WfName, using the alias name WfName.
     * @return the WfName
     */
    public String getWfName() {
        return (String)getAttributeInternal(WFNAME);
    }

    /**
     * Sets <code>value</code> as the attribute value for WfName.
     * @param value value to set the WfName
     */
    public void setWfName(String value) {
        setAttributeInternal(WFNAME, value);
    }

    /**
     * Gets the attribute value for Seq, using the alias name Seq.
     * @return the Seq
     */
    public Number getSeq() {
        return (Number)getAttributeInternal(SEQ);
    }

    /**
     * Sets <code>value</code> as the attribute value for Seq.
     * @param value value to set the Seq
     */
    public void setSeq(Number value) {
        setAttributeInternal(SEQ, value);
    }

    /**
     * Gets the attribute value for WfCom, using the alias name WfCom.
     * @return the WfCom
     */
    public String getWfCom() {
        return (String)getAttributeInternal(WFCOM);
    }

    /**
     * Sets <code>value</code> as the attribute value for WfCom.
     * @param value value to set the WfCom
     */
    public void setWfCom(String value) {
        setAttributeInternal(WFCOM, value);
    }

    /**
     * Gets the attribute value for CreatedAt, using the alias name CreatedAt.
     * @return the CreatedAt
     */
    public Date getCreatedAt() {
        return (Date)getAttributeInternal(CREATEDAT);
    }

    /**
     * Sets <code>value</code> as the attribute value for CreatedAt.
     * @param value value to set the CreatedAt
     */
    public void setCreatedAt(Date value) {
        setAttributeInternal(CREATEDAT, value);
    }

    /**
     * Gets the attribute value for UpdatedAt, using the alias name UpdatedAt.
     * @return the UpdatedAt
     */
    public Date getUpdatedAt() {
        return (Date)getAttributeInternal(UPDATEDAT);
    }

    /**
     * Sets <code>value</code> as the attribute value for UpdatedAt.
     * @param value value to set the UpdatedAt
     */
    public void setUpdatedAt(Date value) {
        setAttributeInternal(UPDATEDAT, value);
    }

    /**
     * Gets the attribute value for CreatedBy, using the alias name CreatedBy.
     * @return the CreatedBy
     */
    public String getCreatedBy() {
        return (String)getAttributeInternal(CREATEDBY);
    }

    /**
     * Sets <code>value</code> as the attribute value for CreatedBy.
     * @param value value to set the CreatedBy
     */
    public void setCreatedBy(String value) {
        setAttributeInternal(CREATEDBY, value);
    }

    /**
     * Gets the attribute value for UpdatedBy, using the alias name UpdatedBy.
     * @return the UpdatedBy
     */
    public String getUpdatedBy() {
        return (String)getAttributeInternal(UPDATEDBY);
    }

    /**
     * Sets <code>value</code> as the attribute value for UpdatedBy.
     * @param value value to set the UpdatedBy
     */
    public void setUpdatedBy(String value) {
        setAttributeInternal(UPDATEDBY, value);
    }

    /**
     * Gets the attribute value for WfStatus, using the alias name WfStatus.
     * @return the WfStatus
     */
    public String getWfStatus() {
        return (String)getAttributeInternal(WFSTATUS);
    }

    /**
     * Sets <code>value</code> as the attribute value for WfStatus.
     * @param value value to set the WfStatus
     */
    public void setWfStatus(String value) {
        setAttributeInternal(WFSTATUS, value);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index,
                                           AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value,
                                         AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }

    /**
     * @param id key constituent
     * @param locale key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(String id, String locale) {
        return new Key(new Object[]{id, locale});
    }


}
