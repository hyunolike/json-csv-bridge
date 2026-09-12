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
    <img src="https://img.shields.io/badge/%EC%9D%B4%EB%84%88%EC%84%9C%ED%81%B4_1%EA%B8%B0-%EB%B0%B1%EC%97%94%ED%8A%B8%ED%8C%8C%ED%8A%B8_%EC%9E%A5%ED%98%84%ED%98%B8-ffcc8b" alt="Inner Circle 1st, Backend Part, Hyunho Jang" height="23">
  </a>
</p>

<p align="center">
  <b>English</b> ·
  <a href="docs/README.ko.md">한국어</a> ·
  <a href="docs/README.zh.md">简体中文</a> ·
  <a href="docs/README.ja.md">日本語</a>
</p>

> Inner Circle BE 1st — open source library project <br />
> Backend Part, Hyunho Jang

A library that turns JSON into CSV files.

### 🧑🏼‍🎨 Open source best-practice flow
<img width="1044" alt="image" src="https://github.com/user-attachments/assets/f49bbe60-8b2d-4a31-bacb-5b48bb87ec99">

### 🧑🏼‍🌾 Development log
- [[Design / Final] Class diagram](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-05.-%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%B5%9C%EC%A2%85%EB%B3%B8) (written in Korean)
- [[Design] Class diagram](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-03.-%08%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%84%A4%EA%B3%84) (written in Korean)

---
## Features (8)
- Basic. JSON to CSV conversion 🚀 `done`
  - Accepts both a JSON array and a single JSON object. Column order follows the key order of the input JSON.
- Custom CSV field mapping ⚠️ `not implemented`
  - Lets you map JSON fields to CSV columns by hand — for example, sending a given JSON field to a given CSV column.
- Data validation and cleansing 🚀 `done`
  - Validates the JSON before converting and normalises nested objects, arrays and `null` into something CSV can hold. Malformed input raises an `IllegalArgumentException` that points at the offending position.
- CSV format options 🚀 `done`
  - `FormattingOptions` controls the delimiter, encoding, line terminator, empty-value token, the BOM for Excel, and whether nested values are flattened into columns.
- Filtering and selective conversion 🚀 `done`
  - `FilterCriteria` keeps only the records that match your conditions. (Column-level filtering — exporting only certain fields — is not supported yet.)
- Merging 🚀 `done`
  - Merges several JSON documents into a single CSV file. The header is the union of every key seen across the inputs.
- Post-processing after conversion ⚠️ `not implemented`
  - Additional work on the produced CSV file, such as dropping, adding or reordering columns.
- CSV to JSON 🚀 `done`
  - The reverse direction. Reads a CSV back into JSON, restoring numbers, booleans, `null` and nested values.

## Dependencies
Depends on:
- Java 21
- Kotlin 1.9

The only runtime dependencies are Jackson and the SLF4J API. **The library works in projects that do not use Spring.**

## Include in your project
Published on Maven Central (and still on JitPack).

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  // the library on its own
  implementation("io.github.hyunolike:json-csv-bridge:2.2.0")

  // or, in a Spring Boot application, the starter (it brings the library with it)
  implementation("io.github.hyunolike:json-csv-bridge-spring-boot-starter:2.2.0")
}
```

<details>
<summary>Using JitPack instead</summary>

```kotlin
repositories {
  mavenCentral()
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.hyunolike:json-csv-bridge:v2.2.0'
  implementation 'com.github.hyunolike.json-csv-bridge:json-csv-bridge-spring-boot-starter:v2.2.0'
}
```
</details>

## Usage
### Sample projects ![](https://img.shields.io/badge/spring_boot-6DB33F?style=flat&logo=springboot&logoColor=white)
- 🚀[kotlin + spring boot](https://github.com/hyunolike/json-csv-bridge/blob/develop/examples/spring-boot-kotlin/src/main/kotlin/com/example/SpringBootKotlinApplication.kt)
- 🚀[java + spring boot](https://github.com/hyunolike/json-csv-bridge/blob/develop/examples/spring-boot-java/src/main/java/com/example/springbootjava/SpringBootJavaApplication.java)

```kotlin
import com.jsoncsvbridge.csv.FlattenMode
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateCsv
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateJson
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateMergeCsv
import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.FilterCriteria
import com.jsoncsvbridge.json.JsonToCsvCreator
```

### 1️⃣ Basic. JSON to CSV conversion
```kotlin
// Feature 1. Convert a JSON string into a CSV file
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
Parent directories of the output path are created for you.

### 2️⃣ Merging
```kotlin
// Feature 2. Convert two JSON strings into a single CSV file
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

// Three or more documents can be merged in one call
mergeJsonCreator.createMergedCsv(listOf(data1, data2, data3), "csv_output/output_merge_all.csv")
```
```csv
name,age,city,country,occupation,hobby
John,30,New York,USA,,
Alice,25,London,,Engineer,
Bob,35,Paris,,,Photography
```

### 3️⃣ CSV format options
```kotlin
val options = FormattingOptions(
    delimiter = ';',          // field delimiter
    encoding = "UTF-8",       // file encoding
    lineTerminator = "\r\n",  // row terminator
    nullValue = "N/A",        // token for empty values (default: an empty cell)
    byteOrderMark = true,     // set true when Excel garbles non-ASCII text
    flatten = FlattenMode.BRACKET, // spread nested values into columns
)

generateCsv("json", options).createCsv(jsonInput, "csv_output/output_formatted.csv")
```

### 4️⃣ Filtering
```kotlin
val criteria = FilterCriteria.of(Condition("city", "Chicago"))

val creator = generateCsv("json") as JsonToCsvCreator
creator.createCsv(jsonInput, "csv_output/output_filtered.csv", criteria)
```
With several conditions only the records matching all of them (AND) survive; with no conditions everything is converted.

### 5️⃣ Flattening nested values
```kotlin
val json = """[{"id": 1, "addr": {"city": "Seoul"}, "tags": ["a", "b"]}]"""

generateCsv("json", FormattingOptions(flatten = FlattenMode.BRACKET)).convertToString(json)
```
```csv
id,addr.city,tags[0],tags[1]
1,Seoul,a,b
```
`FlattenMode.DOT` joins array indices with a dot (`tags.0`) instead. With the default `NONE` a nested value stays as a JSON string in a single cell.

### 6️⃣ Strings and streams instead of files
Every conversion can go to a `String`, a `Writer`, an `OutputStream` or a file, and can read from a `String` or a `Reader`. Handles you pass in are never closed — their lifetime stays yours.

```kotlin
// Straight to a string
val csv: String = generateCsv("json").convertToString(jsonInput)

// Straight into an HTTP response, no temp file
generateCsv("json").createCsv(jsonInput, response.outputStream)

// Read a large file without holding it in a String
File("big.json").reader().use { generateCsv("json").createCsv(it, "out.csv") }
```

### 7️⃣ CSV to JSON
```kotlin
val converter = generateJson()

converter.toJsonString("name,age\nJohn,30")   // [{"name":"John","age":30}]
converter.toRecords(csv)                       // List<Map<String, Any?>>
converter.convertFile("in.csv", "out.json")
```
Numbers, booleans, `null` and nested JSON cells are restored to their types. Values that would change meaning as numbers — `007`, `+1`, `010-1234-5678` — stay strings. Pass `CsvToJsonCreator(typeInference = false)` to keep every cell a string.

Note that CSV is rectangular, so it cannot tell "key absent" from "value is null". Records with different key sets come back holding the union of all columns; `CsvToJsonCreator(includeNullFields = false)` drops the empty ones if the original shape matters more than keeping explicit nulls.

### 8️⃣ Spring Boot
Add the starter and the converters are auto-configured — no `@Bean` of your own required.

```kotlin
@Service
class ReportService(
    private val csvCreator: CsvCreator,          // JSON → CSV
    private val converter: CsvToJsonConverter,   // CSV → JSON
) {
    fun export(json: String): String = csvCreator.convertToString(json)
}
```

Formatting comes from `application.yml`:

```yaml
json-csv-bridge:
  delimiter: ";"
  line-terminator: "\r\n"
  null-value: "N/A"
  byte-order-mark: true
  flatten: bracket
```

`CsvCreator`, `MergeCsvCreator`, `CsvToJsonConverter`, `CsvCreatorFactory` and
`FormattingOptions` are all registered. Every one is conditional on you not defining your
own, so declaring a `FormattingOptions` bean replaces the configured one everywhere.

The core library stays free of Spring — only the starter depends on it.

## What's new in v2.2.0
- Published to **Maven Central** under `io.github.hyunolike`. JitPack keeps working under the old coordinates.
- New `json-csv-bridge-spring-boot-starter` module: auto-configuration, `application.yml` properties, and IDE completion for them.
- The core library is unchanged — no API or behaviour differences from v2.1.0.

## What's new in v2.1.0
Additions only — nothing from v2.0.0 changes behaviour.

- Output to a `String`, `Writer` or `OutputStream`, and input from a `Reader`, alongside the existing file paths.
- `CsvToJsonConverter` / `generateJson()` for the reverse direction.
- `FormattingOptions.flatten` spreads nested objects and arrays into columns.

## Migrating to v2.0.0
Behaviour that changes when you move up from v1.x.

| Topic | v1.0.0 | v2.0.0 |
| --- | --- | --- |
| CSV column order | Hash order, so it differed from the input | Follows the key order of the input JSON |
| Empty values | The string `NULL` | An empty cell (configurable via `FormattingOptions.nullValue`) |
| Nested objects / arrays | Object strings such as `{c=Seoul}` | JSON strings (`{"c":"Seoul"}`) |
| Header escaping | None — a `,` in a key broke the columns | Quoted per RFC 4180 |
| A single JSON object as input | Threw | Converted as one record |
| Empty input | No file written | An empty file is created |
| `MergeCsvCreator.createCsv` | `UnsupportedOperationException` | Works as a merge of one input |
| Spring dependency | `spring-boot-starter-web` came transitively | None (compile-time only) |
| Logging | `println` | SLF4J (`debug` level) |

The unused `CsvFormatter` parameter is gone from the `JsonToCsvCreator` constructor. If you build creators through `generateCsv` / `generateMergeCsv` rather than constructing them directly, nothing changes for you.

## Contributing
```bash
./gradlew ktlintCheck test   # run before opening a PR (CI runs the same checks)
```

## Learn more about JsonCSVBridge
### Get Involved
- 🚀[Contributing Guide](https://github.com/hyunolike/json-csv-bridge/discussions/33)
