package com.jsoncsvbridge.spring

import com.jsoncsvbridge.csv.CsvCreator
import com.jsoncsvbridge.csv.CsvToJsonConverter
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.csv.MergeCsvCreator
import com.jsoncsvbridge.factory.CsvCreatorFactory
import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory
import com.jsoncsvbridge.json.CsvToJsonCreator
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * 변환기들을 빈으로 등록한다. 애플리케이션은 [CsvCreator] 를 주입받아 바로 쓰면 된다.
 *
 * ```kotlin
 * @Service
 * class ReportService(private val csvCreator: CsvCreator) {
 *     fun export(json: String): String = csvCreator.convertToString(json)
 * }
 * ```
 *
 * 모든 빈에 `@ConditionalOnMissingBean` 이 걸려 있으므로, 같은 타입의 빈을 직접 정의하면
 * 그쪽이 우선한다.
 */
@AutoConfiguration
@ConditionalOnClass(CsvCreator::class)
@EnableConfigurationProperties(JsonCsvBridgeProperties::class)
class JsonCsvBridgeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun jsonCsvBridgeFormattingOptions(properties: JsonCsvBridgeProperties): FormattingOptions =
        properties.toFormattingOptions()

    @Bean
    @ConditionalOnMissingBean
    fun csvCreatorFactory(options: FormattingOptions): CsvCreatorFactory = DefaultCsvCreatorFactory(options)

    /**
     * JSON → CSV 변환기.
     *
     * [MergeCsvCreator] 도 [CsvCreator] 이므로, 타입만으로 주입할 때 이 쪽이 선택되도록 `@Primary` 로 둔다.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun csvCreator(factory: CsvCreatorFactory): CsvCreator = factory.createCsvCreator("json")

    /** 여러 JSON 을 한 CSV 로 병합하는 변환기. */
    @Bean
    @ConditionalOnMissingBean
    fun mergeCsvCreator(factory: CsvCreatorFactory): MergeCsvCreator =
        factory.createCsvCreator("mergeJson") as MergeCsvCreator

    /** 반대 방향. CSV → JSON 변환기. */
    @Bean
    @ConditionalOnMissingBean
    fun csvToJsonConverter(options: FormattingOptions): CsvToJsonConverter = CsvToJsonCreator(options)
}
