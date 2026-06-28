package com.github.katohk.tool.diff2xls.difffile;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

public class ContextDiffBlockTest {

    @Test
    public void testNewFile_Addition() throws Exception {
        String diff = """
*** /dev/null
--- b/test/path/to/new/file.py
***************
*** 0 ****
--- 108 ****
+ import os
+ import uuid
""";
        ContextDiffBlock block = new ContextDiffBlock(new StringReader(diff));
        
        DiffBlockState state = block.getDiffEntry();
        assertEquals(DiffBlockState.SOE, state);
        assertEquals("/dev/null", block.getLeftFullName());
        assertEquals("test/path/to/new/file.py", block.getRightFullName());
    }

    @Test
    public void testFileDeletion() throws Exception {
        String diff = """
*** a/old_file.py
--- /dev/null
***************
*** 1 ****
- old content
--- 0 ----
""";
        ContextDiffBlock block = new ContextDiffBlock(new StringReader(diff));
        
        DiffBlockState state = block.getDiffEntry();
        assertEquals(DiffBlockState.SOE, state);
        assertEquals("old_file.py", block.getLeftFullName());
        assertEquals("/dev/null", block.getRightFullName());
    }

    @Test
    public void testFileModification() throws Exception {
        String diff = """
*** a/test/path/to/service.py
--- b/test/path/to/service.py
***************
*** 1 ****
- old line
\\ No newline at end of file
--- 1 ----
+ new line
""";
        ContextDiffBlock block = new ContextDiffBlock(new StringReader(diff));
        
        DiffBlockState state = block.getDiffEntry();
        assertEquals(DiffBlockState.SOE, state);
        assertEquals("test/path/to/service.py", block.getLeftFullName());
        assertEquals("test/path/to/service.py", block.getRightFullName());
    }

    @Test
    public void testNoNewlineAtEndOfFile() throws Exception {
        String diff = """
*** a/file.txt
--- b/file.txt
***************
*** 1 ****
- line2
\\ No newline at end of file
--- 1 ----
+ line2
""";
        ContextDiffBlock block = new ContextDiffBlock(new StringReader(diff));
        
        // First entry should be SOE
        DiffBlockState state = block.getDiffEntry();
        assertEquals(DiffBlockState.SOE, state);
        
        // Should process the diff lines correctly
        state = block.getDiffEntry();
        assertEquals(DiffBlockState.EOF, state);
    }

    @Test
    public void testSOETiming() throws Exception {
        String diff = """
*** a/file1.txt
--- b/file1.txt
***************
*** 1 ****
- content
--- 1 ----
+ content2
""";
        ContextDiffBlock block = new ContextDiffBlock(new StringReader(diff));
        
        // SOE should be returned after --- line, not after *** line
        DiffBlockState state = block.getDiffEntry();
        assertEquals(DiffBlockState.SOE, state);
        
        // Both names should be set
        assertEquals("file1.txt", block.getLeftFullName());
        assertEquals("file1.txt", block.getRightFullName());
    }

    @Test
    public void testMultipleFiles() throws Exception {
        String diff = """
*** a/file1.txt
--- b/file1.txt
***************
*** 1 ****
- content
--- 1 ----
+ content2
********************
*** a/file2.txt
--- b/file2.txt
***************
*** 1 ****
- old
--- 1 ----
+ new
""";
        ContextDiffBlock block = new ContextDiffBlock(new StringReader(diff));
        
        // First file
        DiffBlockState state = block.getDiffEntry();
        assertEquals(DiffBlockState.SOE, state);
        assertEquals("file1.txt", block.getLeftFullName());
        
        state = block.getDiffEntry();
        assertEquals(DiffBlockState.EOB, state);
        
        // Second file - **** marks end of block and start of new file
        state = block.getDiffEntry();
        assertEquals(DiffBlockState.EOB, state);
        
        state = block.getDiffEntry();
        assertEquals(DiffBlockState.EOF, state);
    }
}
