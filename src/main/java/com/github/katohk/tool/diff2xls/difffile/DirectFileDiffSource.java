package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;

/**
 * DiffSource implementation that directly compares two files using java-diff-utils.
 * Generates unified or context diff format output.
 * 
 * @author katohk
 */
public class DirectFileDiffSource implements DiffSource {
    
    private final File leftFile;
    private final File rightFile;
    private final Charset encoding;
    private final boolean isContext;
    private BufferedReader reader;
    
    /**
     * Create a DirectFileDiffSource for comparing two files.
     * 
     * @param leftFile the original file
     * @param rightFile the revised file
     * @param encoding character encoding for reading files
     * @param isContext true for context diff, false for unified diff
     */
    public DirectFileDiffSource(File leftFile, File rightFile, Charset encoding, boolean isContext) {
        this.leftFile = leftFile;
        this.rightFile = rightFile;
        this.encoding = encoding;
        this.isContext = isContext;
    }
    
    @Override
    public BufferedReader getDiffReader() throws IOException {
        if (reader == null) {
            List<String> leftLines = Files.readAllLines(leftFile.toPath(), encoding);
            List<String> rightLines = Files.readAllLines(rightFile.toPath(), encoding);
            
            StringBuilder sb = new StringBuilder();
            
            if (isContext) {
                generateContextDiff(leftLines, rightLines, sb);
            } else {
                generateUnifiedDiff(leftLines, rightLines, sb);
            }
            
            reader = new BufferedReader(new StringReader(sb.toString()));
        }
        return reader;
    }
    
    @Override
    public String getLeftFileName() {
        return leftFile.getPath();
    }
    
    @Override
    public String getRightFileName() {
        return rightFile.getPath();
    }
    
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
    
    /**
     * Generate unified diff format using the actual Patch API exposed by java-diff-utils.
     */
    private void generateUnifiedDiff(List<String> leftLines, List<String> rightLines,
            StringBuilder sb) {
        String leftName = normalizeFileName(leftFile.getPath());
        String rightName = normalizeFileName(rightFile.getPath());

        Patch<String> patch = DiffUtils.diff(leftLines, rightLines);
        if (patch.getDeltas().isEmpty()) {
            return;
        }

        sb.append("--- ").append(leftName).append("\n");
        sb.append("+++ ").append(rightName).append("\n");

        for (var delta : patch.getDeltas()) {
            int leftStart = delta.getSource().getPosition() + 1;
            int rightStart = delta.getTarget().getPosition() + 1;
            int leftLength = Math.max(delta.getSource().size(), 0);
            int rightLength = Math.max(delta.getTarget().size(), 0);

            sb.append("@@ -")
              .append(formatRange(leftStart, leftLength))
              .append(" +")
              .append(formatRange(rightStart, rightLength))
              .append(" @@\n");

            for (String line : delta.getSource().getLines()) {
                sb.append('-').append(line).append('\n');
            }
            for (String line : delta.getTarget().getLines()) {
                sb.append('+').append(line).append('\n');
            }
        }
    }
    
    /**
     * Generate context diff format from the patch object.
     */
    private void generateContextDiff(List<String> leftLines, List<String> rightLines,
            StringBuilder sb) {
        String leftName = normalizeFileName(leftFile.getPath());
        String rightName = normalizeFileName(rightFile.getPath());

        Patch<String> patch = DiffUtils.diff(leftLines, rightLines);
        if (patch.getDeltas().isEmpty()) {
            return;
        }

        sb.append("*** ").append(leftName).append("\n");
        sb.append("--- ").append(rightName).append("\n");
        sb.append("***************\n");

        for (var delta : patch.getDeltas()) {
            int leftStart = delta.getSource().getPosition() + 1;
            int rightStart = delta.getTarget().getPosition() + 1;
            int leftLength = Math.max(delta.getSource().size(), 1);
            int rightLength = Math.max(delta.getTarget().size(), 1);

            sb.append("*** ")
              .append(formatRange(leftStart, leftLength))
              .append(" ****\n");

            for (String line : delta.getSource().getLines()) {
                sb.append('-').append(line).append('\n');
            }

            sb.append("--- ")
              .append(formatRange(rightStart, rightLength))
              .append(" ----\n");

            for (String line : delta.getTarget().getLines()) {
                sb.append('+').append(line).append('\n');
            }
        }
    }

    private String formatRange(int start, int length) {
        if (length <= 0) {
            return String.valueOf(start) + ",0";
        }
        return start + "," + length;
    }
    
    /**
     * Normalize file path (backslash to forward slash).
     */
    private String normalizeFileName(String path) {
        return path.replace("\\", "/");
    }
}
