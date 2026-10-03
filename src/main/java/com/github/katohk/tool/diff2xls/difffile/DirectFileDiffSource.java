package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    private final List<String> leftLinesOverride;
    private final List<String> rightLinesOverride;
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
        this(leftFile, rightFile, encoding, isContext, null, null);
    }

    DirectFileDiffSource(File leftFile, File rightFile, Charset encoding, boolean isContext,
            List<String> leftLinesOverride, List<String> rightLinesOverride) {
        this.leftFile = leftFile;
        this.rightFile = rightFile;
        this.encoding = encoding;
        this.isContext = isContext;
        this.leftLinesOverride = leftLinesOverride;
        this.rightLinesOverride = rightLinesOverride;
    }
    
    @Override
    public BufferedReader getDiffReader() throws IOException {
        if (reader == null) {
            Charset leftEncoding = detectEncodingForFile(leftFile, encoding);
            Charset rightEncoding = detectEncodingForFile(rightFile, encoding);
            List<String> leftLines = leftLinesOverride != null
                    ? leftLinesOverride
                    : Files.readAllLines(leftFile.toPath(), leftEncoding);
            List<String> rightLines = rightLinesOverride != null
                    ? rightLinesOverride
                    : Files.readAllLines(rightFile.toPath(), rightEncoding);
            
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
    
    static Charset detectEncodingForFile(File file, Charset preferred) {
        List<Charset> candidates = new ArrayList<>();
        if (preferred != null) {
            candidates.add(preferred);
        }
        if (preferred == null || !preferred.equals(StandardCharsets.UTF_8)) {
            candidates.add(StandardCharsets.UTF_8);
        }
        candidates.add(Charset.forName("Shift_JIS"));
        candidates.add(Charset.forName("Windows-31J"));

        for (Charset candidate : candidates) {
            if (canDecode(file.toPath(), candidate)) {
                return candidate;
            }
        }

        return preferred != null ? preferred : StandardCharsets.UTF_8;
    }

    private static boolean canDecode(Path path, Charset charset) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            CharsetDecoder decoder = charset.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
            decoder.decode(ByteBuffer.wrap(bytes));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Normalize file path (backslash to forward slash).
     */
    private String normalizeFileName(String path) {
        return path.replace("\\", "/");
    }
}
