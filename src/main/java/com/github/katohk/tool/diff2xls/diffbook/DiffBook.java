package com.github.katohk.tool.diff2xls.diffbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * DiffBook.java
 *
 *
 * Created: Fri Apr  2 18:25:56 2004
 *
 * @version 1.0
 */
public class DiffBook {
    private String templateFname = "";
    XSSFWorkbook workbook = null;
    private int sheetindex = 0;

    /* template info */
    private XSSFSheet tmpsheet = null;
    private DiffTemplate diffTemplate = null;


    /**
     * Creates a new <code>DiffBook</code> instance.
     *
     * @param fname a <code>String</code> value
     * @exception IOException if an error occurs
     */
    public DiffBook(String fname) throws IOException {
        templateFname = fname;
        workbook = getWorkbook();
    }

    /**
     * Describe <code>makeDiffSheet</code> method here.
     *
     * @param sname a <code>String</code> value
     * @return a <code>DiffSheet</code> value
     */
    public DiffSheet makeDiffSheet(String sname){
        XSSFSheet sheet = workbook.cloneSheet(0);
        sheetindex++;
        try{
            workbook.setSheetName(sheetindex, sname);
            return new DiffSheet(this, sheet);
        }catch(IllegalArgumentException ex){
            System.err.println("name="+sname);
            throw ex;
        }
    }

    private XSSFWorkbook getWorkbook() throws IOException {
        FileInputStream in = null;
        XSSFWorkbook book = null;
        try{
            in = new FileInputStream(templateFname);
            //POIFSFileSystem fs = new POIFSFileSystem(in);
            book = new XSSFWorkbook(in);

        } finally {
            in.close();
        }
        return book;
    }

    /**
     * Describe <code>write</code> method here.
     *
     * @param fname a <code>OutputStream</code> value
     * @exception IOException if an error occurs
     */
    public void write(OutputStream out) throws IOException {
        workbook.removeSheetAt(0); // delete template
        workbook.write(out);
    }

    /**
     * Describe <code>write</code> method here.
     *
     * @param fname a <code>String</code> value
     * @exception IOException if an error occurs
     */
    public void write(String fname) throws IOException {

        FileOutputStream out = null;
        try{
            out = new FileOutputStream(fname);
            this.write(out);
        } finally {
            out.close();
        }
    }

    DiffTemplate getTemplate(){
        if ( diffTemplate == null ){
            diffTemplate = new DiffTemplate();

            tmpsheet = workbook.getSheetAt(0);

            /*
            for ( short i = 0; i < workbook.getNumberOfFonts(); i++ ){
                XSSFFont font = workbook.getFontAt(i);
                System.out.println( font.toString() );
            }
            */

            Iterator<Row> rowIterator = tmpsheet.rowIterator(); 
            while(rowIterator.hasNext()){
                XSSFRow row = (XSSFRow)rowIterator.next(); 

                XSSFCell cell0 = row.getCell((short)0);
                if ( cell0 == null ){
                    continue;
                }
            
                String value = cell0.getStringCellValue();
                if ( "%top".equals(value) ){
                    setColInfo(row,diffTemplate);
                    diffTemplate.setTopRow(row);
                    diffTemplate.setRowNum(row.getRowNum());
                }else if ( "%middle".equals(value) ){
                    diffTemplate.setMiddleRow(row);
                }else if ( "%bottom".equals(value) ){
                    diffTemplate.setBottomRow(row);
                }else if ( "%attrib".equals(value) ){
                    setAttribInfo(row,diffTemplate);
                }
            }
        }

        return diffTemplate;
    }

    private void setColInfo(XSSFRow row, DiffTemplate temp){
        for ( short i = row.getFirstCellNum();
              i < row.getLastCellNum(); i++ ){
            XSSFCell cell = row.getCell(i);

            String value = cell.getStringCellValue();
            if ( "#left".equals(value) ){
                temp.setLeftCol(i);
            } else if ( "#right".equals(value) ){
                temp.setRightCol(i);
            } else if ( "#seq".equals(value) ){
                temp.setSeqCol(i);
            }
            
        }
    }

    private void setAttribInfo(XSSFRow row, DiffTemplate temp){
        for ( short i = row.getFirstCellNum();
              i < row.getLastCellNum(); i++ ){
            XSSFCell cell = row.getCell(i);
            XSSFCellStyle style = cell.getCellStyle();

            String value = cell.getStringCellValue();
            if ( "#!".equals(value) ){
                temp.setChangeStyle(style,i);
            } else if ( "#+".equals(value) ){
                temp.setAddStyle(style,i);
            } else if ( "#-".equals(value) ){
                temp.setDeleteStyle(style,i);
            }
            
        }
    }

    void copyTemplateTop(XSSFRow row){
        copyTemplate(row,diffTemplate.getTopRow());
    }

    void copyTemplateMiddle(XSSFRow row){
        copyTemplate(row,diffTemplate.getMiddleRow());
    }

    void copyTemplateBottom(XSSFRow row){
        copyTemplate(row,diffTemplate.getBottomRow());
    }

    private void copyTemplate(XSSFRow row, XSSFRow templateRow){
        Iterator<Cell> tmpIterator = templateRow.cellIterator();
        while(tmpIterator.hasNext()){
            XSSFCell tmpCell = (XSSFCell)tmpIterator.next();
            XSSFCellStyle tmpStyle = tmpCell.getCellStyle();
            int num = tmpCell.getColumnIndex();

            XSSFCell cell = row.createCell(num);
            cell.setCellStyle(tmpStyle);

            String value = cell.getStringCellValue();
            if ( value != null && value.length() > 0 ){
                char ch = value.charAt(0);
                if ( ch == '%' || ch == '#' ){
                    value = null;
                }
            }

            cell.setCellValue(value);
        }
    }
    
}
