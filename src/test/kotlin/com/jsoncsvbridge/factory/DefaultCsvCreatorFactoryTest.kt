package com.jsoncsvbridge.factory

import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.csv.MergeCsvCreator
import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.FilterCriteria
import com.jsoncsvbridge.json.JsonToCsvCreator
import com.jsoncsvbridge.json.MergeJsonToCsvCreator
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DefaultCsvCreatorFactoryTest {
    private val factory = DefaultCsvCreatorFactory()

    @Test
    fun `json 타입은 JsonToCsvCreator 를 만든다`() {
        assertTrue(factory.createCsvCreator("json") is JsonToCsvCreator)
    }

    @Test
    fun `mergeJson 타입은 MergeJsonToCsvCreator 를 만든다`() {
        assertTrue(factory.createCsvCreator("mergeJson") is MergeJsonToCsvCreator)
    }

    @Test
    fun `타입 문자열의 대소문자는 가리지 않는다`() {
        assertTrue(factory.createCsvCreator("MergeJson") is MergeJsonToCsvCreator)
        assertTrue(factory.createCsvCreator("JSON") is JsonToCsvCreator)
    }

    @Test
    fun `모르는 타입은 지원 목록을 알려주며 실패한다`() {
        val error = assertFailsWith<IllegalArgumentException> { factory.createCsvCreator("unknown") }

        assertTrue(error.message!!.contains("json"), "실제 메시지: ${error.message}")
    }

    @Test
    fun `정적 메서드로도 같은 변환기를 만든다`() {
        assertTrue(DefaultCsvCreatorFactory.generateCsv("json") is JsonToCsvCreator)
        assertTrue(DefaultCsvCreatorFactory.generateMergeCsv() is MergeJsonToCsvCreator)
    }

    @Test
    fun `JSON 을 입력 순서 그대로 CSV 로 변환한다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("output.csv").toFile()
        val data =
            """
            [
                {"name": "John", "age": 30, "city": "New York"},
                {"name": "Alice", "age": 25, "city": "London"},
                {"name": "Bob", "age": 35, "city": "Paris"}
            ]
            """.trimIndent()

        factory.createCsvCreator("json").createCsv(data, file.path)

        assertEquals(
            listOf(
                "name,age,city",
                "John,30,New York",
                "Alice,25,London",
                "Bob,35,Paris",
            ),
            file.readLines(),
        )
    }

    @Test
    fun `조건에 맞는 레코드만 변환한다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("filtered.csv").toFile()
        val data =
            """
            [
                {"name": "John", "city": "Seoul"},
                {"name": "Alice", "city": "London"},
                {"name": "Bob", "city": "Seoul"}
            ]
            """.trimIndent()

        val creator = factory.createCsvCreator("json") as JsonToCsvCreator
        creator.createCsv(data, file.path, FilterCriteria.of(Condition("city", "Seoul")))

        assertEquals(listOf("name,city", "John,Seoul", "Bob,Seoul"), file.readLines())
    }

    @Test
    fun `두 JSON 을 병합하면 키 합집합이 헤더가 된다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("merged.csv").toFile()
        val data1 = """[{"name": "John", "age": 30, "city": "New York", "country": "USA"}]"""
        val data2 =
            """
            [
                {"name": "Alice", "age": 25, "city": "London", "occupation": "Engineer"},
                {"name": "Bob", "age": 35, "city": "Paris", "hobby": "Photography"}
            ]
            """.trimIndent()

        (factory.createCsvCreator("mergeJson") as MergeCsvCreator).createMergedCsv(data1, data2, file.path)

        assertEquals(
            listOf(
                "name,age,city,country,occupation,hobby",
                "John,30,New York,USA,,",
                "Alice,25,London,,Engineer,",
                "Bob,35,Paris,,,Photography",
            ),
            file.readLines(),
        )
    }

    @Test
    fun `병합 변환기의 createCsv 는 입력 하나짜리 병합으로 동작한다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("single.csv").toFile()

        DefaultCsvCreatorFactory.generateMergeCsv().createCsv("""[{"a": 1, "b": 2}]""", file.path)

        assertEquals(listOf("a,b", "1,2"), file.readLines())
    }

    @Test
    fun `세 개 이상의 JSON 도 병합한다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("multi.csv").toFile()
        val inputs = listOf("""[{"a": 1}]""", """{"b": 2}""", """[{"a": 3, "b": 4}]""")

        DefaultCsvCreatorFactory.generateMergeCsv().createMergedCsv(inputs, file.path)

        assertEquals(listOf("a,b", "1,", ",2", "3,4"), file.readLines())
    }

    @Test
    fun `포맷 옵션을 지정해 변환기를 만든다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("tsv.csv").toFile()
        val options = FormattingOptions(delimiter = '\t', nullValue = "NULL")

        DefaultCsvCreatorFactory
            .generateCsv("json", options)
            .createCsv("""[{"a": 1, "b": null}]""", file.path)

        assertEquals(listOf("a\tb", "1\tNULL"), file.readLines())
    }
}
