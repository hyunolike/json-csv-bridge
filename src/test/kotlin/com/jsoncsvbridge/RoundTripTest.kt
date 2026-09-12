package com.jsoncsvbridge

import com.jsoncsvbridge.csv.FlattenMode
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateCsv
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateJson
import com.jsoncsvbridge.json.CsvToJsonCreator
import com.jsoncsvbridge.json.JsonDataValidator
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * JSON → CSV → JSON 왕복에서 값이 그대로 돌아오는지 확인한다.
 *
 * 변환기가 서로의 역이라는 것이 이 라이브러리의 핵심 약속이므로, 개별 기능 테스트와 별도로 둔다.
 */
class RoundTripTest {
    private val validator = JsonDataValidator()

    private fun assertRoundTrips(json: String, options: FormattingOptions = FormattingOptions()) {
        val csv = generateCsv("json", options).convertToString(json)
        val back = generateJson(options).toRecords(csv)

        assertEquals(validator.validate(json), back, "왕복 후 값이 달라졌습니다. 중간 CSV:\n$csv")
    }

    @Test
    fun `평범한 레코드가 그대로 돌아온다`() {
        assertRoundTrips(
            """
            [
                {"name": "John", "age": 30, "city": "New York"},
                {"name": "Alice", "age": 25, "city": "London"}
            ]
            """.trimIndent(),
        )
    }

    @Test
    fun `여러 타입이 섞여도 그대로 돌아온다`() {
        assertRoundTrips(
            """
            [
                {"int": 30, "long": 12345678901, "big": 12345678901234567890,
                 "dec": 1.10, "bool": true, "nothing": null, "text": "hello"}
            ]
            """.trimIndent(),
        )
    }

    @Test
    fun `중첩 객체와 배열이 그대로 돌아온다`() {
        assertRoundTrips(
            """
            [
                {"id": 1, "addr": {"city": "Seoul", "zip": "06236"}, "tags": ["a", "b"]},
                {"id": 2, "addr": {"city": "Busan", "zip": "48058"}, "tags": []}
            ]
            """.trimIndent(),
        )
    }

    @Test
    fun `구분자 인용부호 개행이 든 값이 그대로 돌아온다`() {
        assertRoundTrips(
            """
            [
                {"note": "Seoul, Korea", "quote": "he said \"hi\"", "multiline": "line1\nline2"}
            ]
            """.trimIndent(),
        )
    }

    @Test
    fun `유니코드와 이모지가 그대로 돌아온다`() {
        assertRoundTrips("""[{"name": "장현호", "emoji": "🚀", "kanji": "日本語"}]""")
    }

    @Test
    fun `숫자처럼 보이는 문자열이 숫자로 바뀌지 않는다`() {
        assertRoundTrips("""[{"zip": "007", "phone": "010-1234-5678", "signed": "+1"}]""")
    }

    @Test
    fun `레코드마다 키가 다르면 CSV 가 직사각형이라 열의 합집합으로 돌아온다`() {
        val json = """[{"a": 1, "b": 2}, {"a": 3, "c": 4}]"""

        val back = generateJson().toRecords(generateCsv("json").convertToString(json))

        // CSV 는 "키가 없음"과 "값이 null"을 구분하지 못한다. 이 라이브러리의 한계가 아니라 포맷의 한계다.
        assertEquals(listOf(mapOf("a" to 1, "b" to 2, "c" to null), mapOf("a" to 3, "b" to null, "c" to 4)), back)
    }

    @Test
    fun `includeNullFields 를 끄면 레코드마다 키가 달라도 원래 모양으로 돌아온다`() {
        val json = """[{"a": 1, "b": 2}, {"a": 3, "c": 4}]"""
        val csv = generateCsv("json").convertToString(json)

        val back = CsvToJsonCreator(includeNullFields = false).toRecords(csv)

        assertEquals(validator.validate(json), back)
    }

    @Test
    fun `구분자와 행 구분자를 바꿔도 그대로 돌아온다`() {
        assertRoundTrips(
            """[{"a": "x;y", "b": "1,2"}]""",
            FormattingOptions(delimiter = ';', lineTerminator = "\r\n"),
        )
    }

    @Test
    fun `nullValue 를 지정해도 그대로 돌아온다`() {
        assertRoundTrips("""[{"a": null, "b": 1}]""", FormattingOptions(nullValue = "N/A"))
    }

    @Test
    fun `빈 배열이 그대로 돌아온다`() {
        assertRoundTrips("[]")
    }

    @Test
    fun `펼친 CSV 는 열 이름이 경로가 되어 돌아온다`() {
        val json = """[{"id": 1, "addr": {"city": "Seoul"}, "tags": ["a", "b"]}]"""
        val options = FormattingOptions(flatten = FlattenMode.BRACKET)

        val csv = generateCsv("json", options).convertToString(json)
        val back = generateJson(options).toRecords(csv)

        // 펼친 결과는 중첩 구조가 아니라 평평한 열로 돌아온다.
        assertEquals(mapOf("id" to 1, "addr.city" to "Seoul", "tags[0]" to "a", "tags[1]" to "b"), back[0])
    }
}
