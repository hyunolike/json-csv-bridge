package com.jsoncsvbridge.factory

import com.jsoncsvbridge.csv.CsvCreator
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.csv.MergeCsvCreator
import com.jsoncsvbridge.json.JsonDataFilter
import com.jsoncsvbridge.json.JsonDataValidator
import com.jsoncsvbridge.json.JsonToCsvCreator
import com.jsoncsvbridge.json.JsonToCsvDataWriter
import com.jsoncsvbridge.json.MergeJsonRecords
import com.jsoncsvbridge.json.MergeJsonToCsvCreator
import org.springframework.stereotype.Component

/**
 * 변환기 생성 진입점.
 *
 * Spring 없이도 [generateCsv] / [generateMergeCsv] 정적 메서드로 바로 쓸 수 있다.
 * `@Component` 는 Spring 을 쓰는 프로젝트에서만 의미가 있으며, 라이브러리가 Spring 을
 * 런타임 의존성으로 끌고 오지는 않는다.
 */
@Component
class DefaultCsvCreatorFactory(
    private val options: FormattingOptions,
) : CsvCreatorFactory {
    constructor() : this(FormattingOptions())

    override fun createCsvCreator(type: String): CsvCreator {
        val writer = JsonToCsvDataWriter(options)
        return when (type.lowercase()) {
            JSON -> JsonToCsvCreator(JsonDataFilter(), JsonDataValidator(), writer)
            MERGE_JSON -> MergeJsonToCsvCreator(MergeJsonRecords(), writer)
            else -> throw IllegalArgumentException("Unknown type: '$type'. Supported types: json, mergeJson")
        }
    }

    companion object {
        private const val JSON = "json"
        private const val MERGE_JSON = "mergejson"

        /** JSON → CSV 변환기를 만든다. [type] 은 `json` 또는 `mergeJson`. */
        @JvmStatic
        @JvmOverloads
        fun generateCsv(type: String, options: FormattingOptions = FormattingOptions()): CsvCreator =
            DefaultCsvCreatorFactory(options).createCsvCreator(type)

        /** 여러 JSON 을 한 CSV 로 병합하는 변환기를 만든다. */
        @JvmStatic
        @JvmOverloads
        fun generateMergeCsv(options: FormattingOptions = FormattingOptions()): MergeCsvCreator =
            DefaultCsvCreatorFactory(options).createCsvCreator("mergeJson") as MergeCsvCreator
    }
}
