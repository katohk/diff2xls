package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Abstract interface for diff input sources.
 * Implementations provide BufferedReader with diff content and file name information.
 * 
 * Supported formats:
 * - Unified diff (unified format parser will consume)
 * - Context diff (context format parser will consume)
 * 
 * @author katohk
 */
public interface DiffSource {
    
    /**
     * Get a BufferedReader containing diff content.
     * The diff should be formatted as either unified or context diff format.
     * 
     * @return BufferedReader with diff content
     * @throws IOException if diff cannot be read
     */
    BufferedReader getDiffReader() throws IOException;
    
    /**
     * Get the left (original/base) file name.
     * Used for diff header generation and Excel sheet naming.
     * 
     * @return left file name, or null if not applicable
     */
    String getLeftFileName();
    
    /**
     * Get the right (revised/modified) file name.
     * Used for diff header generation and Excel sheet naming.
     * 
     * @return right file name, or null if not applicable
     */
    String getRightFileName();
    
    /**
     * Close any resources held by this DiffSource.
     * Implementations should clean up file handles, streams, etc.
     * 
     * @throws IOException if resource cleanup fails
     */
    void close() throws IOException;
}
