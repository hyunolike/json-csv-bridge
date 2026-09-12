package com.jsoncsvbridge.json

import com.jsoncsvbridge.csv.CsvWriter
import com.jsoncsvbridge.csv.MergeCsvCreator
import java.io.OutputStream
import java.io.Reader
import java.io.Writer

/**
 * 여러 JSON 문서를 하나의 CSV 로 병합한다.
 *
 * 헤더는 모든 입력에 등장한 키의 합집합이고, 어떤 레코드에 없는 키는
 * [com.jsoncsvbridge.csv.FormattingOptions.nullValue] 로 채운다.
 */
class MergeJsonToCsvCreator(
    private val records: MergeJsonRecords,
    private val writer: CsvWriter,
) : MergeCsvCreator {
    /** 입력이 하나뿐인 병합. 단일 JSON 변환과 결과가 같다. */
    override fun createCsv(data: String, outputPath: String) = createMergedCsv(listOf(data), outputPath)

    override fun createCsv(data: String, out: Writer) = createMergedCsv(listOf(data), out)

    override fun createCsv(data: String, out: OutputStream) = writer.write(records.combineRecords(listOf(data)), out)

    override fun createCsv(input: Reader, outputPath: String) = writer.write(records.combineRecords(input), outputPath)

    override fun createCsv(input: Reader, out: Writer) = writer.write(records.combineRecords(input), out)

    override fun convertToString(data: String): String = mergeToString(listOf(data))

    override fun createMergedCsv(data1: String, data2: String, outputPath: String) =
        createMergedCsv(listOf(data1, data2), outputPath)

    override fun createMergedCsv(data: List<String>, outputPath: String) =
        writer.write(records.combineRecords(data), outputPath)

    override fun createMergedCsv(data: List<String>, out: Writer) = writer.write(records.combineRecords(data), out)

    override fun mergeToString(data: List<String>): String = writer.writeToString(records.combineRecords(data))
}
