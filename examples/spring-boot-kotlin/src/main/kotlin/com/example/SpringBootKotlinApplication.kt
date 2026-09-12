package com.example

import com.jsoncsvbridge.csv.FlattenMode
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateCsv
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateJson
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateMergeCsv
import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.FilterCriteria
import com.jsoncsvbridge.json.JsonToCsvCreator
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.io.File

@SpringBootApplication
class SpringBootKotlinApplication

fun main(args: Array<String>) {
    runApplication<SpringBootKotlinApplication>(*args)
}

@Component
class CsvCreatorRunner : CommandLineRunner {
    override fun run(vararg args: String) {
        val outputDir = File("csv_output")

        // 기능1. JSON 형식 문자열을 CSV 파일로 변환
        val jsonInput = """
            [
                {"name": "Hyunho", "age": 30, "city": "New York"},
                {"name": "Bob", "age": 25, "city": "Los Angeles"},
                {"name": "Charlie", "age": 35, "city": "Chicago"}
            ]
        """

        val jsonOutputPath = outputDir.resolve("output_json.csv").absolutePath
        generateCsv("json").createCsv(jsonInput, jsonOutputPath)
        println("JSON to CSV conversion completed. File saved at: $jsonOutputPath")

        // 기능2. 2개 JSON 형식 문자열을 CSV 파일 하나로 병합
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

        val mergeJsonOutputPath = outputDir.resolve("output_merge_json.csv").absolutePath
        generateMergeCsv().createMergedCsv(data1, data2, mergeJsonOutputPath)
        println("Merged 2 JSON files and converted to CSV. File saved at: $mergeJsonOutputPath")

        // 기능3. CSV 포맷 지정 (구분자, 행 구분자, 빈 값 표기, Excel 용 BOM)
        val options =
            FormattingOptions(
                delimiter = ';',
                lineTerminator = "\r\n",
                nullValue = "N/A",
                byteOrderMark = true,
            )

        val formattedOutputPath = outputDir.resolve("output_formatted.csv").absolutePath
        generateCsv("json", options).createCsv(jsonInput, formattedOutputPath)
        println("Formatted CSV saved at: $formattedOutputPath")

        // 기능4. 조건에 맞는 레코드만 변환
        val criteria = FilterCriteria.of(Condition("city", "Chicago"))

        val filteredOutputPath = outputDir.resolve("output_filtered.csv").absolutePath
        (generateCsv("json") as JsonToCsvCreator).createCsv(jsonInput, filteredOutputPath, criteria)
        println("Filtered CSV saved at: $filteredOutputPath")

        // 기능5. 중첩 값을 열로 펼치기
        val nestedInput = """
            [
                {"id": 1, "addr": {"city": "Seoul"}, "tags": ["a", "b"]}
            ]
        """

        val flattened = generateCsv("json", FormattingOptions(flatten = FlattenMode.BRACKET))
        println("Flattened CSV:\n${flattened.convertToString(nestedInput)}")

        // 기능6. 파일을 거치지 않고 CSV 문자열로 받기
        val csv = generateCsv("json").convertToString(jsonInput)
        println("CSV as a string:\n$csv")

        // 기능7. CSV 를 다시 JSON 으로 되돌리기
        println("Back to JSON: ${generateJson().toJsonString(csv)}")
    }
}
