package com.github.katohk.tool.diff2xls.diffbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * DiffSheet.java
 *
 *
 * Created: Fri Apr  2 18:28:17 2004
 *
 * @version 1.0
 */
public class DiffSheet {
    private DiffBook diffbook = null;

    private int curRowNum = 0;
    private int leftRowNum = 0;
    private int rightRowNum = 0;
    private int seqNum = 0;

    private XSSFSheet sheet = null;
    private DiffTemplate diffTemplate = null;

    public static final char LEFT = 'L';
    public static final char RIGHT = 'R';

    DiffSheet(DiffBook book, XSSFSheet sheet) {
        this.diffbook = book;
        this.sheet = sheet;
    }

    public XSSFCell getNextCell(char rlflag){
        if ( rlflag == LEFT ){
            return getNextLeftCell();
        }else{
            return getNextRightCell();
        }
    }

    public XSSFCell getNextLeftCell(){
        XSSFRow row = getCurRow(leftRowNum); 
        XSSFCell cell = row.getCell(diffTemplate.getLeftCol());
        leftRowNum++;
        return cell;
    }

    public XSSFCell getNextRightCell(){
        XSSFRow row = getCurRow(rightRowNum); 
        XSSFCell cell = row.getCell(diffTemplate.getRightCol());
        rightRowNum++;
        return cell;
    }

    private XSSFRow getCurRow(int rownum){
        if ( rownum > curRowNum ){
            curRowNum = rownum;
            XSSFRow rem = sheet.getRow(curRowNum);
            if ( rem != null ){
                sheet.removeRow(rem);
            }
            XSSFRow row = sheet.createRow(curRowNum);
            diffbook.copyTemplateMiddle(row);
            return row;
        }else{
            return sheet.getRow(rownum);
        }
    }

    private XSSFRow createNextRow(int rownum){
        curRowNum = rownum;
        XSSFRow rem = sheet.getRow(curRowNum);
        if ( rem != null ){
            sheet.removeRow(rem);
        }
        XSSFRow row = sheet.createRow(curRowNum);
        diffbook.copyTemplateTop(row);

        leftRowNum = rownum;
        rightRowNum = rownum;
        return row;
    }

    /**
     * Describe <code>done</code> method here.
     *
     */
    public void done(){
        curRowNum++;
        XSSFRow rem = sheet.getRow(curRowNum);
        if ( rem != null ){
            sheet.removeRow(rem);
        }
        XSSFRow row = sheet.createRow(curRowNum);
        diffbook.copyTemplateBottom(row);

        for(int i = curRowNum + 1; i < sheet.getLastRowNum(); i++ ){
            sheet.removeRow(sheet.getRow(i));
        }
    }

    /**
     * Describe <code>setCurSeq</code> method here.
     *
     * @return an <code>int</code> value
     */
    public int setCurSeq(){

        int rowNum;
        if ( seqNum == 0 ){
            diffTemplate = diffbook.getTemplate();
            rowNum = diffTemplate.getRowNum();
            curRowNum = rowNum;
            leftRowNum = rowNum;
            rightRowNum = rowNum;
        }else{
            rowNum = curRowNum + 1;
        }

        seqNum++;
        XSSFRow row = createNextRow(rowNum); 
        XSSFCell cell = row.getCell(diffTemplate.getSeqCol());
        cell.setCellValue( Integer.toString(seqNum) );

        return rowNum;
    }
    
    public void setFileName(String fileName) {
    	XSSFRow row = sheet.createRow(0);
    	XSSFCell cell = row.createCell(0);
    	cell.setCellValue(fileName);
    }

    public void setCellValue(XSSFCell cell, String line){
        cell.setCellValue( line );
    }

    public void setCellValueAttrib(XSSFCell cell, String line){
        cell.setCellValue( line );
        if ( line.length() != 0 ){
            setAttrib(cell, line.charAt(0));
        }
    }

    public void setNextCellValue(char rlflag, String line){
        XSSFCell cell = getNextCell(rlflag);
        setCellValue(cell,line);
    }

    public void setNextCellValue(char rlflag, String line, char mark){
        XSSFCell cell = getNextCell(rlflag);
        setCellValue(cell,line);
        setAttrib(cell, mark);
    }

    public void setNextCellValueAttrib(char rlflag, String line){
        XSSFCell cell = getNextCell(rlflag);
        setCellValueAttrib(cell, line);
    }

    private void setAttrib(XSSFCell cell, char mark){
        int index = cell.getColumnIndex();
        switch(mark){
        case '!': 
            if ( diffTemplate.getChangeStyle(index) != null ){
                cell.setCellStyle(diffTemplate.getChangeStyle(index));
            }
            break;
        case '+': 
            if ( diffTemplate.getAddStyle(index) != null ){
                cell.setCellStyle(diffTemplate.getAddStyle(index));
            }
            break;
        case '-': 
            if ( diffTemplate.getDeleteStyle(index) != null ){
                cell.setCellStyle(diffTemplate.getDeleteStyle(index));
            }
            break;
        }
    }
    
} // DiffSheet
