package com.jsoncsvbridge.spring

import com.jsoncsvbridge.csv.FlattenMode
import com.jsoncsvbridge.csv.FormattingOptions
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * `application.yml` 에서 CSV 출력 포맷을 설정한다.
 *
 * ```yaml
 * json-csv-bridge:
 *   delimiter: ";"
 *   line-terminator: "\r\n"
 *   null-value: "N/A"
 *   byte-order-mark: true
 *   flatten: bracket
 * ```
 *
 * 항목별 의미는 [FormattingOptions] 와 같다.
 */
@ConfigurationProperties(prefix = JsonCsvBridgeProperties.PREFIX)
data class JsonCsvBridgeProperties(
    val delimiter: Char = ',',
    val encoding: String = "UTF-8",
    val lineTerminator: String = "\n",
    val quoteChar: Char = '"',
    val nullValue: String = "",
    val byteOrderMark: Boolean = false,
    val flatten: FlattenMode = FlattenMode.NONE,
) {
    fun toFormattingOptions(): FormattingOptions =
        FormattingOptions(
            delimiter = delimiter,
            encoding = encoding,
            lineTerminator = lineTerminator,
            quoteChar = quoteChar,
            nullValue = nullValue,
            byteOrderMark = byteOrderMark,
            flatten = flatten,
        )

    companion object {
        const val PREFIX = "json-csv-bridge"
    }
}
