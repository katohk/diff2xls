package com.github.katohk.tool.diff2xls.difffile;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DiffBlockBaseTest {

    // Test subclass to access protected methods
    private static class TestDiffBlock extends DiffBlockBase {
        public TestDiffBlock(String content) throws Exception {
            super(new StringReader(content));
        }

        public List<String> testGetNameField(String line) {
            return getNameField(line);
        }

        public String testGetFullPathName(List<String> nameField) {
            return getFullPathName(nameField);
        }

        @Override
        public DiffBlockState getDiffEntry() {
            return null;
        }
    }

    @Test
    public void testGetNameField_NormalPath() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> result = block.testGetNameField("--- a/path/to/file.txt");
        assertEquals(List.of("path", "to", "file.txt"), result);
    }

    @Test
    public void testGetNameField_DevNull() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> result = block.testGetNameField("--- /dev/null");
        assertEquals(List.of("", "dev", "null"), result);
    }

    @Test
    public void testGetNameField_WithAPrefix() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> result = block.testGetNameField("--- a/path/to/file.txt");
        assertEquals(List.of("path", "to", "file.txt"), result);
    }

    @Test
    public void testGetNameField_WithBPrefix() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> result = block.testGetNameField("+++ b/path/to/file.txt");
        assertEquals(List.of("path", "to", "file.txt"), result);
    }

    @Test
    public void testGetNameField_AbsolutePath() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> result = block.testGetNameField("--- /absolute/path/to/file.txt");
        assertEquals(List.of("", "absolute", "path", "to", "file.txt"), result);
    }

    @Test
    public void testGetNameField_RelativePath() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> result = block.testGetNameField("--- relative/path/to/file.txt");
        assertEquals(List.of("relative", "path", "to", "file.txt"), result);
    }

    @Test
    public void testGetFullPathName_DevNull() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> nameField = List.of("", "dev", "null");
        String result = block.testGetFullPathName(nameField);
        assertEquals("/dev/null", result);
    }

    @Test
    public void testGetFullPathName_NormalPath() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> nameField = List.of("path", "to", "file.txt");
        String result = block.testGetFullPathName(nameField);
        assertEquals("path/to/file.txt", result);
    }

    @Test
    public void testGetFullPathName_AbsolutePath() throws Exception {
        TestDiffBlock block = new TestDiffBlock("");
        List<String> nameField = List.of("", "absolute", "path");
        String result = block.testGetFullPathName(nameField);
        assertEquals("/absolute/path", result);
    }
}
