package com.jsoncsvbridge.csv

import java.io.OutputStream
import java.io.Reader
import java.io.Writer

/**
 * 입력 문서를 CSV 로 변환한다.
 *
 * 출력은 파일 경로, [Writer], [OutputStream], 문자열 중에서 고를 수 있다.
 * 호출자가 넘긴 [Reader] / [Writer] / [OutputStream] 은 닫지 않는다. 자원의 수명은 넘긴 쪽이 쥔다.
 *
 * @throws IllegalArgumentException 입력 형식이 올바르지 않을 때
 */
interface CsvCreator {
    /**
     * @param data 변환할 원본 문서
     * @param outputPath 생성할 CSV 파일 경로. 상위 디렉터리는 자동으로 만든다.
     */
    fun createCsv(data: String, outputPath: String)

    /** 이미 열려 있는 [Writer] 로 흘려보낸다. HTTP 응답이나 압축 스트림에 바로 쓸 때. */
    fun createCsv(data: String, out: Writer)

    /** [OutputStream] 으로 흘려보낸다. 인코딩은 설정된 [FormattingOptions.encoding] 을 따른다. */
    fun createCsv(data: String, out: OutputStream)

    /** 큰 입력을 통째로 문자열에 담지 않도록 [Reader] 에서 읽어 파일로 쓴다. */
    fun createCsv(input: Reader, outputPath: String)

    /** [Reader] 에서 읽어 [Writer] 로 흘려보낸다. */
    fun createCsv(input: Reader, out: Writer)

    /** 파일을 거치지 않고 CSV 문자열로 돌려준다. */
    fun convertToString(data: String): String
}
