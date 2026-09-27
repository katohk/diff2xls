package com.github.katohk.tool.diff2xls.difffile;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DirectFileAndDirectoryDiffSourceTest {

    @Test
    void directFileComparisonGeneratesUnifiedDiff() throws IOException {
        Path tempDir = Files.createTempDirectory("diff2xls-file");
        Path left = tempDir.resolve("left.txt");
        Path right = tempDir.resolve("right.txt");

        Files.writeString(left, "alpha\nbeta\n", StandardCharsets.UTF_8);
        Files.writeString(right, "alpha\ngamma\n", StandardCharsets.UTF_8);

        DirectFileDiffSource diffSource = new DirectFileDiffSource(
                left.toFile(), right.toFile(), StandardCharsets.UTF_8, false);

        String output = readAll(diffSource);

        assertTrue(output.contains("--- " + left.toString().replace("\\", "/")));
        assertTrue(output.contains("+++ " + right.toString().replace("\\", "/")));
        assertTrue(output.contains("-beta"));
        assertTrue(output.contains("+gamma"));
    }

    @Test
    void directoryComparisonIncludesAddedAndDeletedFiles() throws IOException {
        Path tempDir = Files.createTempDirectory("diff2xls-dir");
        Path leftDir = tempDir.resolve("left");
        Path rightDir = tempDir.resolve("right");
        Files.createDirectories(leftDir);
        Files.createDirectories(rightDir);

        Files.writeString(leftDir.resolve("keep.txt"), "same\n", StandardCharsets.UTF_8);
        Files.writeString(leftDir.resolve("delete.txt"), "remove me\n", StandardCharsets.UTF_8);
        Files.writeString(rightDir.resolve("keep.txt"), "same\n", StandardCharsets.UTF_8);
        Files.writeString(rightDir.resolve("add.txt"), "new file\n", StandardCharsets.UTF_8);

        DirectoryDiffSource diffSource = new DirectoryDiffSource(
                leftDir.toFile(), rightDir.toFile(), StandardCharsets.UTF_8, false);

        String output = readAll(diffSource);

        assertTrue(output.contains("keep.txt") || output.contains("add.txt") || output.contains("delete.txt"));
        assertTrue(output.contains("add.txt"));
        assertTrue(output.contains("delete.txt"));
    }

    private String readAll(DiffSource diffSource) throws IOException {
        try (var reader = diffSource.getDiffReader()) {
            List<String> lines = reader.lines().toList();
            return String.join(System.lineSeparator(), lines);
        }
    }
}
