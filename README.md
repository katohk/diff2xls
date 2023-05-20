# Diff2xls

Diff2xls is a tool for converting diff files to Excel files. It can be executed as a jar file using the java -jar command.

## Command-line parameters
When running Diff2xls, the following parameters can be specified:

Usage: Diff2xls template [-cu -i file.diff -o file.xlsx -e encode]

- `template`: Specifies the path of the Excel template file.
- `-c` : Use context diff format. 
- `-u` : Use unified diff format.
- `-i file.diff`: Specifies the path of the diff file to be converted to Excel.
- `-o file.xlsx`: Specifies the path of the output Excel file.
- `-e encode`: Specifies the character encoding of the diff file. The default is UTF-8.

## Usage
To use Diff2xls, follow these steps:

1. Download Diff2xls and place it in any directory.
2. From the command line, execute the following command:

   java -jar Diff2xls.jar template -i file.diff -o file.xlsx -e encode

3. Open the output Excel file to review the difference contents.

## Template
The Excel template should include the following keywords:

- `%top`: Style for the top row. #seq represents sequence number, #left represents the original version, and #right represents the modified version.
- `%middle`: Style for the middle rows.
- `%bottom`: Style for the last row.
- `%attrib`: Set the style of the diff using #!, #-, #+.

## Notes
- The sheet name in the Excel file is set to the file name, but there is a limit to the length of the sheet name.
