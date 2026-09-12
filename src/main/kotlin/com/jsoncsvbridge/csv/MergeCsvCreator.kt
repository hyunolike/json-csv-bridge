package com.jsoncsvbridge.csv

import java.io.Writer

/** 여러 입력 문서를 한 CSV 로 병합하는 변환기. */
interface MergeCsvCreator : CsvCreator {
    /** 두 문서를 병합해 파일로 쓴다. */
    fun createMergedCsv(data1: String, data2: String, outputPath: String)

    /** 문서 개수에 제한 없이 병합해 파일로 쓴다. */
    fun createMergedCsv(data: List<String>, outputPath: String)

    /** 병합 결과를 [Writer] 로 흘려보낸다. */
    fun createMergedCsv(data: List<String>, out: Writer)

    /** 병합 결과를 CSV 문자열로 돌려준다. */
    fun mergeToString(data: List<String>): String
}
