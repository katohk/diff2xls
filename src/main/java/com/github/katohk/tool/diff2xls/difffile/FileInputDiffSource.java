package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * DiffSource implementation that reads pre-generated diff from a file.
 * This implements the existing behavior: reading diff output from a file or stdin.
 * 
 * @author katohk
 */
public class FileInputDiffSource implements DiffSource {
    
    private final String filePath;
    private final String encoding;
    private BufferedReader reader;
    
    /**
     * Create a FileInputDiffSource that reads from a file or stdin.
     * 
     * @param filePath path to diff file, or null to read from stdin
     * @param encoding character encoding of the diff file
     */
    public FileInputDiffSource(String filePath, String encoding) {
        this.filePath = filePath;
        this.encoding = encoding;
    }
    
    @Override
    public BufferedReader getDiffReader() throws IOException {
        if (reader == null) {
            if (filePath == null || filePath.isEmpty()) {
                // Read from stdin
                reader = new BufferedReader(new InputStreamReader(System.in, encoding));
            } else {
                // Read from file
                FileInputStream fis = new FileInputStream(new File(filePath));
                reader = new BufferedReader(new InputStreamReader(fis, encoding));
            }
        }
        return reader;
    }
    
    @Override
    public String getLeftFileName() {
        // File-based input doesn't know file names in advance
        // They will be extracted from diff headers by parsers
        return null;
    }
    
    @Override
    public String getRightFileName() {
        // File-based input doesn't know file names in advance
        // They will be extracted from diff headers by parsers
        return null;
    }
    
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
}
