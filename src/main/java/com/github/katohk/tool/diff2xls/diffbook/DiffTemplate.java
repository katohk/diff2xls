package com.github.katohk.tool.diff2xls.diffbook;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;


/**
 * DiffTemplate.java
 *
 *
 * Created: Fri Apr  2 18:21:20 2004
 *
 * @version 1.0
 */
class DiffTemplate {
    private short seqCol = 1;
    private short leftCol = 2;
    private short rightCol = 3;
    private int rowNum = 0;

    private XSSFRow topRow = null;
    private XSSFRow middleRow = null;
    private XSSFRow bottomRow = null;

    /*
    private XSSFCellStyle changeStyle = null;
    private XSSFCellStyle addStyle = null;
    private XSSFCellStyle deleteStyle = null;
    */

    private Map<Integer,XSSFCellStyle> changeStyle = new HashMap<>();
    private Map<Integer,XSSFCellStyle> addStyle = new HashMap<>();
    private Map<Integer,XSSFCellStyle> deleteStyle = new HashMap<>();
    
    private XSSFCellStyle fileNameStyle;

    /**
     * Creates a new <code>DiffTemplate</code> instance.
     */
    DiffTemplate(){
    }
   
    /**
     * Gets the value of seqCol
     *
     * @return the value of seqCol
     */
    public short getSeqCol()  {
        return this.seqCol;
    }

    /**
     * Gets the value of leftCol
     *
     * @return the value of leftCol
     */
    public short getLeftCol()  {
        return this.leftCol;
    }

    /**
     * Gets the value of rightCol
     *
     * @return the value of rightCol
     */
    public short getRightCol()  {
        return this.rightCol;
    }

    /**
     * Gets the value of rowNum
     *
     * @return the value of rowNum
     */
    public int getRowNum()  {
        return this.rowNum;
    }

    /**
     * Sets the value of seqCol
     *
     * @param argSeqCol Value to assign to this.seqCol
     */
    public void setSeqCol(short argSeqCol) {
        this.seqCol = argSeqCol;
    }

    /**
     * Sets the value of leftCol
     *
     * @param argLeftCol Value to assign to this.leftCol
     */
    public void setLeftCol(short argLeftCol) {
        this.leftCol = argLeftCol;
    }

    /**
     * Sets the value of rightCol
     *
     * @param argRightCol Value to assign to this.rightCol
     */
    public void setRightCol(short argRightCol) {
        this.rightCol = argRightCol;
    }

    /**
     * Sets the value of rowNum
     *
     * @param argRowNum Value to assign to this.rowNum
     */
    public void setRowNum(int argRowNum) {
        this.rowNum = argRowNum;
    }
    

    /**
     * Gets the value of topRow
     *
     * @return the value of topRow
     */
    public XSSFRow getTopRow()  {
        return this.topRow;
    }

    /**
     * Sets the value of topRow
     *
     * @param argTopRow Value to assign to this.topRow
     */
    public void setTopRow(XSSFRow argTopRow) {
        this.topRow = argTopRow;
    }

    /**
     * Gets the value of middleRow
     *
     * @return the value of middleRow
     */
    public XSSFRow getMiddleRow()  {
        return this.middleRow;
    }

    /**
     * Sets the value of middleRow
     *
     * @param argMiddleRow Value to assign to this.middleRow
     */
    public void setMiddleRow(XSSFRow argMiddleRow) {
        this.middleRow = argMiddleRow;
    }

    /**
     * Gets the value of bottomRow
     *
     * @return the value of bottomRow
     */
    public XSSFRow getBottomRow()  {
        return this.bottomRow;
    }

    /**
     * Sets the value of bottomRow
     *
     * @param argBottomRow Value to assign to this.bottomRow
     */
    public void setBottomRow(XSSFRow argBottomRow) {
        this.bottomRow = argBottomRow;
    }



    /**
     * Gets the value of changeStyle
     *
     * @return the value of changeStyle
     */
    public XSSFCellStyle getChangeStyle(int index)  {
        return this.changeStyle.get(index);
    }

    /**
     * Sets the value of changeStyle
     *
     * @param argChangeStyle Value to assign to this.changeStyle
     */
    public void setChangeStyle(XSSFCellStyle argChangeStyle, int index) {
        this.changeStyle.put(index, argChangeStyle);
    }

    /**
     * Gets the value of addStyle
     *
     * @return the value of addStyle
     */
    public XSSFCellStyle getAddStyle(int index)  {
        return this.addStyle.get(index);
    }

    /**
     * Sets the value of addStyle
     *
     * @param argAddStyle Value to assign to this.addStyle
     */
    public void setAddStyle(XSSFCellStyle argAddStyle, int index) {
        this.addStyle.put(index, argAddStyle);
    }

    /**
     * Gets the value of deleteStyle
     *
     * @return the value of deleteStyle
     */
    public XSSFCellStyle getDeleteStyle(int index)  {
        return this.deleteStyle.get(index);
    }

    /**
     * Sets the value of deleteStyle
     *
     * @param argDeleteStyle Value to assign to this.deleteStyle
     */
    public void setDeleteStyle(XSSFCellStyle argDeleteStyle, int index) {
        this.deleteStyle.put(index, argDeleteStyle);
    }

   public XSSFCellStyle getFileNameStyle() {
	   return this.fileNameStyle;
   }
    
   public void setFileNameStyle(XSSFCellStyle argFileNameStyle) {
	   this.fileNameStyle = argFileNameStyle;
   }
}
