package com.github.katohk.tool.diff2xls.difffile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DiffLine.java
 *
 *
 * Created: Thu May  6 16:27:57 2004
 *
 * @version 1.0
 */
public class DiffLine {

    private Iterator<String> it;
    private String curLine = "";
    private char curMark = ' ';
    private boolean hasNext = true;
    private boolean hold = false;

    private List<String> outArray = new ArrayList<String>();

    DiffLine(Iterator<String> it){
        this.it = it;
    }

    public List<String> getLines(){
        return outArray;
    }

    public String getLine(){
        return curLine;
    }

    public char getMark(){
        return curMark;
    }

    public boolean hasNext(){
        return hasNext;
    }

    private void holdLine(){
        hold = true;
    }

    private void skipLine(){
        outArray.add("");
    }

    private void addLine(){
        hold = false;
        outArray.add(curLine);
    }


    public void getNext(){
        
        String line = "";

        if ( hold ){
            return;
        }

        if ( hasNext ){
            if ( it.hasNext() ){
                line = (String)it.next();
                line = line.replace('\t',' ');
                if ( line.indexOf("***") == 0 || line.indexOf("---") == 0 ||
                	 line.indexOf("@@ ") == 0 ){
                    line = " " + line;
                }
            }else{
                hasNext = false;
            }
            if ( line.length() > 0 ){
                curMark = line.charAt(0);
            }else{
                curMark = ' ';
            }
        }
        curLine = line;
    }

    public void syncLine(DiffLine other){
        char mark2 = other.getMark();

        if ( getMark() == ' ' ){
            if ( mark2 == ' ' ){
                addLine();
            }else{              // ! + -
                holdLine();
                skipLine();
            }
        }else{                  // ! + -
            addLine(); 
        }
    }

    public static void syncLines(DiffLine l1, DiffLine l2){
        while ( l1.hasNext() || l2.hasNext() ){
            l1.getNext();
            l2.getNext();
            l1.syncLine(l2);
            l2.syncLine(l1);
        }
    }
    
} // DiffLine
