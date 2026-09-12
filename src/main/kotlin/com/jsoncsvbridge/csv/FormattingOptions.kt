package com.jsoncsvbridge.csv

/**
 * CSV 출력 포맷 설정.
 *
 * @property delimiter 필드 구분자. 기본값 `,`
 * @property encoding 파일 인코딩. 기본값 `UTF-8`
 * @property lineTerminator 행 구분자. 기본값 `\n` (Excel 호환이 필요하면 `\r\n`)
 * @property quoteChar 인용 문자. 기본값 `"`
 * @property nullValue null 이거나 값이 없는 필드를 표기할 문자열. 기본값은 빈 문자열
 * @property byteOrderMark BOM 기록 여부. Excel 에서 한글이 깨질 때 true 로 설정
 */
data class FormattingOptions @JvmOverloads constructor(
    val delimiter: Char = ',',
    val encoding: String = "UTF-8",
    val lineTerminator: String = "\n",
    val quoteChar: Char = '"',
    val nullValue: String = "",
    val byteOrderMark: Boolean = false,
)
