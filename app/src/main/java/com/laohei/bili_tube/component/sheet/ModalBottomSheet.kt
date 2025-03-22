package com.laohei.bili_tube.component.sheet

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowManager
import android.window.BackEvent
import android.window.OnBackAnimationCallback
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentDialog
import androidx.activity.addCallback
import androidx.activity.compose.LocalActivity
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForInspector
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.core.view.WindowCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.laohei.bili_tube.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.max
import kotlin.math.min


@Composable
@ExperimentalMaterial3Api
fun rememberModalBottomSheet(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
) =
    rememberSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange,
        initialValue = Hidden,
    )

@Composable
@ExperimentalMaterial3Api
internal fun rememberSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    initialValue: SheetValue = Hidden,
    skipHiddenState: Boolean = false,
): SheetState {
    val density = LocalDensity.current
    return rememberSaveable(
        skipPartiallyExpanded,
        confirmValueChange,
        skipHiddenState,
        saver =
            SheetState.Saver(
                skipPartiallyExpanded = skipPartiallyExpanded,
                confirmValueChange = confirmValueChange,
                density = density,
                skipHiddenState = skipHiddenState,
            )
    ) {
        SheetState(
            skipPartiallyExpanded,
            density,
            initialValue,
            confirmValueChange,
            skipHiddenState,
        )
    }
}


@Composable
private fun Scrim(color: Color, onDismissRequest: () -> Unit, visible: Boolean) {
    if (color.isSpecified) {
        val alpha by
        animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = TweenSpec())
        val closeSheet = "close sheet"
        val dismissSheet =
            if (visible) {
                Modifier
//                    .pointerInput(onDismissRequest) { detectTapGestures { onDismissRequest() } }
                    .semantics(mergeDescendants = true) {
                        traversalIndex = 1f
                        contentDescription = closeSheet
//                        onClick {
//                            onDismissRequest()
//                            true
//                        }
                    }
            } else {
                Modifier
            }
        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissSheet)
        ) {
            drawRect(color = color, alpha = alpha.coerceIn(0f, 1f))
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun ModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheet(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    properties: com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val animateToDismiss: () -> Unit = {
        if (sheetState.anchoredDraggableState.confirmValueChange(Hidden)) {
            scope
                .launch { sheetState.hide() }
                .invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismissRequest()
                    }
                }
        }
    }
    val settleToDismiss: (velocity: Float) -> Unit = {
        scope
            .launch { sheetState.settle(it) }
            .invokeOnCompletion { if (!sheetState.isVisible) onDismissRequest() }
    }

    val predictiveBackProgress = remember { Animatable(initialValue = 0f) }

    ModalBottomSheetDialog(
        properties = properties,
        onDismissRequest = {
            if (sheetState.currentValue == Expanded && sheetState.hasPartiallyExpandedState) {
                // Smoothly animate away predictive back transformations since we are not fully
                // dismissing. We don't need to do this in the else below because we want to
                // preserve the predictive back transformations (scale) during the hide animation.
                scope.launch { predictiveBackProgress.animateTo(0f) }
                scope.launch { sheetState.partialExpand() }
            } else { // Is expanded without collapsed state or is collapsed.
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            }
        },
        predictiveBackProgress = predictiveBackProgress,
    ) {
        Scrim(
            color = scrimColor,
            onDismissRequest = animateToDismiss,
            visible = sheetState.targetValue != Hidden,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .semantics { isTraversalGroup = true }) {
            ModalBottomSheetContent(
                predictiveBackProgress,
                scope,
                animateToDismiss,
                settleToDismiss,
                modifier,
                sheetState,
                sheetMaxWidth,
                shape,
                containerColor,
                contentColor,
                tonalElevation,
                dragHandle,
                contentWindowInsets,
                content
            )
        }
    }
    if (sheetState.hasExpandedState) {
        LaunchedEffect(sheetState) { sheetState.show() }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@ExperimentalMaterial3Api
internal fun BoxScope.ModalBottomSheetContent(
    predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    scope: CoroutineScope,
    animateToDismiss: () -> Unit,
    settleToDismiss: (velocity: Float) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheet(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable ColumnScope.() -> Unit
) {
    val bottomSheetPaneTitle = "bottom sheet"

    Surface(
        modifier =
            modifier
                .align(Alignment.TopCenter)
                .widthIn(max = sheetMaxWidth)
                .fillMaxWidth()
                .nestedScroll(
                    remember(sheetState) {
                        ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                            sheetState = sheetState,
                            orientation = Orientation.Vertical,
                            onFling = settleToDismiss
                        )
                    }
                )
                .draggableAnchors(
                    sheetState.anchoredDraggableState,
                    Orientation.Vertical
                ) { sheetSize,
                    constraints ->
                    val fullHeight = constraints.maxHeight.toFloat()
                    val newAnchors = DraggableAnchors {
                        Hidden at fullHeight
                        if (
                            sheetSize.height > (fullHeight / 2) && !sheetState.skipPartiallyExpanded
                        ) {
                            PartiallyExpanded at fullHeight / 2f
                        }
                        if (sheetSize.height != 0) {
                            Expanded at max(0f, fullHeight - sheetSize.height)
                        }
                    }
                    val newTarget =
                        when (sheetState.anchoredDraggableState.targetValue) {
                            Hidden -> Hidden
                            PartiallyExpanded,
                            Expanded -> {
                                val hasPartiallyExpandedState =
                                    newAnchors.hasAnchorFor(PartiallyExpanded)
                                val newTarget =
                                    if (hasPartiallyExpandedState) PartiallyExpanded
                                    else if (newAnchors.hasAnchorFor(Expanded)) Expanded else Hidden
                                newTarget
                            }
                        }
                    return@draggableAnchors newAnchors to newTarget
                }
                .draggable(
                    state = sheetState.anchoredDraggableState.draggableState,
                    orientation = Orientation.Vertical,
                    enabled = sheetState.isVisible,
                    startDragImmediately = sheetState.anchoredDraggableState.isAnimationRunning,
                    onDragStopped = { settleToDismiss(it) }
                )
                .semantics {
                    paneTitle = bottomSheetPaneTitle
                    traversalIndex = 0f
                }
                .graphicsLayer {
                    val sheetOffset = sheetState.anchoredDraggableState.offset
                    val sheetHeight = size.height
                    if (!sheetOffset.isNaN() && !sheetHeight.isNaN() && sheetHeight != 0f) {
                        val progress = predictiveBackProgress.value
                        scaleX = calculatePredictiveBackScaleX(progress)
                        scaleY = calculatePredictiveBackScaleY(progress)
                        transformOrigin =
                            TransformOrigin(0.5f, (sheetOffset + sheetHeight) / sheetHeight)
                    }
                },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(contentWindowInsets())
                .graphicsLayer {
                    val progress = predictiveBackProgress.value
                    val predictiveBackScaleX = calculatePredictiveBackScaleX(progress)
                    val predictiveBackScaleY = calculatePredictiveBackScaleY(progress)

                    // Preserve the original aspect ratio and alignment of the child content.
                    scaleY =
                        if (predictiveBackScaleY != 0f) predictiveBackScaleX / predictiveBackScaleY
                        else 1f
                    transformOrigin = PredictiveBackChildTransformOrigin
                }
        ) {
            if (dragHandle != null) {
                val collapseActionLabel = "collapse action"
                val dismissActionLabel = "dismiss action"
                val expandActionLabel = "expand action"
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .semantics(
                            mergeDescendants = true
                        ) {
                            // Provides semantics to interact with the bottomsheet based on its
                            // current value.
                            with(sheetState) {
                                dismiss(dismissActionLabel) {
                                    animateToDismiss()
                                    true
                                }
                                if (currentValue == PartiallyExpanded) {
                                    expand(expandActionLabel) {
                                        if (anchoredDraggableState.confirmValueChange(Expanded)) {
                                            scope.launch { sheetState.expand() }
                                        }
                                        true
                                    }
                                } else if (hasPartiallyExpandedState) {
                                    collapse(collapseActionLabel) {
                                        if (
                                            anchoredDraggableState.confirmValueChange(
                                                PartiallyExpanded
                                            )
                                        ) {
                                            scope.launch { partialExpand() }
                                        }
                                        true
                                    }
                                }
                            }
                        }
                ) {
                    dragHandle()
                }
            }
            content()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ModalBottomSheetDialog(
    onDismissRequest: () -> Unit,
    properties: com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties,
    predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    content: @Composable () -> Unit
) {
    val activity = LocalActivity.current
    val view = LocalView.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val composition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val dialogId = rememberSaveable { UUID.randomUUID() }
    val scope = rememberCoroutineScope()
    val darkThemeEnabled = isSystemInDarkTheme()
    val dialog =
        remember(view, density) {
            ModalBottomSheetDialogWrapper(
                onDismissRequest,
                properties,
                view,
                layoutDirection,
                density,
                activity,
                dialogId,
                predictiveBackProgress,
                scope,
                darkThemeEnabled,
            )
                .apply {
                    setContent(composition) {
                        Box(
                            Modifier
                                .background(Color.Transparent)
                                .semantics { dialog() },
                        ) {
                            currentContent()
                        }
                    }
                }
        }

    DisposableEffect(dialog) {
        dialog.show()

        onDispose {
            dialog.dismiss()
            dialog.disposeComposition()
        }
    }

    SideEffect {
        dialog.updateParameters(
            onDismissRequest = onDismissRequest,
            properties = properties,
            layoutDirection = layoutDirection
        )
    }
}

@SuppressLint("PrivateResource")
@ExperimentalMaterial3Api
private class ModalBottomSheetDialogWrapper(
    private var onDismissRequest: () -> Unit,
    private var properties: com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties,
    private val composeView: View,
    layoutDirection: LayoutDirection,
    density: Density,
    private val activity: Activity? = null,
    dialogId: UUID,
    predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    scope: CoroutineScope,
    darkThemeEnabled: Boolean,
) :
    ComponentDialog(
        ContextThemeWrapper(
            composeView.context,
            androidx.compose.material3.R.style.EdgeToEdgeFloatingDialogWindowTheme
        )
    ),
    ViewRootForInspector {

    private val dialogLayout: ModalBottomSheetDialogLayout

    // On systems older than Android S, there is a bug in the surface insets matrix math used by
    // elevation, so high values of maxSupportedElevation break accessibility services: b/232788477.
    private val maxSupportedElevation = 8.dp

    override val subCompositionView: AbstractComposeView
        get() = dialogLayout

    init {
        val window = window ?: error("Dialog has no window")
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        dialogLayout =
            ModalBottomSheetDialogLayout(
                context,
                window,
                properties.shouldDismissOnBackPress,
                onDismissRequest,
                predictiveBackProgress,
                scope,
            )
                .apply {
                    // Set unique id for AbstractComposeView. This allows state restoration for the
                    // state
                    // defined inside the Dialog via rememberSaveable()
                    setTag(R.id.compose_view_saveable_id_tag, "Dialog:$dialogId")
                    // Enable children to draw their shadow by not clipping them
                    clipChildren = false
                    // Allocate space for elevation
                    with(density) { elevation = maxSupportedElevation.toPx() }
                    // Simple outline to force window manager to allocate space for shadow.
                    // Note that the outline affects clickable area for the dismiss listener. In
                    // case of
                    // shapes like circle the area for dismiss might be to small (rectangular
                    // outline
                    // consuming clicks outside of the circle).
                    outlineProvider =
                        object : ViewOutlineProvider() {
                            override fun getOutline(view: View, result: Outline) {
                                result.setRect(0, 0, view.width, view.height)
                                // We set alpha to 0 to hide the view's shadow and let the
                                // composable to draw
                                // its own shadow. This still enables us to get the extra space
                                // needed in the
                                // surface.
                                result.alpha = 0f
                            }
                        }
                }
        // Clipping logic removed because we are spanning edge to edge.

        setContentView(dialogLayout)
        dialogLayout.setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        dialogLayout.setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        dialogLayout.setViewTreeSavedStateRegistryOwner(
            composeView.findViewTreeSavedStateRegistryOwner()
        )

        // Initial setup
        updateParameters(onDismissRequest, properties, layoutDirection)

//        WindowCompat.getInsetsController(window, window.decorView).apply {
//            isAppearanceLightStatusBars = !darkThemeEnabled
//            isAppearanceLightNavigationBars = !darkThemeEnabled
//        }
        // Due to how the onDismissRequest callback works
        // (it enforces a just-in-time decision on whether to update the state to hide the dialog)
        // we need to unconditionally add a callback here that is always enabled,
        // meaning we'll never get a system UI controlled predictive back animation
        // for these dialogs
        onBackPressedDispatcher.addCallback(this) {
            if (properties.shouldDismissOnBackPress) {
                onDismissRequest()
            }
        }
    }

    private fun setLayoutDirection(layoutDirection: LayoutDirection) {
        dialogLayout.layoutDirection =
            when (layoutDirection) {
                LayoutDirection.Ltr -> android.util.LayoutDirection.LTR
                LayoutDirection.Rtl -> android.util.LayoutDirection.RTL
            }
    }

    fun setContent(parentComposition: CompositionContext, children: @Composable () -> Unit) {
        dialogLayout.setContent(parentComposition, children)
    }

    private fun setSecurePolicy(securePolicy: SecureFlagPolicy) {
        val secureFlagEnabled =
            securePolicy.shouldApplySecureFlag(composeView.isFlagSecureEnabled())
        window!!.setFlags(
            if (secureFlagEnabled) {
                WindowManager.LayoutParams.FLAG_SECURE
            } else {
                WindowManager.LayoutParams.FLAG_SECURE.inv()
            },
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    fun updateParameters(
        onDismissRequest: () -> Unit,
        properties: com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties,
        layoutDirection: LayoutDirection
    ) {
        this.onDismissRequest = onDismissRequest
        this.properties = properties
        setSecurePolicy(properties.securePolicy)
        setLayoutDirection(layoutDirection)

        // Window flags to span parent window.
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
        )
        window?.setSoftInputMode(
            if (Build.VERSION.SDK_INT >= 30) {
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
            } else {
                @Suppress("DEPRECATION") WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            },
        )
    }

    fun disposeComposition() {
        dialogLayout.disposeComposition()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 透明区域点击事件交给当前的 activity 处理
        return activity?.dispatchTouchEvent(event) ?: false
    }

    override fun cancel() {
        // Prevents the dialog from dismissing itself
        return
    }
}


@Suppress("ViewConstructor")
private class ModalBottomSheetDialogLayout(
    context: Context,
    override val window: Window,
    val shouldDismissOnBackPress: Boolean,
    private val onDismissRequest: () -> Unit,
    private val predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
) : AbstractComposeView(context), DialogWindowProvider {

    private var content: @Composable () -> Unit by mutableStateOf({})

    private var backCallback: Any? = null

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    fun setContent(parent: CompositionContext, content: @Composable () -> Unit) {
        setParentCompositionContext(parent)
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
        createComposition()
    }

    // Display width and height logic removed, size will always span fillMaxSize().

    @Composable
    override fun Content() {
        content()
    }

    // Existing predictive back behavior below.
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        maybeRegisterBackCallback()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        maybeUnregisterBackCallback()
    }

    private fun maybeRegisterBackCallback() {
        if (!shouldDismissOnBackPress || Build.VERSION.SDK_INT < 33) {
            return
        }
        if (backCallback == null) {
            backCallback =
                if (Build.VERSION.SDK_INT >= 34) {
                    Api34Impl.createBackCallback(onDismissRequest, predictiveBackProgress, scope)
                } else {
                    Api33Impl.createBackCallback(onDismissRequest)
                }
        }
        Api33Impl.maybeRegisterBackCallback(this, backCallback)
    }

    private fun maybeUnregisterBackCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
            Api33Impl.maybeUnregisterBackCallback(this, backCallback)
        }
        backCallback = null
    }

    @RequiresApi(34)
    private object Api34Impl {
        @JvmStatic
        @DoNotInline
        fun createBackCallback(
            onDismissRequest: () -> Unit,
            predictiveBackProgress: Animatable<Float, AnimationVector1D>,
            scope: CoroutineScope
        ) =
            object : OnBackAnimationCallback {
                override fun onBackStarted(backEvent: BackEvent) {
                    scope.launch {
                        predictiveBackProgress.snapTo(PredictiveBack.transform(backEvent.progress))
                    }
                }

                override fun onBackProgressed(backEvent: BackEvent) {
                    scope.launch {
                        predictiveBackProgress.snapTo(PredictiveBack.transform(backEvent.progress))
                    }
                }

                override fun onBackInvoked() {
                    onDismissRequest()
                }

                override fun onBackCancelled() {
                    scope.launch { predictiveBackProgress.animateTo(0f) }
                }
            }
    }

    @RequiresApi(33)
    private object Api33Impl {
        @JvmStatic
        @DoNotInline
        fun createBackCallback(onDismissRequest: () -> Unit) =
            OnBackInvokedCallback(onDismissRequest)

        @JvmStatic
        @DoNotInline
        fun maybeRegisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view
                    .findOnBackInvokedDispatcher()
                    ?.registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                        backCallback
                    )
            }
        }

        @JvmStatic
        @DoNotInline
        fun maybeUnregisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view.findOnBackInvokedDispatcher()?.unregisterOnBackInvokedCallback(backCallback)
            }
        }
    }
}

@Immutable
@ExperimentalMaterial3Api
class ModalBottomSheetProperties(
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    val shouldDismissOnBackPress: Boolean = true,
) {
    constructor(
        shouldDismissOnBackPress: Boolean,
    ) : this(
        securePolicy = SecureFlagPolicy.Inherit,
        shouldDismissOnBackPress = shouldDismissOnBackPress
    )

    @Deprecated(
        message = "'isFocusable' param is no longer used. Use constructor without this parameter.",
        level = DeprecationLevel.WARNING,
        replaceWith =
            ReplaceWith("ModalBottomSheetProperties(securePolicy, shouldDismissOnBackPress)")
    )
    @Suppress("UNUSED_PARAMETER")
    constructor(
        securePolicy: SecureFlagPolicy,
        isFocusable: Boolean,
        shouldDismissOnBackPress: Boolean,
    ) : this(securePolicy, shouldDismissOnBackPress)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModalBottomSheetProperties) return false
        if (securePolicy != other.securePolicy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = securePolicy.hashCode()
        result = 31 * result + shouldDismissOnBackPress.hashCode()
        return result
    }
}

/** Default values for [ModalBottomSheet] */
@Immutable
@ExperimentalMaterial3Api
object NoScrimModalBottomSheetDefaults {

    /** Properties used to customize the behavior of a [ModalBottomSheet]. */
    val properties: com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties =
        com.laohei.bili_tube.component.sheet.ModalBottomSheetProperties()
}


private fun GraphicsLayerScope.calculatePredictiveBackScaleX(progress: Float): Float {
    val width = size.width
    return if (width.isNaN() || width == 0f) {
        1f
    } else {
        1f - lerp(0f, min(PredictiveBackMaxScaleXDistance.toPx(), width), progress) / width
    }
}

private fun GraphicsLayerScope.calculatePredictiveBackScaleY(progress: Float): Float {
    val height = size.height
    return if (height.isNaN() || height == 0f) {
        1f
    } else {
        1f - lerp(0f, min(PredictiveBackMaxScaleYDistance.toPx(), height), progress) / height
    }
}

// Taken from AndroidPopup.android.kt
private fun SecureFlagPolicy.shouldApplySecureFlag(isSecureFlagSetOnParent: Boolean): Boolean {
    return when (this) {
        SecureFlagPolicy.SecureOff -> false
        SecureFlagPolicy.SecureOn -> true
        SecureFlagPolicy.Inherit -> isSecureFlagSetOnParent
    }
}

internal fun View.isFlagSecureEnabled(): Boolean {
    val windowParams = rootView.layoutParams as? WindowManager.LayoutParams
    if (windowParams != null) {
        return (windowParams.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }
    return false
}

private val PredictiveBackMaxScaleXDistance = 48.dp
private val PredictiveBackMaxScaleYDistance = 24.dp
private val PredictiveBackChildTransformOrigin = TransformOrigin(0.5f, 0f)