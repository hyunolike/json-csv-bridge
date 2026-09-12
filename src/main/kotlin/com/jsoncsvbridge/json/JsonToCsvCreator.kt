package com.jsoncsvbridge.json

import com.jsoncsvbridge.csv.CsvCreator
import com.jsoncsvbridge.csv.CsvValidator
import com.jsoncsvbridge.csv.CsvWriter
import com.jsoncsvbridge.filter.DataFilter
import com.jsoncsvbridge.filter.FilterCriteria

/**
 * JSON 문자열 하나를 CSV 파일로 변환한다.
 *
 * @throws IllegalArgumentException 입력이 올바른 JSON 배열/객체가 아닐 때
 */
class JsonToCsvCreator(
    private val filter: DataFilter,
    private val validator: CsvValidator,
    private val writer: CsvWriter,
) : CsvCreator {
    override fun createCsv(data: String, outputPath: String) = createCsv(data, outputPath, FilterCriteria())

    /** [criteria] 를 만족하는 레코드만 골라 변환한다. */
    fun createCsv(data: String, outputPath: String, criteria: FilterCriteria) {
        val records = filter.filterRecords(validator.validate(data), criteria)
        writer.write(records, outputPath)
    }
}
