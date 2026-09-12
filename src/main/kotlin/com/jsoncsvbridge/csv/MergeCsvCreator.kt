package com.jsoncsvbridge.csv

/** 여러 입력 문서를 한 CSV 파일로 병합하는 변환기. */
interface MergeCsvCreator : CsvCreator {
    /** 두 문서를 병합한다. */
    fun createMergedCsv(data1: String, data2: String, outputPath: String)

    /** 문서 개수에 제한 없이 병합한다. */
    fun createMergedCsv(data: List<String>, outputPath: String)
}
