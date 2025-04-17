package com.laohei.bili_tube.model

data class BangumiFilterModel(
    val seasonVersion: String = "-1",
    val spokenLanguageType: String = "-1",
    val area: String = "-1",
    val isFinish: String = "-1",
    val copyright: String = "-1",
    val seasonStatus: String = "-1",
    val seasonMonth: String = "-1",
    val styleId: String = "-1",
) {
    fun getValue(key: String): String {
        return when (key) {
            "类型" -> seasonVersion
            "配音" -> spokenLanguageType
            "地区" -> area
            "状态" -> isFinish
            "版权" -> copyright
            "付费" -> seasonStatus
            "季度" -> seasonMonth
            else -> styleId
        }
    }

    fun update(key: String, value: String): BangumiFilterModel {
        return when (key) {
            "类型" -> this.copy(seasonVersion = value)
            "配音" -> this.copy(spokenLanguageType = value)
            "地区" -> this.copy(area = value)
            "状态" -> this.copy(isFinish = value)
            "版权" -> this.copy(copyright = value)
            "付费" -> this.copy(seasonStatus = value)
            "季度" -> this.copy(seasonMonth = value)
            else -> this.copy(styleId = value)
        }
    }
}
