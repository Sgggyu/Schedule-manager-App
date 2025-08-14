package com.example.schedulemanager.logic.model

class ChineseConverter {
}

fun toChinese(num: Int): String {
    return when (num) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        0 -> "日"
        else -> ""
    }
}