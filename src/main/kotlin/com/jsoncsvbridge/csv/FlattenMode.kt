package com.jsoncsvbridge.csv

/**
 * 중첩된 객체와 배열을 어떻게 열로 펼칠지 정한다.
 *
 * 예) `{"id": 1, "addr": {"city": "Seoul"}, "tags": ["a", "b"]}`
 *
 * | 모드 | 생성되는 열 |
 * | --- | --- |
 * | [NONE] | `id`, `addr`, `tags` (값은 JSON 문자열) |
 * | [DOT] | `id`, `addr.city`, `tags.0`, `tags.1` |
 * | [BRACKET] | `id`, `addr.city`, `tags[0]`, `tags[1]` |
 */
enum class FlattenMode {
    /** 펼치지 않는다. 중첩 값은 JSON 문자열 한 칸에 담긴다. */
    NONE,

    /** 객체도 배열도 점으로 잇는다. (`addr.city`, `tags.0`) */
    DOT,

    /** 객체는 점, 배열 인덱스는 대괄호로 잇는다. (`addr.city`, `tags[0]`) */
    BRACKET,
}
