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
    <img src="https://img.shields.io/badge/%EC%9D%B4%EB%84%88%EC%84%9C%ED%81%B4_1%EA%B8%B0-%EB%B0%B1%EC%97%94%ED%8A%B8%ED%8C%8C%ED%8A%B8_%EC%9E%A5%ED%98%84%ED%98%B8-ffcc8b" alt="Inner Circle 第一期 后端组 张贤浩" height="23">
  </a>
</p>

<p align="center">
  <a href="../README.md">English</a> ·
  <a href="README.ko.md">한국어</a> ·
  <b>简体中文</b> ·
  <a href="README.ja.md">日本語</a>
</p>

> Inner Circle BE 第一期 —— 开源库项目 <br />
> 后端组 张贤浩 (Hyunho Jang)

一个把 JSON 转换成 CSV 文件的库。

### 🧑🏼‍🎨 开源使用最佳实践流程图
<img width="1044" alt="image" src="https://github.com/user-attachments/assets/f49bbe60-8b2d-4a31-bacb-5b48bb87ec99">

### 🧑🏼‍🌾 开发日志
- [[设计/最终版] 类图](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-05.-%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%B5%9C%EC%A2%85%EB%B3%B8)（韩语）
- [[设计] 类图](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-03.-%08%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%84%A4%EA%B3%84)（韩语）

---
## Features (8)
- 基础。JSON 转 CSV 🚀 `已完成`
  - 同时支持 JSON 数组和单个 JSON 对象。CSV 的列顺序与输入 JSON 的键顺序一致。
- 自定义 CSV 字段映射 ⚠️ `未完成`
  - 支持手动把 JSON 字段映射到 CSV 的列，例如把某个 JSON 字段指定输出到某一列。
- 数据校验与清洗 🚀 `已完成`
  - 转换前校验 JSON 格式，并把嵌套对象、数组和 `null` 整理成 CSV 可以承载的形式。格式有误时抛出 `IllegalArgumentException`，并指出出错的位置。
- CSV 格式设置 🚀 `已完成`
  - 通过 `FormattingOptions` 指定分隔符、编码、行结束符、空值表示、给 Excel 用的 BOM，以及是否把嵌套值展开成列。
- 数据过滤与选择性转换 🚀 `已完成`
  - 通过 `FilterCriteria` 只转换满足条件的记录。（按列过滤，也就是只导出部分字段，目前还不支持。）
- 合并 🚀 `已完成`
  - 把多个 JSON 文档合并成一个 CSV 文件。表头是所有输入中出现过的键的并集。
- 转换后的后处理 ⚠️ `未完成`
  - 对生成的 CSV 文件做进一步处理，例如删除列、新增列或调整列顺序。
- CSV 转 JSON 🚀 `已完成`
  - 反方向转换。把 CSV 读回 JSON，并还原数字、布尔值、`null` 和嵌套结构。

## Dependencies
依赖：
- Java 21
- Kotlin 1.9

运行时依赖只有 Jackson 和 SLF4J API。**在不使用 Spring 的项目中也可以直接使用。**

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
        //implementation("com.github.hyunolike:json-csv-bridge:v2.1.0")
}
```

## Usage
### 示例项目 ![](https://img.shields.io/badge/spring_boot-6DB33F?style=flat&logo=springboot&logoColor=white)
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

### 1️⃣ 基础。JSON 转 CSV
```kotlin
// 功能1. 把 JSON 字符串转换成 CSV 文件
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
输出路径的上级目录会自动创建。

### 2️⃣ 合并
```kotlin
// 功能2. 把两个 JSON 字符串合并成一个 CSV 文件
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

// 三个以上的文档也可以一次合并
mergeJsonCreator.createMergedCsv(listOf(data1, data2, data3), "csv_output/output_merge_all.csv")
```
```csv
name,age,city,country,occupation,hobby
John,30,New York,USA,,
Alice,25,London,,Engineer,
Bob,35,Paris,,,Photography
```

### 3️⃣ CSV 格式设置
```kotlin
val options = FormattingOptions(
    delimiter = ';',          // 分隔符
    encoding = "UTF-8",       // 编码
    lineTerminator = "\r\n",  // 行结束符
    nullValue = "N/A",        // 空值的表示（默认：空单元格）
    byteOrderMark = true,     // Excel 中出现乱码时设为 true
    flatten = FlattenMode.BRACKET, // 把嵌套值展开成列
)

generateCsv("json", options).createCsv(jsonInput, "csv_output/output_formatted.csv")
```

### 4️⃣ 数据过滤
```kotlin
val criteria = FilterCriteria.of(Condition("city", "Chicago"))

val creator = generateCsv("json") as JsonToCsvCreator
creator.createCsv(jsonInput, "csv_output/output_filtered.csv", criteria)
```
有多个条件时只保留全部满足（AND）的记录；没有条件时则全部转换。

### 5️⃣ 展开嵌套值
```kotlin
val json = """[{"id": 1, "addr": {"city": "Seoul"}, "tags": ["a", "b"]}]"""

generateCsv("json", FormattingOptions(flatten = FlattenMode.BRACKET)).convertToString(json)
```
```csv
id,addr.city,tags[0],tags[1]
1,Seoul,a,b
```
`FlattenMode.DOT` 会用点连接数组下标（`tags.0`）。默认的 `NONE` 则把嵌套值作为 JSON 字符串放在一个单元格里。

### 6️⃣ 用字符串和流代替文件
每种转换都可以输出到 `String`、`Writer`、`OutputStream` 或文件，也可以从 `String` 和 `Reader` 读取。传入的句柄不会被关闭，其生命周期仍由调用方掌握。

```kotlin
// 直接拿到字符串
val csv: String = generateCsv("json").convertToString(jsonInput)

// 不经临时文件，直接写入 HTTP 响应
generateCsv("json").createCsv(jsonInput, response.outputStream)

// 读取大文件而不把它整个放进字符串
File("big.json").reader().use { generateCsv("json").createCsv(it, "out.csv") }
```

### 7️⃣ CSV 转 JSON
```kotlin
val converter = generateJson()

converter.toJsonString("name,age\nJohn,30")   // [{"name":"John","age":30}]
converter.toRecords(csv)                       // List<Map<String, Any?>>
converter.convertFile("in.csv", "out.json")
```
数字、布尔值、`null` 和嵌套 JSON 单元格都会还原成原来的类型。像 `007`、`+1`、`010-1234-5678` 这类转成数字后含义会变的值仍保持字符串。若想让所有单元格都是字符串，使用 `CsvToJsonCreator(typeInference = false)`。

注意 CSV 是矩形的，无法区分"没有这个键"和"值为 null"。键集合不同的记录读回后会带上所有列的并集；如果原始结构更重要，可以用 `CsvToJsonCreator(includeNullFields = false)` 去掉空的键，但原本存在的 `null` 值也会一并消失。

## What's new in v2.1.0
只有新增，v2.0.0 的行为没有变化。

- 除已有的文件路径外，还可以输出到 `String`、`Writer`、`OutputStream`，并从 `Reader` 读取。
- 新增反方向的 `CsvToJsonConverter` / `generateJson()`。
- `FormattingOptions.flatten` 可以把嵌套对象和数组展开成列。

## Migrating to v2.0.0
从 v1.x 升级时发生变化的行为。

| 项目 | v1.0.0 | v2.0.0 |
| --- | --- | --- |
| CSV 列顺序 | 哈希顺序，与输入不一致 | 与输入 JSON 的键顺序一致 |
| 空值表示 | 字符串 `NULL` | 空单元格（可用 `FormattingOptions.nullValue` 修改） |
| 嵌套对象／数组 | `{c=Seoul}` 这样的对象字符串 | JSON 字符串（`{"c":"Seoul"}`） |
| 表头转义 | 没有（键里有 `,` 时列会错位） | 按 RFC 4180 加引号 |
| 单个 JSON 对象作为输入 | 抛异常 | 作为一条记录转换 |
| 空输入 | 不生成文件 | 生成空文件 |
| `MergeCsvCreator.createCsv` | `UnsupportedOperationException` | 按只有一个输入的合并处理 |
| Spring 依赖 | 传递依赖 `spring-boot-starter-web` | 无（仅编译期使用） |
| 日志 | `println` | SLF4J（`debug` 级别） |

`JsonToCsvCreator` 构造函数中未被使用的 `CsvFormatter` 参数已被移除。如果你是通过 `generateCsv` / `generateMergeCsv` 创建转换器而不是直接构造，那么不受影响。

## Contributing
```bash
./gradlew ktlintCheck test   # 提 PR 前执行（CI 会做同样的检查）
```

## Learn more about JsonCSVBridge
### Get Involved
- 🚀[Contributing Guide](https://github.com/hyunolike/json-csv-bridge/discussions/33)
