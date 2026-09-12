package com.jsoncsvbridge.spring

import com.jsoncsvbridge.csv.CsvCreator
import com.jsoncsvbridge.csv.CsvToJsonConverter
import com.jsoncsvbridge.csv.FlattenMode
import com.jsoncsvbridge.csv.FormattingOptions
import com.jsoncsvbridge.csv.MergeCsvCreator
import com.jsoncsvbridge.json.JsonToCsvCreator
import com.jsoncsvbridge.json.MergeJsonToCsvCreator
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.MapPropertySource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * 이 파일만 메서드 이름이 영문이고 설명은 [DisplayName] 으로 단다.
 *
 * `ApplicationContextRunner.run { }` 이 SAM 변환 클래스를 만드는데, 백틱 한글 메서드 안에서
 * 만들어지면 클래스 파일 이름에 한글이 섞인다. 로케일이 C/POSIX 인 환경(도커 기반 CI 등)에서는
 * 그 경로를 쓰지 못해 컴파일이 깨진다.
 */
class JsonCsvBridgeAutoConfigurationTest {
    private val runner =
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JsonCsvBridgeAutoConfiguration::class.java))

    private val json = """[{"name": "John", "city": "Seoul"}]"""

    @Test
    @DisplayName("설정이 없어도 변환기 빈이 등록된다")
    fun registersBeansWithoutConfiguration() {
        runner.run { context ->
            assertTrue(context.getBean(CsvCreator::class.java) is JsonToCsvCreator)
            assertTrue(context.getBean(MergeCsvCreator::class.java) is MergeJsonToCsvCreator)
            assertTrue(context.containsBean("csvToJsonConverter"))
        }
    }

    @Test
    @DisplayName("타입만으로 주입하면 JSON 변환기가 선택된다")
    fun prefersJsonCreatorWhenInjectedByType() {
        // MergeCsvCreator 도 CsvCreator 라서, @Primary 가 없으면 주입이 모호해진다.
        runner.run { context ->
            assertTrue(context.getBean(CsvCreator::class.java) is JsonToCsvCreator)
        }
    }

    @Test
    @DisplayName("기본 설정으로 변환하면 표준 CSV 가 나온다")
    fun convertsWithDefaults() {
        runner.run { context ->
            assertEquals("name,city\nJohn,Seoul\n", context.getBean(CsvCreator::class.java).convertToString(json))
        }
    }

    @Test
    @DisplayName("application 설정이 출력 포맷에 반영된다")
    fun appliesApplicationProperties() {
        // withPropertyValues 는 "키=값" 문자열을 파싱하므로 값에 실제 개행을 담을 수 없다.
        // 개행이 들어간 lineTerminator 까지 검증하려면 PropertySource 를 직접 얹어야 한다.
        val properties =
            mapOf<String, Any>(
                "json-csv-bridge.delimiter" to ";",
                "json-csv-bridge.line-terminator" to "\r\n",
                "json-csv-bridge.null-value" to "N/A",
            )

        runner
            .withInitializer { context ->
                context.environment.propertySources.addFirst(MapPropertySource("test", properties))
            }.run { context ->
                val options = context.getBean(FormattingOptions::class.java)
                assertEquals(';', options.delimiter)
                assertEquals("\r\n", options.lineTerminator)
                assertEquals("N/A", options.nullValue)

                assertEquals(
                    "name;city\r\nJohn;Seoul\r\n",
                    context.getBean(CsvCreator::class.java).convertToString(json),
                )
            }
    }

    @Test
    @DisplayName("flatten 설정이 반영된다")
    fun appliesFlattenProperty() {
        runner.withPropertyValues("json-csv-bridge.flatten=bracket").run { context ->
            assertEquals(FlattenMode.BRACKET, context.getBean(FormattingOptions::class.java).flatten)

            val csv =
                context
                    .getBean(CsvCreator::class.java)
                    .convertToString("""[{"id": 1, "addr": {"city": "Seoul"}}]""")

            assertEquals("id,addr.city\n1,Seoul\n", csv)
        }
    }

    @Test
    @DisplayName("CSV → JSON 변환기도 같은 설정을 쓴다")
    fun sharesOptionsWithReverseConverter() {
        runner.withPropertyValues("json-csv-bridge.delimiter=;").run { context ->
            val records = context.getBean(CsvToJsonConverter::class.java).toRecords("a;b\n1;2")

            assertEquals(mapOf("a" to 1, "b" to 2), records[0])
        }
    }

    @Test
    @DisplayName("사용자가 직접 정의한 빈이 우선한다")
    fun backsOffWhenUserDefinesBean() {
        runner.withUserConfiguration(CustomOptionsConfiguration::class.java).run { context ->
            assertSame(CustomOptionsConfiguration.OPTIONS, context.getBean(FormattingOptions::class.java))

            assertEquals("name\tcity\nJohn\tSeoul\n", context.getBean(CsvCreator::class.java).convertToString(json))
        }
    }

    @Configuration(proxyBeanMethods = false)
    class CustomOptionsConfiguration {
        @Bean
        fun formattingOptions(): FormattingOptions = OPTIONS

        companion object {
            val OPTIONS = FormattingOptions(delimiter = '\t')
        }
    }
}
