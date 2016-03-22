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
// ---    Mon Mar 21 15:49:24 CST 2016
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class InfaRoleWorkflowEOImpl extends EntityImpl {
    private static EntityDefImpl mDefinitionObject;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Id {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getId();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setId((String)value);
            }
        }
        ,
        RoleId {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getRoleId();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setRoleId((String)value);
            }
        }
        ,
        WorkflowId {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getWorkflowId();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setWorkflowId((String)value);
            }
        }
        ,
        Locale {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getLocale();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setLocale((String)value);
            }
        }
        ,
        CreatedAt {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getCreatedAt();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setCreatedAt((Date)value);
            }
        }
        ,
        UpdatedAt {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getUpdatedAt();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setUpdatedAt((Date)value);
            }
        }
        ,
        UpdatedBy {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getUpdatedBy();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setUpdatedBy((String)value);
            }
        }
        ,
        CreatedBy {
            public Object get(InfaRoleWorkflowEOImpl obj) {
                return obj.getCreatedBy();
            }

            public void put(InfaRoleWorkflowEOImpl obj, Object value) {
                obj.setCreatedBy((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(InfaRoleWorkflowEOImpl object);

        public abstract void put(InfaRoleWorkflowEOImpl object, Object value);

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
    public static final int ROLEID = AttributesEnum.RoleId.index();
    public static final int WORKFLOWID = AttributesEnum.WorkflowId.index();
    public static final int LOCALE = AttributesEnum.Locale.index();
    public static final int CREATEDAT = AttributesEnum.CreatedAt.index();
    public static final int UPDATEDAT = AttributesEnum.UpdatedAt.index();
    public static final int UPDATEDBY = AttributesEnum.UpdatedBy.index();
    public static final int CREATEDBY = AttributesEnum.CreatedBy.index();

    /**
     * This is the default constructor (do not remove).
     */
    public InfaRoleWorkflowEOImpl() {
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        if (mDefinitionObject == null) {
            mDefinitionObject = EntityDefImpl.findDefObject("team.epm.infa.model.InfaRoleWorkflowEO");
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
     * Gets the attribute value for RoleId, using the alias name RoleId.
     * @return the RoleId
     */
    public String getRoleId() {
        return (String)getAttributeInternal(ROLEID);
    }

    /**
     * Sets <code>value</code> as the attribute value for RoleId.
     * @param value value to set the RoleId
     */
    public void setRoleId(String value) {
        setAttributeInternal(ROLEID, value);
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
