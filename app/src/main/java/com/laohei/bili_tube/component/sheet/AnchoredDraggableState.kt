package com.laohei.bili_tube.component.sheet

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.AnchoredDragScope
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.structuralEqualityPolicy
import com.laohei.bili_tube.component.sheet.AnchoredDraggableState.Companion.Saver
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs

@Stable
internal class AnchoredDraggableState<T>(
    initialValue: T,
    internal val positionalThreshold: (totalDistance: Float) -> Float,
    internal val velocityThreshold: () -> Float,
    val animationSpec: AnimationSpec<Float>,
    internal val confirmValueChange: (newValue: T) -> Boolean = { true }
) {

    /**
     * Construct an [AnchoredDraggableState] instance with anchors.
     *
     * @param initialValue The initial value of the state.
     * @param anchors The anchors of the state. Use [updateAnchors] to update the anchors later.
     * @param animationSpec The default animation that will be used to animate to a new state.
     * @param confirmValueChange Optional callback invoked to confirm or veto a pending state
     *   change.
     * @param positionalThreshold The positional threshold, in px, to be used when calculating the
     *   target state while a drag is in progress and when settling after the drag ends. This is the
     *   distance from the start of a transition. It will be, depending on the direction of the
     *   interaction, added or subtracted from/to the origin offset. It should always be a positive
     *   value.
     * @param velocityThreshold The velocity threshold (in px per second) that the end velocity has
     *   to exceed in order to animate to the next state, even if the [positionalThreshold] has not
     *   been reached.
     */
    @OptIn(ExperimentalFoundationApi::class)
    constructor(
        initialValue: T,
        anchors: DraggableAnchors<T>,
        positionalThreshold: (totalDistance: Float) -> Float,
        velocityThreshold: () -> Float,
        animationSpec: AnimationSpec<Float>,
        confirmValueChange: (newValue: T) -> Boolean = { true }
    ) : this(
        initialValue,
        positionalThreshold,
        velocityThreshold,
        animationSpec,
        confirmValueChange
    ) {
        this.anchors = anchors
        trySnapTo(initialValue)
    }

    private val dragMutex = InternalMutatorMutex()

    @OptIn(ExperimentalFoundationApi::class)
    internal val draggableState =
        object : DraggableState {

            private val dragScope =
                object : DragScope {
                    override fun dragBy(pixels: Float) {
                        with(anchoredDragScope) { dragTo(newOffsetForDelta(pixels)) }
                    }
                }

            override suspend fun drag(
                dragPriority: MutatePriority,
                block: suspend DragScope.() -> Unit
            ) {
                this@AnchoredDraggableState.anchoredDrag(dragPriority) {
                    with(dragScope) { block() }
                }
            }

            override fun dispatchRawDelta(delta: Float) {
                this@AnchoredDraggableState.dispatchRawDelta(delta)
            }
        }

    /** The current value of the [AnchoredDraggableState]. */
    var currentValue: T by mutableStateOf(initialValue)
        private set

    /**
     * The target value. This is the closest value to the current offset, taking into account
     * positional thresholds. If no interactions like animations or drags are in progress, this will
     * be the current value.
     */
    val targetValue: T by derivedStateOf {
        dragTarget
            ?: run {
                val currentOffset = offset
                if (!currentOffset.isNaN()) {
                    computeTarget(currentOffset, currentValue, velocity = 0f)
                } else currentValue
            }
    }

    /**
     * The closest value in the swipe direction from the current offset, not considering thresholds.
     * If an [anchoredDrag] is in progress, this will be the target of that anchoredDrag (if
     * specified).
     */
    internal val closestValue: T by derivedStateOf {
        dragTarget
            ?: run {
                val currentOffset = offset
                if (!currentOffset.isNaN()) {
                    computeTargetWithoutThresholds(currentOffset, currentValue)
                } else currentValue
            }
    }

    /**
     * The current offset, or [Float.NaN] if it has not been initialized yet.
     *
     * The offset will be initialized when the anchors are first set through [updateAnchors].
     *
     * Strongly consider using [requireOffset] which will throw if the offset is read before it is
     * initialized. This helps catch issues early in your workflow.
     */
    var offset: Float by mutableFloatStateOf(Float.NaN)
        private set

    /**
     * Require the current offset.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     * @see offset
     */
    fun requireOffset(): Float {
        check(!offset.isNaN()) {
            "The offset was read before being initialized. Did you access the offset in a phase " +
                    "before layout, like effects or composition?"
        }
        return offset
    }

    /** Whether an animation is currently in progress. */
    val isAnimationRunning: Boolean
        get() = dragTarget != null

    /**
     * The fraction of the progress going from [currentValue] to [closestValue], within [0f..1f]
     * bounds, or 1f if the [AnchoredDraggableState] is in a settled state.
     */
    @OptIn(ExperimentalFoundationApi::class)
    @get:FloatRange(from = 0.0, to = 1.0)
    val progress: Float by
    derivedStateOf(structuralEqualityPolicy()) {
        val a = anchors.positionOf(currentValue)
        val b = anchors.positionOf(closestValue)
        val distance = abs(b - a)
        if (!distance.isNaN() && distance > 1e-6f) {
            val progress = (this.requireOffset() - a) / (b - a)
            // If we are very close to 0f or 1f, we round to the closest
            if (progress < 1e-6f) 0f else if (progress > 1 - 1e-6f) 1f else progress
        } else 1f
    }

    /**
     * The velocity of the last known animation. Gets reset to 0f when an animation completes
     * successfully, but does not get reset when an animation gets interrupted. You can use this
     * value to provide smooth reconciliation behavior when re-targeting an animation.
     */
    var lastVelocity: Float by mutableFloatStateOf(0f)
        private set

    private var dragTarget: T? by mutableStateOf(null)

    @OptIn(ExperimentalFoundationApi::class)
    var anchors: DraggableAnchors<T> by mutableStateOf(emptyDraggableAnchors())
        private set

    /**
     * Update the anchors. If there is no ongoing [anchoredDrag] operation, snap to the [newTarget],
     * otherwise restart the ongoing [anchoredDrag] operation (e.g. an animation) with the new
     * anchors.
     *
     * <b>If your anchors depend on the size of the layout, updateAnchors should be called in the
     * layout (placement) phase, e.g. through Modifier.onSizeChanged.</b> This ensures that the
     * state is set up within the same frame. For static anchors, or anchors with different data
     * dependencies, [updateAnchors] is safe to be called from side effects or layout.
     *
     * @param newAnchors The new anchors.
     * @param newTarget The new target, by default the closest anchor or the current target if there
     *   are no anchors.
     */
    @OptIn(ExperimentalFoundationApi::class)
    fun updateAnchors(
        newAnchors: DraggableAnchors<T>,
        newTarget: T =
            if (!offset.isNaN()) {
                newAnchors.closestAnchor(offset) ?: targetValue
            } else targetValue
    ) {
        if (anchors != newAnchors) {
            anchors = newAnchors
            // Attempt to snap. If nobody is holding the lock, we can immediately update the offset.
            // If anybody is holding the lock, we send a signal to restart the ongoing work with the
            // updated anchors.
            val snapSuccessful = trySnapTo(newTarget)
            if (!snapSuccessful) {
                dragTarget = newTarget
            }
        }
    }

    /**
     * Find the closest anchor, taking into account the [velocityThreshold] and
     * [positionalThreshold], and settle at it with an animation.
     *
     * If the [velocity] is lower than the [velocityThreshold], the closest anchor by distance and
     * [positionalThreshold] will be the target. If the [velocity] is higher than the
     * [velocityThreshold], the [positionalThreshold] will <b>not</b> be considered and the next
     * anchor in the direction indicated by the sign of the [velocity] will be the target.
     */
    suspend fun settle(velocity: Float) {
        val previousValue = this.currentValue
        val targetValue =
            computeTarget(
                offset = requireOffset(),
                currentValue = previousValue,
                velocity = velocity
            )
        if (confirmValueChange(targetValue)) {
            animateTo(targetValue, velocity)
        } else {
            // If the user vetoed the state change, rollback to the previous state.
            animateTo(previousValue, velocity)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun computeTarget(offset: Float, currentValue: T, velocity: Float): T {
        val currentAnchors = anchors
        val currentAnchorPosition = currentAnchors.positionOf(currentValue)
        val velocityThresholdPx = velocityThreshold()
        return if (currentAnchorPosition == offset || currentAnchorPosition.isNaN()) {
            currentValue
        } else if (currentAnchorPosition < offset) {
            // Swiping from lower to upper (positive).
            if (velocity >= velocityThresholdPx) {
                currentAnchors.closestAnchor(offset, true)!!
            } else {
                val upper = currentAnchors.closestAnchor(offset, true)!!
                val distance = abs(currentAnchors.positionOf(upper) - currentAnchorPosition)
                val relativeThreshold = abs(positionalThreshold(distance))
                val absoluteThreshold = abs(currentAnchorPosition + relativeThreshold)
                if (offset < absoluteThreshold) currentValue else upper
            }
        } else {
            // Swiping from upper to lower (negative).
            if (velocity <= -velocityThresholdPx) {
                currentAnchors.closestAnchor(offset, false)!!
            } else {
                val lower = currentAnchors.closestAnchor(offset, false)!!
                val distance = abs(currentAnchorPosition - currentAnchors.positionOf(lower))
                val relativeThreshold = abs(positionalThreshold(distance))
                val absoluteThreshold = abs(currentAnchorPosition - relativeThreshold)
                if (offset < 0) {
                    // For negative offsets, larger absolute thresholds are closer to lower anchors
                    // than smaller ones.
                    if (abs(offset) < absoluteThreshold) currentValue else lower
                } else {
                    if (offset > absoluteThreshold) currentValue else lower
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun computeTargetWithoutThresholds(
        offset: Float,
        currentValue: T,
    ): T {
        val currentAnchors = anchors
        val currentAnchorPosition = currentAnchors.positionOf(currentValue)
        return if (currentAnchorPosition == offset || currentAnchorPosition.isNaN()) {
            currentValue
        } else if (currentAnchorPosition < offset) {
            currentAnchors.closestAnchor(offset, true) ?: currentValue
        } else {
            currentAnchors.closestAnchor(offset, false) ?: currentValue
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private val anchoredDragScope: AnchoredDragScope =
        object : AnchoredDragScope {
            override fun dragTo(newOffset: Float, lastKnownVelocity: Float) {
                offset = newOffset
                lastVelocity = lastKnownVelocity
            }
        }

    /**
     * Call this function to take control of drag logic and perform anchored drag with the latest
     * anchors.
     *
     * All actions that change the [offset] of this [AnchoredDraggableState] must be performed
     * within an [anchoredDrag] block (even if they don't call any other methods on this object) in
     * order to guarantee that mutual exclusion is enforced.
     *
     * If [anchoredDrag] is called from elsewhere with the [dragPriority] higher or equal to ongoing
     * drag, the ongoing drag will be cancelled.
     *
     * <b>If the [anchors] change while the [block] is being executed, it will be cancelled and
     * re-executed with the latest anchors and target.</b> This allows you to target the correct
     * state.
     *
     * @param dragPriority of the drag operation
     * @param block perform anchored drag given the current anchor provided
     */
    @OptIn(ExperimentalFoundationApi::class)
    suspend fun anchoredDrag(
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend AnchoredDragScope.(anchors: DraggableAnchors<T>) -> Unit
    ) {
        try {
            dragMutex.mutate(dragPriority) {
                restartable(inputs = { anchors }) { latestAnchors ->
                    anchoredDragScope.block(latestAnchors)
                }
            }
        } finally {
            val closest = anchors.closestAnchor(offset)
            if (
                closest != null &&
                abs(offset - anchors.positionOf(closest)) <= 0.5f &&
                confirmValueChange.invoke(closest)
            ) {
                currentValue = closest
            }
        }
    }

    /**
     * Call this function to take control of drag logic and perform anchored drag with the latest
     * anchors and target.
     *
     * All actions that change the [offset] of this [AnchoredDraggableState] must be performed
     * within an [anchoredDrag] block (even if they don't call any other methods on this object) in
     * order to guarantee that mutual exclusion is enforced.
     *
     * This overload allows the caller to hint the target value that this [anchoredDrag] is intended
     * to arrive to. This will set [AnchoredDraggableState.targetValue] to provided value so
     * consumers can reflect it in their UIs.
     *
     * <b>If the [anchors] or [AnchoredDraggableState.targetValue] change while the [block] is being
     * executed, it will be cancelled and re-executed with the latest anchors and target.</b> This
     * allows you to target the correct state.
     *
     * If [anchoredDrag] is called from elsewhere with the [dragPriority] higher or equal to ongoing
     * drag, the ongoing drag will be cancelled.
     *
     * @param targetValue hint the target value that this [anchoredDrag] is intended to arrive to
     * @param dragPriority of the drag operation
     * @param block perform anchored drag given the current anchor provided
     */
    @OptIn(ExperimentalFoundationApi::class)
    suspend fun anchoredDrag(
        targetValue: T,
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend AnchoredDragScope.(anchors: DraggableAnchors<T>, targetValue: T) -> Unit
    ) {
        if (anchors.hasAnchorFor(targetValue)) {
            try {
                dragMutex.mutate(dragPriority) {
                    dragTarget = targetValue
                    restartable(inputs = { anchors to this@AnchoredDraggableState.targetValue }) { (latestAnchors, latestTarget) ->
                        anchoredDragScope.block(latestAnchors, latestTarget)
                    }
                }
            } finally {
                dragTarget = null
                val closest = anchors.closestAnchor(offset)
                if (
                    closest != null &&
                    abs(offset - anchors.positionOf(closest)) <= 0.5f &&
                    confirmValueChange.invoke(closest)
                ) {
                    currentValue = closest
                }
            }
        } else {
            // Todo: b/283467401, revisit this behavior
            currentValue = targetValue
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    internal fun newOffsetForDelta(delta: Float) =
        ((if (offset.isNaN()) 0f else offset) + delta).coerceIn(
            anchors.minAnchor(),
            anchors.maxAnchor()
        )

    /**
     * Drag by the [delta], coerce it in the bounds and dispatch it to the [AnchoredDraggableState].
     *
     * @return The delta the consumed by the [AnchoredDraggableState]
     */
    fun dispatchRawDelta(delta: Float): Float {
        val newOffset = newOffsetForDelta(delta)
        val oldOffset = if (offset.isNaN()) 0f else offset
        offset = newOffset
        return newOffset - oldOffset
    }

    /**
     * Attempt to snap synchronously. Snapping can happen synchronously when there is no other drag
     * transaction like a drag or an animation is progress. If there is another interaction in
     * progress, the suspending [snapTo] overload needs to be used.
     *
     * @return true if the synchronous snap was successful, or false if we couldn't snap synchronous
     */
    @OptIn(ExperimentalFoundationApi::class)
    private fun trySnapTo(targetValue: T): Boolean =
        dragMutex.tryMutate {
            with(anchoredDragScope) {
                val targetOffset = anchors.positionOf(targetValue)
                if (!targetOffset.isNaN()) {
                    dragTo(targetOffset)
                    dragTarget = null
                }
                currentValue = targetValue
            }
        }

    companion object {
        /** The default [Saver] implementation for [AnchoredDraggableState]. */
        fun <T : Any> Saver(
            animationSpec: AnimationSpec<Float>,
            confirmValueChange: (T) -> Boolean,
            positionalThreshold: (distance: Float) -> Float,
            velocityThreshold: () -> Float,
        ) =
            androidx.compose.runtime.saveable.Saver<AnchoredDraggableState<T>, T>(
                save = { it.currentValue },
                restore = {
                    AnchoredDraggableState(
                        initialValue = it,
                        animationSpec = animationSpec,
                        confirmValueChange = confirmValueChange,
                        positionalThreshold = positionalThreshold,
                        velocityThreshold = velocityThreshold
                    )
                }
            )
    }
}

/**
 * Snap to a [targetValue] without any animation. If the [targetValue] is not in the set of anchors,
 * the [AnchoredDraggableState.currentValue] will be updated to the [targetValue] without updating
 * the offset.
 *
 * @param targetValue The target value of the animation
 * @throws CancellationException if the interaction interrupted by another interaction like a
 *   gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
 */
@OptIn(ExperimentalFoundationApi::class)
internal suspend fun <T> AnchoredDraggableState<T>.snapTo(targetValue: T) {
    anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
        val targetOffset = anchors.positionOf(latestTarget)
        if (!targetOffset.isNaN()) dragTo(targetOffset)
    }
}

/**
 * Animate to a [targetValue]. If the [targetValue] is not in the set of anchors, the
 * [AnchoredDraggableState.currentValue] will be updated to the [targetValue] without updating the
 * offset.
 *
 * @param targetValue The target value of the animation
 * @param velocity The velocity the animation should start with
 * @throws CancellationException if the interaction interrupted by another interaction like a
 *   gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
 */
@OptIn(ExperimentalFoundationApi::class)
internal suspend fun <T> AnchoredDraggableState<T>.animateTo(
    targetValue: T,
    velocity: Float = this.lastVelocity,
) {
    anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
        val targetOffset = anchors.positionOf(latestTarget)
        if (!targetOffset.isNaN()) {
            var prev = if (offset.isNaN()) 0f else offset
            animate(prev, targetOffset, velocity, animationSpec) { value, velocity ->
                // Our onDrag coerces the value within the bounds, but an animation may
                // overshoot, for example a spring animation or an overshooting interpolator
                // We respect the user's intention and allow the overshoot, but still use
                // DraggableState's drag for its mutex.
                dragTo(value, velocity)
                prev = value
            }
        }
    }
}

private fun <T> emptyDraggableAnchors() = MapDraggableAnchors<T>(emptyMap())


@OptIn(ExperimentalFoundationApi::class)
private class MapDraggableAnchors<T>(private val anchors: Map<T, Float>) :
    DraggableAnchors<T> {

    override fun positionOf(value: T): Float = anchors[value] ?: Float.NaN

    override fun hasAnchorFor(value: T) = anchors.containsKey(value)

    override fun closestAnchor(position: Float): T? =
        anchors.minByOrNull { abs(position - it.value) }?.key

    override fun closestAnchor(position: Float, searchUpwards: Boolean): T? {
        return anchors
            .minByOrNull { (_, anchor) ->
                val delta = if (searchUpwards) anchor - position else position - anchor
                if (delta < 0) Float.POSITIVE_INFINITY else delta
            }
            ?.key
    }

    override fun forEach(block: (T, Float) -> Unit) {

    }

    override fun minAnchor() = anchors.values.minOrNull() ?: Float.NaN

    override fun maxAnchor() = anchors.values.maxOrNull() ?: Float.NaN

    override val size: Int
        get() = anchors.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapDraggableAnchors<*>) return false

        return anchors == other.anchors
    }

    override fun hashCode() = 31 * anchors.hashCode()

    override fun toString() = "MapDraggableAnchors($anchors)"
}

@Stable
internal class InternalMutatorMutex {
    private class Mutator(val priority: MutatePriority, val job: Job) {
        fun canInterrupt(other: Mutator) = priority >= other.priority

        fun cancel() = job.cancel()
    }

    private val currentMutator = InternalAtomicReference<Mutator?>(null)
    private val mutex = Mutex()

    private fun tryMutateOrCancel(mutator: Mutator) {
        while (true) {
            val oldMutator = currentMutator.get()
            if (oldMutator == null || mutator.canInterrupt(oldMutator)) {
                if (currentMutator.compareAndSet(oldMutator, mutator)) {
                    oldMutator?.cancel()
                    break
                }
            } else throw CancellationException("Current mutation had a higher priority")
        }
    }

    /**
     * Enforce that only a single caller may be active at a time.
     *
     * If [mutate] is called while another call to [mutate] or [mutateWith] is in progress, their
     * [priority] values are compared. If the new caller has a [priority] equal to or higher than
     * the call in progress, the call in progress will be cancelled, throwing
     * [CancellationException] and the new caller's [block] will be invoked. If the call in progress
     * had a higher [priority] than the new caller, the new caller will throw
     * [CancellationException] without invoking [block].
     *
     * @param priority the priority of this mutation; [MutatePriority.Default] by default. Higher
     *   priority mutations will interrupt lower priority mutations.
     * @param block mutation code to run mutually exclusive with any other call to [mutate],
     *   [mutateWith] or [tryMutate].
     */
    suspend fun <R> mutate(
        priority: MutatePriority = MutatePriority.Default,
        block: suspend () -> R
    ) = coroutineScope {
        val mutator = Mutator(priority, coroutineContext[Job]!!)

        tryMutateOrCancel(mutator)

        mutex.withLock {
            try {
                block()
            } finally {
                currentMutator.compareAndSet(mutator, null)
            }
        }
    }

    /**
     * Enforce that only a single caller may be active at a time.
     *
     * If [mutateWith] is called while another call to [mutate] or [mutateWith] is in progress,
     * their [priority] values are compared. If the new caller has a [priority] equal to or higher
     * than the call in progress, the call in progress will be cancelled, throwing
     * [CancellationException] and the new caller's [block] will be invoked. If the call in progress
     * had a higher [priority] than the new caller, the new caller will throw
     * [CancellationException] without invoking [block].
     *
     * This variant of [mutate] calls its [block] with a [receiver], removing the need to create an
     * additional capturing lambda to invoke it with a receiver object. This can be used to expose a
     * mutable scope to the provided [block] while leaving the rest of the state object read-only.
     * For example:
     *
     * @param receiver the receiver `this` that [block] will be called with
     * @param priority the priority of this mutation; [MutatePriority.Default] by default. Higher
     *   priority mutations will interrupt lower priority mutations.
     * @param block mutation code to run mutually exclusive with any other call to [mutate],
     *   [mutateWith] or [tryMutate].
     */
    suspend fun <T, R> mutateWith(
        receiver: T,
        priority: MutatePriority = MutatePriority.Default,
        block: suspend T.() -> R
    ) = coroutineScope {
        val mutator = Mutator(priority, coroutineContext[Job]!!)

        tryMutateOrCancel(mutator)

        mutex.withLock {
            try {
                receiver.block()
            } finally {
                currentMutator.compareAndSet(mutator, null)
            }
        }
    }

    /**
     * Attempt to mutate synchronously if there is no other active caller. If there is no other
     * active caller, the [block] will be executed in a lock. If there is another active caller,
     * this method will return false, indicating that the active caller needs to be cancelled
     * through a [mutate] or [mutateWith] call with an equal or higher mutation priority.
     *
     * Calls to [mutate] and [mutateWith] will suspend until execution of the [block] has finished.
     *
     * @param block mutation code to run mutually exclusive with any other call to [mutate],
     *   [mutateWith] or [tryMutate].
     * @return true if the [block] was executed, false if there was another active caller and the
     *   [block] was not executed.
     */
    fun tryMutate(block: () -> Unit): Boolean {
        val didLock = mutex.tryLock()
        if (didLock) {
            try {
                block()
            } finally {
                mutex.unlock()
            }
        }
        return didLock
    }
}

private class AnchoredDragFinishedSignal : CancellationException() {

    override fun fillInStackTrace(): Throwable {
        stackTrace = emptyArray()
        return this
    }
}

private suspend fun <I> restartable(inputs: () -> I, block: suspend (I) -> Unit) {
    try {
        coroutineScope {
            var previousDrag: Job? = null
            snapshotFlow(inputs).collect { latestInputs ->
                previousDrag?.apply {
                    cancel(AnchoredDragFinishedSignal())
                    join()
                }
                previousDrag =
                    launch(start = CoroutineStart.UNDISPATCHED) {
                        block(latestInputs)
                        this@coroutineScope.cancel(AnchoredDragFinishedSignal())
                    }
            }
        }
    } catch (anchoredDragFinished: AnchoredDragFinishedSignal) {
        // Ignored
    }
}

internal class InternalAtomicReference<V>(value: V) {
    private val atomicRef = AtomicReference(value)
    fun get(): V {
        return atomicRef.get()
    }

    fun set(value: V) {
        atomicRef.set(value)
    }

    fun getAndSet(value: V): V {
        return atomicRef.getAndSet(value)
    }

    fun compareAndSet(expect: V, newValue: V): Boolean {
        return atomicRef.compareAndSet(expect, newValue)
    }
}