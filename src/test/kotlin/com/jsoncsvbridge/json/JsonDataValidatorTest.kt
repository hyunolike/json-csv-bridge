package com.jsoncsvbridge.json

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonDataValidatorTest {
    private val validator = JsonDataValidator()

    @Test
    fun `여러 타입이 섞인 배열을 검증한다`() {
        val jsonInput =
            """
            [
                {"id": 1, "name": "John Doe", "email": "john@example.com", "age": 30},
                {"id": 2, "name": "Jane Smith", "email": null, "city": "New York"},
                {"id": 3, "name": "Bob Johnson", "age": null, "isStudent": true},
                {"id": 4, "name": "Alice Brown", "score": 95, "subjects": ["Math", "Science"]}
            ]
            """.trimIndent()

        val result = validator.validate(jsonInput)

        assertEquals(4, result.size)
        assertEquals(1, result[0]["id"])
        assertEquals("John Doe", result[0]["name"])
        assertEquals("john@example.com", result[0]["email"])
        assertEquals(30, result[0]["age"])

        assertNull(result[1]["email"])
        assertEquals("New York", result[1]["city"])

        assertEquals(true, result[2]["isStudent"])
        assertNull(result[2]["age"])

        assertEquals(95, result[3]["score"])
        assertEquals(listOf("Math", "Science"), result[3]["subjects"])
    }

    @Test
    fun `키 순서는 입력 JSON 순서를 유지한다`() {
        val result = validator.validate("""[{"name": "A", "age": 1, "city": "X"}]""")

        assertEquals(listOf("name", "age", "city"), result[0].keys.toList())
    }

    @Test
    fun `단일 JSON 객체도 레코드 한 건으로 받는다`() {
        val result = validator.validate("""{"name": "A", "age": 1}""")

        assertEquals(1, result.size)
        assertEquals("A", result[0]["name"])
    }

    @Test
    fun `중첩 객체는 Map 으로 정제한다`() {
        val result = validator.validate("""[{"addr": {"city": "Seoul", "zip": "06236"}}]""")

        assertEquals(mapOf("city" to "Seoul", "zip" to "06236"), result[0]["addr"])
    }

    @Test
    fun `소수와 큰 정수의 표기를 잃지 않는다`() {
        val result = validator.validate("""[{"dec": 1.10, "big": 12345678901234567890}]""")

        assertEquals(BigDecimal("1.10"), result[0]["dec"])
        assertEquals(BigInteger("12345678901234567890"), result[0]["big"])
    }

    @Test
    fun `잘못된 JSON 은 IllegalArgumentException 을 던진다`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                validator.validate("""{ "This is not valid JSON" }""")
            }

        assertTrue(error.message!!.startsWith("Invalid JSON format:"))
    }

    @Test
    fun `배열 원소가 객체가 아니면 위치를 알려주며 실패한다`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                validator.validate("""[{"a": 1}, "not an object"]""")
            }

        assertTrue(error.message!!.contains("index 1"), "실제 메시지: ${error.message}")
    }

    @Test
    fun `최상위가 배열도 객체도 아니면 실패한다`() {
        assertFailsWith<IllegalArgumentException> { validator.validate("\"just a string\"") }
    }
}
