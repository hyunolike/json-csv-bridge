package com.jsoncsvbridge.csv

import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultCsvFormatterTest {
    private val formatter = DefaultCsvFormatter()

    @Test
    fun `구분자를 바꿔도 인용된 필드 안의 쉼표는 데이터로 남는다`() {
        val csv = "name,note\nJohn,\"Seoul, Korea\""

        val result = formatter.format(csv, FormattingOptions(delimiter = ';'))

        assertEquals("name;note\nJohn;Seoul, Korea", result)
    }

    @Test
    fun `행 구분자를 바꾼다`() {
        val result = formatter.format("a,b\n1,2", FormattingOptions(lineTerminator = "\r\n"))

        assertEquals("a,b\r\n1,2", result)
    }

    @Test
    fun `인용된 필드 안의 개행은 보존한다`() {
        val csv = "a\n\"line1\nline2\""

        val result = formatter.format(csv, FormattingOptions(delimiter = ';'))

        assertEquals("a\n\"line1\nline2\"", result)
    }

    @Test
    fun `새 구분자가 값에 들어 있으면 다시 인용한다`() {
        val result = formatter.format("a,b\nx;y,2", FormattingOptions(delimiter = ';'))

        assertEquals("a;b\n\"x;y\";2", result)
    }
}
