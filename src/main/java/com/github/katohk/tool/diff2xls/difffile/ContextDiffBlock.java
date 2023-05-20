package com.github.katohk.tool.diff2xls.difffile;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Context diff format
 *
 * Created: Fri Apr  9 13:53:58 2004
 *
 * @version 1.0
 */
public class ContextDiffBlock extends DiffBlockBase{


	public ContextDiffBlock(Reader rd) throws IOException {
		super(rd);
	}

	public ContextDiffBlock(InputStream in, String enc) throws IOException {
		super(in,enc);
	}

	public ContextDiffBlock(String fname, String enc) throws IOException {
		super(fname, enc);
	}

	public DiffBlockState getDiffEntry() throws IOException {

		leftBlock = new ArrayList<String>();
		rightBlock = new ArrayList<String>();

		while( (line = br.readLine()) != null ){

			char ch = '\0';
			if ( line.length() > 1 ){
				ch = line.charAt(0);
			}
			
			if ( line.indexOf("@@ ") == 0 ) {
				throw new IllegalArgumentException("invalid context diff format");
			}

			if ( line.indexOf("****") == 0 ) {

				// block start
				status = ELSE;

				if ( block ){
					return DiffBlockState.EOB; // end of block
				}else{
					block = true;
					return DiffBlockState.SOE;
				}

			}else if ( line.indexOf("*** ") == 0 ){

				// left block
				if ( block == false ){
					// set filename
					leftName = getNameField(line);
					System.out.println(leftName);
				}else{ 
					status = LEFT;
					addLine(leftBlock,line);
				}

			}else if ( line.indexOf("--- ") == 0 ){
				// right block
				if ( block == false ){
					// set filename
					rightName = getNameField(line);
				}else{
					status = RIGHT;
					addLine(rightBlock,line);
				}

			}else if ( ch == '!' || ch == '+' || ch == '-' || ch == ' ' ){

				if ( status == LEFT ){
					addLine(leftBlock,line);
				}else if ( status == RIGHT ){
					addLine(rightBlock,line);
				}

			}else{
				// block end
				if ( block ){
					block = false;
					return DiffBlockState.EOB;     // end of entry
				}
				block = false;
			}
		}
		return DiffBlockState.EOF;
	}


} // ContextDiffBlock
