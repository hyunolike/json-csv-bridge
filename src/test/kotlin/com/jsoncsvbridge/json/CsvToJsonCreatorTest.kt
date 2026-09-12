package com.jsoncsvbridge.json

import com.jsoncsvbridge.csv.FormattingOptions
import org.junit.jupiter.api.io.TempDir
import java.io.StringReader
import java.io.StringWriter
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CsvToJsonCreatorTest {
    private val converter = CsvToJsonCreator()

    @Test
    fun `첫 행을 헤더로 읽어 레코드를 만든다`() {
        val records = converter.toRecords("name,age\nJohn,30\nAlice,25")

        assertEquals(2, records.size)
        assertEquals(listOf("name", "age"), records[0].keys.toList())
        assertEquals("John", records[0]["name"])
        assertEquals(30, records[0]["age"])
    }

    @Test
    fun `숫자 불리언 null 을 원래 타입으로 되살린다`() {
        val records = converter.toRecords("i,l,d,b,n\n30,12345678901,1.10,true,")

        assertEquals(30, records[0]["i"])
        assertEquals(12345678901L, records[0]["l"])
        assertEquals(BigDecimal("1.10"), records[0]["d"])
        assertEquals(true, records[0]["b"])
        assertNull(records[0]["n"])
    }

    @Test
    fun `Long 을 넘는 정수는 BigInteger 로 읽는다`() {
        val records = converter.toRecords("big\n12345678901234567890")

        assertEquals(BigInteger("12345678901234567890"), records[0]["big"])
    }

    @Test
    fun `숫자로 바꾸면 뜻이 달라지는 값은 문자열로 남긴다`() {
        val records = converter.toRecords("zip,phone,signed,money\n007,010-1234-5678,+1,1000")

        assertEquals("007", records[0]["zip"])
        assertEquals("010-1234-5678", records[0]["phone"])
        assertEquals("+1", records[0]["signed"])
        assertEquals(1000, records[0]["money"])
    }

    @Test
    fun `중첩 JSON 문자열을 객체와 배열로 되살린다`() {
        val csv = "id,addr,tags\n1,\"{\"\"city\"\":\"\"Seoul\"\"}\",\"[\"\"a\"\",\"\"b\"\"]\""

        val records = converter.toRecords(csv)

        assertEquals(mapOf("city" to "Seoul"), records[0]["addr"])
        assertEquals(listOf("a", "b"), records[0]["tags"])
    }

    @Test
    fun `JSON 처럼 생겼지만 파싱되지 않는 값은 문자열로 둔다`() {
        val records = converter.toRecords("note\n\"{not json\"")

        assertEquals("{not json", records[0]["note"])
    }

    @Test
    fun `인용된 구분자와 개행을 데이터로 되살린다`() {
        val csv = "name,note\nJohn,\"Seoul, Korea\nsecond line\""

        val records = converter.toRecords(csv)

        assertEquals(1, records.size)
        assertEquals("Seoul, Korea\nsecond line", records[0]["note"])
    }

    @Test
    fun `타입 추론을 끄면 모든 칸이 문자열이 된다`() {
        val records = CsvToJsonCreator(typeInference = false).toRecords("a,b\n30,true")

        assertEquals("30", records[0]["a"])
        assertEquals("true", records[0]["b"])
    }

    @Test
    fun `nullValue 로 지정한 표기를 null 로 읽는다`() {
        val records = CsvToJsonCreator(FormattingOptions(nullValue = "N/A")).toRecords("a,b\nN/A,1")

        assertNull(records[0]["a"])
        assertEquals(1, records[0]["b"])
    }

    @Test
    fun `구분자 옵션을 적용한다`() {
        val records = CsvToJsonCreator(FormattingOptions(delimiter = ';')).toRecords("a;b\n1,2;3")

        assertEquals("1,2", records[0]["a"])
        assertEquals(3, records[0]["b"])
    }

    @Test
    fun `헤더보다 짧은 행은 뒤쪽을 null 로 채운다`() {
        val records = converter.toRecords("a,b,c\n1,2")

        assertEquals(1, records[0]["a"])
        assertEquals(2, records[0]["b"])
        assertNull(records[0]["c"])
    }

    @Test
    fun `헤더보다 긴 행은 줄 번호를 알려주며 실패한다`() {
        val error = assertFailsWith<IllegalArgumentException> { converter.toRecords("a,b\n1,2\n1,2,3") }

        assertTrue(error.message!!.contains("row 3"), "실제 메시지: ${error.message}")
    }

    @Test
    fun `중복된 헤더는 뒤쪽에 번호를 붙여 값을 잃지 않는다`() {
        val records = converter.toRecords("name,name\nA,B")

        assertEquals("A", records[0]["name"])
        assertEquals("B", records[0]["name_2"])
    }

    @Test
    fun `BOM 이 붙은 CSV 도 첫 열 이름을 잃지 않는다`() {
        val records = converter.toRecords("﻿name,age\nJohn,30")

        assertEquals("John", records[0]["name"])
    }

    @Test
    fun `빈 CSV 는 빈 배열이 된다`() {
        assertEquals(emptyList(), converter.toRecords(""))
        assertEquals("[]", converter.toJsonString(""))
    }

    @Test
    fun `헤더만 있는 CSV 는 빈 배열이 된다`() {
        assertEquals("[]", converter.toJsonString("a,b\n"))
    }

    @Test
    fun `JSON 문자열로 출력한다`() {
        assertEquals("""[{"name":"John","age":30}]""", converter.toJsonString("name,age\nJohn,30"))
    }

    @Test
    fun `prettyPrint 를 켜면 들여쓰기해 출력한다`() {
        val json = CsvToJsonCreator(prettyPrint = true).toJsonString("a\n1")

        assertTrue(json.contains("\n"), "실제 출력: $json")
        assertTrue(json.contains("\"a\" : 1"), "실제 출력: $json")
    }

    @Test
    fun `Reader 에서 읽어 Writer 로 쓴다`() {
        val out = StringWriter()

        converter.convert(StringReader("a,b\n1,2"), out)

        assertEquals("""[{"a":1,"b":2}]""", out.toString())
    }

    @Test
    fun `CSV 파일을 JSON 파일로 바꾼다`(
        @TempDir tempDir: Path,
    ) {
        val csv = tempDir.resolve("in.csv").toFile().apply { writeText("name,age\n장현호,30") }
        val json = tempDir.resolve("nested/out.json").toFile()

        converter.convertFile(csv.path, json.path)

        assertEquals("""[{"name":"장현호","age":30}]""", json.readText())
    }
}
