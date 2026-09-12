package com.jsoncsvbridge.json

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.jsoncsvbridge.csv.CsvSyntax
import com.jsoncsvbridge.csv.CsvToJsonConverter
import com.jsoncsvbridge.csv.FormattingOptions
import org.slf4j.LoggerFactory
import java.io.File
import java.io.Reader
import java.io.Writer
import java.math.BigInteger

/**
 * CSV 를 JSON 으로 되돌린다.
 *
 * 첫 행을 헤더로 보고, 이후 각 행을 레코드 하나로 만든다.
 *
 * - [typeInference] 가 켜져 있으면 숫자·불리언·null·중첩 JSON 을 원래 타입으로 되살린다.
 *   `007` 이나 `+1` 처럼 숫자로 바꾸면 의미가 달라지는 값은 문자열로 남긴다.
 * - 빈 칸(또는 [FormattingOptions.nullValue] 와 같은 값)은 `null` 이 된다.
 * - 헤더에 같은 이름이 두 번 나오면 뒤의 것에 `_2`, `_3` 을 붙여 값을 잃지 않게 한다.
 *
 * CSV 는 직사각형이라 "키가 없음"과 "값이 null"을 구분하지 못한다. 레코드마다 키가 다른 JSON 을
 * CSV 로 내보냈다가 되읽으면 모든 레코드가 열의 합집합을 갖게 된다. 원래 모양이 더 중요하면
 * [includeNullFields] 를 꺼서 값이 없는 키를 빼면 되지만, 그러면 원래 있던 `null` 값도 함께 사라진다.
 *
 * @param options 읽어들일 CSV 의 방언(구분자, 인용 문자, 빈 값 표기, 인코딩)
 * @param typeInference 값의 타입을 추론할지 여부. 끄면 모든 칸이 문자열이 된다.
 * @param prettyPrint JSON 을 들여쓰기해 출력할지 여부
 * @param includeNullFields 값이 비어 있는 키를 결과에 남길지 여부
 */
class CsvToJsonCreator @JvmOverloads constructor(
    private val options: FormattingOptions = FormattingOptions(),
    private val typeInference: Boolean = true,
    private val prettyPrint: Boolean = false,
    private val includeNullFields: Boolean = true,
) : CsvToJsonConverter {
    private val objectMapper =
        jacksonMapperBuilder()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .disable(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)
            .build()

    private val jsonWriter
        get() = if (prettyPrint) objectMapper.writerWithDefaultPrettyPrinter() else objectMapper.writer()

    override fun toJsonString(csv: String): String = jsonWriter.writeValueAsString(toRecords(csv))

    override fun toRecords(csv: String): List<Map<String, Any?>> {
        val rows = CsvSyntax.parseRows(csv.removePrefix(BOM), options).filterNot { it.isBlankRow() }
        if (rows.isEmpty()) return emptyList()

        val headers = deduplicate(rows.first())
        return rows.drop(1).mapIndexed { index, row -> toRecord(headers, row, index + 2) }
    }

    override fun convert(csv: Reader, out: Writer) {
        out.write(toJsonString(csv.readText()))
        out.flush()
    }

    override fun convertFile(csvPath: String, jsonOutputPath: String) {
        val json = toJsonString(File(csvPath).readText(charset(options.encoding)))
        val output = File(jsonOutputPath)
        output.absoluteFile.parentFile?.mkdirs()
        output.writeText(json, charset(options.encoding))
        log.debug("JSON 파일이 생성되었습니다: {}", jsonOutputPath)
    }

    private fun List<String>.isBlankRow(): Boolean = size == 1 && first().isEmpty()

    /** 같은 이름의 헤더에 `_2`, `_3` 을 붙여 뒤 열이 앞 열을 덮어쓰지 않게 한다. */
    private fun deduplicate(headers: List<String>): List<String> {
        val seen = mutableMapOf<String, Int>()
        return headers.map { header ->
            val count = seen.merge(header, 1, Int::plus)!!
            if (count == 1) header else "${header}_$count"
        }
    }

    private fun toRecord(headers: List<String>, row: List<String>, lineNumber: Int): Map<String, Any?> {
        require(row.size <= headers.size) {
            "Malformed CSV: row $lineNumber has ${row.size} fields but the header has ${headers.size}"
        }

        return linkedMapOf<String, Any?>().apply {
            // 헤더보다 짧은 행은 뒤쪽을 null 로 채운다. 실제 CSV 에서 흔한 형태다.
            headers.forEachIndexed { index, header ->
                val value = toValue(row.getOrNull(index))
                if (value != null || includeNullFields) put(header, value)
            }
        }
    }

    private fun toValue(text: String?): Any? {
        if (text == null || text.isEmpty() || text == options.nullValue) return null
        if (!typeInference) return text

        return when {
            text == "true" -> true
            text == "false" -> false
            text == "null" -> null
            INTEGER.matches(text) -> text.toIntOrNull() ?: text.toLongOrNull() ?: BigInteger(text)
            DECIMAL.matches(text) -> objectMapper.readValue(text, Any::class.java)
            text.startsWith('{') || text.startsWith('[') -> parseNested(text)
            else -> text
        }
    }

    /** JSON 처럼 생겼을 뿐인 값이 있을 수 있으므로, 파싱에 실패하면 문자열 그대로 둔다. */
    private fun parseNested(text: String): Any? =
        try {
            objectMapper.readValue(text, Any::class.java)
        } catch (e: JacksonException) {
            log.debug("JSON 으로 해석되지 않아 문자열로 둡니다: {}", e.originalMessage)
            text
        }

    private companion object {
        private const val BOM = "﻿"

        // 앞자리 0("007")이나 부호 표기("+1")는 숫자로 바꾸면 뜻이 달라지므로 제외한다.
        private val INTEGER = Regex("""-?(0|[1-9]\d*)""")
        private val DECIMAL = Regex("""-?(0|[1-9]\d*)(\.\d+)?([eE][-+]?\d+)?""")

        private val log = LoggerFactory.getLogger(CsvToJsonCreator::class.java)
    }
}
