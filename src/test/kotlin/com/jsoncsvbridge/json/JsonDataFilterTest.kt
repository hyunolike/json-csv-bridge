package com.jsoncsvbridge.json

import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.FilterCriteria
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonDataFilterTest {
    private val filter = JsonDataFilter()

    private val records =
        listOf(
            mapOf<String, Any?>("name" to "John", "city" to "Seoul", "age" to 30),
            mapOf<String, Any?>("name" to "Alice", "city" to "London", "age" to 30),
            mapOf<String, Any?>("name" to "Bob", "city" to "Seoul", "age" to 25),
        )

    @Test
    fun `조건이 없으면 모든 레코드를 그대로 통과시킨다`() {
        assertEquals(records, filter.filterRecords(records, FilterCriteria()))
    }

    @Test
    fun `조건에 맞는 레코드는 필드를 잃지 않는다`() {
        val result = filter.filterRecords(records, FilterCriteria.of(Condition("city", "Seoul")))

        assertEquals(2, result.size)
        assertEquals(listOf("name", "city", "age"), result[0].keys.toList())
        assertEquals("John", result[0]["name"])
    }

    @Test
    fun `조건이 여러 개면 모두 만족해야 남는다`() {
        val criteria = FilterCriteria.of(Condition("city", "Seoul"), Condition("age", "30"))

        val result = filter.filterRecords(records, criteria)

        assertEquals(listOf("John"), result.map { it["name"] })
    }

    @Test
    fun `없는 필드를 조건으로 주면 아무것도 남지 않는다`() {
        assertEquals(emptyList(), filter.filterRecords(records, FilterCriteria.of(Condition("nope", "x"))))
    }

    @Test
    fun `JSON 문자열을 걸러 JSON 배열 문자열로 돌려준다`() {
        val data = """[{"name": "John", "city": "Seoul"}, {"name": "Alice", "city": "London"}]"""

        val result = filter.filter(data, FilterCriteria.of(Condition("city", "Seoul")))

        assertEquals("""[{"name":"John","city":"Seoul"}]""", result)
    }
}
