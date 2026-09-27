# Diff2xls

Diff2xls は、diff 出力・ファイル比較・ディレクトリ比較を Excel に変換するツールです。
`java -jar` で実行できます。

## 機能

- 既存の diff ファイル (`-i file.diff`) を Excel に変換
- diff を事前生成せずに 2 つのファイルを直接比較
- 2 つのディレクトリを再帰的に比較して差分を出力
- unified diff (`-u`) と context diff (`-c`) の両方に対応

## コマンドライン使用方法

Usage:

```bash
Diff2xls template [-u|-c] [-i file.diff] [-o file.xlsx] [-e encode]
Diff2xls template [-u|-c] --file1 path1 --file2 path2 [-o file.xlsx]
Diff2xls template [-u|-c] --dir1 path1 --dir2 path2 [-o file.xlsx]
Diff2xls template [-u|-c] file1 file2 [-o file.xlsx]
```

オプション:

- `template`: Excel テンプレートのパスを指定します。
- `-u`: unified diff 形式を使用します。デフォルトです。
- `-c`: context diff 形式を使用します。
- `-i file.diff`: Excel 化する diff ファイルのパスを指定します。
- `--file1 path1`: 比較元のファイルを指定します。
- `--file2 path2`: 比較先のファイルを指定します。
- `--dir1 path1`: 比較元のディレクトリを指定します。
- `--dir2 path2`: 比較先のディレクトリを指定します。
- `-o file.xlsx`: 出力する Excel ファイルのパスを指定します。
- `-e encode`: 入力テキストの文字コードを指定します。デフォルトは `UTF-8` です。

## 使用例

### 1. diff ファイルを変換する

```bash
java -jar Diff2xls.jar template -u -i changes.diff -o result.xlsx
```

### 2. 2 つのファイルを直接比較する

```bash
java -jar Diff2xls.jar template -u --file1 before.txt --file2 after.txt -o result.xlsx
```

### 3. 2 つのディレクトリを直接比較する

```bash
java -jar Diff2xls.jar template -c --dir1 old_dir --dir2 new_dir -o result.xlsx
```

### 4. 位置引数で指定する

```bash
java -jar Diff2xls.jar template before.txt after.txt -o result.xlsx
```

## テンプレート

Excel のテンプレートには以下のキーワードを設定します。

- `%top`: 先頭行のスタイルです。`#seq` はシーケンス番号、`#left` は修正前、`#right` は修正後を表します。
- `%middle`: 中間行のスタイルです。
- `%bottom`: 最終行のスタイルです。
- `%attrib`: diff のスタイルを `#!` `#-` `#+` で設定します。

## 注意事項

- 従来の `-i file.diff` による diff ファイル入力は引き続きサポートされています。
- ファイル比較やディレクトリ比較は入力ファイルからその場で差分を生成します。
- Excel のシート名はファイル名から自動設定されますが、長さには制限があります。
- ディレクトリ比較では、新規追加・削除ファイルが合成 diff として出力され、バイナリファイルはスキップされます。

