<h1 align="center">JsonCSVBridge</h1>
<p align="center">
  <a href="https://github.com/hyunolike/json-csv-bridge">
    <img src="https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fhyunolike%2Fjson-csv-bridge&count_bg=%23C83D3D&title_bg=%23555555&icon=github.svg&icon_color=%23E7E7E7&title=hits&edge_flat=false" height="18"/>
  </a>
  <a href="https://github.com/hyunolike/json-csv-bridge">
    <img src="https://jitpack.io/v/hyunolike/json-csv-bridge.svg" alt="jitpack" height="18">      
  </a>
  <a href="https://github.com/hyunolike/json-csv-bridge">
    <img src="https://img.shields.io/badge/license-MIT-blue" alt="MIT json-csv-bridge" height="18">
  </a>
  <br />
  <a href="https://github.com/hyunolike/json-csv-bridge">
    <img src="https://img.shields.io/badge/%EC%9D%B4%EB%84%88%EC%84%9C%ED%81%B4_1%EA%B8%B0-%EB%B0%B1%EC%97%94%ED%8A%B8%ED%8C%8C%ED%8A%B8_%EC%9E%A5%ED%98%84%ED%98%B8-ffcc8b" alt="Inner Circle 1期 バックエンドパート チャン・ヒョンホ" height="23">
  </a>
</p>

<p align="center">
  <a href="../README.md">English</a> ·
  <a href="README.ko.md">한국어</a> ·
  <a href="README.zh.md">简体中文</a> ·
  <b>日本語</b>
</p>

> Inner Circle BE 1期 オープンソースライブラリプロジェクト <br />
> バックエンドパート チャン・ヒョンホ (Hyunho Jang)

JSON を CSV ファイルに変換するライブラリです。

### 🧑🏼‍🎨 オープンソース活用のベストプラクティス図
<img width="1044" alt="image" src="https://github.com/user-attachments/assets/f49bbe60-8b2d-4a31-bacb-5b48bb87ec99">

### 🧑🏼‍🌾 開発日誌
- [[設計/最終版] クラス図](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-05.-%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%B5%9C%EC%A2%85%EB%B3%B8)（韓国語）
- [[設計] クラス図](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-03.-%08%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%84%A4%EA%B3%84)（韓国語）

---
## Features (7)
- 基本。JSON から CSV への変換 🚀 `実装済み`
  - JSON 配列と単一の JSON オブジェクトの両方を受け取ります。CSV の列順は入力 JSON のキー順をそのまま保ちます。
- CSV フィールドのカスタムマッピング ⚠️ `未実装`
  - JSON のフィールドを CSV の列に手動で対応づけられるようにします。たとえば特定の JSON フィールドを特定の列へ出力する、といった指定です。
- データ検証とクレンジング 🚀 `実装済み`
  - 変換前に JSON 形式を検証し、ネストしたオブジェクト・配列・`null` を CSV に書ける形へ整えます。形式が不正な場合は、どこが問題かを示す `IllegalArgumentException` を投げます。
- CSV フォーマット設定 🚀 `実装済み`
  - `FormattingOptions` で区切り文字、エンコーディング、改行コード、空値の表記、Excel 用の BOM を指定できます。
- データのフィルタリングと選択的変換 🚀 `実装済み`
  - `FilterCriteria` で条件を満たすレコードだけを変換します。（特定のフィールドだけを出力する列単位のフィルタリングは未対応です。）
- マージ 🚀 `実装済み`
  - 複数の JSON ドキュメントを 1 つの CSV ファイルにまとめます。ヘッダーは全入力に現れたキーの和集合です。
- 変換後の後処理 ⚠️ `未実装`
  - 生成した CSV ファイルに対する追加処理（列の削除・追加・並べ替えなど）を提供します。

## Dependencies
依存関係:
- Java 21
- Kotlin 1.9

ランタイム依存は Jackson と SLF4J API だけです。**Spring を使わないプロジェクトでもそのまま利用できます。**

## Include in your project
```kotlin
//Add it in your root build.gradle at the end of repositories:
repositories {
  mavenCentral()
  maven { url 'https://jitpack.io' }
}

//Add the dependency
dependencies {
        implementation 'com.github.hyunolike:json-csv-bridge:Tag'
        //implementation("com.github.hyunolike:json-csv-bridge:v2.0.0")
}
```

## Usage
### サンプルプロジェクト ![](https://img.shields.io/badge/spring_boot-6DB33F?style=flat&logo=springboot&logoColor=white)
- 🚀[kotlin + spring boot](https://github.com/hyunolike/json-csv-bridge/blob/develop/examples/spring-boot-kotlin/src/main/kotlin/com/example/SpringBootKotlinApplication.kt)
- 🚀[java + spring boot](https://github.com/hyunolike/json-csv-bridge/blob/develop/examples/spring-boot-java/src/main/java/com/example/springbootjava/SpringBootJavaApplication.java)

```kotlin
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateCsv
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateMergeCsv
import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.FilterCriteria
import com.jsoncsvbridge.json.JsonToCsvCreator
```

### 1️⃣ 基本。JSON から CSV への変換
```kotlin
// 機能1. JSON 文字列を CSV ファイルに変換する
val jsonCreator = generateCsv("json")

val jsonInput = """
    [
        {"name": "Hyunho", "age": 30, "city": "New York"},
        {"name": "Bob", "age": 25, "city": "Los Angeles"},
        {"name": "Charlie", "age": 35, "city": "Chicago"}
    ]
"""

jsonCreator.createCsv(jsonInput, "csv_output/output_json.csv")
```
```csv
name,age,city
Hyunho,30,New York
Bob,25,Los Angeles
Charlie,35,Chicago
```
出力パスの親ディレクトリは自動的に作成されます。

### 2️⃣ マージ
```kotlin
// 機能2. 2 つの JSON 文字列を 1 つの CSV ファイルにまとめる
val mergeJsonCreator = generateMergeCsv()

val data1 = """
    [
        {"name": "John", "age": 30, "city": "New York", "country": "USA"}
    ]
"""
val data2 = """
    [
        {"name": "Alice", "age": 25, "city": "London", "occupation": "Engineer"},
        {"name": "Bob", "age": 35, "city": "Paris", "hobby": "Photography"}
    ]
"""

mergeJsonCreator.createMergedCsv(data1, data2, "csv_output/output_merge_json.csv")

// 3 つ以上のドキュメントも一度にマージできます
mergeJsonCreator.createMergedCsv(listOf(data1, data2, data3), "csv_output/output_merge_all.csv")
```
```csv
name,age,city,country,occupation,hobby
John,30,New York,USA,,
Alice,25,London,,Engineer,
Bob,35,Paris,,,Photography
```

### 3️⃣ CSV フォーマット設定
```kotlin
val options = FormattingOptions(
    delimiter = ';',          // 区切り文字
    encoding = "UTF-8",       // エンコーディング
    lineTerminator = "\r\n",  // 改行コード
    nullValue = "N/A",        // 空値の表記（デフォルト: 空セル）
    byteOrderMark = true,     // Excel で文字化けする場合は true
)

generateCsv("json", options).createCsv(jsonInput, "csv_output/output_formatted.csv")
```

### 4️⃣ データのフィルタリング
```kotlin
val criteria = FilterCriteria.of(Condition("city", "Chicago"))

val creator = generateCsv("json") as JsonToCsvCreator
creator.createCsv(jsonInput, "csv_output/output_filtered.csv", criteria)
```
条件が複数ある場合はすべてを満たす（AND）レコードだけが残り、条件がなければすべて変換されます。

## Migrating to v2.0.0
v1.x から上げるときに変わる挙動です。

| 項目 | v1.0.0 | v2.0.0 |
| --- | --- | --- |
| CSV の列順 | ハッシュ順のため入力と異なる | 入力 JSON のキー順を保つ |
| 空値の表記 | 文字列 `NULL` | 空セル（`FormattingOptions.nullValue` で変更可） |
| ネストしたオブジェクト／配列 | `{c=Seoul}` のようなオブジェクト文字列 | JSON 文字列（`{"c":"Seoul"}`） |
| ヘッダーのエスケープ | なし（キーに `,` があると列がずれる） | RFC 4180 に従って引用 |
| 単一 JSON オブジェクトの入力 | 例外 | レコード 1 件として変換 |
| 空の入力 | ファイルを作らない | 空のファイルを作成 |
| `MergeCsvCreator.createCsv` | `UnsupportedOperationException` | 入力 1 件のマージとして動作 |
| Spring 依存 | `spring-boot-starter-web` が推移的に付く | なし（コンパイル時のみ） |
| ログ | `println` | SLF4J（`debug` レベル） |

`JsonToCsvCreator` のコンストラクタから、使われていなかった `CsvFormatter` パラメータを削除しました。変換器を直接生成せず `generateCsv` / `generateMergeCsv` で作っている場合は影響ありません。

## Contributing
```bash
./gradlew ktlintCheck test   # PR を出す前に実行（CI でも同じ検査を行います）
```

## Learn more about JsonCSVBridge
### Get Involved
- 🚀[Contributing Guide](https://github.com/hyunolike/json-csv-bridge/discussions/33)
