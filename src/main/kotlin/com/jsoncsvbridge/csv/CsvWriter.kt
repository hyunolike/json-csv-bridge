package com.jsoncsvbridge.csv

/** 레코드 목록을 CSV 파일로 기록한다. */
interface CsvWriter {
    /**
     * @param data 각 레코드가 열 이름 → 값 인 목록. 비어 있으면 빈 파일을 만든다.
     * @param outputPath 생성할 CSV 파일 경로
     */
    fun write(data: List<Map<String, Any?>>, outputPath: String)
}
