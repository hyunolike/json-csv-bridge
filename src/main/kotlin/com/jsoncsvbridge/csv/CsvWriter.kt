package com.jsoncsvbridge.csv

import java.io.OutputStream
import java.io.Writer

/**
 * 레코드 목록을 CSV 로 기록한다.
 *
 * 호출자가 넘긴 [Writer] 와 [OutputStream] 은 닫지 않고 flush 만 한다. 자원의 수명은 넘긴 쪽이 쥔다.
 */
interface CsvWriter {
    /**
     * @param data 각 레코드가 열 이름 → 값 인 목록. 비어 있으면 빈 파일을 만든다.
     * @param outputPath 생성할 CSV 파일 경로. 상위 디렉터리는 자동으로 만든다.
     */
    fun write(data: List<Map<String, Any?>>, outputPath: String)

    /** 이미 열려 있는 [Writer] 에 기록한다. 인코딩은 [Writer] 가 가진 것을 따른다. */
    fun write(data: List<Map<String, Any?>>, out: Writer)

    /** [OutputStream] 에 기록한다. 인코딩은 설정된 [FormattingOptions.encoding] 을 따른다. */
    fun write(data: List<Map<String, Any?>>, out: OutputStream)

    /** 파일을 거치지 않고 CSV 문자열로 돌려준다. */
    fun writeToString(data: List<Map<String, Any?>>): String
}
