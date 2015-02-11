package dcm;

import common.DmsUtils;

import dcm.template.TemplateEO;

import java.io.IOException;
import java.io.OutputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.List;

import oracle.jbo.server.DBTransaction;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Excel2003WriterImpl {
    private String sql;
    private TemplateEO curTempalte;
    private List<ColumnDef> colsdef;
    private OutputStream outputStream;
    public Excel2003WriterImpl(String sql,TemplateEO curTempalte,List<ColumnDef> colsdef,OutputStream outputStream) {
        this.sql=sql;
        this.curTempalte=curTempalte;
        this.colsdef=colsdef;
        this.outputStream=outputStream;
    }
    public void writoToFile() throws SQLException, IOException {
        DBTransaction dbTransaction =(DBTransaction)DmsUtils.getDcmApplicationModule().getTransaction();
        PreparedStatement stat =dbTransaction.createPreparedStatement(sql, -1);
        ResultSet rs = stat.executeQuery();
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
        int n = (int)this.curTempalte.getDataStartLine().getValue() - 1;
        while (rs.next()) {
            int colInx = 0;
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(n);
            for (ColumnDef col : this.colsdef) {
                Cell cell = row.createCell(colInx);
                ++colInx;
                cell.setCellValue(rs.getString(col.getDbTableCol()));
            }
            ++n;
        }
        rs.close();
        wb.write(outputStream);
    }
}
