package com.jsoncsvbridge.csv

/**
 * RFC 4180 기준의 CSV 필드 인용/해제 규칙.
 *
 * 헤더와 데이터 모두 이 규칙을 거치므로, 키에 구분자가 들어 있어도 열 개수가 어긋나지 않는다.
 */
internal object CsvSyntax {
    /**
     * 필드를 CSV 안전한 형태로 인용한다.
     *
     * 구분자, 인용 문자, CR, LF 가 포함되거나 앞뒤 공백이 있으면 인용하고,
     * 내부의 인용 문자는 두 번 반복해 이스케이프한다.
     */
    fun escape(value: String, options: FormattingOptions): String {
        val quote = options.quoteChar
        val needsQuote =
            value.any { it == options.delimiter || it == quote || it == '\r' || it == '\n' } ||
                value != value.trim()

        return if (needsQuote) {
            "$quote${value.replace(quote.toString(), "$quote$quote")}$quote"
        } else {
            value
        }
    }

    /** 한 행을 구분자로 이어 붙인다. */
    fun joinRow(values: List<String>, options: FormattingOptions): String =
        values.joinToString(options.delimiter.toString()) { escape(it, options) }

    /**
     * CSV 문서를 행/필드 2차원 목록으로 파싱한다. 인용된 필드 안의 구분자와 개행을 보존한다.
     */
    fun parseRows(data: String, options: FormattingOptions): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val row = mutableListOf<String>()
        val field = StringBuilder()
        var quoted = false
        var index = 0

        fun endField() {
            row.add(field.toString())
            field.setLength(0)
        }

        fun endRow() {
            endField()
            rows.add(row.toList())
            row.clear()
        }

        while (index < data.length) {
            val char = data[index]
            when {
                quoted && char == options.quoteChar && data.getOrNull(index + 1) == options.quoteChar -> {
                    field.append(options.quoteChar)
                    index++
                }
                char == options.quoteChar -> quoted = !quoted
                !quoted && char == options.delimiter -> endField()
                !quoted && (char == '\n' || char == '\r') -> {
                    if (char == '\r' && data.getOrNull(index + 1) == '\n') index++
                    endRow()
                }
                else -> field.append(char)
            }
            index++
        }

        if (field.isNotEmpty() || row.isNotEmpty()) endRow()
        return rows
    }
}
