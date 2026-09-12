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
    <img src="https://img.shields.io/badge/%EC%9D%B4%EB%84%88%EC%84%9C%ED%81%B4_1%EA%B8%B0-%EB%B0%B1%EC%97%94%ED%8A%B8%ED%8C%8C%ED%8A%B8_%EC%9E%A5%ED%98%84%ED%98%B8-ffcc8b" alt="이너서클 1기 백엔트파트 장현호" height="23">
  </a>
</p>

<p align="center">
  <a href="../README.md">English</a> ·
  <b>한국어</b> ·
  <a href="README.zh.md">简体中文</a> ·
  <a href="README.ja.md">日本語</a>
</p>

> Inner Circle BE 1기 오픈소스 라이브러리 프로젝트 <br />
> 백엔트파트 장현호

JSON에 맞춰 자동으로 CSV파일을 생성해주는 라이브러리 입니다.

### 🧑🏼‍🎨오픈소스 사용 모범사례 흐름도
<img width="1044" alt="image" src="https://github.com/user-attachments/assets/f49bbe60-8b2d-4a31-bacb-5b48bb87ec99">

### 🧑🏼‍🌾개발 일지
- [[설계/최종본] 클래스다이어그램](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-05.-%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%B5%9C%EC%A2%85%EB%B3%B8)
- [[설계] 클래스다이어그램](https://github.com/hyunolike/json-csv-bridge/wiki/%EA%B0%9C%EB%B0%9C%EA%B8%B0%EB%A1%9D-03.-%08%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8-%EC%84%A4%EA%B3%84)

---
## Features (7)
- 기본. JSON 값 CSV 파일 변환 생성 🚀 `개발완료`
  - JSON 배열과 단일 JSON 객체를 모두 받습니다. CSV 열 순서는 입력 JSON의 키 순서를 그대로 따릅니다.
- 커스텀 CSV 필드 매핑 ⚠️ `개발 미완료`
  - 사용자가 JSON 필드를 CSV 열과 수동으로 매핑할 수 있도록 지원합니다. 예를 들어, JSON의 특정 필드를 CSV의 특정 열로 지정할 수 있습니다.
- 데이터 검증 및 정제 🚀 `개발완료`
  - 변환 전에 JSON 형식을 검증하고, 중첩 객체·배열·`null`을 CSV에 쓸 수 있는 형태로 정제합니다. 형식이 잘못되면 어느 위치가 문제인지 알려주는 `IllegalArgumentException`을 던집니다.
- CSV 포맷 설정 🚀 `개발완료`
  - `FormattingOptions`로 구분자, 인코딩, 행 구분자, 빈 값 표기, Excel용 BOM을 지정할 수 있습니다.
- 데이터 필터링 및 선택적 변환 🚀 `개발완료`
  - `FilterCriteria`로 조건을 만족하는 레코드만 골라 변환합니다. (특정 필드만 선택해 내보내는 열 단위 필터링은 아직 지원하지 않습니다.)
- 병합 및 합치기 🚀 `개발완료`
  - 여러 JSON 문서를 하나의 CSV 파일로 병합합니다. 헤더는 모든 입력에 등장한 키의 합집합입니다.
- 변환 후 후처리 작업 ⚠️ `개발 미완료`
  - 변환 후 CSV 파일에 대해 추가적인 후처리 작업(예: 특정 열 삭제, 추가, 순서 변경)을 할 수 있는 기능을 제공합니다.

## Dependencies
Depends on:
- Java 21
- Kotlin 1.9

런타임 의존성은 Jackson과 SLF4J API뿐입니다. **Spring을 쓰지 않는 프로젝트에서도 그대로 사용할 수 있습니다.**

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
### 예제샘플 바로가기 ![](https://img.shields.io/badge/spring_boot-6DB33F?style=flat&logo=springboot&logoColor=white)
- 🚀[kotiln + spring boot](https://github.com/hyunolike/json-csv-bridge/blob/develop/examples/spring-boot-kotlin/src/main/kotlin/com/example/SpringBootKotlinApplication.kt)
- 🚀[java + spring boot](https://github.com/hyunolike/json-csv-bridge/blob/develop/examples/spring-boot-java/src/main/java/com/example/springbootjava/SpringBootJavaApplication.java)

```kotlin
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateCsv
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateMergeCsv
import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.FilterCriteria
import com.jsoncsvbridge.json.JsonToCsvCreator
```

### 1️⃣ 기본. JSON 값 CSV 파일 변환 생성
```kotlin
// 기능1. JSON 형식 문자열 CSV 파일로 변환
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
출력 경로의 상위 디렉터리는 자동으로 만들어집니다.

### 2️⃣ 병합 및 합치기
```kotlin
// 기능2. 2개 JSON 형식 문자열 CSV 파일로 변환
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

// 3개 이상도 한 번에 병합할 수 있습니다
mergeJsonCreator.createMergedCsv(listOf(data1, data2, data3), "csv_output/output_merge_all.csv")
```
```csv
name,age,city,country,occupation,hobby
John,30,New York,USA,,
Alice,25,London,,Engineer,
Bob,35,Paris,,,Photography
```

### 3️⃣ CSV 포맷 설정
```kotlin
val options = FormattingOptions(
    delimiter = ';',          // 구분자
    encoding = "UTF-8",       // 인코딩
    lineTerminator = "\r\n",  // 행 구분자
    nullValue = "N/A",        // 빈 값 표기 (기본값: 빈 칸)
    byteOrderMark = true,     // Excel에서 한글이 깨질 때 true
)

generateCsv("json", options).createCsv(jsonInput, "csv_output/output_formatted.csv")
```

### 4️⃣ 데이터 필터링
```kotlin
val criteria = FilterCriteria.of(Condition("city", "Chicago"))

val creator = generateCsv("json") as JsonToCsvCreator
creator.createCsv(jsonInput, "csv_output/output_filtered.csv", criteria)
```
조건이 여러 개면 모두 만족하는(AND) 레코드만 남고, 조건이 없으면 전체가 변환됩니다.

## Migrating to v2.0.0
v1.x에서 올라올 때 달라지는 동작입니다.

| 항목 | v1.0.0 | v2.0.0 |
| --- | --- | --- |
| CSV 열 순서 | 해시 순서라 입력과 다르게 섞임 | 입력 JSON의 키 순서 유지 |
| 빈 값 표기 | 문자열 `NULL` | 빈 칸 (`FormattingOptions.nullValue`로 변경 가능) |
| 중첩 객체/배열 | `{c=Seoul}` 같은 객체 문자열 | JSON 문자열 (`{"c":"Seoul"}`) |
| 헤더 이스케이프 | 없음 (키에 `,`가 있으면 열이 깨짐) | RFC 4180 규칙으로 인용 |
| 단일 JSON 객체 입력 | 예외 | 레코드 1건으로 변환 |
| 빈 입력 | 파일을 만들지 않음 | 빈 파일 생성 |
| `MergeCsvCreator.createCsv` | `UnsupportedOperationException` | 입력 1개짜리 병합으로 동작 |
| Spring 의존성 | `spring-boot-starter-web` 전이 | 없음 (컴파일 시점에만 사용) |
| 로그 | `println` | SLF4J (`debug` 레벨) |

`JsonToCsvCreator` 생성자에서 쓰이지 않던 `CsvFormatter` 파라미터가 빠졌습니다. 변환기를 직접 `new` 하지 않고 `generateCsv` / `generateMergeCsv`로 만들었다면 영향이 없습니다.

## Contributing
```bash
./gradlew ktlintCheck test   # PR 전에 실행 (CI에서 동일하게 검사합니다)
```

## Learn more about JsonCSVBridge
### Get Involved
- 🚀[Contributing Guide](https://github.com/hyunolike/json-csv-bridge/discussions/33)
