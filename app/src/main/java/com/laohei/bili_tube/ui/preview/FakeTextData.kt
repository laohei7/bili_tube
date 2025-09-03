package com.laohei.bili_tube.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class FakeTextData: PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = sequenceOf(
            """
                🎯 解释 [微笑]
                视频地址：https://www.youtube.com/watch?v=gT3DXN41s_0 
                Instant.fromEpochMilliseconds(timestamp): 解析毫秒时间戳（KMP 适用）。
                TimeZone.currentSystemDefault(): 使用设备默认时区。
                .toLocalDateTime(): 转换为 LocalDateTime，然后获取 date。
                localDate.year/monthNumber/dayOfMonth[微笑]:[微笑] 分别获取[微笑]年、月、日。
                📌 适用于 KMP 的优势：
                跨平台支持（iOS、Android、JVM、JS）。
                线程安全，无 SimpleDateFormat 的线程问题。
                🏆 结论
                Kotlin Multiplatform（KMP）推荐使用 kotlinx-datetime。
                格式化后输出：yyyy年MM月dd日，可用于 Android、iOS、JVM 等平台。
                视频地址：https://www.youtube.com/watch?v=gT3DXN41s_0
                比 SimpleDateFormat 更现代化、线程安全、跨平台。
                如果你有其他需求（比如自定义格式、处理 LocalDateTime），告诉我，我可以提供更详细的方案！ 😊
                视频地址：https://www.youtube.com/watch?v=gT3DXN41s_0
            """.trimIndent(),
            """
                上上下下花了1500越玩越大[笑哭]
            """.trimIndent(),
        )
}