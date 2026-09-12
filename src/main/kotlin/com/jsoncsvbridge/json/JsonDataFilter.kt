package com.jsoncsvbridge.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jsoncsvbridge.csv.CsvValidator
import com.jsoncsvbridge.filter.Condition
import com.jsoncsvbridge.filter.DataFilter
import com.jsoncsvbridge.filter.FilterCriteria

/**
 * 조건에 맞는 레코드만 남기는 필터.
 *
 * 필드 단위가 아니라 레코드 단위로 판단한다. 예를 들어 `Condition("city", "Seoul")` 은
 * `city` 가 `Seoul` 인 레코드를 남기며, 그 레코드의 나머지 필드는 그대로 보존된다.
 */
class JsonDataFilter(
    private val validator: CsvValidator = JsonDataValidator(),
) : DataFilter {
    private val objectMapper = jacksonObjectMapper()

    override fun filter(data: String, criteria: FilterCriteria): String =
        objectMapper.writeValueAsString(filterRecords(validator.validate(data), criteria))

    override fun filterRecords(
        records: List<Map<String, Any?>>,
        criteria: FilterCriteria,
    ): List<Map<String, Any?>> =
        if (criteria.isEmpty) records else records.filter { record -> criteria.conditions.all { record.matches(it) } }

    private fun Map<String, Any?>.matches(condition: Condition): Boolean =
        containsKey(condition.field) && this[condition.field]?.toString() == condition.value
}
