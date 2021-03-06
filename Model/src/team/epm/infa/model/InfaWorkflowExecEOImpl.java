package team.epm.infa.model;

import oracle.jbo.AttributeList;
import oracle.jbo.Key;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.TransactionEvent;

import org.apache.commons.lang.ObjectUtils;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Mar 16 20:33:18 CST 2016
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class InfaWorkflowExecEOImpl extends EntityImpl {
    private static EntityDefImpl mDefinitionObject;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Id {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getId();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setId((String)value);
            }
        }
        ,
        WorkflowId {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getWorkflowId();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setWorkflowId((String)value);
            }
        }
        ,
        Params {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getParams();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setParams((String)value);
            }
        }
        ,
        ExecStatus {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getExecStatus();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setExecStatus((String)value);
            }
        }
        ,
        RunId {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getRunId();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setRunId((String)value);
            }
        }
        ,
        LogText {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getLogText();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setLogText((String)value);
            }
        }
        ,
        HasException {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getHasException();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setHasException((String)value);
            }
        }
        ,
        CreatedAt {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getCreatedAt();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setCreatedAt((Date)value);
            }
        }
        ,
        UpdatedAt {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getUpdatedAt();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setUpdatedAt((Date)value);
            }
        }
        ,
        UpdatedBy {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getUpdatedBy();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setUpdatedBy((String)value);
            }
        }
        ,
        CreatedBy {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getCreatedBy();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setCreatedBy((String)value);
            }
        }
        ,
        FinishTime {
            public Object get(InfaWorkflowExecEOImpl obj) {
                return obj.getFinishTime();
            }

            public void put(InfaWorkflowExecEOImpl obj, Object value) {
                obj.setFinishTime((Date)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(InfaWorkflowExecEOImpl object);

        public abstract void put(InfaWorkflowExecEOImpl object, Object value);

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

    @Override
    protected void prepareForDML(int operation, TransactionEvent transactionEvent) {
        super.prepareForDML(operation, transactionEvent);
        if (operation == DML_UPDATE) {
            //update the updatedat and updatedby attributes
            this.setAttribute("UpdatedAt",
                              new Date(new java.sql.Timestamp(System.currentTimeMillis())));
            this.setAttribute("UpdatedBy",
                              ObjectUtils.toString(this.getDBTransaction().getSession().getUserData().get("userId")));
        }
    }

    public static final int ID = AttributesEnum.Id.index();
    public static final int WORKFLOWID = AttributesEnum.WorkflowId.index();
    public static final int PARAMS = AttributesEnum.Params.index();
    public static final int EXECSTATUS = AttributesEnum.ExecStatus.index();
    public static final int RUNID = AttributesEnum.RunId.index();
    public static final int LOGTEXT = AttributesEnum.LogText.index();
    public static final int HASEXCEPTION = AttributesEnum.HasException.index();
    public static final int CREATEDAT = AttributesEnum.CreatedAt.index();
    public static final int UPDATEDAT = AttributesEnum.UpdatedAt.index();
    public static final int UPDATEDBY = AttributesEnum.UpdatedBy.index();
    public static final int CREATEDBY = AttributesEnum.CreatedBy.index();
    public static final int FINISHTIME = AttributesEnum.FinishTime.index();

    /**
     * This is the default constructor (do not remove).
     */
    public InfaWorkflowExecEOImpl() {
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        if (mDefinitionObject == null) {
            mDefinitionObject = EntityDefImpl.findDefObject("team.epm.infa.model.InfaWorkflowExecEO");
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
     * Gets the attribute value for WorkflowId, using the alias name WorkflowId.
     * @return the WorkflowId
     */
    public String getWorkflowId() {
        return (String)getAttributeInternal(WORKFLOWID);
    }

    /**
     * Sets <code>value</code> as the attribute value for WorkflowId.
     * @param value value to set the WorkflowId
     */
    public void setWorkflowId(String value) {
        setAttributeInternal(WORKFLOWID, value);
    }

    /**
     * Gets the attribute value for Params, using the alias name Params.
     * @return the Params
     */
    public String getParams() {
        return (String)getAttributeInternal(PARAMS);
    }

    /**
     * Sets <code>value</code> as the attribute value for Params.
     * @param value value to set the Params
     */
    public void setParams(String value) {
        setAttributeInternal(PARAMS, value);
    }

    /**
     * Gets the attribute value for ExecStatus, using the alias name ExecStatus.
     * @return the ExecStatus
     */
    public String getExecStatus() {
        return (String)getAttributeInternal(EXECSTATUS);
    }

    /**
     * Sets <code>value</code> as the attribute value for ExecStatus.
     * @param value value to set the ExecStatus
     */
    public void setExecStatus(String value) {
        setAttributeInternal(EXECSTATUS, value);
    }

    /**
     * Gets the attribute value for RunId, using the alias name RunId.
     * @return the RunId
     */
    public String getRunId() {
        return (String)getAttributeInternal(RUNID);
    }

    /**
     * Sets <code>value</code> as the attribute value for RunId.
     * @param value value to set the RunId
     */
    public void setRunId(String value) {
        setAttributeInternal(RUNID, value);
    }

    /**
     * Gets the attribute value for LogText, using the alias name LogText.
     * @return the LogText
     */
    public String getLogText() {
        return (String)getAttributeInternal(LOGTEXT);
    }

    /**
     * Sets <code>value</code> as the attribute value for LogText.
     * @param value value to set the LogText
     */
    public void setLogText(String value) {
        setAttributeInternal(LOGTEXT, value);
    }

    /**
     * Gets the attribute value for HasException, using the alias name HasException.
     * @return the HasException
     */
    public String getHasException() {
        return (String)getAttributeInternal(HASEXCEPTION);
    }

    /**
     * Sets <code>value</code> as the attribute value for HasException.
     * @param value value to set the HasException
     */
    public void setHasException(String value) {
        setAttributeInternal(HASEXCEPTION, value);
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
     * Gets the attribute value for FinishTime, using the alias name FinishTime.
     * @return the FinishTime
     */
    public Date getFinishTime() {
        return (Date)getAttributeInternal(FINISHTIME);
    }

    /**
     * Sets <code>value</code> as the attribute value for FinishTime.
     * @param value value to set the FinishTime
     */
    public void setFinishTime(Date value) {
        setAttributeInternal(FINISHTIME, value);
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

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(String id) {
        return new Key(new Object[]{id});
    }

    /**
     * Add attribute defaulting logic in this method.
     * @param attributeList list of attribute names/values to initialize the row
     */
    protected void create(AttributeList attributeList) {
        super.create(attributeList);
    }

    /**
     * Add entity remove logic in this method.
     */
    public void remove() {
        super.remove();
    }

    /**
     * Add locking logic here.
     */
    public void lock() {
        super.lock();
    }

    /**
     * Custom DML update/insert/delete logic here.
     * @param operation the operation type
     * @param e the transaction event
     */
    protected void doDML(int operation, TransactionEvent e) {
        super.doDML(operation, e);
    }
}
