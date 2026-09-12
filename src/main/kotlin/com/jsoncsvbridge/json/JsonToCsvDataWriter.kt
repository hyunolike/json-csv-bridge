package com.jsoncsvbridge.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jsoncsvbridge.csv.CsvSyntax
import com.jsoncsvbridge.csv.CsvWriter
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.csv.RecordFlattener
import org.slf4j.LoggerFactory
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.io.Writer

/**
 * 레코드 목록을 CSV 로 기록한다.
 *
 * - 헤더는 모든 레코드의 키를 처음 등장한 순서대로 모은 합집합이다.
 * - 헤더와 데이터 모두 [CsvSyntax] 규칙으로 인용하므로 키에 구분자가 있어도 열이 어긋나지 않는다.
 * - [FormattingOptions.flatten] 이 켜져 있으면 중첩 객체/배열을 열로 펼치고,
 *   꺼져 있으면 JSON 문자열 한 칸에 담는다.
 *
 * @param options 기본 출력 포맷. 호출마다 바꾸려면 [options] 를 받는 오버로드를 쓴다.
 */
class JsonToCsvDataWriter(
    private val options: FormattingOptions = FormattingOptions(),
) : CsvWriter {
    private val objectMapper = jacksonObjectMapper()

    override fun write(data: List<Map<String, Any?>>, outputPath: String) = write(data, outputPath, options)

    override fun write(data: List<Map<String, Any?>>, out: Writer) = write(data, out, options)

    override fun write(data: List<Map<String, Any?>>, out: OutputStream) = write(data, out, options)

    override fun writeToString(data: List<Map<String, Any?>>): String = writeToString(data, options)

    /** [options] 대신 이번 호출에만 적용할 포맷을 지정해 파일로 기록한다. */
    fun write(data: List<Map<String, Any?>>, outputPath: String, options: FormattingOptions) {
        val file = File(outputPath)
        file.absoluteFile.parentFile?.mkdirs()

        file.outputStream().bufferedWriter(charset(options.encoding)).use { out -> render(data, out, options) }
        log.debug("CSV 파일이 생성되었습니다: {}", outputPath)
    }

    /** 이미 열려 있는 [Writer] 에 기록한다. 이 메서드는 [out] 을 닫지 않는다. */
    fun write(data: List<Map<String, Any?>>, out: Writer, options: FormattingOptions) {
        render(data, out, options)
        out.flush()
    }

    /** [OutputStream] 에 기록한다. 이 메서드는 [out] 을 닫지 않는다. */
    fun write(data: List<Map<String, Any?>>, out: OutputStream, options: FormattingOptions) {
        // 스트림을 닫으면 호출자의 자원까지 닫히므로 flush 까지만 한다.
        val writer = OutputStreamWriter(out, charset(options.encoding))
        render(data, writer, options)
        writer.flush()
    }

    /** 파일을 거치지 않고 CSV 문자열로 돌려준다. */
    fun writeToString(data: List<Map<String, Any?>>, options: FormattingOptions): String =
        StringWriter().also { render(data, it, options) }.toString()

    private fun render(data: List<Map<String, Any?>>, out: Writer, options: FormattingOptions) {
        val rows = data.map { RecordFlattener.flatten(it, options.flatten) }
        val headers = rows.flatMapTo(LinkedHashSet()) { it.keys }

        if (headers.isEmpty()) {
            // 빈 입력이어도 출력은 만든다. 호출자가 존재 여부로 분기하지 않아도 되게.
            log.debug("변환할 데이터가 없어 빈 CSV 를 생성했습니다.")
            return
        }

        if (options.byteOrderMark) out.write(BOM)

        out.write(CsvSyntax.joinRow(headers.toList(), options))
        out.write(options.lineTerminator)

        rows.forEach { row ->
            out.write(CsvSyntax.joinRow(headers.map { header -> render(row[header], options) }, options))
            out.write(options.lineTerminator)
        }
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
