package com.laohei.bili_tube.ui.component.text.rich_text

private val EmotePattern = "\\[.*?]".toRegex()

private val UrlPattern =
    """(https?://|www\.)[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}([-a-zA-Z0-9()@:%_+.~#?&/=!;]*)"""
        .toRegex()

private val BiliRichTextPatterns = listOf(EmotePattern, UrlPattern)

sealed class TextNode(open val tag: String? = null) {
    data class PlainText(val text: String, override val tag: String? = null) : TextNode(tag)
    data class Emote(val code: String, override val tag: String? = null) : TextNode(tag)
    data class Url(val url: String, override val tag: String? = null) : TextNode(tag)
}


fun parseText(text: String, patterns: List<Regex> = BiliRichTextPatterns): List<TextNode> {
    val matches = patterns
        .asSequence()
        .flatMap { it.findAll(text) }
        .sortedWith(compareBy({ it.range.first }, { it.range.last }))
        .toList()

    val nodes = mutableListOf<TextNode>()
    var lastIndex = 0

    for (match in matches) {
        // append the plain text before the particular node
        if (match.range.first > lastIndex) {
            nodes.add(TextNode.PlainText(text.substring(lastIndex, match.range.first)))
        }

        // generate nodes base on the match type
        val value = match.value
        when {
            EmotePattern.matches(value) -> nodes.add(
                TextNode.Emote(
                    code = value,
                    tag = "emote-${match.range.first}"
                )
            )

            UrlPattern.matches(value) -> nodes.add(
                TextNode.Url(
                    url = value,
                    tag = "url-${match.range.first}"
                )
            )
        }

        lastIndex = match.range.last + 1
    }

    // append the plain text after the last node
    if (lastIndex < text.length) {
        nodes.add(TextNode.PlainText(text.substring(lastIndex)))
    }

    return nodes
}
