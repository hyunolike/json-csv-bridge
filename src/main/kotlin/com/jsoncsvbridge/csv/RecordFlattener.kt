package com.jsoncsvbridge.csv

/**
 * 중첩된 레코드를 한 단계짜리 `키 -> 값` 으로 펼친다.
 *
 * 값이 비어 있는 객체/배열은 열이 통째로 사라지지 않도록 `{}` / `[]` 를 그대로 남긴다.
 */
internal object RecordFlattener {
    fun flatten(record: Map<String, Any?>, mode: FlattenMode): Map<String, Any?> {
        if (mode == FlattenMode.NONE) return record

        val flat = linkedMapOf<String, Any?>()
        record.forEach { (key, value) -> put(flat, key, value, mode) }
        return flat
    }

    private fun put(target: MutableMap<String, Any?>, prefix: String, value: Any?, mode: FlattenMode) {
        when {
            value is Map<*, *> && value.isNotEmpty() ->
                value.forEach { (key, nested) -> put(target, "$prefix.$key", nested, mode) }

            value is Collection<*> && value.isNotEmpty() ->
                value.forEachIndexed { index, nested -> put(target, index(prefix, index, mode), nested, mode) }

            value is Map<*, *> -> target[prefix] = "{}"
            value is Collection<*> -> target[prefix] = "[]"
            else -> target[prefix] = value
        }
    }

    private fun index(prefix: String, index: Int, mode: FlattenMode): String =
        if (mode == FlattenMode.BRACKET) "$prefix[$index]" else "$prefix.$index"
}
