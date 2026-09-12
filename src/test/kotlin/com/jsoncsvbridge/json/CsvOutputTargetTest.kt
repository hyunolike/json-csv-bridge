package com.jsoncsvbridge.json

import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateCsv
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateMergeCsv
import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.FilterCriteria
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 닫혔는지 기록하는 [StringWriter]. 익명 객체로 두면 클래스 파일 이름에 한글이 섞이므로 이름을 준다. */
private class CloseTrackingWriter : StringWriter() {
    var closed = false
        private set

    override fun close() {
        closed = true
    }
}

/** 파일 말고도 문자열, Writer, OutputStream, Reader 로 주고받을 수 있는지 확인한다. */
class CsvOutputTargetTest {
    private val json =
        """
        [
            {"name": "John", "city": "Seoul"},
            {"name": "Alice", "city": "London"}
        ]
        """.trimIndent()

    private val expected = "name,city\nJohn,Seoul\nAlice,London\n"

    @Test
    fun `CSV 문자열로 바로 받는다`() {
        assertEquals(expected, generateCsv("json").convertToString(json))
    }

    @Test
    fun `Writer 로 흘려보낸다`() {
        val out = StringWriter()

        generateCsv("json").createCsv(json, out)

        assertEquals(expected, out.toString())
    }

    @Test
    fun `OutputStream 으로 흘려보낸다`() {
        val out = ByteArrayOutputStream()

        generateCsv("json").createCsv(json, out)

        assertEquals(expected, out.toString(Charsets.UTF_8))
    }

    @Test
    fun `OutputStream 은 지정한 인코딩으로 쓴다`() {
        val out = ByteArrayOutputStream()

        generateCsv("json", FormattingOptions(encoding = "EUC-KR"))
            .createCsv("""[{"name": "장현호"}]""", out)

        assertEquals("name\n장현호\n", out.toString(charset("EUC-KR")))
    }

    @Test
    fun `넘겨받은 Writer 는 닫지 않는다`() {
        val out = CloseTrackingWriter()

        generateCsv("json").createCsv(json, out)

        assertFalse(out.closed, "호출자가 넘긴 Writer 의 수명은 호출자가 쥐어야 한다")
        assertTrue(out.toString().isNotEmpty())
    }

    @Test
    fun `Reader 에서 읽어 Writer 로 흘려보낸다`() {
        val out = StringWriter()

        generateCsv("json").createCsv(StringReader(json), out)

        assertEquals(expected, out.toString())
    }

    @Test
    fun `Reader 에서 읽어 파일로 쓴다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("from_reader.csv").toFile()

        generateCsv("json").createCsv(StringReader(json), file.path)

        assertEquals(expected, file.readText())
    }

    @Test
    fun `필터를 걸고 문자열로 받는다`() {
        val creator = generateCsv("json") as JsonToCsvCreator

        val csv = creator.convertToString(json, FilterCriteria.of(Condition("city", "Seoul")))

        assertEquals("name,city\nJohn,Seoul\n", csv)
    }

    @Test
    fun `필터를 걸고 Writer 로 흘려보낸다`() {
        val out = StringWriter()
        val creator = generateCsv("json") as JsonToCsvCreator

        creator.createCsv(json, out, FilterCriteria.of(Condition("city", "London")))

        assertEquals("name,city\nAlice,London\n", out.toString())
    }

    @Test
    fun `병합 결과를 문자열로 받는다`() {
        val merged = generateMergeCsv().mergeToString(listOf("""[{"a": 1}]""", """{"b": 2}"""))

        assertEquals("a,b\n1,\n,2\n", merged)
    }

    @Test
    fun `병합 결과를 Writer 로 흘려보낸다`() {
        val out = StringWriter()

        generateMergeCsv().createMergedCsv(listOf("""[{"a": 1}]""", """[{"a": 2}]"""), out)

        assertEquals("a\n1\n2\n", out.toString())
    }

    @Test
    fun `빈 입력은 빈 문자열이 된다`() {
        assertEquals("", generateCsv("json").convertToString("[]"))
    }
}
