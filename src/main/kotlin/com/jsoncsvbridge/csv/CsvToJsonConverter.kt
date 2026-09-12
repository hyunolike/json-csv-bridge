package com.jsoncsvbridge.csv

import java.io.Reader
import java.io.Writer

/**
 * CSV 를 JSON 으로 되돌린다. [CsvCreator] 의 반대 방향이다.
 *
 * 호출자가 넘긴 [Reader] / [Writer] 는 닫지 않는다. 자원의 수명은 넘긴 쪽이 쥔다.
 *
 * @throws IllegalArgumentException CSV 의 열 개수가 헤더와 맞지 않을 때
 */
interface CsvToJsonConverter {
    /** CSV 문자열을 JSON 배열 문자열로 바꾼다. */
    fun toJsonString(csv: String): String

    /** CSV 문자열을 레코드 목록으로 읽는다. JSON 문자열을 거치지 않는다. */
    fun toRecords(csv: String): List<Map<String, Any?>>

    /** [Reader] 에서 CSV 를 읽어 [Writer] 로 JSON 을 쓴다. */
    fun convert(csv: Reader, out: Writer)

    /** CSV 파일을 읽어 JSON 파일로 쓴다. 인코딩은 [FormattingOptions.encoding] 을 따른다. */
    fun convertFile(csvPath: String, jsonOutputPath: String)
}
