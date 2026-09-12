package com.jsoncsvbridge.filter

/**
 * 레코드 선택 조건. 모든 [conditions] 를 만족하는 레코드만 남는다(AND 결합).
 *
 * 비어 있으면 필터링을 하지 않는다.
 */
data class FilterCriteria(
    val conditions: List<Condition> = emptyList(),
) {
    val isEmpty: Boolean get() = conditions.isEmpty()

    companion object {
        /** `FilterCriteria.of(Condition("city", "Seoul"))` 형태로 짧게 만든다. */
        @JvmStatic
        fun of(vararg conditions: Condition): FilterCriteria = FilterCriteria(conditions.toList())
    }
}
