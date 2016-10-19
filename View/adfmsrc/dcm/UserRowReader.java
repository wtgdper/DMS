package dcm;

import com.bea.security.utils.DigestUtils;

import common.JSFUtils;
import common.ReplaceSpecialChar;

import java.sql.PreparedStatement;

import java.sql.SQLException;

import java.text.DecimalFormat;

import java.util.List;
import java.util.TreeMap;

import oracle.jbo.server.DBTransaction;

import org.hexj.excelhandler.reader.IRowReader;

public class UserRowReader implements IRowReader{
    private int startLine;
    private PreparedStatement stmt;
    private List<ColumnDef> colsdef;
    private DBTransaction trans;
    private int n=0;
    private static final int batchSize = 5000;
    private String operator;
    private DecimalFormat dfm;
    
    public UserRowReader(DBTransaction trans,int startLine,List<ColumnDef> colsdef,String operator) {
        this.trans = trans;
        this.startLine = startLine;
        this.colsdef = colsdef;
        this.operator = operator;
        dfm = new DecimalFormat();
        dfm.setMaximumFractionDigits(6);
        dfm.setGroupingUsed(false);
        this.prepareSqlStatement();
    }
    
    private void prepareSqlStatement(){
        StringBuffer sql = new StringBuffer();
        StringBuffer sqlValue = new StringBuffer();
        sql.append("INSERT INTO DMS_USER_TEMP (UPDATED_AT,UPDATED_BY");
        sqlValue.append(") VALUES(SYSDATE,").append(this.operator);
        for(int i=0;i<this.colsdef.size();i++){
            sql.append(",").append(this.colsdef.get(i).getDbTableCol());
            sqlValue.append(",?");
        }
        sqlValue.append(")");
        stmt = this.trans.createPreparedStatement(sql.toString()+sqlValue.toString(), 0);
    }
    
    public void getRows(int sheetIndex, String sheetName, int curRow,
                        TreeMap<Integer, String> rowlist) {
        ReplaceSpecialChar rsc = new ReplaceSpecialChar();
        if(curRow >= this.startLine - 1&&sheetName.startsWith("用户管理")){
            boolean isEpty = true;
                        try {
                            String uAcc = "";
                            for (int i = 0; i < this.colsdef.size(); i++) {
                            String tmpstr = rowlist.get(i);
                            if(this.colsdef.get(i).getDataType().equals("NUMBER")){
                                if (null == tmpstr || "".equals(tmpstr.trim())) {
                                    this.stmt.setString(i + 1, "");
                                } else {
                                    isEpty = false;
                                    this.stmt.setString(i + 1, dfm.format(Double.parseDouble(tmpstr.trim())));
                                }
                            }else{
                                if (null == tmpstr || "".equals(tmpstr.trim())) {
                                    this.stmt.setString(i + 1, "");
                                } else {
                                    isEpty = false;
                                    if(this.colsdef.get(i).getDbTableCol().equals("ACC")){
                                        uAcc = tmpstr.trim();
                                    }
                                    if(this.colsdef.get(i).getDbTableCol().equals("PWD")&&(!"******".equals(tmpstr.trim()))){
                                        String encyptPwd = DigestUtils.digestSHA1(uAcc + tmpstr.trim());
                                        this.stmt.setString(i + 1, rsc.decodeString(encyptPwd));
                                    }else{   
                                        this.stmt.setString(i + 1, rsc.decodeString(tmpstr.trim())); 
                                    }
                                }
                            }
                            }
                            if (!isEpty) {
                                this.stmt.addBatch();
                            }
                            if ((this.n + 1) % this.batchSize == 0) {
                                this.stmt.executeBatch();
                                this.trans.commit();
                            }
                            this.n += 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
        }
    }
    
    public boolean close() {
        boolean flag = true;
            try {
                this.stmt.executeBatch();
                this.stmt.close();
                this.trans.commit();
            } catch (SQLException e) {
//                e.printStackTrace();
                JSFUtils.addFacesErrorMessage("ssss:"+e.getMessage());
                flag = false;
            }
        return flag;
    }
}