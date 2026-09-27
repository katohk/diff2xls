# Diff2xls

Diff2xls converts diff output, file diffs, or directory diffs into Excel files using a template.
It can be launched as a JAR by running `java -jar`.

## Features

- Convert an existing diff file (`-i file.diff`) into Excel.
- Compare two files directly without first generating a diff file.
- Compare two directories recursively and export the combined file differences.
- Output in either unified diff format (`-u`) or context diff format (`-c`).

## Command-line usage

Usage:

```bash
Diff2xls template [-u|-c] [-i file.diff] [-o file.xlsx] [-e encode]
Diff2xls template [-u|-c] --file1 path1 --file2 path2 [-o file.xlsx]
Diff2xls template [-u|-c] --dir1 path1 --dir2 path2 [-o file.xlsx]
Diff2xls template [-u|-c] file1 file2 [-o file.xlsx]
```

Options:

- `template`: Path to the Excel template file.
- `-u`: Use unified diff format. This is the default behavior.
- `-c`: Use context diff format.
- `-i file.diff`: Path to a diff file to convert to Excel.
- `--file1 path1`: Original file to compare.
- `--file2 path2`: Modified file to compare.
- `--dir1 path1`: Original directory to compare.
- `--dir2 path2`: Modified directory to compare.
- `-o file.xlsx`: Output Excel file path.
- `-e encode`: Character encoding for input text. Default is `UTF-8`.

## Examples

### 1. Convert a diff file

```bash
java -jar Diff2xls.jar template -u -i changes.diff -o result.xlsx
```

### 2. Compare two files directly

```bash
java -jar Diff2xls.jar template -u --file1 before.txt --file2 after.txt -o result.xlsx
```

### 3. Compare two directories directly

```bash
java -jar Diff2xls.jar template -c --dir1 old_dir --dir2 new_dir -o result.xlsx
```

### 4. Use positional arguments

```bash
java -jar Diff2xls.jar template before.txt after.txt -o result.xlsx
```

## Template

The Excel template should include the following keywords:

- `%top`: Style for the top row. `#seq` is the sequence number, `#left` is the original content, and `#right` is the modified content.
- `%middle`: Style for the middle rows.
- `%bottom`: Style for the last row.
- `%attrib`: Style for diff markers using `#!`, `#-`, and `#+`.

## Notes

- The legacy diff-file workflow via `-i file.diff` is still supported.
- Direct file and directory comparisons are generated from the input files on the fly.
- The Excel sheet name is derived from the file name, but sheet names have a length limit.
- For directory comparisons, new or deleted files are represented as synthetic diff entries, and binary files are skipped.
