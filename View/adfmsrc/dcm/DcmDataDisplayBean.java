package dcm;

import common.ADFUtils;
import common.DmsLog;
import common.DmsUtils;

import common.JSFUtils;

import common.TablePagination;

import common.lov.DmsComBoxLov;
import common.lov.ValueSetRow;

import dcm.combinantion.CombinationEO;

import dcm.template.TemplateEO;

import dcm.template.TemplateEntity;

import dms.login.Person;

import dms.quartz.core.QuartzSchedulerSingleton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.sql.Types;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.faces.model.SelectItem;

import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputComboboxListOfValues;
import oracle.adf.view.rich.component.rich.input.RichInputFile;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.adf.view.rich.component.rich.output.RichPanelCollection;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adf.view.rich.event.QueryEvent;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.server.DBTransaction;

import org.apache.commons.lang.ObjectUtils;
import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.event.SortEvent;
import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.SortCriterion;
import org.apache.myfaces.trinidad.model.UploadedFile;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.hexj.excelhandler.reader.ExcelReaderUtil;

import org.quartz.SchedulerException;

import team.epm.dcm.view.DcmCombinationViewRowImpl;
import team.epm.dcm.view.DcmTemplateColumnViewRowImpl;
import team.epm.dcm.view.DcmTemplateViewRowImpl;

public class DcmDataDisplayBean extends TablePagination{
    private CollectionModel dataModel;
    //模板信息
    private TemplateEO curTempalte;
    //模板列信息
    private List<ColumnDef> colsdef = new ArrayList<ColumnDef>();
    //组合信息
    private CombinationEO curCombiantion;
    //是否增量导入
    private boolean isIncrement = true;
    //是否是2007及以上格式
    private boolean isXlsx = true;
    //组合信息
    private List<ComHeader> templateHeader = new ArrayList<ComHeader>();
    //值集信息
    private Map valueSet=new HashMap();
    //是否可编辑
    private boolean isEditable=true;
    //是否冻结
    private boolean isClose=false;
    //当前组合是否可编辑
    private boolean curCombinationRecordEditable=true;
    //
    private String curCombiantionRecord;
    //日志
    private static ADFLogger _logger =ADFLogger.createADFLogger(DcmDataDisplayBean.class);
    //页面绑定组件
    private RichInputFile fileInput;
    private RichInputComboboxListOfValues comIclovs;
    private RichPanelCollection panelaCollection;
    private Person curUser;
    private RichPopup errorWindow;
    private RichPopup dataImportWnd;
    private RichPopup dataExportWnd;
    private RichPopup templateExportWnd;
    //排序
    private List<SortCriterion> sortCriterions;
    //搜索
    private FilterableQueryDescriptor queryDescriptor=new DcmQueryDescriptor();
    private Map filters;
    private boolean useQuartz = false;
    //和利时特殊模板
    private boolean special = false;
    //组合状态是否禁用
    private boolean defultOpenCom = false;
    private boolean batchExcel = false;
    private List<String> batchTempList;
    private RichPopup batchErrPop;
    private RichPopup subPop;
    private String subSql;
    private String subTempId;
    private RichTable subTable;
    private Map<String,String> subVNameMap;
    private Map<String,String> subValuesMap;
    //搜索
    private FilterableQueryDescriptor subDescriptor=new DcmQueryDescriptor();
    //初始化
    public DcmDataDisplayBean() {
        this.curUser =(Person)ADFContext.getCurrent().getSessionScope().get("cur_user");
        this.dataModel = new DcmDataTableModel();
        this.initTemplate();
        this.initCombination();
        this.queryTemplateData();
        //提交状态
        if("Y".equals(this.curTempalte.getIsCloseRecord())){
            this.initSubSql();
        }
    }

    public CollectionModel getDataModel() {
        return this.dataModel;
    }
    //值发生改变时更改数据的状态为更新
    public void valueChangeListener(ValueChangeEvent valueChangeEvent) {
        Map rowData = (Map)this.dataModel.getRowData();
        //编辑时仅能选中一行
        if (((DcmDataTableModel)this.dataModel).getSelectedRows().size() > 1) {
            String msg =DmsUtils.getMsg("dcm.msg.can_not_select_multiple_row");
            JSFUtils.addFacesInformationMessage(msg);
            return;
        }
        if (null == rowData.get("OPERATION")) {
            rowData.put("OPERATION", DcmDataTableModel.OPERATE_UPDATE);
        }
    }
    //行选中时设置当前选中行
    public void rowSelectionListener(SelectionEvent selectionEvent) {
        RichTable table = (RichTable)selectionEvent.getSource();
        RowKeySet rks = selectionEvent.getAddedSet();
        if (rks != null) {
            int setSize = rks.size();
            if (setSize == 0) {
                return;
            }
            Object rowKey = rks.iterator().next();
            table.setRowKey(rowKey);
        }
    }
    //新增数据行操作
    public void operation_new() {
        List<Map> modelData = (List<Map>)this.dataModel.getWrappedData();
        Map newRow = new HashMap();
        for (ColumnDef col : this.colsdef) {
            newRow.put(col.getDbTableCol(), null);
        }
        newRow.put("OPERATION", DcmDataTableModel.OPERATE_CREATE);
        modelData.add(0, newRow);
    }
    //删除数据行操作
    public void operation_delete() {
        List<Map> modelData = (List<Map>)this.dataModel.getWrappedData();
        RowKeySet keySet =
            ((DcmDataTableModel)this.dataModel).getSelectedRows();
        for (Object key : keySet) {
            Map rowData = (Map)this.dataModel.getRowData(key);
            //若为新增操作则直接从数据集删除数据
            if (DcmDataTableModel.OPERATE_CREATE.equals(rowData.get("OPERATION"))) {
                modelData.remove(rowData);
            } 
            //若为更新或数据未修改则直接将数据集数据标记为删除
            else if (DcmDataTableModel.OPERATE_UPDATE.equals(rowData.get("OPERATION")) ||
                       null == rowData.get("OPERATION")) {
                rowData.put("OPERATION", DcmDataTableModel.OPERATE_DELETE);
            }
            //已经为删除状态的数据无需做任何处理
        }
    }
    //数据保存操作
    public void operation_save() {
        boolean flag = true;
        String curComRecordId = this.curCombiantionRecord;
        List<Map> modelData = (List<Map>)this.dataModel.getWrappedData();
        StringBuffer sql_insert = new StringBuffer();
        StringBuffer sql_value = new StringBuffer();
        sql_insert.append("INSERT INTO \"").append(this.curTempalte.getTmpTable()).append("\"(");
        sql_insert.append("TEMPLATE_ID,COM_RECORD_ID,ORIGIN_ROWID");
        sql_insert.append(",CREATED_BY,UPDATED_BY,CREATED_AT,UPDATED_AT,SHEET_NAME,ROW_NO,EDIT_TYPE");
        sql_value.append(" VALUES('").append(this.curTempalte.getId()).append("'");
        sql_value.append(",'").append(ObjectUtils.toString(curComRecordId)).append("'");
        sql_value.append(",?,'").append(this.curUser.getId()).append("'");
        sql_value.append(",'").append(this.curUser.getId()).append("'");
        sql_value.append(",SYSDATE,SYSDATE,'NA',?,?");
        for (int i = 0; i < this.colsdef.size(); i++) {
            sql_insert.append(",COLUMN").append(i + 1);
            sql_value.append(",?");
        }
        sql_insert.append(")");
        sql_value.append(")");
        try {
            DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
            this.clearTmpTableAndErrTable(curComRecordId);
            //将数据插入临时表
            PreparedStatement stat =trans.createPreparedStatement(sql_insert.toString() +sql_value.toString(), 0);
            int rowNo=1;
            for (Map rowData : modelData) {
                stat.setInt(2, rowNo);
                rowNo++;
                if (DcmDataTableModel.OPERATE_CREATE.equals(rowData.get("OPERATION"))) {
                    stat.setString(3, "NEW");
                } else if (DcmDataTableModel.OPERATE_DELETE.equals(rowData.get("OPERATION"))) {
                    stat.setString(3, "DELETE");
                } else if (DcmDataTableModel.OPERATE_UPDATE.equals(rowData.get("OPERATION"))) {
                    stat.setString(3, "UPDATE");
                }
                if (null != rowData.get("OPERATION")) {
                    for (int i = 0; i < this.colsdef.size(); i++) {
                        stat.setString(4 + i,(String)rowData.get(this.colsdef.get(i).getDbTableCol()));
                    }
                    stat.setString(1, (String)rowData.get("ROW_ID"));
                    stat.addBatch();
                }
            }   
            stat.executeBatch();
            trans.commit();
            stat.close();
            flag = this.handleData("EDIT", curComRecordId);
            if(flag){ this.queryTemplateData();}
        } catch (Exception e) {
            flag = false;
            if (e.getMessage().length() > 2048) {
                this.writeErrorMsg(e.getMessage().substring(0, 2048),curComRecordId);
            } else {
                this.writeErrorMsg(e.getMessage(),curComRecordId);
            }
            this._logger.severe(e);
        }
        if(flag){
            DmsLog dmsLog = new DmsLog();
            dmsLog.operationLog(this.curUser.getAcc(),this.curTempalte.getId(),this.getCurComRecordText(),"UPDATE");
        }
        if (!flag) {
            this.showErrorPop();
        }
    }
    //数据重置则直接刷新数据
    public void operation_reset() {
        this.queryTemplateData();
    }
    //数据导入操作
    public void operation_import(ActionEvent actionEvent) throws SQLException {
        this.dataImportWnd.cancel();
        String curComRecordId = this.curCombiantionRecord;
        //组合找不到
        if (this.curTempalte.getCombinationId() != null && curComRecordId == null) {
            JSFUtils.addFacesErrorMessage(DmsUtils.getMsg("dcm.inform.select_correct_combination"));
            return;
        }
        //上传文件为空
        System.out.println(this.fileInput.getValue()+"777777");
        if (null == this.fileInput.getValue()) {
            JSFUtils.addFacesErrorMessage(DmsUtils.getMsg("dcm.plz_select_import_file"));
            return;
        }
        //获取文件上传路径
        String filePath = this.uploadFile();
        System.out.println(filePath);
        this.fileInput.resetValue();
        if (null == filePath) {
            return;
        }
        
        //如果批量导入，验证sheet页权限
        if(this.batchExcel){
            //删除批量导入记录表
            this.clearBatchErrorTable();
            this.batchTempList = new ArrayList<String>();
            XSSFWorkbook xs;
            try {
                OPCPackage opcp = OPCPackage.open(filePath);
                xs = new XSSFWorkbook(opcp);
                int stNum = xs.getNumberOfSheets();
                for(int i = 0;i < stNum ; i++){
                    XSSFSheet st = xs.getSheetAt(i);
                    this.vaildateSheetAuthority(st.getSheetName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //读取excel数据到数据库临时表
        List<TemplateEntity> tempList = new ArrayList<TemplateEntity>();
        if(this.batchExcel){
            tempList = this.getTempList(batchTempList);
            if (!this.batchHandleExcel(filePath, curComRecordId, tempList)) {
                return;
            }
        }else{
            if (!this.handleExcel(filePath, curComRecordId)) {
                return;
            }
        }
        
        //是否后台导入
        if(useQuartz){
            if(this.excelValidate(this.isIncrement ? "INCREMENT" : "REPLACE",curComRecordId)){
                try {
                    this.newImportJob(filePath);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }else{
                //若出现错误则显示错误信息提示框
                this.showErrorPop();
            }
        }else if(this.batchExcel){
            //依次数据处理，前置，校验，善后
            boolean mark = true;
            for(TemplateEntity temp : tempList){
                mark = this.handleDataBatch(this.isIncrement ? "INCREMENT" : "REPLACE", curComRecordId, temp);
                if(mark){
                    this.writeBatchError(temp.getTemplateName(), "成功", "", "1");   
                }
            }
            //弹出日志框
            this.showBatchLog(actionEvent);
            //刷新数据
            this.queryTemplateData();
        }else{
            //进行数据处理（前置程序、校验和善后程序）
            if (this.handleData(this.isIncrement ? "INCREMENT" : "REPLACE",curComRecordId)) {
                DmsLog dmsLog = new DmsLog();
                dmsLog.operationLog(this.curUser.getAcc(),this.curTempalte.getId(),this.getCurComRecordText(),"IMPORT");
                String msg = DmsUtils.getMsg("dcm.inform.data_import_success");
                JSFUtils.addFacesInformationMessage(msg);
            } else {
                //若出现错误则显示错误信息提示框
                this.showErrorPop();
            }
            //刷新数据
            this.queryTemplateData();
        }

    }
    
    private boolean vaildateSheetAuthority(String sheetName){
        boolean flag = true;
        //query template by sheetName
        ViewObject vo = DmsUtils.getDcmApplicationModule().getDcmTemplateView();
        vo.setWhereClause("NAME = '" + sheetName + "' AND LOCALE = '" + this.curUser.getLocale() + "'");
        vo.executeQuery();
        vo.setWhereClause(null);
        String tempId = "";
        if(vo.hasNext()){
            Row row = vo.first();
            tempId = row.getAttribute("Id").toString();
        }else{
            //找不到模板
            flag = false;
            this.writeBatchError(sheetName, "失败","Sheet页名称错误，匹配不到系统模板！", "1");
            return flag;
        }
        //校验用户是否有模板权限
        ViewObject authVo = DmsUtils.getDcmApplicationModule().getDcmUserTemplateView();
        authVo.setWhereClause("TEMPLATE_ID = '" + tempId + "' AND READ_ONLY = 'N'" );
        authVo.executeQuery();
        authVo.setWhereClause(null);
        if(!authVo.hasNext()){
            //用户没有权限
            flag = false;
            this.writeBatchError(sheetName, "失败", "没有该模板导入权限，不执行导入！", "1");
            return flag;
        }
        //能在该组合下导入，则用户在其他模板下也具有该组合值集权限
        //校验组合是否打开
        ViewObject comVo = DmsUtils.getDcmApplicationModule().getDcmTemplateCombinationView();
        comVo.setWhereClause("TEMPLATE_ID = '" + tempId + "' AND COM_RECORD_ID ='" + this.curCombiantionRecord + "' AND STATUS = 'OPEN'");
        comVo.executeQuery();
        comVo.setWhereClause(null);
        if(!comVo.hasNext()){
            flag = false;
            this.writeBatchError(sheetName, "失败", "当前组合处于关闭状态，无法导入数据！", "1");
        }
        
        if(flag){
            this.batchTempList.add(tempId);
        }
        return flag;
    }
    
    private void newImportJob(String filePath) throws SchedulerException{
        HashMap<String,String> jobDataMap = new HashMap<String,String>();
   
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dateFormat.format(new Date());
        String jobName = this.curUser.getAcc() + "-" + date;
        //import info
        int startL = (int)this.curTempalte.getDataStartLine().getValue();
        jobDataMap.put("startLine", ObjectUtils.toString(startL));
        jobDataMap.put("tempId",this.curTempalte.getId());
        jobDataMap.put("comId",this.curCombiantionRecord);
        jobDataMap.put("tempTable",this.curTempalte.getTmpTable());
        jobDataMap.put("colSize",ObjectUtils.toString(this.colsdef.size()));
        jobDataMap.put("userId",this.curUser.getId());
        jobDataMap.put("userName",this.curUser.getName());
        jobDataMap.put("tempName",this.curTempalte.getName());
        jobDataMap.put("filePath", filePath);
        jobDataMap.put("impPro", this.curTempalte.getHandleProgram());
        jobDataMap.put("afterPro", this.curTempalte.getAfterProgram());
        jobDataMap.put("mode",this.isIncrement ? "INCREMENT" : "REPLACE");
        jobDataMap.put("locale", this.curUser.getLocale());
        //job info
        jobDataMap.put("jobType", "ImportData");
        jobDataMap.put("connType", "jdbcDS");
        jobDataMap.put("jndiName", "jdbc/DMSConnDS");
        jobDataMap.put("jobName", jobName);
        jobDataMap.put("jobGroup", "DEFAULT");
        QuartzSchedulerSingleton qss = QuartzSchedulerSingleton.getInstance();
        qss.scheduleJobMap(jobName, "dms.quartz.job.DBJob", "NULL", jobDataMap);
        
        
    }
    
    //只执行数据校验
    private boolean excelValidate(String mode ,String curComRecordId){
        boolean successFlag = true;
        DBTransaction trans = (DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        try {
            //执行前置程序
            if (this.curTempalte.getPreProgram() != null) {
                CallableStatement prcs =trans.createCallableStatement("{CALl " + this.curTempalte.getPreProgram() +"(?,?,?,?,?)}", 0);
                prcs.setString(1, this.curTempalte.getId());
                prcs.setString(2, curComRecordId);
                prcs.setString(3, this.curUser.getId());
                prcs.setString(4, mode);
                prcs.setString(5, this.curUser.getLocale());
                prcs.execute();
                trans.commit();
                prcs.close();
            }
            //执行校验程序
            ViewObject vo =DmsUtils.getDcmApplicationModule().getDcmValidationQueryView();
            vo.setNamedWhereClauseParam("templateId", this.curTempalte.getId());
            vo.executeQuery();
            vo.reset();
            while (vo.hasNext()) {
                Row row = vo.next();
                CallableStatement cs =trans.createCallableStatement("{CALL " + row.getAttribute("Program") +"(?,?,?,?,?,?,?,?,?)}", 0);
                cs.setString(1, (String)row.getAttribute("ValidationId"));
                cs.setString(2, this.curTempalte.getId());
                cs.setString(3, curComRecordId);
                //获取校验对应的临时表列
                for (int i = 1; i <= this.colsdef.size(); i++) {
                    if (this.colsdef.get(i -1).getDbTableCol().equals((String)row.getAttribute("DbTableCol"))) {
                        cs.setString(4, "COLUMN" + i);
                        break;
                    }
                }
                cs.setString(5, (String)row.getAttribute("DbTableCol"));
                cs.setString(6, mode);
                cs.setString(7, this.curUser.getLocale());
                cs.setString(8, (String)row.getAttribute("Args"));
                cs.registerOutParameter(9, Types.VARCHAR);
                cs.execute();
                //若返回值为N则校验失败
                if ("N".equals(cs.getString(9))) {
                    successFlag = false;
                }
                trans.commit();
                cs.close();
            }
        } catch (Exception e) {
            successFlag = false;
            String msg = e.getMessage();
            if (msg.length() > 2048) {
                msg = msg.substring(0, 2048);
            }
            this.writeErrorMsg(msg, curComRecordId);
            this._logger.severe(e);
        }
        return successFlag;    
    }
    
    //执行数据校验和数据转移
    private boolean handleDataBatch(String mode, String curComRecordId,TemplateEntity temp) {
        boolean successFlag = true;
        DBTransaction trans = (DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        try {
            //执行前置程序
            if (!"".equals(temp.getPreGrogram()) && temp.getPreGrogram() != null) {
                CallableStatement prcs =trans.createCallableStatement("{CALl " + temp.getPreGrogram() +"(?,?,?,?,?)}", 0);
                prcs.setString(1, temp.getTemplateId());
                prcs.setString(2, curComRecordId);
                prcs.setString(3, this.curUser.getId());
                prcs.setString(4, mode);
                prcs.setString(5, this.curUser.getLocale());
                prcs.execute();
                trans.commit();
                prcs.close();
            }
            //执行校验程序
            ViewObject vo =DmsUtils.getDcmApplicationModule().getDcmValidationQueryView();
            vo.setNamedWhereClauseParam("templateId", temp.getTemplateId());
            vo.executeQuery();
            vo.reset();
            while (vo.hasNext()) {
                Row row = vo.next();
                CallableStatement cs =trans.createCallableStatement("{CALL " + row.getAttribute("Program") +"(?,?,?,?,?,?,?,?,?)}", 0);
                cs.setString(1, (String)row.getAttribute("ValidationId"));
                cs.setString(2, temp.getTemplateId());
                cs.setString(3, curComRecordId);
                //获取校验对应的临时表列
                for (int i = 1; i <= temp.getColsdef().size(); i++) {
                    if (temp.getColsdef().get(i -1).equals((String)row.getAttribute("DbTableCol"))) {
                        cs.setString(4, "COLUMN" + i);
                        break;
                    }
                }
                cs.setString(5, (String)row.getAttribute("DbTableCol"));
                cs.setString(6, mode);
                cs.setString(7, this.curUser.getLocale());
                cs.setString(8, (String)row.getAttribute("Args"));
                cs.registerOutParameter(9, Types.VARCHAR);
                cs.execute();
                //若返回值为N则校验失败
                if ("N".equals(cs.getString(9))) {
                    successFlag = false;
                    this.copyBatchError(temp.getTemplateId());
                }
                trans.commit();
                cs.close();
            }
            if(successFlag){
                //若校验通过则执行数据导入
                CallableStatement cs =trans.createCallableStatement("{CALl " + temp.getImpGrogram() + "(?,?,?,?,?)}", 0);
                cs.setString(1, temp.getTemplateId());
                cs.setString(2, curComRecordId);
                cs.setString(3, this.curUser.getId());
                cs.setString(4, mode);
                cs.setString(5, this.curUser.getLocale());
                cs.execute();
                trans.commit();
                cs.close();
                //执行善后程序
                if (!"".equals(temp.getAfterGrogram()) && temp.getAfterGrogram() != null) {
                    CallableStatement afcs = trans.createCallableStatement("{CALl " + temp.getAfterGrogram() +"(?,?,?,?,?)}", 0);
                    afcs.setString(1, temp.getTemplateId());
                    afcs.setString(2, curComRecordId);
                    afcs.setString(3, this.curUser.getId());
                    afcs.setString(4, mode);
                    afcs.setString(5, this.curUser.getLocale());
                    afcs.execute();
                    trans.commit();
                    afcs.close();
                }
            }else{
                this.writeBatchError(temp.getTemplateName(), "失败", "校验不通过！", "1");    
            }
        } catch (Exception e) {
            successFlag = false;
            String msg = e.getMessage();
            if (msg.length() > 2048) {
                msg = msg.substring(0, 2048);
            }
            this.writeBatchError(temp.getTemplateName(), "失败", msg, "1");
            this._logger.severe(e);
        }
        return successFlag;
    }
    
    //执行数据校验和数据转移
    private boolean handleData(String mode, String curComRecordId) {
        boolean successFlag = true;
        DBTransaction trans = (DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        try {
            //执行前置程序
            if (this.curTempalte.getPreProgram() != null) {
                CallableStatement prcs =trans.createCallableStatement("{CALl " + this.curTempalte.getPreProgram() +"(?,?,?,?,?)}", 0);
                prcs.setString(1, this.curTempalte.getId());
                prcs.setString(2, curComRecordId);
                prcs.setString(3, this.curUser.getId());
                prcs.setString(4, mode);
                prcs.setString(5, this.curUser.getLocale());
                prcs.execute();
                trans.commit();
                prcs.close();
            }
            //执行校验程序
            ViewObject vo =DmsUtils.getDcmApplicationModule().getDcmValidationQueryView();
            vo.setNamedWhereClauseParam("templateId", this.curTempalte.getId());
            vo.executeQuery();
            vo.reset();
            while (vo.hasNext()) {
                Row row = vo.next();
                CallableStatement cs =trans.createCallableStatement("{CALL " + row.getAttribute("Program") +"(?,?,?,?,?,?,?,?,?)}", 0);
                cs.setString(1, (String)row.getAttribute("ValidationId"));
                cs.setString(2, this.curTempalte.getId());
                cs.setString(3, curComRecordId);
                //获取校验对应的临时表列
                for (int i = 1; i <= this.colsdef.size(); i++) {
                    if (this.colsdef.get(i -1).getDbTableCol().equals((String)row.getAttribute("DbTableCol"))) {
                        cs.setString(4, "COLUMN" + i);
                        break;
                    }
                }
                cs.setString(5, (String)row.getAttribute("DbTableCol"));
                cs.setString(6, mode);
                cs.setString(7, this.curUser.getLocale());
                cs.setString(8, (String)row.getAttribute("Args"));
                cs.registerOutParameter(9, Types.VARCHAR);
                cs.execute();
                //若返回值为N则校验失败
                if ("N".equals(cs.getString(9))) {
                    successFlag = false;
                }
                trans.commit();
                cs.close();
            }
            if(successFlag){
                //若校验通过则执行数据导入
                CallableStatement cs =trans.createCallableStatement("{CALl " + this.curTempalte.getHandleProgram() + "(?,?,?,?,?)}", 0);
                cs.setString(1, this.curTempalte.getId());
                cs.setString(2, curComRecordId);
                cs.setString(3, this.curUser.getId());
                cs.setString(4, mode);
                cs.setString(5, this.curUser.getLocale());
                cs.execute();
                trans.commit();
                cs.close();
                //执行善后程序
                if (this.curTempalte.getAfterProgram() != null) {
                    CallableStatement afcs = trans.createCallableStatement("{CALl " + this.curTempalte.getAfterProgram() +"(?,?,?,?,?)}", 0);
                    afcs.setString(1, this.curTempalte.getId());
                    afcs.setString(2, curComRecordId);
                    afcs.setString(3, this.curUser.getId());
                    afcs.setString(4, mode);
                    afcs.setString(5, this.curUser.getLocale());
                    afcs.execute();
                    trans.commit();
                    afcs.close();
                }
            }
        } catch (Exception e) {
            successFlag = false;
            String msg = e.getMessage();
            if (msg.length() > 2048) {
                msg = msg.substring(0, 2048);
            }
            this.writeErrorMsg(msg, curComRecordId);
            this._logger.severe(e);
        }
        return successFlag;
    }
    //写入数据到错误表
    private void writeErrorMsg(String msg, String curComRecordId) {
        ViewObject vo = ADFUtils.findIterator("DcmErrorViewIterator").getViewObject();
        Row row = vo.createRow();
        row.setAttribute("TemplateId", this.curTempalte.getId());
        row.setAttribute("ComRecordId", curComRecordId);
        row.setAttribute("Msg", msg);
        row.setAttribute("SheetName", "NA");
        row.setAttribute("ValidationId",UUID.randomUUID().toString().replace("-", ""));
        row.setAttribute("Level", "Error");
        vo.insertRow(row);
        vo.getApplicationModule().getTransaction().commit();
    }
    
    //复制错误信息到批量导入错误表
    public void copyBatchError(String tempId){
        DBTransaction trans = (DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        Statement stat = trans.createStatement(DBTransaction.DEFAULT);
        String sql = "INSERT INTO DCM_ERROR_BATCH SELECT T.SHEET_NAME,'校验错误',T.ROW_NUM,'2',T.CREATED_BY,T.LOCALE,T.MSG,SYSDATE FROM DCM_ERROR T "
            + "WHERE T.TEMPLATE_ID = '" + tempId + "' AND T.COM_RECORD_ID = '" + this.curCombiantionRecord + "'";
        try {
            stat.executeUpdate(sql);
            trans.commit();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //写入错误到批量导入错误表
    public void writeBatchError(String sheetName,String status,String msg,String level){
        DBTransaction trans = (DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        Statement stat = trans.createStatement(DBTransaction.DEFAULT);
        String sql = "INSERT INTO DCM_ERROR_BATCH(SHEET_NAME,STATUS,ERROR_LEVEL,CREATED_BY,LOCALE,MSG,CREATED_AT) VALUES('"
             + sheetName + "','" + status + "','" + level + "','" + this.curUser.getId() + "','" + this.curUser.getLocale() + "','" + msg + "',SYSDATE)";
        try {
            stat.executeUpdate(sql);
            trans.commit();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //获取当前的组合
    private String getCurCombinationRecord() {
        String comRecordId = null;
        if (this.curCombiantion != null) {
            String combinationCode  = this.curCombiantion.getCode();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT ID FROM ").append("\"").append(combinationCode.toUpperCase()).append("\"");
            sql.append(" WHERE 1=1");
            for (ComHeader h : this.templateHeader) {
                sql.append(" AND ");
                sql.append("\"").append(h.getCode()).append("\"='").append(h.getValue()).append("'");
            }
            DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
            Statement stat = trans.createStatement(1);
            try {

                ResultSet rs = stat.executeQuery(sql.toString());
                if (rs.next()) {
                    comRecordId = rs.getString("ID");
                }
                stat.close();
            } catch (SQLException e) {
                this._logger.severe(e);
            }     
        }
        return comRecordId;
    }
    
    //获取当前组合的文本信息用于拼接文件名
    private String getCurComRecordText() {
        String text = "";
        if (this.curCombiantion!=null) {
            for (ComHeader header : this.templateHeader) {
                    for (SelectItem item : header.getValues()) {
                        if (header.getValue().equals(item.getValue())) {
                            text += "_"+item.getLabel();
                        }
                    }
            }
        }
        return text;
    }
    
    //构造批量导入模板List
    private List<TemplateEntity> getTempList(List<String> batchTempList){
        DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        Statement stat = trans.createStatement(DBTransaction.DEFAULT);
        List<TemplateEntity> tempList = new ArrayList<TemplateEntity>();
        for(String tempId : batchTempList){
            String temptable = "";
            int startLine = 2;
            int columnSize = 0;
            List<String> cols = new ArrayList<String>();
            String templateName = "";
            String preGro = "";
            String impGro = "";
            String afterGro = "";
            String sql = "SELECT T.ID,T.NAME,T.DATA_START_LINE,T.TMP_TABLE,T.PRE_PROGRAM,T.HANDLE_PROGRAM,T.AFTER_PROGRAM FROM DCM_TEMPLATE T WHERE T.LOCALE = '" + this.curUser.getLocale() 
                         + "' AND T.ID = '" + tempId + "'";
            String countSql = "SELECT T.DB_TABLE_COL FROM DCM_TEMPLATE_COLUMN T WHERE T.LOCALE = '" + this.curUser.getLocale()
                         + "' AND T.TEMPLATE_ID = '" + tempId + "' ORDER BY T.SEQ ASC";
            ResultSet rs;
            ResultSet cRs;
            try {
                rs = stat.executeQuery(sql);
                if(rs.next()){
                    temptable = rs.getString("TMP_TABLE"); 
                    templateName = rs.getString("NAME");
                    startLine = rs.getInt("DATA_START_LINE");
                    preGro = rs.getString("PRE_PROGRAM");
                    impGro = rs.getString("HANDLE_PROGRAM");
                    afterGro = rs.getString("AFTER_PROGRAM");
                }
                rs.close();
                cRs = stat.executeQuery(countSql);
                while(cRs.next()){
                    cols.add(cRs.getString("DB_TABLE_COL"));
                }
                cRs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            TemplateEntity te = new TemplateEntity(tempId,templateName,temptable,cols.size(),startLine,preGro,impGro,afterGro,cols);
            tempList.add(te);
        }
        try {
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tempList;
    }
    
    //批量读取数据到临时表
    private boolean batchHandleExcel(String fileName,String curComRecordId,List<TemplateEntity> tempList) throws SQLException {
        DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        //清空临时表,错误表数据
        for(TemplateEntity temp : tempList){
            this.clearTmpTableAndErrTable(curComRecordId, temp);
        }  
        //读取Excel数据
        String combinationRecord = ObjectUtils.toString(curComRecordId);
        BatchExcelReader beReader = new BatchExcelReader(trans,combinationRecord,this.curUser.getId(),tempList);

        try {
            //读取Excel
            ExcelReaderUtil.readExcel(beReader, fileName, true);
            beReader.close();
        } catch (Exception e) {
            this._logger.severe(e);
            JSFUtils.addFacesErrorMessage(DmsUtils.getMsg("dcm.excel_handle_error"));
            return false;
        }
        return true;    
    }
    
    //读取excel数据到临时表
    private boolean handleExcel(String fileName, String curComRecordId) throws SQLException {
        DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        String combinationRecord = ObjectUtils.toString(curComRecordId);
        //清空已有零时表数据
        this.clearTmpTableAndErrTable(curComRecordId);
        System.out.println(this.curTempalte.getDataStartLine().getValue());
        RowReader reader =new RowReader(trans, (int)this.curTempalte.getDataStartLine().getValue(), this.curTempalte.getId(),combinationRecord, this.curTempalte.getTmpTable(),
                          this.colsdef.size(), this.curUser.getId(),this.curTempalte.getName());
        try {
            ExcelReaderUtil.readExcel(reader, fileName, true);
            reader.close();
        } catch (Exception e) {
            this._logger.severe(e);
            JSFUtils.addFacesErrorMessage(DmsUtils.getMsg("dcm.excel_handle_error"));
            return false;
        }
        return true;
    }
    
    //文件上传
    private String uploadFile() {
        UploadedFile file = (UploadedFile)this.fileInput.getValue();
        if (!(file.getFilename().endsWith(".xls") ||
              file.getFilename().endsWith(".xlsx"))) {
            String msg = DmsUtils.getMsg("dcm.file_format_error");
            JSFUtils.addFacesErrorMessage(msg);
            return null;
        }
        String fileExtension =file.getFilename().substring(file.getFilename().lastIndexOf("."));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dateFormat.format(new Date());
        File dmsBaseDir = new File("DMS/UPLOAD/" + this.curTempalte.getName());
        //如若文件路径不存在则创建文件目录
        if (!dmsBaseDir.exists()) {
            dmsBaseDir.mkdirs();
        }
        String fileName = "DMS/UPLOAD/" + this.curTempalte.getName() + "/";
        if (this.curCombiantion == null) {
            fileName +=this.curTempalte.getName() + "_" + this.curUser.getName() + "_" +date + fileExtension;
        } else {
            fileName +=this.getCurComRecordText() + "_" + this.curUser.getName() +"_" + date + fileExtension;
        }
        System.out.println(fileName);
        try {
            InputStream inputStream = file.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(fileName);
            BufferedInputStream bufferedInputStream =new BufferedInputStream(inputStream);
            BufferedOutputStream bufferedOutputStream =
                new BufferedOutputStream(outputStream);
            byte[] buffer = new byte[10240];
            int bytesRead = 0;
            while ((bytesRead = bufferedInputStream.read(buffer, 0, 10240)) !=-1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
            }
            bufferedOutputStream.flush();
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            file.dispose();
        } catch (IOException e) {
            this._logger.severe(e);
            String msg = DmsUtils.getMsg("dcm.file_upload_error");
            JSFUtils.addFacesErrorMessage(msg);
            return null;
        }
        return (new File(fileName)).getAbsolutePath();
    }
    //数据导出
    public void operation_export(FacesContext facesContext,java.io.OutputStream outputStream) {
        this.dataExportWnd.cancel();
        String type = this.isXlsx ? "xlsx" : "xls";
        try {
            if ("xls".equals(type)) {
                Excel2003WriterImpl writer=new Excel2003WriterImpl(
                                               this.getQuerySql(),
                                               this.curTempalte,
                                               this.colsdef,
                                               outputStream);
                writer.writoToFile();
            } else {
                Excel2007WriterImpl writer=new Excel2007WriterImpl(this.getExportSql(),
                                                                       (int)this.curTempalte.getDataStartLine().getValue(),
                                                                       this.colsdef,this.special);
                writer.process(outputStream, this.curTempalte.getName());  
                outputStream.flush();
            }
        } catch (Exception e) {
            this._logger.severe(e);
        }
        DmsLog dmsLog = new DmsLog();
        dmsLog.operationLog(this.curUser.getAcc(),this.curTempalte.getId(),this.getCurComRecordText(),"EXPORT");
    }
    
    public void quartz_export(ActionEvent actionEvent) {
        this.dataExportWnd.cancel();
        this.newExportJob();
    }
    
    public void newExportJob(){
        try {
                HashMap<String,String> jobDataMap = new HashMap<String,String>();
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = dateFormat.format(new Date());
                String jobName = this.curUser.getAcc() + "-" + date;
                //import info
                int startL = (int)this.curTempalte.getDataStartLine().getValue();
                jobDataMap.put("startLine", ObjectUtils.toString(startL));
                jobDataMap.put("tempId",this.curTempalte.getId());
                jobDataMap.put("isXlsx",this.isXlsx ? "xlsx" : "xls");

                jobDataMap.put("userId",this.curUser.getId());
                jobDataMap.put("userName",this.curUser.getName());
                jobDataMap.put("querySql", this.getQuerySql());
                jobDataMap.put("fileName", this.getExportDataExcelName());
                jobDataMap.put("sheetName", this.curTempalte.getName());

                //job info
                jobDataMap.put("jobType", "ExportData");
                jobDataMap.put("jndiName", "jdbc/DMSConnDS");
                jobDataMap.put("jobName", jobName);
                jobDataMap.put("jobGroup", "DEFAULT");
                QuartzSchedulerSingleton qss = QuartzSchedulerSingleton.getInstance();
                qss.scheduleJobMap(jobName, "dms.quartz.job.ExportExcelJob", "NULL", jobDataMap);
        } catch (Exception e) {
            this._logger.severe(e);
        }
    }
    
    //下载模板
    public void operation_download(FacesContext facesContext,java.io.OutputStream outputStream) {
        this.templateExportWnd.cancel();
        try {
            if (this.isXlsx) {
                if (this.curTempalte.getTemplateFile() == null) {
                    XSSFWorkbook wb = new XSSFWorkbook();
                    XSSFSheet sheet = wb.createSheet(this.curTempalte.getName());
                    XSSFRow row = sheet.createRow((int)this.curTempalte.getDataStartLine().getValue() - 2);
                    for (int i = 0; i < this.colsdef.size(); i++) {
                        row.createCell(i).setCellValue(this.colsdef.get(i).getColumnLabel());
                    }
                    wb.write(outputStream);
                    outputStream.flush();
                } else {
                    FileInputStream inputStream =
                        new FileInputStream(this.curTempalte.getTemplateFile() + ".xlsx");
                    byte[] buffer = new byte[10240];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(buffer, 0, 10240)) !=
                           -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
            } else {
                if (this.curTempalte.getTemplateFile() == null) {
                    // 创建excel2003对象
                    Workbook wb = new HSSFWorkbook();
                    // 创建新的表单
                    Sheet sheet = wb.createSheet(this.curTempalte.getName());
                    // 创建新行
                    org.apache.poi.ss.usermodel.Row headerRow =
                        sheet.createRow((int)this.curTempalte.getDataStartLine().getValue() - 2);
                    for (int i = 0; i < this.colsdef.size(); i++) {
                        headerRow.createCell(i).setCellValue(this.colsdef.get(i).getColumnLabel());
                    }
                    wb.write(outputStream);
                    outputStream.flush();
                } else {
                    FileInputStream inputStream =
                        new FileInputStream(this.curTempalte.getTemplateFile() + ".xls");
                    byte[] buffer = new byte[10240];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(buffer, 0, 10240)) !=
                           -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            this._logger.severe(e);
            String msg = DmsUtils.getMsg("dcm.template_download_error");
            JSFUtils.addFacesErrorMessage(msg);
            return;
        }
    }
    //初始化模板和模板列信息

    private void initTemplate() {
        String curTemplateId = (String)ADFContext.getCurrent().getPageFlowScope().get("curTemplateId");
        
        ViewObject templateView =DmsUtils.getDcmApplicationModule().getDcmTemplateView();
        templateView.executeQuery();
        Row[] rows=templateView.findByKey(new Key(new Object[]{curTemplateId,ADFContext.getCurrent().getLocale().toString()}), 1);
        if(rows.length>0){
            this.curTempalte=new TemplateEO((DcmTemplateViewRowImpl)rows[0]);
            
            if("Y".equals(this.curTempalte.getIsSpecial())){
                //特殊页面功能禁用，所以模板默认false
                this.special = false;
                //组合默认全部打开
                this.defultOpenCom = true;
            }else{
                this.special = false;
            }
            
            //只读模板
            if("Y".equals(this.curTempalte.getReadonly())){
                this.isEditable=false;
            }
            //对该模板仅有只读权限
            if(this.isEditable){
                this.isEditable=!this.isReadOnly();
            }
            //不支持增量导入则设置默认覆盖导入
            if(!this.curTempalte.getHandleMode().contains("I")){
               this.isIncrement=false; 
            }
            templateView.setCurrentRow(rows[0]);
            DcmTemplateViewRowImpl row=(DcmTemplateViewRowImpl)rows[0];
            RowIterator itr=row.getDcmTemplateColumnView();
            while(itr.hasNext()){
                Row colRow = itr.next();
                ColumnDef colDef = new ColumnDef((DcmTemplateColumnViewRowImpl)colRow);
                this.colsdef.add(colDef);
                //获取值列表
                if(colDef.getValueSetId()!=null&&this.valueSet.get(colDef.getValueSetId())==null){
                    this.valueSet.put(colDef.getValueSetId(), 
                                      this.fetchValueList(colDef.getValueSetId()));            
                }
            }
            ((DcmDataTableModel)this.dataModel).setColsdef(this.colsdef);
        }else{
            this._logger.severe(DmsUtils.getMsg("dcm.template_not_found"));
            throw new RuntimeException(DmsUtils.getMsg("dcm.template_not_found")+":tempateId:"+curTemplateId);
        }
        
    }
    
    private void initSubSql(){
        //无组合模板不提供状态显示
        if(null == this.curTempalte.getCombinationId()){
            return;    
        }
        this.subVNameMap = new HashMap<String,String>();
        this.subValuesMap = new HashMap<String,String>();
        
        StringBuffer sql = new StringBuffer();
        StringBuffer viewSql = new StringBuffer();
        StringBuffer where_in = new StringBuffer();
        //组合表
        sql.append("SELECT ");
        for( ComHeader header : this.templateHeader){
            
            //初始化显示Map
            this.subVNameMap.put(header.getCode(), header.getName()); 
            
            viewSql.append(" T.").append(header.getCode()).append(",");
            //ORACLE IN 子句中不能超过1000个值
            if(header.getValues().size() > 0&&header.getValues().size() <= 999){
                where_in.append(" AND T.").append(header.getCode()).append(" IN(");
                for(SelectItem sim : header.getValues()){
                    where_in.append("'").append(sim.getValue()).append("',");
                    this.subValuesMap.put(sim.getValue().toString(), sim.getLabel());
                } 
                where_in.append("'')");
            }else if(header.getValues().size() > 0&&header.getValues().size() > 999){
                where_in.append(" AND (T.").append(header.getCode()).append(" IN(");
                for(int i = 0 ; i < header.getValues().size() ; i++){
                    where_in.append("'").append(header.getValues().get(i).getValue()).append("',");    
                    if((i+1)%999==0){
                        where_in.append("'') OR T.").append(header.getCode()).append(" IN (");
                    }
                    this.subValuesMap.put(header.getValues().get(i).getValue().toString(), header.getValues().get(i).getLabel());
                } 
                where_in.append("''))");
            }
            
        }
        sql.append(viewSql).append(" S.CREATED_AT,S.CREATED_BY,DECODE(S.CREATED_BY,NULL,'未提交','已提交') AS STATUS ").append("FROM ").append(this.curCombiantion.getCode()).append(" T")
            .append(", (SELECT COM_ID,CREATED_AT,CREATED_BY FROM DMS_SUBMIT_STATUS WHERE TEMP_ID = '").append(this.curTempalte.getId())
            .append("') S WHERE T.ID = S.COM_ID(+) ").append(where_in);
        
        this.subSql = sql.toString();
        
        this.subVNameMap.put("CREATED_BY", "提交者");
        this.subVNameMap.put("CREATED_AT", "提交时间");
        this.subVNameMap.put("STATUS", "状态");
           
        this.enableSubmit();
    }
    
    private boolean enableSub;
    private void enableSubmit(){
        this.enableSub = true;
        DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        String sql = "SELECT 1 FROM DMS_SUBMIT_STATUS T WHERE T.TEMP_ID = '" + this.curTempalte.getId() 
                     + "' AND T.COM_ID = '" + this.curCombiantionRecord + "'";
        Statement stat = trans.createStatement(DBTransaction.DEFAULT);
        ResultSet rs;
        try {
            rs = stat.executeQuery(sql);
            if(!rs.next()){
                //不存在，可提交,可编辑
                this.enableSub = false;
                this.curCombinationRecordEditable = true;
            }else{
                //存在，不可编辑
                this.curCombinationRecordEditable = false;
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //获取值列表
    private List<SelectItem> fetchValueList(String vsId){
        List<SelectItem> list=new ArrayList<SelectItem>();
        list.add(new SelectItem());
        Row[] vsRows=DmsUtils.getDmsApplicationModule().getDmsValueSetView()
            .findByKey(new Key(new Object[]{vsId,ADFContext.getCurrent().getLocale().toString()}), 1);
        if(vsRows.length>0){
            String vsCode=(String)vsRows[0].getAttribute("Source");
            StringBuffer sql=new StringBuffer();
            sql.append("SELECT T.CODE, T.MEANING FROM \"").append(vsCode)
            .append("\" T WHERE T.LOCALE = '").append(ADFContext.getCurrent().getLocale()).append("'  ORDER BY T.IDX,T.MEANING ");
            Statement stmt= DmsUtils.getDmsApplicationModule().getDBTransaction().createStatement(DBTransaction.DEFAULT);
            try {
                ResultSet rs = stmt.executeQuery(sql.toString());
                while(rs.next()){
                    SelectItem itm=new SelectItem();
                    itm.setLabel(rs.getString("MEANING"));
                    itm.setValue(rs.getString("MEANING"));
                    list.add(itm);
                }
            } catch (SQLException e) {
                this._logger.severe(e);
            }
        }
        return list;
    }
    //查询数据
    private void queryTemplateData() {
        
        List<Map> data = new ArrayList<Map>();
        DBTransaction dbTransaction =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        String countSql=this.getCountSql(this.getQuerySql());
        PreparedStatement cstat =dbTransaction.createPreparedStatement(countSql, -1);
        try {
            ResultSet crs = cstat.executeQuery();
            if(crs.next()){
                this.setTotalCount(crs.getInt(1));
            }else{
                this.setTotalCount(0);    
            }
            crs.close();
            cstat.close();
        } catch (SQLException e) {
            this._logger.severe(e);
        }
        String sql =this.getPaginationSql(this.getQuerySql());
        PreparedStatement stat =dbTransaction.createPreparedStatement(sql, -1);
        ResultSet rs = null;
        try {
            rs = stat.executeQuery();
            DecimalFormat dfm = new DecimalFormat();
            dfm.setMaximumFractionDigits(4);
            dfm.setGroupingUsed(false);
            SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");//"yyyy-MM-dd hh:mm:ss"
            boolean flag = true;
            while (rs.next()) {
                flag = false;
                Map row = new HashMap();
                for (ColumnDef col : this.colsdef) {
                    Object obj=rs.getObject(col.getDbTableCol().toUpperCase());
                    
                    //列有数据，则显示
                    if(obj != null && this.special){
                        col.setDataNotNull("Y");    
                    }
                    //非特殊模板，设置为显示
                    if(!this.special){
                        col.setDataNotNull("Y");
                    }
                    
                    if(obj instanceof java.util.Date){
                        if(obj != null){
                            obj=format.format((java.util.Date)obj);
                        }
                    }else if(col.getDataType().equals("NUMBER")){
                        if(obj != null){
                            obj = dfm.format(Double.valueOf(obj.toString()));
                        }
                    }else{
                        obj=ObjectUtils.toString(obj);
                    }
                    row.put(col.getDbTableCol(),obj);
                }
                row.put("ROW_ID", rs.getString("ROW_ID"));
                data.add(row);
            }
            
            if(flag){
                //无数据情况下
                for (ColumnDef col : this.colsdef) {
                    //非特殊模板，设置为显示
                    if(!this.special){
                        col.setDataNotNull("Y");
                    }
                    //特殊模板，设置为不显示
                    if(this.special){
                        col.setDataNotNull("N");
                    }
                }

            }
            
            rs.close();
            stat.close();
        } catch (SQLException e) {
            JSFUtils.addFacesErrorMessage(DmsUtils.getMsg("dcm.query_data_error"));
            this._logger.severe(e);
        }
        this.dataModel.setWrappedData(data);
    }
    //获取数据查询语句
    private String getQuerySql() {
        StringBuffer sql_select = new StringBuffer();
        StringBuffer sql_from = new StringBuffer();
        StringBuffer sql_where = new StringBuffer();
        if (null != this.curTempalte.getDbView()) {
            sql_select.append("SELECT ROW_ID");
            sql_from.append(" ").append("FROM ").append(this.curTempalte.getDbView());
        } else {
            sql_select.append("SELECT ROWID ROW_ID");
            sql_from.append(" ").append("FROM ").append(this.curTempalte.getDbTable());
        }
        for (ColumnDef col : this.colsdef) {
            sql_select.append(",\"").append(col.getDbTableCol()).append("\"");
        }
        if (this.curCombiantion != null) {
            sql_where.append(" WHERE COM_RECORD_ID='").append(this.curCombiantionRecord).append("'");
        } else {
            sql_where.append(" WHERE COM_RECORD_ID IS NULL");
        }
        if(this.filters!=null){
            for(Object key:this.filters.keySet()){
                if(!ObjectUtils.toString(this.filters.get(key)).trim().equals("")){
                    String fv=ObjectUtils.toString(this.filters.get(key));
                    if("NULL".equals(fv.toUpperCase())){
                        sql_where.append(" AND \"").append(key).append("\" IS NULL");
                    }else{
                        sql_where.append(" AND UPPER(\"").append(key).append("\") LIKE UPPER('");
                        if(fv.startsWith("%")){
                            sql_where.append("%");
                            fv=fv.substring(1);
                        }
                        if(fv.endsWith("%")){
                            fv=fv.substring(0,fv.lastIndexOf("%"));
                            sql_where.append(fv);
                            sql_where.append("%");
                        }else{
                            sql_where.append(fv);
                        } 
                        sql_where.append("')");
                    }
                }
            }
        }
        if(this.sortCriterions==null){
            sql_where.append(" ORDER BY IDX");
        }else{
            sql_where.append(" ORDER BY ");
            for(int i=0;i<this.sortCriterions.size();i++){
                SortCriterion s=this.sortCriterions.get(i);
                if(i>0){
                    sql_where.append(",");
                }
                sql_where.append("\"").append(s.getProperty()).append("\"").append(s.isAscending() ? " ASC":" DESC");
            }
        }
        return sql_select.toString() + sql_from.toString() +
            sql_where.toString();
    }
    
    //获取数据查询语句
    private String getExportSql() {
        StringBuffer sql_select = new StringBuffer();
        StringBuffer sql_from = new StringBuffer();
        StringBuffer sql_where = new StringBuffer();
        if (null != this.curTempalte.getDbView()) {
            sql_select.append("SELECT ROW_ID");
            sql_from.append(" ").append("FROM ").append(this.curTempalte.getDbView());
        } else {
            sql_select.append("SELECT ROWID ROW_ID");
            sql_from.append(" ").append("FROM ").append(this.curTempalte.getDbTable());
        }
        for (ColumnDef col : this.colsdef) {
            if(this.special){
                if("Y".equals(col.getDataNotNull())){
                    sql_select.append(",\"").append(col.getDbTableCol()).append("\"");
                }
            }else{
                sql_select.append(",\"").append(col.getDbTableCol()).append("\"");
            }
            //sql_select.append(",\"").append(col.getDbTableCol()).append("\"");
        }
        if (this.curCombiantion != null) {
            sql_where.append(" WHERE COM_RECORD_ID='").append(this.curCombiantionRecord).append("'");
        } else {
            sql_where.append(" WHERE COM_RECORD_ID IS NULL");
        }
        if(this.filters!=null){
            for(Object key:this.filters.keySet()){
                if(!ObjectUtils.toString(this.filters.get(key)).trim().equals("")){
                    String fv=ObjectUtils.toString(this.filters.get(key));
                    if("NULL".equals(fv.toUpperCase())){
                        sql_where.append(" AND \"").append(key).append("\" IS NULL");
                    }else{
                        sql_where.append(" AND UPPER(\"").append(key).append("\") LIKE UPPER('");
                        if(fv.startsWith("%")){
                            sql_where.append("%");
                            fv=fv.substring(1);
                        }
                        if(fv.endsWith("%")){
                            fv=fv.substring(0,fv.lastIndexOf("%"));
                            sql_where.append(fv);
                            sql_where.append("%");
                        }else{
                            sql_where.append(fv);
                        } 
                        sql_where.append("')");
                    }
                }
            }
        }
        if(this.sortCriterions==null){
            sql_where.append(" ORDER BY IDX");
        }else{
            sql_where.append(" ORDER BY ");
            for(int i=0;i<this.sortCriterions.size();i++){
                SortCriterion s=this.sortCriterions.get(i);
                if(i>0){
                    sql_where.append(",");
                }
                sql_where.append("\"").append(s.getProperty()).append("\"").append(s.isAscending() ? " ASC":" DESC");
            }
        }
        return sql_select.toString() + sql_from.toString() +
            sql_where.toString();
    }
    
    //初始化组合信息
    private void initCombination() {
        if (null != this.curTempalte.getCombinationId()) {
            //初始化组合基本信息
            ViewObject cVo =DmsUtils.getDcmApplicationModule().getDcmCombinationView();
            Row rows[]=cVo.findByKey(new Key(new Object[]{this.curTempalte.getCombinationId(),ADFContext.getCurrent().getLocale().toString()}), 1);
            if (rows.length>0) {
                this.curCombiantion = new CombinationEO((DcmCombinationViewRowImpl)rows[0]);;
            }
            //初始化组合头信息
            ViewObject vo =DmsUtils.getDcmApplicationModule().getDcmComVsQueryView();
            vo.setNamedWhereClauseParam("combinationId", this.curTempalte.getCombinationId());
            vo.executeQuery();
            vo.reset();
            while (vo.hasNext()) {
                Row row = vo.next();
                ComHeader header = new ComHeader();
                header.setName((String)row.getAttribute("Name"));
                header.setIsAuthority((String)row.getAttribute("IsAuthority"));
                header.setSrcTable((String)row.getAttribute("Source"));
                header.setValueSetId((String)row.getAttribute("ValueSetId"));
                header.setCode((String)row.getAttribute("Code"));
                header.setLength(Integer.parseInt(row.getAttribute("DisplayLength").toString()));
                this.initHeaderValueList(header);
//                this.setDefaultHeaderValue(header);
                this.templateHeader.add(header);
            }
            this.curCombiantionRecord=this.getCurCombinationRecord();
            this.setCurCombinationRecordEditable();
        }
    }
    private void setCurCombinationRecordEditable(){
        if(this.curCombiantionRecord==null){
            this.curCombinationRecordEditable=false;
        }else if(this.defultOpenCom){
            this.curCombinationRecordEditable=true;
            //默认值之后判断是否可提交，可编辑
            //this.enableSubmit();
        }else{
            boolean flag=true;
            StringBuffer sql=new StringBuffer();
            sql.append("SELECT T.STATUS FROM DCM_TEMPLATE_COMBINATION T WHERE T.TEMPLATE_ID='")
                .append(this.curTempalte.getId()).append("' AND T.COM_RECORD_ID='")
                .append(this.curCombiantionRecord).append("'");
            DBTransaction dbTransaction =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
            Statement stat=dbTransaction.createStatement(DBTransaction.DEFAULT);
            try {
                ResultSet rs=stat.executeQuery(sql.toString());
                if(rs.next()){
                    String status=rs.getString("STATUS");
                    if("OPEN".equals(status)){
                        for(ComHeader h:this.templateHeader){
                            StringBuffer sql0=new StringBuffer();
                            sql0.append("SELECT T.ENABLED FROM \"").append(h.getSrcTable())
                                .append("\" T WHERE T.CODE='")
                                .append(h.getValue()).append("' AND T.LOCALE='")
                                .append(ADFContext.getCurrent().getLocale()).append("'");
                            ResultSet rst=stat.executeQuery(sql0.toString());
                            if(rst.next()){
                                String enabled=rst.getString("ENABLED");
                                if("N".equals(enabled)){
                                    flag=false;
                                    break;
                                }
                            }else{
                                flag=false;
                                break;
                            }
                            rst.close();
                        }
                    }else{
                        flag=false;
                    }         
                }else{
                    flag=false;
                }
                rs.close();
                stat.close();
            } catch (SQLException e) {
                this._logger.severe(e);
            }
            this.curCombinationRecordEditable=flag;
        }
    }
    private void initHeaderValueList(ComHeader header){
        List<SelectItem> values = new ArrayList<SelectItem>();
        DBTransaction dbTransaction =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT V.CODE,V.MEANING FROM \"");
        sql.append(header.getSrcTable()).append("\" V");
        sql.append(" WHERE V.LOCALE='").append(this.curUser.getLocale()).append("'");
        if ("Y".equals(header.getIsAuthority())) {
            sql.append(" AND EXISTS(SELECT 1 FROM ");
            sql.append(" DMS_USER_VALUE_V T");
            sql.append(" WHERE T.USER_ID = '").append(this.curUser.getId()).append("'");
            sql.append(" AND T.VALUE_SET_ID = '").append(header.getValueSetId()).append("'");
            sql.append(" AND T.VALUE_ID=V.CODE)");
        }
        sql.append(" ORDER BY V.IDX,V.MEANING");
        PreparedStatement stat =
            dbTransaction.createPreparedStatement(sql.toString(), -1);
        try {
            ResultSet rs = stat.executeQuery();
            List<ValueSetRow> list = new ArrayList<ValueSetRow>(); 
            while (rs.next()) {
                SelectItem item = new SelectItem();
                item.setLabel(rs.getString("MEANING"));
                item.setValue(rs.getString("CODE"));
                values.add(item);
                //模糊搜索框
                ValueSetRow vsr = new ValueSetRow(rs.getString("CODE"),rs.getString("MEANING"),rs.getString("CODE"));
                list.add(vsr);
            }
            DmsComBoxLov dcl = new DmsComBoxLov(list);
            header.setComLov(dcl);
        } catch (SQLException e) {
            this._logger.severe(e);
        }   
        header.setValues(values);
    }
    private void setDefaultHeaderValue(ComHeader header){
        DBTransaction dbTransaction =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT V.CODE,V.MEANING FROM \"");
        sql.append(header.getSrcTable()).append("\" V");
        sql.append(" WHERE V.LOCALE='").append(this.curUser.getLocale()).append("'");
        sql.append(" AND EXISTS(SELECT 1 FROM \"").append(this.curCombiantion.getCode()).append("\" T,DCM_TEMPLATE_COMBINATION A ");
        sql.append(" WHERE T.\"").append(header.getCode()).append("\"=V.CODE");
        sql.append(" AND T.ID=A.COM_RECORD_ID");
        sql.append(" AND A.TEMPLATE_ID='").append(this.curTempalte.getId()).append("'");
        sql.append(" AND A.STATUS='OPEN'");
        for (ComHeader h : this.templateHeader) {
            if (!h.equals(header)) {
                sql.append("AND T.\"").append(h.getCode()).append("\"='");
                sql.append(h.getValue()).append("'");
            }else{
                break;
            }
        }
        sql.append(")");
        if ("Y".equals(header.getIsAuthority())) {
            sql.append(" AND EXISTS(SELECT 1 FROM ");
            sql.append(" DMS_USER_VALUE_V T");
            sql.append(" WHERE T.USER_ID = '").append(this.curUser.getId()).append("'");
            sql.append(" AND T.VALUE_SET_ID = '").append(header.getValueSetId()).append("'");
            sql.append(" AND T.VALUE_ID=V.CODE)");
        }
        sql.append(" ORDER BY V.IDX");
        PreparedStatement stat =
            dbTransaction.createPreparedStatement(sql.toString(), -1);
        try {
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                header.setValue(rs.getString("CODE"));
            }
        } catch (SQLException e) {
            this._logger.severe(e);
        }    
    }

    public String getCombinationId() {
        return curTempalte.getCombinationId();
    }

    public void setTemplateHeader(List<ComHeader> templateHeader) {
        this.templateHeader = templateHeader;
    }

    public List<ComHeader> getTemplateHeader() {
        return templateHeader;
    }

    public void setFileInput(RichInputFile fileInput) {
        this.fileInput = fileInput;
    }

    public RichInputFile getFileInput() {
        return fileInput;
    }
    //选择不同的组合时的处理逻辑
    public void headerSelectChangeListener(ValueChangeEvent event) {
        
        for(ComHeader ch : this.templateHeader){
            if(ch.getName().equals(this.comIclovs.getLabel())){
                if("".equals(event.getNewValue()) || event.getNewValue() == null){
                    ch.setValue("");
                }else{
                    for(SelectItem sim : ch.getValues()){
                        if(sim.getLabel() == null) continue;
                        if(sim.getLabel().equals(event.getNewValue())){
                            ch.setValue(sim.getValue().toString());    
                        }    
                    }  
                }
            }    
        }
        
        this.curCombiantionRecord=this.getCurCombinationRecord();
        this.setCurCombinationRecordEditable();
        this.setCurPage(1);
        
        //切换组合时重置显示属性
        for(ColumnDef col : this.colsdef){
            col.setDataNotNull("N");    
        }
        
        this.queryTemplateData();
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(this.panelaCollection);
        //查询提交权限
        if("Y".equals(this.curTempalte.getIsCloseRecord())){
            this.enableSubmit();
        }
    }

    public void setPanelaCollection(RichPanelCollection panelaCollection) {
        this.panelaCollection = panelaCollection;
    }

    public RichPanelCollection getPanelaCollection() {
        return panelaCollection;
    }

    public String getTemplateName() {
        return this.curTempalte.getName();
    }

    public void setIsIncrement(boolean isIncrement) {
        this.isIncrement = isIncrement;
    }

    public boolean isIsIncrement() {
        return isIncrement;
    }

    public void setIsXlsx(boolean isXlsx) {
        this.isXlsx = isXlsx;
    }

    public boolean isIsXlsx() {
        return isXlsx;
    }
    //获取导出数据时的文件名

    public String getExportDataExcelName() {
        if (this.isXlsx) {
            return this.curTempalte.getName() + this.getCurComRecordText() + ".xlsx";
        } else {
            return this.curTempalte.getName() + this.getCurComRecordText() + ".xls";
        }
    }
    //获取模板文件名

    public String getExportTemplateExcelName() {
        if (this.isXlsx) {
            return this.curTempalte.getName() + ".xlsx";
        } else {
            return this.curTempalte.getName() + ".xls";
        }
    }

    public void showErrors(ActionEvent actionEvent) {
        this.showErrorPop();
    }
    //显示错误信息窗口
    private void showErrorPop() {
        ViewObject vo =ADFUtils.findIterator("DcmErrorViewIterator").getViewObject();
        ViewCriteria viewCriteria = vo.createViewCriteria();
        ViewCriteriaRow vr = viewCriteria.createViewCriteriaRow();
        if (this.curCombiantion == null) {
            vr.setAttribute("ComRecordId", " is null");
        } else {
            vr.setAttribute("ComRecordId", "='" + this.curCombiantionRecord + "'");
        }
        vr.setAttribute("TemplateId", "='" + this.curTempalte.getId() + "'");
        viewCriteria.addRow(vr);
        vo.applyViewCriteria(viewCriteria);
        vo.executeQuery();
        vo.getViewCriteriaManager().setApplyViewCriteriaNames(null);
        RichPopup.PopupHints ph = new RichPopup.PopupHints();
        this.errorWindow.show(ph);
    }

    public void setErrorWindow(RichPopup errorWindow) {
        this.errorWindow = errorWindow;
    }

    public RichPopup getErrorWindow() {
        return errorWindow;
    }
    
    private void clearTmpTableAndErrTable(String comRecordId,TemplateEntity temp){
        String clearTmpTableSql="DELETE FROM \"" + temp.getTemptable()
                                +"\" WHERE TEMPLATE_ID='" +
                                temp.getTemplateId() +"' AND COM_RECORD_ID " 
                                +(comRecordId == null ?
                                "IS NULL" :("='" + comRecordId + "'"));
        String clearErrTableSql="DELETE FROM DCM_ERROR WHERE TEMPLATE_ID='" 
                                +temp.getTemplateId() +"' AND COM_RECORD_ID " 
                                +(comRecordId == null ?
                                "IS NULL" :("='" + comRecordId + "'"));
        ApplicationModule dcmApplicationModule =DmsUtils.getDcmApplicationModule();
        //清空临时表相应数据
        dcmApplicationModule.getTransaction().executeCommand(clearTmpTableSql);
        //清空错误表相应数据
        dcmApplicationModule.getTransaction().executeCommand(clearErrTableSql);
        dcmApplicationModule.getTransaction().commit();
    }
    
    private void clearTmpTableAndErrTable(String comRecordId) throws SQLException {
        String clearTmpTableSql="DELETE FROM \"" +this.curTempalte.getTmpTable() 
                                +"\" WHERE TEMPLATE_ID='" +
                                this.curTempalte.getId() +"' AND COM_RECORD_ID " 
                                +(comRecordId == null ?
                                "IS NULL" :("='" + comRecordId + "'"));
        String clearErrTableSql="DELETE FROM DCM_ERROR WHERE TEMPLATE_ID='" 
                                +this.curTempalte.getId() +"' AND COM_RECORD_ID " 
                                +(comRecordId == null ?
                                "IS NULL" :("='" + comRecordId + "'"));
        ApplicationModule dcmApplicationModule =DmsUtils.getDcmApplicationModule();
        //清空临时表相应数据
        dcmApplicationModule.getTransaction().executeCommand(clearTmpTableSql);
        //清空错误表相应数据
        dcmApplicationModule.getTransaction().executeCommand(clearErrTableSql);
        dcmApplicationModule.getTransaction().commit();
    }

    public void clearBatchErrorTable(){
        String sql = "DELETE DCM_ERROR_BATCH WHERE CREATED_BY = '" + this.curUser.getId() + "'";
        ApplicationModule dcmApplicationModule =DmsUtils.getDcmApplicationModule();
        dcmApplicationModule.getTransaction().executeCommand(sql);
        dcmApplicationModule.getTransaction().commit();
    }

    public void sortListener(SortEvent sortEvent) {
        this.sortCriterions=sortEvent.getSortCriteria();
        this.queryTemplateData();
    }
    public FilterableQueryDescriptor getQueryDescriptor(){
        return this.queryDescriptor;
    }

    public void queryListener(QueryEvent queryEvent) {
        DcmQueryDescriptor descriptor = (DcmQueryDescriptor)queryEvent.getDescriptor();
        this.filters=descriptor.getFilterCriteria();
        this.setCurPage(1);
        this.queryTemplateData();
    }

    public Map getValueSet() {
        return valueSet;
    }

    public void nextPage(ActionEvent event) {
        this.setCurPage(this.getCurPage()+1);
        this.queryTemplateData();
    }

    public void prePage(ActionEvent event) {
        this.setCurPage(this.getCurPage()-1);
        this.queryTemplateData();
    }

    public void refreshPage(ActionEvent event) {
    }

    public void gotoPage(ValueChangeEvent event) {
        this.setCurPage((Integer)event.getNewValue());
        this.queryTemplateData();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.panelaCollection);
    }

    public void firstPage(ActionEvent event) {
        this.setCurPage(1);
        this.queryTemplateData();
    }

    public void lastPage(ActionEvent event) {
        this.setCurPage(this.getTotalPage());
        this.queryTemplateData();
    }

    public void setDataImportWnd(RichPopup dataImportWnd) {
        this.dataImportWnd = dataImportWnd;
    }

    public RichPopup getDataImportWnd() {
        return dataImportWnd;
    }

    public void setDataExportWnd(RichPopup dataExportWnd) {
        this.dataExportWnd = dataExportWnd;
    }

    public RichPopup getDataExportWnd() {
        return dataExportWnd;
    }

    public void setTemplateExportWnd(RichPopup templateExportWnd) {
        this.templateExportWnd = templateExportWnd;
    }

    public RichPopup getTemplateExportWnd() {
        return templateExportWnd;
    }
    public boolean getIsEditable(){
        if(this.curCombiantion==null){
            return this.isEditable;
        }else{
            return this.isEditable&&this.curCombiantionRecord!=null&&this.curCombinationRecordEditable;
        }
    }
    private boolean isReadOnly(){
        boolean readonly=false;
        StringBuffer sql=new StringBuffer();
        sql.append("SELECT 1 ")
           .append("  FROM DCM_USER_TEMPLATE_V T ")
           .append(" WHERE T.READ_ONLY='Y' AND T.USER_ID = '").append(this.curUser.getId()).append("' ")
           .append("   AND T.TEMPLATE_ID = '").append(this.curTempalte.getId()).append("'");
        Statement stmt=DmsUtils.getDcmApplicationModule().getDBTransaction().createStatement(DBTransaction.DEFAULT);
        try {
            ResultSet rs = stmt.executeQuery(sql.toString());
            if(rs.next()){
                readonly=true;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            this._logger.severe(e);
        }
        return readonly;
    }
    public boolean getCanIncrementImport(){
        return this.curTempalte.getHandleMode().contains("I") ? true:false;
    }
    public boolean getCanReplaceImport(){
        return this.curTempalte.getHandleMode().contains("R") ? true:false;
    }
    public boolean getIsReplaceDefault(){
        return !this.getCanIncrementImport();
    }
    public void setIsReplaceDefault(boolean flag){
       
    }

    public void setUseQuartz(boolean useQuartz) {
        this.useQuartz = useQuartz;
    }

    public boolean isUseQuartz() {
        return useQuartz;
    }
    
    public void setSpecial(boolean special) {
        this.special = special;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setIsClose(boolean isClose) {
        this.isClose = isClose;
    }

    public boolean isIsClose() {
        boolean flag=false;
        
        if("Y".equals(this.curTempalte.getIsCloseRecord())){
            flag=true;
        }
        return flag;
    }

    public void closeCombination(ActionEvent actionEvent) {
        //保存
        this.operation_save();
        
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE DCM_TEMPLATE_COMBINATION SET STATUS=\'CLOSE\',UPDATED_AT=SYSDATE WHERE ")
            .append("TEMPLATE_ID= \'").append(this.curTempalte.getId()).append("' ")
            .append("AND COM_RECORD_ID=\'").append(this.curCombiantionRecord).append("' ");
        try {
            DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
            PreparedStatement stat =trans.createPreparedStatement(sql.toString(), 0);
            stat.executeUpdate();
            trans.commit();
            stat.close();
        } catch (SQLException e) {
            this._logger.severe(e);
        }
        //更改状态为已提交
        this.changeSubmitStatus("SUBMIT");
        this.enableSub = true;
        this.curCombinationRecordEditable = false;
        //刷新数据
        this.queryTemplateData();
    }
    
    public void changeSubmitStatus(String type){
        String sql = "";
        if(type.equals("SUBMIT")){
            sql = "INSERT INTO DMS_SUBMIT_STATUS(TEMP_ID,COM_ID,CREATED_AT,CREATED_BY) VALUES('" + this.curTempalte.getId() + "','"
                + this.curCombiantionRecord + "',SYSDATE,'" + this.curUser.getAcc() + "-" + this.curUser.getName() + "')";
        }else{
            sql = "DELETE DMS_SUBMIT_STATUS T WHERE T.TEMP_ID = '" + this.curTempalte.getId() + "' AND T.COM_ID = '" + this.curCombiantionRecord + "'";
        }
        DmsUtils.getDcmApplicationModule().getTransaction().executeCommand(sql);
        DmsUtils.getDcmApplicationModule().getTransaction().commit();
    }

    public void setBatchExcel(boolean batchExcel) {
        this.batchExcel = batchExcel;
    }

    public boolean isBatchExcel() {
        return batchExcel;
    }

    public void setBatchErrPop(RichPopup batchErrPop) {
        this.batchErrPop = batchErrPop;
    }

    public RichPopup getBatchErrPop() {
        return batchErrPop;
    }

    public void showBatchLog(ActionEvent actionEvent) {
        DmsUtils.getDcmApplicationModule().getDcmErrorBatchVO().executeQuery();
        RichPopup.PopupHints hints = new RichPopup.PopupHints();
        this.batchErrPop.show(hints);
    }

    public void setSubPop(RichPopup subPop) {
        this.subPop = subPop;
    }

    public RichPopup getSubPop() {
        return subPop;
    }

    public void setSubSql(String subSql) {
        this.subSql = subSql;
    }

    public String getSubSql() {
        return subSql;
    }

    public void setSubTempId(String subTempId) {
        this.subTempId = subTempId;
    }

    public String getSubTempId() {
        return this.curTempalte.getId();
    }

    public void showSubStatus(ActionEvent actionEvent) {
        //是否有提交
        if(!"Y".equals(this.curTempalte.getIsCloseRecord())){
            return;
        }
        
        //无组合模板不提供状态显示
        if(this.curCombiantionRecord == null || "".equals(this.curCombiantionRecord)){
            return;    
        }
        
        RichPopup.PopupHints hints = new RichPopup.PopupHints();
        this.subPop.show(hints);
        
    }

    public void setSubVNameMap(Map<String, String> subVNameMap) {
        this.subVNameMap = subVNameMap;
    }

    public Map<String, String> getSubVNameMap() {
        return subVNameMap;
    }

    public void setSubValuesMap(Map<String,String> subValuesMap) {
        this.subValuesMap = subValuesMap;
    }

    public Map<String, String> getSubValuesMap() {
        return subValuesMap;
    }

    public void setSubTable(RichTable subTable) {
        this.subTable = subTable;
    }

    public RichTable getSubTable() {
        return subTable;
    }

    public void querySubListener(QueryEvent queryEvent) {
        DcmQueryDescriptor descriptor =(DcmQueryDescriptor)queryEvent.getDescriptor();
        if(descriptor.getFilterCriteria()!=null){
            StringBuffer whereCaluse = new StringBuffer();
            whereCaluse.append(" 1=1");
            ViewObject vo=ADFUtils.findIterator("getSubmitStatusVOIterator").getViewObject();
            //vo.clearCache();
            for(Object key:descriptor.getFilterCriteria().keySet()){
                if(!ObjectUtils.toString(descriptor.getFilterCriteria().get(key)).trim().equals("")){
                    if(descriptor.getFilterCriteria().get(key) != null || !"".equals(descriptor.getFilterCriteria().get(key))){
                        if(key.equals("STATUS")){
                            whereCaluse.append(" AND STATUS LIKE '%").append(descriptor.getFilterCriteria().get(key).toString()).append("%'");
                        }else{
                            whereCaluse.append(" AND ").append(key).append(" IN(");
                            for(Map.Entry<String,String> entry : this.subValuesMap.entrySet()){
                                if(entry.getValue() == null) continue;
                                if(entry.getValue().contains(descriptor.getFilterCriteria().get(key).toString())) {
                                    whereCaluse.append("'").append(entry.getKey()).append("',");
                                }  
                            }   
                            whereCaluse.append("'')");
                        }
                    }
                }
            }
            vo.setWhereClause(whereCaluse.toString());
            vo.executeQuery();
            //vo.setWhereClause(null);
            AdfFacesContext.getCurrentInstance().addPartialTarget(this.subTable);
        }
    }

    public void setSubDescriptor(FilterableQueryDescriptor subDescriptor) {
        this.subDescriptor = subDescriptor;
    }

    public FilterableQueryDescriptor getSubDescriptor() {
        return subDescriptor;
    }

    public void setEnableSub(boolean enableSub) {
        this.enableSub = enableSub;
    }

    public boolean isEnableSub() {
        return enableSub;
    }

    public void resetSubmit(ActionEvent actionEvent) {
        this.changeSubmitStatus("RESET");
        this.enableSub = false;
        
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE DCM_TEMPLATE_COMBINATION SET STATUS=\'OPEN\',UPDATED_AT=SYSDATE WHERE ")
            .append("TEMPLATE_ID= \'").append(this.curTempalte.getId()).append("' ")
            .append("AND COM_RECORD_ID=\'").append(this.curCombiantionRecord).append("' ");
        try {
            DBTransaction trans =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
            PreparedStatement stat =trans.createPreparedStatement(sql.toString(), 0);
            stat.executeUpdate();
            trans.commit();
            stat.close();
        } catch (SQLException e) {
            this._logger.severe(e);
        }
        
        this.curCombinationRecordEditable = true;
        //刷新数据
        this.queryTemplateData();
        
    }

    public void setComIclovs(RichInputComboboxListOfValues comIclovs) {
        this.comIclovs = comIclovs;
    }

    public RichInputComboboxListOfValues getComIclovs() {
        return comIclovs;
    }
}
