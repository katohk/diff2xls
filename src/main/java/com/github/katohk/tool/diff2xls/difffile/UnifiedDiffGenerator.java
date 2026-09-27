package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates unified diff format output from LCS edit operations.
 * Unified diff shows context lines around changes.
 * 
 * @author katohk
 */
public class UnifiedDiffGenerator {
    
    private static final int CONTEXT_LINES = 3;
    
    /**
     * Generate unified diff from two file contents.
     * 
     * @param original original file lines
     * @param revised revised file lines
     * @param originalName name for original file header
     * @param revisedName name for revised file header
     * @return BufferedReader with unified diff output
     */
    public static BufferedReader generateDiff(List<String> original, List<String> revised,
                                              String originalName, String revisedName) {
        StringBuilder result = new StringBuilder();
        
        // Add file headers
        result.append("--- ").append(originalName).append("\n");
        result.append("+++ ").append(revisedName).append("\n");
        
        // Compute edits using LCS algorithm
        DiffAlgorithm algorithm = new DiffAlgorithm(original, revised);
        List<DiffAlgorithm.Edit> edits = algorithm.computeLCS();
        
        // Group edits into hunks
        List<Hunk> hunks = groupIntoHunks(edits, original, revised);
        
        // Output hunks
        for (Hunk hunk : hunks) {
            result.append(hunk.toUnifiedString());
        }
        
        return new BufferedReader(new StringReader(result.toString()));
    }
    
    /**
     * Group edits into hunks with context lines.
     */
    private static List<Hunk> groupIntoHunks(List<DiffAlgorithm.Edit> edits,
                                             List<String> original,
                                             List<String> revised) {
        List<Hunk> hunks = new ArrayList<>();
        Hunk currentHunk = null;
        
        int origIdx = 0, revIdx = 0;
        
        for (DiffAlgorithm.Edit edit : edits) {
            if (edit.type == DiffAlgorithm.Edit.Type.EQUAL) {
                // Context line
                if (currentHunk != null) {
                    currentHunk.addContext(edit.line);
                    if (currentHunk.contextLinesAtEnd >= CONTEXT_LINES &&
                        currentHunk.hasChanges()) {
                        hunks.add(currentHunk);
                        currentHunk = null;
                    }
                }
                origIdx++;
                revIdx++;
            } else {
                // Change found
                if (currentHunk == null) {
                    currentHunk = new Hunk(origIdx, revIdx);
                    // Add leading context
                    for (int i = Math.max(0, origIdx - CONTEXT_LINES); i < origIdx; i++) {
                        currentHunk.addContextStart(original.get(i));
                    }
                    origIdx = currentHunk.origStartIdx + currentHunk.origLines.size();
                }
                
                if (edit.type == DiffAlgorithm.Edit.Type.DELETE) {
                    currentHunk.addDelete(edit.line);
                    origIdx++;
                } else {
                    currentHunk.addAdd(edit.line);
                    revIdx++;
                }
                currentHunk.contextLinesAtEnd = 0;
            }
        }
        
        // Add trailing context to last hunk
        if (currentHunk != null && currentHunk.hasChanges()) {
            hunks.add(currentHunk);
        }
        
        return hunks;
    }
    
    /**
     * Represents a single hunk in unified diff.
     */
    private static class Hunk {
        int origStartIdx;
        int revStartIdx;
        List<String> origLines = new ArrayList<>();
        List<String> revLines = new ArrayList<>();
        int contextLinesAtEnd = 0;
        
        Hunk(int origStartIdx, int revStartIdx) {
            this.origStartIdx = origStartIdx;
            this.revStartIdx = revStartIdx;
        }
        
        void addContextStart(String line) {
            origLines.add(" " + line);
            revLines.add(" " + line);
        }
        
        void addContext(String line) {
            origLines.add(" " + line);
            revLines.add(" " + line);
            contextLinesAtEnd++;
        }
        
        void addDelete(String line) {
            origLines.add("-" + line);
            contextLinesAtEnd = 0;
        }
        
        void addAdd(String line) {
            revLines.add("+" + line);
            contextLinesAtEnd = 0;
        }
        
        boolean hasChanges() {
            return origLines.stream().anyMatch(l -> l.startsWith("-")) ||
                   revLines.stream().anyMatch(l -> l.startsWith("+"));
        }
        
        String toUnifiedString() {
            // Remove trailing context beyond threshold
            while (origLines.size() > 0 && origLines.get(origLines.size() - 1).startsWith(" ") &&
                   contextLinesAtEnd > CONTEXT_LINES) {
                origLines.remove(origLines.size() - 1);
                revLines.remove(revLines.size() - 1);
                contextLinesAtEnd--;
            }
            
            int origLineCount = (int) origLines.stream().filter(l -> !l.startsWith("+")).count();
            int revLineCount = (int) revLines.stream().filter(l -> !l.startsWith("-")).count();
            
            StringBuilder hunk = new StringBuilder();
            hunk.append("@@ -").append(origStartIdx + 1).append(",").append(origLineCount)
                .append(" +").append(revStartIdx + 1).append(",").append(revLineCount)
                .append(" @@\n");
            
            // Use original lines which have correct deletions/additions
            for (String line : origLines) {
                if (!line.startsWith("+")) {
                    hunk.append(line).append("\n");
                }
            }
            
            // Now add the additions
            for (String line : revLines) {
                if (line.startsWith("+")) {
                    hunk.append(line).append("\n");
                }
            }
            
            return hunk.toString();
        }
    }
}
