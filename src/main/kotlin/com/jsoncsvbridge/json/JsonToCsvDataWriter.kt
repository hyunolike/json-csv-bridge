package com.jsoncsvbridge.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jsoncsvbridge.csv.CsvSyntax
import com.jsoncsvbridge.csv.CsvWriter
import com.jsoncsvbridge.csv.FormattingOptions
import org.slf4j.LoggerFactory
import java.io.File

/**
 * 레코드 목록을 CSV 파일로 기록한다.
 *
 * - 헤더는 모든 레코드의 키를 처음 등장한 순서대로 모은 합집합이다.
 * - 헤더와 데이터 모두 [CsvSyntax] 규칙으로 인용하므로 키에 구분자가 있어도 열이 어긋나지 않는다.
 * - 중첩된 객체와 배열은 JSON 문자열로 직렬화한다.
 *
 * @param options 기본 출력 포맷. 호출마다 바꾸려면 [write] 의 3인자 오버로드를 쓴다.
 */
class JsonToCsvDataWriter(
    private val options: FormattingOptions = FormattingOptions(),
) : CsvWriter {
    private val objectMapper = jacksonObjectMapper()

    override fun write(data: List<Map<String, Any?>>, outputPath: String) = write(data, outputPath, options)

    /** [options] 대신 이번 호출에만 적용할 포맷을 지정해 기록한다. */
    fun write(data: List<Map<String, Any?>>, outputPath: String, options: FormattingOptions) {
        val headers = data.flatMapTo(LinkedHashSet()) { it.keys }
        val file = File(outputPath)
        file.absoluteFile.parentFile?.mkdirs()

        file.outputStream().bufferedWriter(charset(options.encoding)).use { out ->
            if (headers.isEmpty()) {
                // 빈 입력이어도 파일은 만든다. 호출자가 경로 존재 여부로 분기하지 않아도 되게.
                log.debug("변환할 데이터가 없어 빈 CSV 파일을 생성했습니다: {}", outputPath)
                return@use
            }

            if (options.byteOrderMark) out.write(BOM)

            out.write(CsvSyntax.joinRow(headers.toList(), options))
            out.write(options.lineTerminator)

            data.forEach { row ->
                val values = headers.map { header -> render(row[header], options) }
                out.write(CsvSyntax.joinRow(values, options))
                out.write(options.lineTerminator)
            }
        }

        log.debug("CSV 파일이 생성되었습니다: {}", outputPath)
    }

    private fun render(value: Any?, options: FormattingOptions): String =
        when (value) {
            null -> options.nullValue
            is Map<*, *>, is Collection<*> -> objectMapper.writeValueAsString(value)
            else -> value.toString()
        }

    private companion object {
        private const val BOM = "﻿"
        private val log = LoggerFactory.getLogger(JsonToCsvDataWriter::class.java)
    }
}
