package com.github.katohk.tool.diff2xls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.katohk.tool.diff2xls.diffbook.DiffBook;
import com.github.katohk.tool.diff2xls.diffbook.DiffSheet;
import com.github.katohk.tool.diff2xls.difffile.ContextDiffBlock;
import com.github.katohk.tool.diff2xls.difffile.DiffBlockIF;
import com.github.katohk.tool.diff2xls.difffile.DiffBlockState;
import com.github.katohk.tool.diff2xls.difffile.DiffLine;
import com.github.katohk.tool.diff2xls.difffile.UnifiedDiffBlock;

/**
 * DiffBlockImpl.java
 *
 */
class DiffBlockProcess{
	
	private DiffBlockIF diffBlock = null;

    private DiffBook book = null;
    private DiffSheet ds = null;

    private OutputStream fout = null;
    private int mode = 3;

    private final int MAXROW = 20;

    private Map<String,Integer> sheetNames = new HashMap<String,Integer>();

    DiffBlockProcess(int mode, String template, InputStream in,
                  OutputStream out,String enc, int format) throws IOException {
    	switch (format) {
    	case 0:
    		diffBlock = new UnifiedDiffBlock(in,enc);
    		break;
    	case 1:
    		diffBlock = new ContextDiffBlock(in,enc);
    		break;
    	}
        init(mode, template, out);
    }

    private void init(int mode, 
                      String template, OutputStream out) throws IOException {
        book = new DiffBook(template);
        this.fout = out;
        this.mode = mode;
    }

    // file start
    protected void procSOF(){
    }

    // file end
    protected void procEOF() throws IOException {
        if ( ds != null ){
            ds.done();
            book.write(fout);
        }
    }

    // start of diff entry
    protected void procSOE(){

        if ( ds != null ){
            ds.done();
        }
        ds = book.makeDiffSheet(getSheetName());
        ds.setFileName(diffBlock.getLeftFullName());

    }

    // end of block
    protected void procEOB(){
        ds.setCurSeq();

        switch (mode){
        case 1:
            procEOB1();
            break;
        case 2:
            procEOB2();
            break;
        default:
            procEOB3();
            break;
        }
    }

    // mode = 1
    //
    private void procEOB1(){
        // left cell
        procCell1(diffBlock.getLeftDiffLine(), MAXROW, DiffSheet.LEFT);
        // right cell
        procCell1(diffBlock.getRightDiffLine(), MAXROW, DiffSheet.RIGHT);

    }

    private void procCell1(DiffLine dl, int maxrow, char rlflag){

        do{
            StringBuffer sb = new StringBuffer();

            int count = 0;
            while(dl.hasNext()){
                count++;
                dl.getNext();
                String line = dl.getLine();
                sb.append(line).append("\n");
                if ( count == maxrow ){
                    break;
                }
            }

            ds.setNextCellValue(rlflag, sb.toString());

        }while( dl.hasNext() );
    }

    //
    // mode = 2
    //
    private void procEOB2(){
        // left cell
        procCell2(diffBlock.getLeftDiffLine(), DiffSheet.LEFT);
        // right cell
        procCell2(diffBlock.getRightDiffLine(), DiffSheet.RIGHT);

    }

    private void procCell2(DiffLine dl, char rlflag){

        while(dl.hasNext()){
            dl.getNext();
            ds.setNextCellValue(rlflag, dl.getLine(), dl.getMark());
        }
    }


    //
    // mode = 3
    //
    private void procEOB3(){

        DiffLine left = diffBlock.getLeftDiffLine();
        DiffLine right = diffBlock.getRightDiffLine();

        DiffLine.syncLines(left,right);
        
        Iterator<String> it1 = left.getLines().iterator();
        Iterator<String> it2 = right.getLines().iterator();

        while( it1.hasNext() && it2.hasNext() ){
            String line1 = it1.next();
            String line2 = it2.next();

            ds.setNextCellValueAttrib(DiffSheet.LEFT, line1);
            ds.setNextCellValueAttrib(DiffSheet.RIGHT, line2);
        }
            
    }

    private String getSheetName(){
        String name = diffBlock.getLeftName();

        if ( name.length() > 27 ) {
            name = name.substring(0,24) + "...";
        }
        
        String name2 = name.toUpperCase();
        
        Integer n = sheetNames.get(name2);
        if ( n == null ){
            sheetNames.put(name2, 1);
            return name;
        }else{
            int nn = n.intValue() + 1;
            sheetNames.put(name2, nn);
            name = name + "(" + Integer.toString(nn) + ")";
            return name;
        }
    }

   /*
    * diff file
    */
   public void start() throws IOException{

       DiffBlockState stat;
       procSOF();
       while( DiffBlockState.EOF != (stat = diffBlock.getDiffEntry()) ){

    	   switch (stat){
    	   case EOB:
    		   procEOB();      // end of block 
    		   break;
    	   case SOE:
    		   procSOE();      // start of file entry
    		   break;
    	   default:
    	   }

       }

       if ( ! diffBlock.isEmpty() ){
           procEOB();
       }
       procEOF();

   }
}
