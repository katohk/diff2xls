# Diff2xls

Diff2xlsは、diff形式のファイルをExcelファイルに変換するツールです。実行形式jarなので、java -jar コマンドで起動することができます。

## コマンドラインパラメータ

Diff2xlsを実行する際に、以下のパラメータを指定することができます。

Usage: Diff2xls template [-cu -i file.diff -o file.xlsx -e encode]


- `template`: Excelのテンプレートファイルのパスを指定します。
- `-c` : context diff フォーマットを使用。
- `-u` : unified diff フォーマットを使用。
- `-i file.diff`: Excel化したいdiffファイルのパスを指定します。
- `-o file.xlsx`: 出力するExcelファイルのパスを指定します。
- `-e encode`: diffファイルの文字コードを指定します。デフォルトはUTF-8です。

## 使用方法

以下の手順でDiff2xlsを使用することができます。

1. Diff2xlsをダウンロードして、任意のディレクトリに配置します。
2. コマンドラインから、以下のコマンドを実行します。

   java -jar Diff2xls.jar template -i file.diff -o file.xlsx -e encode

3. 出力されたExcelファイルを開いて、差分内容を確認することができます。

## テンプレート
Excelのテンプレートには以下のキーワードを設定します。

- `%top` :　先頭行のスタイル 、`#seq`がシーケンス番号、`#left`が修正前、`#right`が修正後
- `%middle` : 中間行のスタイル
- `%bottom` : 最終行のスタイル
- `%attrib` : diff のスタイルを `#!` `#-` `#+` で設定

## 注意事項

- Excelのシート名にファイル名を設定しますが、シート名の長さに制限があります。

