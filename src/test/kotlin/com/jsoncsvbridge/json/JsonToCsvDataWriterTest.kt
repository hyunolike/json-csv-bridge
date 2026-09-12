package com.jsoncsvbridge.json

import com.jsoncsvbridge.csv.FormattingOptions
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsonToCsvDataWriterTest {
    private val writer = JsonToCsvDataWriter()

    @Test
    fun `헤더와 데이터를 입력 순서대로 기록한다`(
        @TempDir tempDir: Path,
    ) {
        val data =
            listOf(
                mapOf("name" to "John", "age" to 30, "city" to "New York"),
                mapOf("name" to "Alice", "age" to 25, "city" to "London"),
            )
        val file = tempDir.resolve("output.csv").toFile()

        writer.write(data, file.path)

        assertEquals(
            listOf("name,age,city", "John,30,New York", "Alice,25,London"),
            file.readLines(),
        )
    }

    @Test
    fun `헤더에 구분자가 들어 있어도 열 개수가 어긋나지 않는다`(
        @TempDir tempDir: Path,
    ) {
        val data = listOf(mapOf("a,b" to "x", "q" to "he said \"hi\""))
        val file = tempDir.resolve("escaped.csv").toFile()

        writer.write(data, file.path)

        val lines = file.readLines()
        assertEquals("\"a,b\",q", lines[0])
        assertEquals("x,\"he said \"\"hi\"\"\"", lines[1])
    }

    @Test
    fun `CR LF 와 앞뒤 공백이 있는 값은 인용한다`(
        @TempDir tempDir: Path,
    ) {
        val data = listOf(mapOf("cr" to "x\ry", "lf" to "x\ny", "pad" to " x "))
        val file = tempDir.resolve("newline.csv").toFile()

        writer.write(data, file.path)

        val text = file.readText()
        assertTrue(text.contains("\"x\ry\""), "단독 CR 은 인용되어야 한다: $text")
        assertTrue(text.contains("\"x\ny\""), "LF 는 인용되어야 한다: $text")
        assertTrue(text.contains("\" x \""), "앞뒤 공백은 인용되어야 한다: $text")
    }

    @Test
    fun `null 과 누락된 값은 기본적으로 빈 칸으로 쓴다`(
        @TempDir tempDir: Path,
    ) {
        val data = listOf(mapOf("a" to 1, "b" to null), mapOf("a" to 2, "c" to 3))
        val file = tempDir.resolve("null.csv").toFile()

        writer.write(data, file.path)

        assertEquals(listOf("a,b,c", "1,,", "2,,3"), file.readLines())
    }

    @Test
    fun `nullValue 옵션으로 빈 값 표기를 바꾼다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("null-token.csv").toFile()

        JsonToCsvDataWriter(FormattingOptions(nullValue = "N/A"))
            .write(listOf(mapOf("a" to null)), file.path)

        assertEquals(listOf("a", "N/A"), file.readLines())
    }

    @Test
    fun `구분자와 행 구분자 옵션을 적용한다`(
        @TempDir tempDir: Path,
    ) {
        val options = FormattingOptions(delimiter = ';', lineTerminator = "\r\n")
        val file = tempDir.resolve("semicolon.csv").toFile()

        JsonToCsvDataWriter(options).write(listOf(mapOf("a" to "1,2", "b" to "x;y")), file.path)

        // 구분자가 ';' 이므로 쉼표는 평범한 문자고, 대신 ';' 가 들어간 값이 인용된다.
        assertEquals("a;b\r\n1,2;\"x;y\"\r\n", file.readText())
    }

    @Test
    fun `BOM 옵션을 켜면 파일 앞에 BOM 을 붙인다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("bom.csv").toFile()

        JsonToCsvDataWriter(FormattingOptions(byteOrderMark = true))
            .write(listOf(mapOf("이름" to "장현호")), file.path)

        val bytes = file.readBytes()
        assertEquals(listOf(0xEF, 0xBB, 0xBF), bytes.take(3).map { it.toInt() and 0xFF })
    }

    @Test
    fun `중첩된 객체와 배열은 JSON 문자열로 쓴다`(
        @TempDir tempDir: Path,
    ) {
        val data = listOf(mapOf("id" to 1, "addr" to mapOf("city" to "Seoul"), "tags" to listOf("a", "b")))
        val file = tempDir.resolve("nested.csv").toFile()

        writer.write(data, file.path)

        assertEquals(
            listOf("id,addr,tags", "1,\"{\"\"city\"\":\"\"Seoul\"\"}\",\"[\"\"a\"\",\"\"b\"\"]\""),
            file.readLines(),
        )
    }

    @Test
    fun `빈 데이터여도 파일은 만든다`(
        @TempDir tempDir: Path,
    ) {
        val file = tempDir.resolve("empty.csv").toFile()

        writer.write(emptyList(), file.path)

        assertTrue(file.exists(), "빈 입력이어도 출력 파일은 존재해야 한다")
        assertEquals("", file.readText())
    }

    @Test
    fun `없는 상위 디렉터리를 만들어 준다`(
        @TempDir tempDir: Path,
    ) {
        val file = File(tempDir.toFile(), "nested/dir/output.csv")
        assertFalse(file.parentFile.exists())

        writer.write(listOf(mapOf("a" to 1)), file.path)

        assertTrue(file.exists())
    }
}
