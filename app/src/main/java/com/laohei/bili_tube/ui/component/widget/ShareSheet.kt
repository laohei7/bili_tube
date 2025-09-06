package com.laohei.bili_tube.ui.component.widget

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.extension.getShareTargets
import com.laohei.bili_tube.core.extension.toPainter
import com.laohei.bili_tube.core.runtime.rememberBroadcastReceiver
import com.laohei.bili_tube.model.ShareTarget
import com.laohei.bili_tube.ui.theme.CornerRadiusMd
import com.laohei.bili_tube.ui.theme.CornerRadiusSm
import com.laohei.bili_tube.ui.theme.PaddingLg
import com.laohei.bili_tube.ui.theme.PaddingSm
import com.laohei.bili_tube.ui.theme.PaddingXs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private data class ShareOption(
    val icon: ImageVector,
    @StringRes val labelId: Int,
    val color: Color? = null
)

@Composable
private fun buildShareOptions(colorScheme: ColorScheme = MaterialTheme.colorScheme): List<ShareOption> {
    return remember {
        listOf(
            ShareOption(
                icon = Icons.Rounded.ContentCopy,
                labelId = R.string.str_copy_link
            )
        )
    }
}

@Composable
private fun rememberShareTargets(context: Context = LocalContext.current): List<ShareTarget> {
    val packageChanged = rememberBroadcastReceiver(
        Intent.ACTION_PACKAGE_ADDED,
        Intent.ACTION_PACKAGE_REMOVED,
        Intent.ACTION_PACKAGE_CHANGED
    )

    val targets by produceState(initialValue = emptyList(), packageChanged) {
        value = context.getShareTargets()
    }
    return targets
}


private fun handleShareTarget(
    target: ShareTarget,
    shareContent: String,
    context: Context,
    scope: CoroutineScope
) {
    val intent = Intent().apply {
        this.action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareContent)
        type = "text/plain"
        setPackage(target.packageName)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        scope.launch {
            EventBus.send(Event.AppEvent.ToastEvent(R.string.str_not_install_app))
        }
    }
}

private fun handleShareOption(
    optionId: Int,
    shareContent: String,
    clipboardManager: ClipboardManager,
    scope: CoroutineScope
) {
    clipboardManager.setText(AnnotatedString(shareContent))
    scope.launch {
        EventBus.send(Event.AppEvent.ToastEvent(R.string.str_copy_to_clipboard))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(
    isSheetVisible: Boolean,
    shareContent: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onDismissRequest: () -> Unit
) {
    if (isSheetVisible.not()) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val shareOptions = buildShareOptions()
    val shareTargets = rememberShareTargets()
    ModalBottomSheet(
        modifier = Modifier
            .padding(PaddingSm)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(CornerRadiusMd),
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = stringResource(R.string.str_shared),
            modifier = Modifier
                .padding(horizontal = PaddingLg)
                .padding(bottom = PaddingSm),
            style = MaterialTheme.typography.titleMedium
        )
        ShareTargetsRow(
            shareTargets = shareTargets,
            onHandleTarget = {
                handleShareTarget(it, shareContent, context, scope)
            }
        )
        HorizontalDivider()
        ShareActionsColumn(
            options = shareOptions, style = style,
            onHandleOption = {
                handleShareOption(it, shareContent, clipboardManager, scope)
            }
        )
    }
}

@Composable
private fun ShareTargetsRow(
    shareTargets: List<ShareTarget>,
    onHandleTarget: (ShareTarget) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(PaddingLg),
    ) {
        item { Spacer(Modifier) }
        items(shareTargets) { target ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(60.dp)
                    .clip(RoundedCornerShape(CornerRadiusSm))
                    .clickable {
                        onHandleTarget(target)
                    },
                verticalArrangement = Arrangement.spacedBy(PaddingXs)
            ) {
                Image(
                    painter = target.icon.toPainter(),
                    contentDescription = target.label,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    text = target.label, style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
        item { Spacer(Modifier) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareActionsColumn(
    options: List<ShareOption>,
    style: TextStyle,
    onHandleOption: (Int) -> Unit
) {
    val defaultColors = ListItemDefaults.colors()
    options.fastForEach { option ->
        ListItem(
            modifier = Modifier.clickable {
                onHandleOption(option.labelId)
            },
            colors = ListItemDefaults.colors(
                containerColor = BottomSheetDefaults.ContainerColor,
                leadingIconColor = option.color ?: defaultColors.leadingIconColor,
                headlineColor = option.color ?: defaultColors.headlineColor
            ),
            leadingContent = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.icon.name,
                        modifier = Modifier.padding(PaddingSm)
                    )
                }
            },
            headlineContent = {
                Text(
                    text = stringResource(option.labelId),
                    style = style
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareSheetPreview() {
    ShareSheet(isSheetVisible = true, shareContent = "") {}
}
