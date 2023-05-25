package com.github.katohk.tool.diff2xls.difffile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Unified diff format
 *
 */
public class UnifiedDiffBlock extends DiffBlockBase {

	private int seq = 0;
	private String lineNum = null;

	public UnifiedDiffBlock(Reader rd) throws IOException {
		super(rd);
	}

	public UnifiedDiffBlock(InputStream in, String enc) throws IOException {
		super(in,enc);
	}

	public UnifiedDiffBlock(String fname, String enc) throws IOException {
		super(fname, enc);
	}

	public DiffBlockState getDiffEntry() throws IOException {
		leftBlock = new ArrayList<String>();
		rightBlock = new ArrayList<String>();
		
		if ( lineNum != null ) {
			setLineNum();
		}

		while( (line = br.readLine()) != null ){

			char ch = '\0';
			if ( line.length() > 0 ){
				ch = line.charAt(0);
			}

			if ( line.indexOf("****") == 0 ) {
				throw new IllegalArgumentException("invalid unified diff format");
			}

			if ( line.indexOf("@@ ") == 0 ) {
				seq++;
				block = true;
				lineNum = line;
				if ( seq > 1 ) {
					return DiffBlockState.EOB;
				} else {
					setLineNum();
					lineNum = null;
				}

			}else if ( line.indexOf("--- ") == 0 ){
				seq = 0;
				// set filename
				leftNameField = getNameField(line);
				System.out.println(getFileName(leftNameField));
				return DiffBlockState.SOE;

			}else if ( line.indexOf("+++ ") == 0 ){
				// set filename
				rightNameField = getNameField(line);

			}else if ( ch == '-' ) {
				addLine(leftBlock,line);

			}else if ( ch == '+' ) {
				addLine(rightBlock,line);

			}else if ( ch == ' ' ) {
				addLine(leftBlock,line);
				addLine(rightBlock,line);

			}else{
				if ( block ) {
					block = false;
					lineNum = null;
					return DiffBlockState.EOB; // end of block
				}
			}
		}
		return DiffBlockState.EOF;
	}
	
	private void setLineNum() {
		String[] parts = lineNum.split(" ");
		if ( parts.length > 2 ) {
			leftBlock.add("@@ " + parts[1]);
			rightBlock.add("@@ " + parts[2]);
		}
	}

}
