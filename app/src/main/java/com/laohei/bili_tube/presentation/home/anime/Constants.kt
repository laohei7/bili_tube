package com.laohei.bili_tube.presentation.home.anime

import androidx.compose.ui.util.fastJoinToString

private val SeasonVersion = listOf(
    "全部" to "-1", "正片" to "1", "电影" to "2", "其他" to "3",
)

private val SpokenLanguageType = listOf(
    "全部" to "-1", "原声" to "1", "中文配音" to "2",
)

private val Area = listOf(
    "全部" to "-1",
    "日本" to "2",
    "美国" to "3",
    "其他" to (1..70).filter { (2..3).contains(it).not() }.fastJoinToString(",")
)

private val IsFinish = listOf(
    "全部" to "-1", "完结" to "1", "连载" to "0",
)

private val Copyright = listOf(
    "全部" to "-1", "独家" to "3", "其他" to (1..4).filter { it != 3 }.fastJoinToString(",")
)

private val SeasonStatus = listOf(
    "全部" to "-1", "免费" to "1", "付费" to "2,6", "大会员" to "4,6",
)

private val SeasonMonth = listOf(
    "全部" to "-1", "1月" to "1", "4月" to "4", "7月" to "7", "10月" to "10",
)

private val StyleId = listOf(
    "全部" to "-1",
    "原创" to "10010",
    "漫画改" to "10011",
    "小说改" to "10012",
    "游戏改" to "10013",
    "特摄" to "10102",
    "布袋戏" to "10015",
    "热血" to "10016",
    "穿越" to "10017",
    "奇幻" to "10018",
    "战斗" to "10020",
    "搞笑" to "10021",
    "日常" to "10022",
    "科幻" to "10023",
    "萌系" to "10024",
    "治愈" to "10025",
    "校园" to "10026",
    "少儿" to "10027",
    "泡面" to "10028",
    "恋爱" to "10029",
    "少女" to "10030",
    "魔法" to "10031",
    "冒险" to "10032",
    "历史" to "10033",
    "架空" to "10034",
    "机战" to "10035",
    "神魔" to "10036",
    "声控" to "10037",
    "运动" to "10038",
    "励志" to "10039",
    "音乐" to "10040",
    "推理" to "10041",
    "社团" to "10042",
    "智斗" to "10043",
    "催泪" to "10044",
    "美食" to "10045",
    "偶像" to "10046",
    "乙女" to "10047",
    "职场" to "10048"
)

val BangumiFilters = buildMap {
    put("类型", SeasonVersion)
    put("配音", SpokenLanguageType)
    put("地区", Area)
    put("状态", IsFinish)
    put("版权", Copyright)
    put("付费", SeasonStatus)
    put("季度", SeasonMonth)
    put("风格", StyleId)
}

val AnimationFilters = buildMap {
    put("类型", SeasonVersion)
    put("状态", IsFinish)
    put("版权", Copyright)
    put("付费", SeasonStatus)
    put("季度", SeasonMonth)
    put("风格", StyleId)
}