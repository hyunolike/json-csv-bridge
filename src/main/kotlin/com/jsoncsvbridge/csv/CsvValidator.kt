package com.jsoncsvbridge.csv

import java.io.Reader

/** 원본 문서를 CSV 로 쓸 수 있는 레코드 목록으로 검증/정제한다. */
interface CsvValidator {
    /** @throws IllegalArgumentException 형식이 올바르지 않을 때 */
    fun validate(jsonString: String): List<Map<String, Any?>>

    /**
     * [Reader] 에서 직접 읽어 검증한다. 입력을 통째로 문자열에 담지 않는다.
     * 이 메서드는 [reader] 를 닫지 않는다.
     *
     * @throws IllegalArgumentException 형식이 올바르지 않을 때
     */
    fun validate(reader: Reader): List<Map<String, Any?>>
}
