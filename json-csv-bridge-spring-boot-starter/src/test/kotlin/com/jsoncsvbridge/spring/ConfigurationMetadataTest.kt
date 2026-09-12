package com.jsoncsvbridge.spring

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.DisplayName
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * `spring-configuration-metadata.json` 은 손으로 쓴다.
 * (kapt 의 설정 프로세서가 Kotlin 데이터 클래스의 생성자 프로퍼티를 뽑아내지 못한다.)
 *
 * 손으로 쓰는 이상 코드와 어긋날 수 있으므로, 어긋나면 빌드가 깨지도록 여기서 묶어 둔다.
 */
class ConfigurationMetadataTest {
    private val metadata: Metadata =
        jacksonObjectMapper()
            // defaultValue, sourceType 등 여기서 보지 않는 항목은 흘려보낸다.
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(
                checkNotNull(javaClass.getResourceAsStream(RESOURCE)) { "$RESOURCE 를 찾을 수 없습니다" },
            )

    data class Metadata(val groups: List<Entry>, val properties: List<Entry>)

    data class Entry(val name: String, val type: String? = null, val description: String? = null)

    @Test
    @DisplayName("모든 설정 프로퍼티가 메타데이터에 있다")
    fun documentsEveryProperty() {
        val documented = metadata.properties.map { it.name }.toSet()

        val declared =
            JsonCsvBridgeProperties::class.primaryConstructor!!
                .parameters
                .map { "${JsonCsvBridgeProperties.PREFIX}.${it.name!!.toKebabCase()}" }

        assertEquals(declared.toSet(), documented, "메타데이터와 JsonCsvBridgeProperties 가 어긋났습니다")
    }

    @Test
    @DisplayName("그룹 접두사가 프로퍼티 클래스와 일치한다")
    fun declaresGroupForPrefix() {
        val group = metadata.groups.single()

        assertEquals(JsonCsvBridgeProperties.PREFIX, group.name)
        assertEquals(JsonCsvBridgeProperties::class.qualifiedName, group.type)
    }

    @Test
    @DisplayName("모든 프로퍼티에 설명이 달려 있다")
    fun describesEveryProperty() {
        metadata.properties.forEach { property ->
            assertNotNull(property.description, "${property.name} 에 설명이 없습니다")
            assertTrue(property.description.isNotBlank(), "${property.name} 의 설명이 비어 있습니다")
        }
    }

    private fun String.toKebabCase(): String =
        replace(Regex("([a-z0-9])([A-Z])"), "$1-$2").lowercase()

    private companion object {
        private const val RESOURCE = "/META-INF/spring-configuration-metadata.json"
    }
}
