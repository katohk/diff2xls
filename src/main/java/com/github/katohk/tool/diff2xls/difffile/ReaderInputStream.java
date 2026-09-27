package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Wraps a BufferedReader as an InputStream for compatibility with existing code.
 * Useful for converting diff text streams to InputStream format.
 * 
 * @author katohk
 */
public class ReaderInputStream extends InputStream {
    
    private final BufferedReader reader;
    private final Charset charset;
    private byte[] buffer;
    private int pos = 0;
    private int len = 0;
    private boolean eof = false;
    
    /**
     * Create a ReaderInputStream from a BufferedReader.
     * 
     * @param reader the BufferedReader to wrap
     * @param charset character encoding to use
     */
    public ReaderInputStream(BufferedReader reader, Charset charset) {
        this.reader = reader;
        this.charset = charset;
        this.buffer = new byte[8192];
    }
    
    @Override
    public int read() throws IOException {
        if (pos >= len) {
            if (eof) {
                return -1;
            }
            
            String line = reader.readLine();
            if (line == null) {
                eof = true;
                return -1;
            }
            
            byte[] lineBytes = (line + "\n").getBytes(charset);
            if (buffer.length < lineBytes.length) {
                buffer = new byte[lineBytes.length * 2];
            }
            System.arraycopy(lineBytes, 0, buffer, 0, lineBytes.length);
            pos = 0;
            len = lineBytes.length;
        }
        
        return buffer[pos++] & 0xFF;
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
