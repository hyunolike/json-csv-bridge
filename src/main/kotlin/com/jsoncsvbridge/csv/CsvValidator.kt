package com.jsoncsvbridge.csv

/** 원본 문서를 CSV 로 쓸 수 있는 레코드 목록으로 검증/정제한다. */
interface CsvValidator {
    /** @throws IllegalArgumentException 형식이 올바르지 않을 때 */
    fun validate(jsonString: String): List<Map<String, Any?>>
}
