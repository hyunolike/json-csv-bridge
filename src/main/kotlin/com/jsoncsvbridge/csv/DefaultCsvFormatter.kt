package com.jsoncsvbridge.csv

/**
 * 이미 만들어진 CSV 문서의 포맷(구분자, 행 구분자)을 바꾼다.
 *
 * 인용된 필드 안의 구분자와 개행은 데이터로 보고 건드리지 않는다.
 *
 * @param sourceOptions 입력 CSV 가 따르고 있는 포맷. 기본값은 표준 CSV.
 */
class DefaultCsvFormatter(
    private val sourceOptions: FormattingOptions = FormattingOptions(),
) : CsvFormatter {
    override fun format(data: String, options: FormattingOptions): String =
        CsvSyntax
            .parseRows(data, sourceOptions)
            .joinToString(options.lineTerminator) { row -> CsvSyntax.joinRow(row, options) }
}
