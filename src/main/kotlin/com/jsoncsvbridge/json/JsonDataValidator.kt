@file:Suppress("ktlint:standard:filename")

package com.jsoncsvbridge.json

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.jsoncsvbridge.csv.CsvValidator
import java.io.Reader

/**
 * JSON 문자열을 CSV 로 쓸 수 있는 레코드 목록으로 검증/정제한다.
 *
 * - JSON 배열과 단일 JSON 객체를 모두 받는다. 객체 하나는 레코드 1건으로 취급한다.
 * - 키 순서는 입력 JSON 에 나온 순서를 그대로 유지한다.
 * - 소수는 [java.math.BigDecimal] 로 읽어 `1.10` 같은 표기를 잃지 않는다.
 */
class JsonDataValidator : CsvValidator {
    private val objectMapper =
        jacksonMapperBuilder()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            // Jackson 2.15+ 는 기본으로 BigDecimal 의 끝자리 0 을 떼어낸다. 금액처럼 표기가
            // 곧 의미인 값이 1.10 → 1.1 로 바뀌지 않도록 끈다.
            .disable(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)
            .build()

    override fun validate(jsonString: String): List<Map<String, Any?>> =
        toRecords(read { objectMapper.readTree(jsonString) })

    override fun validate(reader: Reader): List<Map<String, Any?>> =
        toRecords(read { objectMapper.readTree(reader) })

    private inline fun read(parse: () -> JsonNode): JsonNode =
        try {
            parse()
        } catch (e: JacksonException) {
            throw IllegalArgumentException("Invalid JSON format: ${e.originalMessage}", e)
        }

    private fun toRecords(root: JsonNode): List<Map<String, Any?>> =
        when (root) {
            is ArrayNode -> root.mapIndexed { index, element -> toRecord(element, index) }
            is ObjectNode -> listOf(toObject(root))
            else -> throw IllegalArgumentException(
                "Invalid JSON format: expected a JSON array or object but was ${root.nodeType}",
            )
        }

    private fun toRecord(node: JsonNode, index: Int): Map<String, Any?> =
        when (node) {
            is ObjectNode -> toObject(node)
            else -> throw IllegalArgumentException(
                "Invalid JSON format: element at index $index must be a JSON object but was ${node.nodeType}",
            )
        }

    private fun toObject(node: ObjectNode): Map<String, Any?> =
        // LinkedHashMap 이라 JSON 에 적힌 키 순서가 그대로 보존된다.
        linkedMapOf<String, Any?>().apply {
            node.fields().forEach { (key, value) -> put(key, toValue(value)) }
        }

    private fun toValue(node: JsonNode): Any? =
        when (node) {
            is ObjectNode -> toObject(node)
            is ArrayNode -> node.map { toValue(it) }
            else -> if (node.isNull) null else objectMapper.treeToValue(node, Any::class.java)
        }
}
