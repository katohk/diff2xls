package com.github.katohk.tool.diff2xls;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * DiffBlockBuilder.java
 *
 *
 * Created: Mon May 10 12:42:07 2004
 *
 * @version 1.0
 */
public class DiffBlockBuilder {

    private String fileNameIn = null;
    private String fileNameOt = null;

    private InputStream streamIn = null;
    private String enc = "UTF-8";

    private OutputStream streamOt = null;

    private String template = null;

    private int mode = 3;
    private int format = 0;


    public DiffBlockBuilder() {
        
    } // DiffBlockBuilder constructor
   
    /**
     * Sets the value of fileNameIn
     *
     * @param argFileNameIn Value to assign to this.fileNameIn
     */
    public void setFileNameIn(String argFileNameIn) {
        this.fileNameIn = argFileNameIn;
    }

    /**
     * Sets the value of fileNameOt
     *
     * @param argFileNameOt Value to assign to this.fileNameOt
     */
    public void setFileNameOt(String argFileNameOt) {
        this.fileNameOt = argFileNameOt;
    }

    /**
     * Sets the value of streamIn
     *
     * @param argStreamIn Value to assign to this.streamIn
     */
    public void setStreamIn(InputStream argStreamIn) {
        this.streamIn = argStreamIn;
    }

    /**
     * Sets the value of streamOt
     *
     * @param argStreamOt Value to assign to this.streamOt
     */
    public void setStreamOt(OutputStream argStreamOt) {
        this.streamOt = argStreamOt;
    }

    /**
     * Sets the value of template
     *
     * @param argTemplate Value to assign to this.template
     */
    public void setTemplate(String argTemplate) {
        this.template = argTemplate;
    }

    /**
     * Sets the value of mode
     *
     * @param argMode Value to assign to this.mode
     */
    public void setMode(int argMode) {
        this.mode = argMode;
    }

    /**
     * Sets the value of enc 
     *
     * @param argEnc Value to assign to this.enc
     */
    public void setEncode(String argEnc) {
        this.enc = argEnc;
    }

    /**
	 * @param format the format to set
	 */
	public void setFormat(int format) {
		this.format = format;
	}

	public DiffBlockProcess getDiffBlock() throws IOException {

        if ( fileNameIn != null && streamIn == null ){
            streamIn = new FileInputStream(fileNameIn);
        }

        if ( streamIn == null ){
            streamIn = System.in; 
        }

        if ( fileNameOt != null && streamOt == null ){
            streamOt = new FileOutputStream(fileNameOt);
        }
        
        if ( streamOt == null ){
            streamOt = System.out;
        }

        return new DiffBlockProcess(mode, template, streamIn, streamOt, enc, format);
    }
    
} // DiffBlockBuilder
