package com.jsoncsvbridge.json

import com.jsoncsvbridge.csv.CsvCreator
import com.jsoncsvbridge.csv.CsvValidator
import com.jsoncsvbridge.csv.CsvWriter
import com.jsoncsvbridge.filter.DataFilter
import com.jsoncsvbridge.filter.FilterCriteria
import java.io.OutputStream
import java.io.Reader
import java.io.Writer

/**
 * JSON 문서 하나를 CSV 로 변환한다.
 *
 * 모든 오버로드는 [FilterCriteria] 를 받는 짝이 있어, 조건에 맞는 레코드만 어느 출력으로든 보낼 수 있다.
 *
 * @throws IllegalArgumentException 입력이 올바른 JSON 배열/객체가 아닐 때
 */
class JsonToCsvCreator(
    private val filter: DataFilter,
    private val validator: CsvValidator,
    private val writer: CsvWriter,
) : CsvCreator {
    override fun createCsv(data: String, outputPath: String) = createCsv(data, outputPath, FilterCriteria())

    override fun createCsv(data: String, out: Writer) = createCsv(data, out, FilterCriteria())

    override fun createCsv(data: String, out: OutputStream) = createCsv(data, out, FilterCriteria())

    override fun createCsv(input: Reader, outputPath: String) = createCsv(input, outputPath, FilterCriteria())

    override fun createCsv(input: Reader, out: Writer) = createCsv(input, out, FilterCriteria())

    override fun convertToString(data: String): String = convertToString(data, FilterCriteria())

    /** [criteria] 를 만족하는 레코드만 골라 파일로 변환한다. */
    fun createCsv(data: String, outputPath: String, criteria: FilterCriteria) =
        writer.write(records(data, criteria), outputPath)

    /** [criteria] 를 만족하는 레코드만 골라 [Writer] 로 흘려보낸다. */
    fun createCsv(data: String, out: Writer, criteria: FilterCriteria) =
        writer.write(records(data, criteria), out)

    /** [criteria] 를 만족하는 레코드만 골라 [OutputStream] 으로 흘려보낸다. */
    fun createCsv(data: String, out: OutputStream, criteria: FilterCriteria) =
        writer.write(records(data, criteria), out)

    /** [Reader] 에서 읽어 [criteria] 를 만족하는 레코드만 파일로 변환한다. */
    fun createCsv(input: Reader, outputPath: String, criteria: FilterCriteria) =
        writer.write(records(input, criteria), outputPath)

    /** [Reader] 에서 읽어 [criteria] 를 만족하는 레코드만 [Writer] 로 흘려보낸다. */
    fun createCsv(input: Reader, out: Writer, criteria: FilterCriteria) =
        writer.write(records(input, criteria), out)

    /** [criteria] 를 만족하는 레코드만 골라 CSV 문자열로 돌려준다. */
    fun convertToString(data: String, criteria: FilterCriteria): String =
        writer.writeToString(records(data, criteria))

    private fun records(data: String, criteria: FilterCriteria) =
        filter.filterRecords(validator.validate(data), criteria)

    private fun records(input: Reader, criteria: FilterCriteria) =
        filter.filterRecords(validator.validate(input), criteria)
}
