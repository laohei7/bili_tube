package com.laohei.bili_tube.component.text

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.Pink

private val EmotePattern = "\\[.*?]".toRegex()

private val UrlPattern =
    """(https?://|www\.)[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}([-a-zA-Z0-9()@:%_+.~#?&/=!;]*)""".toRegex()

private val KeywordPattern = "<em class=\"keyword\">(.*?)</em>".toRegex()

private const val DEFAULT_MINIMUM_TEXT_LINE = 6

@Composable
fun RichText(
    modifier: Modifier = Modifier,
    enabledExpanded: Boolean = true,
    text: String,
    emote: Map<String, String>,
    minLines: Int = 1,
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
    linkTextColor: Color = Color.Blue,
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
    val allMatches by remember {
        derivedStateOf {
            val emotes = EmotePattern.findAll(displayText)
            val urls = UrlPattern.findAll(displayText)
//            val keywords = KeywordPattern.findAll(displayText)
            (emotes + urls).sortedBy { it.range.first }
        }
    }
    val inlineContentMap = remember { mutableMapOf<String, InlineTextContent>() }

    Text(
        text = buildAnnotatedString {
            var currentIndex = 0
            for (matchResult in allMatches) {
                append(displayText.substring(currentIndex, matchResult.range.first))
                when {
//                    KeywordPattern.matches(matchResult.value) -> {
//                        val keyword = matchResult.groupValues[0]
//                        withStyle(
//                            SpanStyle(
//                                color = Pink,
//                                fontWeight = FontWeight.Bold
//                            )
//                        ) {
//                            append(keyword)
//                        }
//                    }

                    EmotePattern.matches(matchResult.value) -> {
                        val tag = matchResult.groupValues[0]
                        val placeholderId = "tag-${matchResult.range.first}"
                        appendInlineContent(id = placeholderId, alternateText = tag)
                        inlineContentMap[placeholderId] = InlineTextContent(
                            Placeholder(
                                width = 20.sp,
                                height = 20.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                            )
                        ) {
                            val emoteRequest = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(emote[tag])
                                    .crossfade(false)
                                    .placeholder(R.drawable.icon_loading_1_1)
                                    .error(R.drawable.icon_loading_1_1)
                                    .build()
                            )
                            Image(
                                painter = emoteRequest,
                                contentDescription = tag,
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }
                    // Url
                    else -> {
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = matchResult.value
                        )
                        withStyle(
                            style = SpanStyle(
                                color = linkTextColor,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("🔗网页链接")
                        }
                        pop()
                    }
                }
                currentIndex = matchResult.range.last + 1
            }
            append(displayText.substring(currentIndex))
            if (enabledExpanded && isOverflow) {
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
        inlineContent = inlineContentMap,
        modifier = modifier,
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
        minLines = minLines,
        maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { layoutResult ->
            when {
                enabledExpanded && layoutResult.lineCount >= collapsedMaxLine
                        && cutText == null -> {
                    isOverflow = true
                    val lastVisibleLine = collapsedMaxLine - 1
                    val endIndex = (layoutResult.getLineEnd(lastVisibleLine, visibleEnd = true) - 10)
                        .coerceAtLeast(0)
                    cutText = text.substring(0, endIndex).trimEnd()
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun RichTextPreview() {
    RichText(text = "上上下下花了1500越玩越大[笑哭]", emote = emptyMap())
}

@Preview(showBackground = true)
@Composable
private fun ExpandedRichTextPreview() {
    RichText(
        text = """
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
    """.trimIndent(), emote = emptyMap()
    )
}