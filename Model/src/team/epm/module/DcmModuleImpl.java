package team.epm.module;

import java.util.Map;

import oracle.adf.share.ADFContext;

import oracle.jbo.Session;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.ViewLinkImpl;


import oracle.jbo.server.ViewObjectImpl;

import team.epm.dcm.view.DcmComVsViewImpl;
import team.epm.dcm.view.DcmCombinationViewImpl;
import team.epm.dcm.view.DcmTemplateViewImpl;
import team.epm.dms.view.DmsRoleViewImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Nov 14 17:11:23 CST 2014
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class DcmModuleImpl extends ApplicationModuleImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public DcmModuleImpl() {
    }
    @Override
    protected void prepareSession(Session session) {
        super.prepareSession(session);
        Map sessionScope = ADFContext.getCurrent().getSessionScope();
        Object userId=sessionScope.get("userId");
        if(userId!=null){
            this.getSession().getUserData().put("userId", userId);
        }
    }
    /**
     * Container's getter for DcmComVsView.
     * @return DcmComVsView
     */
    public ViewObjectImpl getDcmComVsView() {
        return (ViewObjectImpl)findViewObject("DcmComVsView");
    }

    /**
     * Container's getter for DcmCombinationView.
     * @return DcmCombinationView
     */
    public ViewObjectImpl getDcmCombinationView() {
        return (ViewObjectImpl)findViewObject("DcmCombinationView");
    }

    /**
     * Container's getter for DcmErrorView.
     * @return DcmErrorView
     */
    public ViewObjectImpl getDcmErrorView() {
        return (ViewObjectImpl)findViewObject("DcmErrorView");
    }

    /**
     * Container's getter for DcmJobView.
     * @return DcmJobView
     */
    public ViewObjectImpl getDcmJobView() {
        return (ViewObjectImpl)findViewObject("DcmJobView");
    }

    /**
     * Container's getter for DcmRoleTemplateView.
     * @return DcmRoleTemplateView
     */
    public ViewObjectImpl getDcmRoleTemplateView() {
        return (ViewObjectImpl)findViewObject("DcmRoleTemplateView");
    }

    /**
     * Container's getter for DcmTemplateColumnView.
     * @return DcmTemplateColumnView
     */
    public ViewObjectImpl getDcmTemplateColumnView() {
        return (ViewObjectImpl)findViewObject("DcmTemplateColumnView");
    }

    /**
     * Container's getter for DcmTemplateCombinationView.
     * @return DcmTemplateCombinationView
     */
    public ViewObjectImpl getDcmTemplateCombinationView() {
        return (ViewObjectImpl)findViewObject("DcmTemplateCombinationView");
    }

    /**
     * Container's getter for DcmTemplateValidationView.
     * @return DcmTemplateValidationView
     */
    public ViewObjectImpl getDcmTemplateValidationView() {
        return (ViewObjectImpl)findViewObject("DcmTemplateValidationView");
    }

    /**
     * Container's getter for DcmTemplateView.
     * @return DcmTemplateView
     */
    public ViewObjectImpl getDcmTemplateView() {
        return (ViewObjectImpl)findViewObject("DcmTemplateView");
    }

    /**
     * Container's getter for DcmValidationView.
     * @return DcmValidationView
     */
    public ViewObjectImpl getDcmValidationView() {
        return (ViewObjectImpl)findViewObject("DcmValidationView");
    }

    /**
     * Container's getter for DcmCombinationView1.
     * @return DcmCombinationView1
     */
    public ViewObjectImpl getDcmCombinationView1() {
        return (ViewObjectImpl)findViewObject("DcmCombinationView1");
    }

    /**
     * Container's getter for DcmComVsView1.
     * @return DcmComVsView1
     */
    public ViewObjectImpl getDcmComVsView1() {
        return (ViewObjectImpl)findViewObject("DcmComVsView1");
}

    /**
     * Container's getter for combinationsVS.
     * @return combinationsVS
     */
    public ViewLinkImpl getcombinationsVS() {
        return (ViewLinkImpl)findViewLink("combinationsVS");
    }

    /**
     * Container's getter for DcmRoleTemplateView1.
     * @return DcmRoleTemplateView1
     */
    public ViewObjectImpl getDcmRoleTemplateView1() {
        return (ViewObjectImpl)findViewObject("DcmRoleTemplateView1");
    }



    /**
     * Container's getter for templatecolumnsVL.
     * @return templatecolumnsVL
     */
    public ViewLinkImpl gettemplatecolumnsVL() {
        return (ViewLinkImpl)findViewLink("templatecolumnsVL");
    }

    /**
     * Container's getter for DcmTemplateView1.
     * @return DcmTemplateView1
     */
    public ViewObjectImpl getDcmTemplateView1() {
        return (ViewObjectImpl)findViewObject("DcmTemplateView1");
    }

    /**
     * Container's getter for DcmTemplateColumnView2.
     * @return DcmTemplateColumnView2
     */
    public ViewObjectImpl getDcmTemplateColumnView1() {
        return (ViewObjectImpl)findViewObject("DcmTemplateColumnView1");
    }

    /**
     * Container's getter for templatecolumnsVL1.
     * @return templatecolumnsVL1
     */
    public ViewLinkImpl gettemplatecolumnsVL1() {
        return (ViewLinkImpl)findViewLink("templatecolumnsVL1");
    }

    /**
     * Container's getter for DcmTemplateColumnView2.
     * @return DcmTemplateColumnView2
     */
    public ViewObjectImpl getDcmTemplateColumnView2() {
        return (ViewObjectImpl)findViewObject("DcmTemplateColumnView2");
    }


    /**
     * Container's getter for DcmTemplateValidationView1.
     * @return DcmTemplateValidationView1
     */
    public ViewObjectImpl getDcmTemplateValidationView1() {
        return (ViewObjectImpl)findViewObject("DcmTemplateValidationView1");
    }

    /**
     * Container's getter for templateValidationVL1.
     * @return templateValidationVL1
     */
    public ViewLinkImpl gettemplateValidationVL1() {
        return (ViewLinkImpl)findViewLink("templateValidationVL1");
    }

    /**
     * Container's getter for DmsRoleView1.
     * @return DmsRoleView1
     */
    public DmsRoleViewImpl getDmsRoleView1() {
        return (DmsRoleViewImpl)findViewObject("DmsRoleView1");
    }

    /**
     * Container's getter for roleTemplateVL.
     * @return roleTemplateVL
     */
    public ViewLinkImpl getroleTemplateVL() {
        return (ViewLinkImpl)findViewLink("roleTemplateVL");
    }
    /**
     * Container's getter for DcmComVsQueryView.
     * @return DcmComVsQueryView
     */
    public ViewObjectImpl getDcmComVsQueryView() {
        return (ViewObjectImpl)findViewObject("DcmComVsQueryView");
    }

    /**
     * Container's getter for DcmTemplateCatView.
     * @return DcmTemplateCatView
     */
    public ViewObjectImpl getDcmTemplateCatView() {
        return (ViewObjectImpl)findViewObject("DcmTemplateCatView");
    }
}


