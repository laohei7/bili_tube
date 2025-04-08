package com.laohei.bili_tube.component.text

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.laohei.bili_tube.ui.theme.Pink

private const val DEFAULT_MINIMUM_TEXT_LINE = 6

@Composable
fun ExpandedText(
    modifier: Modifier = Modifier,
    text: String,
    collapsedMaxLine: Int = DEFAULT_MINIMUM_TEXT_LINE,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    actionTextColor: Color = Pink,
    style: TextStyle = LocalTextStyle.current
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isOverflow by rememberSaveable { mutableStateOf(false) }
    var cutText by rememberSaveable { mutableStateOf<String?>(null) }

    val displayText by remember {
        derivedStateOf {
            if (isExpanded || !isOverflow) text else cutText ?: text
        }
    }

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append(displayText)
            if (isOverflow) {
                append(" ")
                pushStringAnnotation(
                    tag = "ACTION",
                    annotation = if (isExpanded) "collapse" else "expand"
                )

                withLink(
                    LinkAnnotation.Clickable(
                        tag = "expand",
                        linkInteractionListener = { isExpanded = !isExpanded }
                    )
                ) {
                    withStyle(SpanStyle(color = actionTextColor)) {
                        append(if (isExpanded) " 收起" else "...展开")
                    }
                }
                pop()
            }
        },
        color = color,
        textAlign = textAlign,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight,
        softWrap = softWrap,
        style = style,
        maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { layoutResult ->
            if (!isExpanded && layoutResult.lineCount >= collapsedMaxLine && cutText == null) {
                isOverflow = true
                val lastVisibleLine = collapsedMaxLine - 1
                val endIndex = layoutResult.getLineEnd(lastVisibleLine, visibleEnd = true) - 10
                cutText = text.substring(0, endIndex).trimEnd()
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ExpandedTextPreview() {
    ExpandedText(
        text = """
        🎯 解释
        Instant.fromEpochMilliseconds(timestamp): 解析毫秒时间戳（KMP 适用）。
        TimeZone.currentSystemDefault(): 使用设备默认时区。
        .toLocalDateTime(): 转换为 LocalDateTime，然后获取 date。
        localDate.year/monthNumber/dayOfMonth: 分别获取年、月、日。
        📌 适用于 KMP 的优势：
        跨平台支持（iOS、Android、JVM、JS）。
        线程安全，无 SimpleDateFormat 的线程问题。
        🏆 结论
        Kotlin Multiplatform（KMP）推荐使用 kotlinx-datetime。
        格式化后输出：yyyy年MM月dd日，可用于 Android、iOS、JVM 等平台。
        比 SimpleDateFormat 更现代化、线程安全、跨平台。
        如果你有其他需求（比如自定义格式、处理 LocalDateTime），告诉我，我可以提供更详细的方案！ 😊
    """.trimIndent()
    )
}
