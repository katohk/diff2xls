package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * DiffSource implementation that recursively compares two directories.
 * Generates aggregated diff output for all files, including new/deleted files.
 * Uses java-diff-utils for diff computation.
 * 
 * @author katohk
 */
public class DirectoryDiffSource implements DiffSource {
    
    private final File leftDir;
    private final File rightDir;
    private final Charset encoding;
    private final boolean isContext;
    private BufferedReader reader;
    private final StringBuilder diffContent;
    
    /**
     * Create a DirectoryDiffSource that recursively compares two directories.
     * 
     * @param leftDir original directory
     * @param rightDir revised directory
     * @param encoding character encoding
     * @param isContext true for context diff, false for unified diff
     */
    public DirectoryDiffSource(File leftDir, File rightDir, Charset encoding, boolean isContext) {
        this.leftDir = leftDir;
        this.rightDir = rightDir;
        this.encoding = encoding;
        this.isContext = isContext;
        this.diffContent = new StringBuilder();
    }
    
    @Override
    public BufferedReader getDiffReader() throws IOException {
        if (reader == null) {
            generateDirectoryDiff();
            reader = new BufferedReader(new StringReader(diffContent.toString()));
        }
        return reader;
    }
    
    @Override
    public String getLeftFileName() {
        return leftDir.getPath();
    }
    
    @Override
    public String getRightFileName() {
        return rightDir.getPath();
    }
    
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
    
    /**
     * Generate diff for entire directory tree.
     */
    private void generateDirectoryDiff() throws IOException {
        // Collect all files from both directories
        Map<String, Path> leftFiles = collectFiles(leftDir.toPath());
        Map<String, Path> rightFiles = collectFiles(rightDir.toPath());
        
        // Process each file in sorted order
        Set<String> allPaths = new TreeSet<>();
        allPaths.addAll(leftFiles.keySet());
        allPaths.addAll(rightFiles.keySet());
        
        for (String relativePath : allPaths) {
            if (leftFiles.containsKey(relativePath) && rightFiles.containsKey(relativePath)) {
                // Both files exist - generate diff
                generateFileDiff(
                    leftFiles.get(relativePath).toFile(),
                    rightFiles.get(relativePath).toFile(),
                    relativePath
                );
            } else if (rightFiles.containsKey(relativePath)) {
                // New file - generate synthetic diff (all lines added)
                generateNewFileDiff(rightFiles.get(relativePath).toFile(), relativePath);
            } else {
                // Deleted file - generate synthetic diff (all lines deleted)
                generateDeletedFileDiff(leftFiles.get(relativePath).toFile(), relativePath);
            }
        }
    }
    
    /**
     * Generate diff for a single file pair using DirectFileDiffSource.
     */
    private void generateFileDiff(File leftFile, File rightFile, String relativePath) throws IOException {
        List<String> leftLines = Files.readAllLines(leftFile.toPath(), encoding);
        List<String> rightLines = Files.readAllLines(rightFile.toPath(), encoding);
        
        // Skip if files are identical
        if (leftLines.equals(rightLines)) {
            return;
        }
        
        // Create DirectFileDiffSource to handle the comparison
        DirectFileDiffSource fileDiffSource = new DirectFileDiffSource(
            leftFile, rightFile, encoding, isContext);
        
        try (BufferedReader diffReader = fileDiffSource.getDiffReader()) {
            String line;
            while ((line = diffReader.readLine()) != null) {
                diffContent.append(line).append("\n");
            }
        }
    }
    
    /**
     * Generate synthetic diff for a new file (all lines marked as added).
     */
    private void generateNewFileDiff(File newFile, String relativePath) throws IOException {
        List<String> lines = Files.readAllLines(newFile.toPath(), encoding);
        
        diffContent.append("--- /dev/null\n");
        diffContent.append("+++ b/").append(normalizeFileName(relativePath)).append("\n");
        diffContent.append("@@ -0,0 +1,").append(lines.size()).append(" @@\n");
        
        for (String line : lines) {
            diffContent.append("+").append(line).append("\n");
        }
        diffContent.append("\n");
    }
    
    /**
     * Generate synthetic diff for a deleted file (all lines marked as removed).
     */
    private void generateDeletedFileDiff(File deletedFile, String relativePath) throws IOException {
        List<String> lines = Files.readAllLines(deletedFile.toPath(), encoding);
        
        diffContent.append("--- a/").append(normalizeFileName(relativePath)).append("\n");
        diffContent.append("+++ /dev/null\n");
        diffContent.append("@@ -1,").append(lines.size()).append(" +0,0 @@\n");
        
        for (String line : lines) {
            diffContent.append("-").append(line).append("\n");
        }
        diffContent.append("\n");
    }
    
    /**
     * Recursively collect all files in a directory as a map of (relative path -> absolute path).
     */
    private Map<String, Path> collectFiles(Path dirPath) throws IOException {
        Map<String, Path> files = new TreeMap<>();
        
        if (!Files.isDirectory(dirPath)) {
            return files;
        }
        
        try (Stream<Path> stream = Files.walk(dirPath)) {
            stream
                .filter(Files::isRegularFile)
                .filter(p -> !isBinaryFile(p))  // Skip binary files
                .forEach(p -> {
                    Path relativePath = dirPath.relativize(p);
                    files.put(relativePath.toString().replace("\\", "/"), p);
                });
        }
        
        return files;
    }
    
    /**
     * Check if a file is binary (simple heuristic: check for null bytes).
     */
    private boolean isBinaryFile(Path filePath) {
        try {
            byte[] buffer = new byte[512];
            try (var input = Files.newInputStream(filePath)) {
                int bytesRead = input.read(buffer);
                for (int i = 0; i < bytesRead; i++) {
                    if (buffer[i] == 0) {
                        return true;  // Null byte detected - likely binary
                    }
                }
            }
        } catch (IOException e) {
            // Assume binary if we can't read
            return true;
        }
        return false;
    }
    
    /**
     * Normalize file path (backslash to forward slash).
     */
    private String normalizeFileName(String path) {
        return path.replace("\\", "/");
    }
}
