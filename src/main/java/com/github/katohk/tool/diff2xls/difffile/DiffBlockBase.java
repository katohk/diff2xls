package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public abstract class DiffBlockBase implements DiffBlockIF {

	String leftName = null;
	String rightName = null;

	List<String> leftBlock = new ArrayList<String>();
	List<String> rightBlock = new ArrayList<String>();

	BufferedReader br = null;
	String line = null;

	final int LEFT = 1;
	final int RIGHT = 2;
	final int ELSE = 0;
	int status = ELSE;
	boolean block = false;

	public DiffBlockBase(Reader rd) throws IOException {
		br = new BufferedReader(rd);
	}

	public DiffBlockBase(InputStream in, String enc) throws IOException {
		init(in,enc);
	}

	public DiffBlockBase(String fname, String enc) throws IOException {
		init(fname, enc);
	}

	private void init(String fname, String enc) throws IOException{
		init(new FileInputStream(fname),enc);
	}

	private void init(InputStream fi, String enc) throws IOException{
		br = new BufferedReader(new InputStreamReader(fi, enc));
	}

	public String getLeftName(){
		return leftName;
	}

	public String getRightName(){
		return rightName;
	}

	public DiffLine getLeftDiffLine(){
		return new DiffLine(leftBlock.iterator());
	}

	public DiffLine getRightDiffLine(){
		return new DiffLine(rightBlock.iterator());
	}

	public abstract DiffBlockState getDiffEntry() throws IOException;

	protected void addLine(List<String> list, String line){
		list.add(line);
	}

	protected String getNameField(String line){

		StringBuffer name = new StringBuffer();
		int word = 0;
		for(int i=0; i < line.length(); i++){
			char ch = line.charAt(i);
			if ( ch == ' ' || ch == '\t' ){
				word++;
				if ( word == 2 ){
					break;
				}
			}else{
				if ( word == 1 ){
					if ( ch == '/' || ch == '\\' ){
						name = new StringBuffer();
					} else{
						name.append(ch);
					}
				}
			}
		}
		return name.toString();
	}


	public boolean isEmpty() {
		return (leftBlock.isEmpty() && rightBlock.isEmpty());
	}
}
