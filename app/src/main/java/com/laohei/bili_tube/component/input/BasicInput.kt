package com.laohei.bili_tube.component.input

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BasicInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Color.Black),
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
    shape: Shape = CircleShape,
    inputIcon: ImageVector? = Icons.Outlined.Search,
    inputIconColor: Color = MaterialTheme.colorScheme.primaryContainer,
    placeholder: (@Composable () -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChanged,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                navigationIcon?.invoke()
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .border(borderStroke, shape)
                        .background(Color.Transparent)
                        .padding(horizontal = 12.dp)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    inputIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = it.name,
                            tint = inputIconColor
                        )
                    }
                    Box(contentAlignment = Alignment.CenterStart) {
                        innerTextField()
                        value.ifEmpty { placeholder?.invoke() }
                    }
                }

                trailingIcon?.invoke()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun BasicInputPreview() {
    var keyword by remember { mutableStateOf("") }

    BasicInput(
        value = keyword,
        onValueChanged = { keyword = it },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.name,
                )
            }
        },
        trailingIcon = {
            TextButton(onClick = {}) {
                Text(text = "搜索")
            }
        },
        placeholder = {
            Text(text = "Placeholder")
        }
    )
}