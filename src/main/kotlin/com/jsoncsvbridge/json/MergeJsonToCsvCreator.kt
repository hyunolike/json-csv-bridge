package com.jsoncsvbridge.json

import com.jsoncsvbridge.csv.CsvWriter
import com.jsoncsvbridge.csv.MergeCsvCreator

/**
 * 여러 JSON 문서를 하나의 CSV 파일로 병합한다.
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

    override fun createMergedCsv(data1: String, data2: String, outputPath: String) =
        createMergedCsv(listOf(data1, data2), outputPath)

    override fun createMergedCsv(data: List<String>, outputPath: String) {
        writer.write(records.combineRecords(data), outputPath)
    }
}
