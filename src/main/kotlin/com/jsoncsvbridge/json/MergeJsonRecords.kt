package com.jsoncsvbridge.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jsoncsvbridge.csv.CsvRecodes
import com.jsoncsvbridge.csv.CsvValidator
import java.io.Reader

/**
 * 여러 JSON 문서를 하나의 레코드 목록으로 합친다.
 *
 * 각 입력은 JSON 배열이어도 되고 단일 JSON 객체여도 된다.
 */
class MergeJsonRecords(
    private val validator: CsvValidator = JsonDataValidator(),
) : CsvRecodes {
    private val objectMapper = jacksonObjectMapper()

    override fun combine(data: List<String>): String = objectMapper.writeValueAsString(combineRecords(data))

    /** 합친 결과를 레코드 목록 그대로 돌려준다. 직렬화 후 재파싱하는 왕복이 없다. */
    fun combineRecords(data: List<String>): List<Map<String, Any?>> = data.flatMap { validator.validate(it) }

    /** [Reader] 하나를 레코드 목록으로 읽는다. */
    fun combineRecords(input: Reader): List<Map<String, Any?>> = validator.validate(input)
}
