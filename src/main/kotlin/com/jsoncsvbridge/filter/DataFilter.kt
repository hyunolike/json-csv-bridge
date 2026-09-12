package com.jsoncsvbridge.filter

/**
 * 변환 대상 레코드를 조건에 따라 골라낸다.
 *
 * 조건이 비어 있으면 모든 레코드를 그대로 통과시킨다.
 */
interface DataFilter {
    /** JSON 문자열을 걸러 다시 JSON 배열 문자열로 돌려준다. */
    fun filter(data: String, criteria: FilterCriteria): String

    /** 이미 파싱된 레코드 목록을 거른다. JSON 재파싱이 없어 변환 경로에서 이 쪽을 쓴다. */
    fun filterRecords(records: List<Map<String, Any?>>, criteria: FilterCriteria): List<Map<String, Any?>>
}
