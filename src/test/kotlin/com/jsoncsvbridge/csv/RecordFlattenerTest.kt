package com.jsoncsvbridge.csv

import com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.Companion.generateCsv
import kotlin.test.Test
import kotlin.test.assertEquals

class RecordFlattenerTest {
    private val record =
        mapOf<String, Any?>(
            "id" to 1,
            "addr" to mapOf("city" to "Seoul", "geo" to mapOf("lat" to 37.5)),
            "tags" to listOf("a", "b"),
        )

    @Test
    fun `NONE 은 원본을 그대로 둔다`() {
        assertEquals(record, RecordFlattener.flatten(record, FlattenMode.NONE))
    }

    @Test
    fun `DOT 은 객체와 배열을 모두 점으로 잇는다`() {
        assertEquals(
            listOf("id", "addr.city", "addr.geo.lat", "tags.0", "tags.1"),
            RecordFlattener.flatten(record, FlattenMode.DOT).keys.toList(),
        )
    }

    @Test
    fun `BRACKET 은 배열 인덱스를 대괄호로 잇는다`() {
        assertEquals(
            listOf("id", "addr.city", "addr.geo.lat", "tags[0]", "tags[1]"),
            RecordFlattener.flatten(record, FlattenMode.BRACKET).keys.toList(),
        )
    }

    @Test
    fun `값을 잃지 않는다`() {
        val flat = RecordFlattener.flatten(record, FlattenMode.BRACKET)

        assertEquals(1, flat["id"])
        assertEquals("Seoul", flat["addr.city"])
        assertEquals(37.5, flat["addr.geo.lat"])
        assertEquals("b", flat["tags[1]"])
    }

    @Test
    fun `빈 객체와 빈 배열은 열이 사라지지 않게 표기를 남긴다`() {
        val flat = RecordFlattener.flatten(mapOf("o" to emptyMap<String, Any?>(), "a" to emptyList<Any?>()), FlattenMode.DOT)

        assertEquals("{}", flat["o"])
        assertEquals("[]", flat["a"])
    }

    @Test
    fun `null 은 그대로 둔다`() {
        assertEquals(mapOf("a" to null), RecordFlattener.flatten(mapOf("a" to null), FlattenMode.DOT))
    }

    @Test
    fun `배열 안의 객체도 끝까지 펼친다`() {
        val flat = RecordFlattener.flatten(mapOf("items" to listOf(mapOf("n" to 1))), FlattenMode.BRACKET)

        assertEquals(mapOf("items[0].n" to 1), flat)
    }

    @Test
    fun `변환기에서 펼친 CSV 를 만든다`() {
        val json = """[{"id": 1, "addr": {"city": "Seoul"}, "tags": ["a", "b"]}]"""

        val csv = generateCsv("json", FormattingOptions(flatten = FlattenMode.BRACKET)).convertToString(json)

        assertEquals("id,addr.city,tags[0],tags[1]\n1,Seoul,a,b\n", csv)
    }

    @Test
    fun `레코드마다 중첩 구조가 달라도 열의 합집합으로 모은다`() {
        val json = """[{"a": {"x": 1}}, {"a": {"y": 2}}]"""

        val csv = generateCsv("json", FormattingOptions(flatten = FlattenMode.DOT)).convertToString(json)

        assertEquals("a.x,a.y\n1,\n,2\n", csv)
    }
}
