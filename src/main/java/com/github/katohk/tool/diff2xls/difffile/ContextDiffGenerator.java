package com.github.katohk.tool.diff2xls.difffile;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

/**
 * Generates context diff format output from LCS edit operations.
 * Context diff shows surrounding context for changed lines.
 * 
 * @author katohk
 */
public class ContextDiffGenerator {
    
    private static final int CONTEXT_LINES = 3;
    
    /**
     * Generate context diff from two file contents.
     * 
     * @param original original file lines
     * @param revised revised file lines
     * @param originalName name for original file header
     * @param revisedName name for revised file header
     * @return BufferedReader with context diff output
     */
    public static BufferedReader generateDiff(List<String> original, List<String> revised,
                                              String originalName, String revisedName) {
        StringBuilder result = new StringBuilder();
        
        // Add file headers
        result.append("*** ").append(originalName).append("\n");
        result.append("--- ").append(revisedName).append("\n");
        
        // Compute edits using LCS algorithm
        DiffAlgorithm algorithm = new DiffAlgorithm(original, revised);
        List<DiffAlgorithm.Edit> edits = algorithm.computeLCS();
        
        // Output context format
        outputContextFormat(result, edits, original, revised);
        
        return new BufferedReader(new StringReader(result.toString()));
    }
    
    /**
     * Output diff in context format.
     */
    private static void outputContextFormat(StringBuilder result,
                                           List<DiffAlgorithm.Edit> edits,
                                           List<String> original,
                                           List<String> revised) {
        // Track positions for context
        int origPos = 0, revPos = 0;
        int origChangeStart = -1, revChangeStart = -1;
        
        // Scan for change regions
        for (int i = 0; i < edits.size(); i++) {
            DiffAlgorithm.Edit edit = edits.get(i);
            
            if (edit.type != DiffAlgorithm.Edit.Type.EQUAL) {
                if (origChangeStart == -1) {
                    // Start of change region
                    origChangeStart = Math.max(0, origPos - CONTEXT_LINES);
                    revChangeStart = Math.max(0, revPos - CONTEXT_LINES);
                }
            } else {
                if (origChangeStart != -1) {
                    // End of change region - output it
                    outputContextSection(result, original, revised, 
                            origChangeStart, origPos, revChangeStart, revPos, edits, i);
                    origChangeStart = -1;
                    revChangeStart = -1;
                }
                origPos++;
                revPos++;
            }
        }
        
        // Output final change region if any
        if (origChangeStart != -1) {
            outputContextSection(result, original, revised,
                    origChangeStart, origPos, revChangeStart, revPos, edits, edits.size());
        }
    }
    
    /**
     * Output a single context diff section.
     */
    private static void outputContextSection(StringBuilder result, List<String> original,
                                            List<String> revised, int origStart, int origEnd,
                                            int revStart, int revEnd,
                                            List<DiffAlgorithm.Edit> edits, int endIdx) {
        // Context format: simple comparison of two regions
        result.append("***************\n");
        result.append("*** ").append(origStart + 1).append(",").append(origEnd).append(" ****\n");
        
        for (int i = origStart; i < origEnd && i < original.size(); i++) {
            result.append("  ").append(original.get(i)).append("\n");
        }
        
        result.append("--- ").append(revStart + 1).append(",").append(revEnd).append(" ----\n");
        
        for (int i = revStart; i < revEnd && i < revised.size(); i++) {
            result.append("  ").append(revised.get(i)).append("\n");
        }
    }
}
