package com.laohei.bili_tube.ui.component.text.rich_text

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.util.fastForEach
import androidx.core.net.toUri
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.preview.FakeTextData
import com.laohei.bili_tube.ui.theme.Pink
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val DEFAULT_MINIMUM_TEXT_LINE = 6

@Composable
fun RichText(
    text: String,
    emote: Map<String, String>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    linkTextColor: Color = Color.Blue,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    val (annotatedString, inlineContentMap) = rememberRichTextContent(
        text = text,
        emote = emote,
        style = style,
        linkTextColor = linkTextColor
    )

    Text(
        text = annotatedString,
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
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun ExpandableRichText(
    text: String,
    emote: Map<String, String>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    linkTextColor: Color = Color.Blue,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    collapsedMaxLine: Int = DEFAULT_MINIMUM_TEXT_LINE,
    actionTextColor: Color = Pink,
) {
    val textMeasurer = rememberTextMeasurer()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var cutText by remember { mutableStateOf(text) }
    var actionTextVisible by rememberSaveable { mutableStateOf(true) }

    val actionText = if (expanded) "收起" else "...展开"
    val displayText = if (expanded) text else cutText

    val (annotatedString, inlineContentMap) = rememberRichTextContent(
        text = displayText,
        emote = emote,
        style = style,
        linkTextColor = linkTextColor,
        expandActionText = if (actionTextVisible) actionText else null,
        actionTextColor = actionTextColor,
        onExpandToggle = { expanded = expanded.not() }
    )

    Text(
        text = annotatedString,
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
        maxLines = maxLines,
        overflow = overflow,
        onTextLayout = { layoutResult ->
            if (expanded) {
                return@Text
            }
            if (layoutResult.lineCount <= collapsedMaxLine) {
                actionTextVisible = false
                return@Text
            }
            val lastLineIndex = collapsedMaxLine
            val lastLineWidth =
                layoutResult.getLineRight(lastLineIndex) - layoutResult.getLineLeft(lastLineIndex)
            val actionTextWidth = textMeasurer.measure(actionText).size.width.toFloat()
            val availableWidth = lastLineWidth - actionTextWidth
            var endIndex = layoutResult.getLineStart(lastLineIndex)
            while (endIndex < layoutResult.getLineEnd(lastLineIndex)) {
                val width = textMeasurer.measure(text.substring(0, endIndex)).size.width
                if (width > availableWidth) break
                endIndex++
            }
            cutText = text.substring(startIndex = 0, endIndex = endIndex)
        }
    )
}


@OptIn(ExperimentalUuidApi::class)
@Composable
private fun rememberRichTextContent(
    text: String,
    emote: Map<String, String>,
    style: TextStyle,
    linkTextColor: Color,
    expandActionText: String? = null,
    actionTextColor: Color = Color.Red,
    onExpandToggle: () -> Unit = {}
): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val textNodes = remember(text) { parseText(text) }
    val inlineContentMap = remember { mutableMapOf<String, InlineTextContent>() }

    val annotatedString = remember(textNodes, expandActionText) {
        buildAnnotatedString {
            textNodes.fastForEach { node ->
                when (node) {
                    is TextNode.Emote -> {
                        val placeholderId = node.tag ?: Uuid.random().toString()
                        appendInlineContent(
                            id = placeholderId,
                            alternateText = node.code
                        )
                        inlineContentMap[placeholderId] = InlineTextContent(
                            Placeholder(
                                width = style.fontSize,
                                height = style.fontSize,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                            )
                        ) {
                            val emoteRequest = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(emote[node.code])
                                    .crossfade(false)
                                    .placeholder(R.drawable.icon_loading_1_1)
                                    .error(R.drawable.icon_loading_1_1)
                                    .build()
                            )
                            Image(
                                painter = emoteRequest,
                                contentDescription = node.tag,
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }

                    is TextNode.PlainText -> append(node.text)

                    is TextNode.Url -> {
                        val displayText = runCatching { node.url.toUri().host }.getOrNull()
                        pushStringAnnotation(
                            tag = node.tag ?: Uuid.random().toString(),
                            annotation = displayText ?: node.url
                        )
                        withLink(
                            LinkAnnotation.Url(
                                url = node.url
                            )
                        ) {
                            withStyle(
                                style = SpanStyle(
                                    color = linkTextColor,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) { append(node.url) }
                        }
                        pop()
                    }
                }
            }

            expandActionText?.let { action ->
                append(" ")
                pushStringAnnotation(tag = "expand_toggle", annotation = action)
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "expand_toggle",
                        linkInteractionListener = { onExpandToggle() }
                    )
                ) {
                    withStyle(
                        style = SpanStyle(
                            color = actionTextColor,
                            fontWeight = FontWeight.Medium
                        )
                    ) { append(action) }
                }
                pop()
            }
        }
    }

    return annotatedString to inlineContentMap
}

@Preview(showBackground = true)
@Composable
private fun RichTextPreview(
    @PreviewParameter(FakeTextData::class) text: String
) {
    RichText(text = text, emote = emptyMap())
}

@Preview(showBackground = true)
@Composable
private fun ExpandableRichTextPreview(
    @PreviewParameter(FakeTextData::class) text: String
) {
    ExpandableRichText(text = text, emote = emptyMap())
}