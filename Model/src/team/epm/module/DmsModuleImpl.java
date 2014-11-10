package team.epm.module;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.RowSet;
import oracle.jbo.Session;
import oracle.jbo.ViewObject;
import oracle.jbo.server.ApplicationModuleImpl;


import oracle.jbo.server.ViewObjectImpl;

import team.epm.dms.view.DmsGroupViewImpl;
import team.epm.dms.view.DmsMenuTreeViewImpl;
import team.epm.dms.view.DmsUserGroupViewImpl;
import team.epm.dms.view.DmsUserViewImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Oct 09 17:49:44 CST 2014
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class DmsModuleImpl extends ApplicationModuleImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public DmsModuleImpl() {
    }

    /**
     * Container's getter for DmsAuditMsgView.
     * @return DmsAuditMsgView
     */
    public ViewObjectImpl getDmsAuditMsgView() {
        return (ViewObjectImpl)findViewObject("DmsAuditMsgView");
    }

    /**
     * Container's getter for DmsFunctionView.
     * @return DmsFunctionView
     */
    public ViewObjectImpl getDmsFunctionView() {
        return (ViewObjectImpl)findViewObject("DmsFunctionView");
    }

    /**
     * Container's getter for DmsGroupRoleView.
     * @return DmsGroupRoleView
     */
    public ViewObjectImpl getDmsGroupRoleView() {
        return (ViewObjectImpl)findViewObject("DmsGroupRoleView");
    }

    /**
     * Container's getter for DmsGroupView.
     * @return DmsGroupView
     */
    public ViewObjectImpl getDmsGroupView() {
        return (ViewObjectImpl)findViewObject("DmsGroupView");
    }

    /**
     * Container's getter for DmsMenuView.
     * @return DmsMenuView
     */
    public ViewObjectImpl getDmsMenuView() {
        return (ViewObjectImpl)findViewObject("DmsMenuView");
    }

    /**
     * Container's getter for DmsPropertyView.
     * @return DmsPropertyView
     */
    public ViewObjectImpl getDmsPropertyView() {
        return (ViewObjectImpl)findViewObject("DmsPropertyView");
    }

    /**
     * Container's getter for DmsRoleFunctionView.
     * @return DmsRoleFunctionView
     */
    public ViewObjectImpl getDmsRoleFunctionView() {
        return (ViewObjectImpl)findViewObject("DmsRoleFunctionView");
    }

    /**
     * Container's getter for DmsRoleMenuView.
     * @return DmsRoleMenuView
     */
    public ViewObjectImpl getDmsRoleMenuView() {
        return (ViewObjectImpl)findViewObject("DmsRoleMenuView");
    }

    /**
     * Container's getter for DmsRoleValueView.
     * @return DmsRoleValueView
     */
    public ViewObjectImpl getDmsRoleValueView() {
        return (ViewObjectImpl)findViewObject("DmsRoleValueView");
    }

    /**
     * Container's getter for DmsRoleView.
     * @return DmsRoleView
     */
    public ViewObjectImpl getDmsRoleView() {
        return (ViewObjectImpl)findViewObject("DmsRoleView");
    }

    /**
     * Container's getter for DmsUserGroupView.
     * @return DmsUserGroupView
     */
    public ViewObjectImpl getDmsUserGroupView() {
        return (ViewObjectImpl)findViewObject("DmsUserGroupView");
    }

    /**
     * Container's getter for DmsUserKeyView.
     * @return DmsUserKeyView
     */
    public ViewObjectImpl getDmsUserKeyView() {
        return (ViewObjectImpl)findViewObject("DmsUserKeyView");
    }

    /**
     * Container's getter for DmsUserView.
     * @return DmsUserView
     */
    public DmsUserViewImpl getDmsUserView() {
        return (DmsUserViewImpl)findViewObject("DmsUserView");
    }

    /**
     * Container's getter for DmsValueSetView.
     * @return DmsValueSetView
     */
    public ViewObjectImpl getDmsValueSetView() {
        return (ViewObjectImpl)findViewObject("DmsValueSetView");
    }

    /**
     * Container's getter for DmsMenuTreeView.
     * @return DmsMenuTreeView
     */
    public DmsMenuTreeViewImpl getDmsMenuTreeView() {
        return (DmsMenuTreeViewImpl)findViewObject("DmsMenuTreeView");
    }

    /**
     * Container's getter for DmsLookupView.
     * @return DmsLookupView
     */
    public ViewObjectImpl getDmsLookupView() {
        return (ViewObjectImpl)findViewObject("DmsLookupView");
    }

    @Override
    protected void prepareSession(Session session) {
        super.prepareSession(session);
        Map sessionScope = ADFContext.getCurrent().getSessionScope();
        Object userId = sessionScope.get("userId");
        if (userId != null) {
            this.getSession().getUserData().put("userId", userId);
        }
    }

    /**
     * Container's getter for DmsUserUngroupedView1.
     * @return DmsUserUngroupedView1
     */
    public ViewObjectImpl getDmsUserUngroupedView1() {
        return (ViewObjectImpl)findViewObject("DmsUserUngroupedView1");
    }

    /**
     * Container's getter for DmsUserUngroupedView.
     * @return DmsUserUngroupedView
     */
    public ViewObjectImpl getDmsUserUngroupedView() {
        return (ViewObjectImpl)findViewObject("DmsUserUngroupedView");
    }

    /**
     * Container's getter for DmsUserGroupedView.
     * @return DmsUserGroupedView
     */
    public ViewObjectImpl getDmsUserGroupedView() {
        return (ViewObjectImpl)findViewObject("DmsUserGroupedView");
    }

    /**
     * Container's getter for DmsGroupsForRoleView1.
     * @return DmsGroupsForRoleView1
     */
    public ViewObjectImpl getDmsGroupsForRoleView() {
        return (ViewObjectImpl)findViewObject("DmsGroupsForRoleView");
    }

    public List<Row> getValuesFromValueSet(String source,
                                      String local) {
        List<Row> valueList=new ArrayList<Row>();
        String sql = "select ID,MEANING from " + source + " where local='" + local + "' ";
        ViewObject valuesVO =
            this.createViewObjectFromQueryStmt("", sql);
        valuesVO.executeQuery();
        RowSet rows = valuesVO.getRowSet();
        while (rows.hasNext()) {
            valueList.add(rows.next());
        }
        return valueList;
    }
}
