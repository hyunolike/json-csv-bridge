package com.jsoncsvbridge.csv

/** 입력 문서 하나를 CSV 파일로 변환한다. */
interface CsvCreator {
    /**
     * @param data 변환할 원본 문서
     * @param outputPath 생성할 CSV 파일 경로. 상위 디렉터리는 자동으로 만든다.
     * @throws IllegalArgumentException [data] 형식이 올바르지 않을 때
     */
    fun createCsv(data: String, outputPath: String)
}
