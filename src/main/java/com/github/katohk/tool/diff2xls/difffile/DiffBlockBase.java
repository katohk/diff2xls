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

	protected List<String> leftNameField = null;
	protected List<String> rightNameField = null;

	protected List<String> leftBlock = new ArrayList<String>();
	protected List<String> rightBlock = new ArrayList<String>();

	protected BufferedReader br = null;
	protected String line = null;

	protected final int LEFT = 1;
	protected final int RIGHT = 2;
	protected final int ELSE = 0;
	protected int status = ELSE;
	protected boolean block = false;

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
		return getFileName(leftNameField);
	}

	public String getRightName(){
		return getFileName(rightNameField);
	}

	public String getLeftFullName(){
		return getFullPathName(leftNameField);
	}

	public String getRightFullName(){
		return getFullPathName(rightNameField);
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

	public boolean isEmpty() {
		return (leftBlock.isEmpty() && rightBlock.isEmpty());
	}

	public String getFullPathName(List<String> nameField) {
		return String.join("/", nameField);
	}
	
	protected String getFileName(List<String> nameField) {
		int size = nameField.size();
		if ( size > 0 ) {
			return nameField.get(size - 1);
		}
		return "";
	}

	protected List<String> getNameField(String line){

		List<String> pathList = new ArrayList<>();
		StringBuilder name = new StringBuilder();
		int word = 0;
		boolean quote = false;
		boolean isAbsolutePath = false;
		for(int i=0; i < line.length(); i++){
			char ch = line.charAt(i);
			
			if ( ch == '"' ) {
				quote = quote ? false : true;
				continue;
			}
			
			if ( quote == false ) {
				if ( ch == ' ' || ch == '\t' ){
					word++;
					continue;
				}
			}

			if ( word == 1 ) {
				if ( ch == '/' || ch == '\\' ){
					String segment = name.toString();
					if (!segment.isEmpty()) {
						pathList.add(segment);
					} else if (pathList.isEmpty()) {
						// Mark as absolute path if first segment is empty
						isAbsolutePath = true;
					}
					name.setLength(0);
				}else {
					name.append(ch);
				}
			} else if ( word == 2 ) {
				break;
			}
		}

		pathList.add(name.toString());
		
		// Strip 'a/' or 'b/' prefix commonly used in git diffs
		if (pathList.size() > 0) {
			String firstComponent = pathList.get(0);
			if (firstComponent.equals("a") || firstComponent.equals("b")) {
				pathList.remove(0);
			}
		}
		
		// Add leading empty string to indicate absolute path
		if (isAbsolutePath && !pathList.isEmpty()) {
			pathList.add(0, "");
		}
		
		return pathList;
	}
}
