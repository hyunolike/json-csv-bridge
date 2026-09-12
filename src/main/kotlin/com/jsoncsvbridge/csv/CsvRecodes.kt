package com.jsoncsvbridge.csv

/** 여러 문서를 하나로 합친다. */
interface CsvRecodes {
    fun combine(data: List<String>): String
}
