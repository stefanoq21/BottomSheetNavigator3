/*
 *  Copyright 2024 stefanoq21
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package com.stefanoq21.material3.navigation


import android.util.Log
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.util.fastForEach
import androidx.navigation.FloatingWindow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorState
import androidx.navigation.compose.LocalOwnersProvider
import com.stefanoq21.material3.navigation.BottomSheetNavigator.Destination
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The state of a [ModalBottomSheetLayout] that the [BottomSheetNavigator] drives
 *
 * @param sheetState The sheet state that is driven by the [BottomSheetNavigator]
 */
public class BottomSheetNavigatorSheetState(private val sheetState: SheetState) {
    /**
     * @see SheetState.isVisible
     */
    public val isVisible: Boolean
        get() = sheetState.isVisible

    /**
     * @see SheetState.currentValue
     */
    public val currentValue: SheetValue
        get() = sheetState.currentValue

    /**
     * @see SheetState.targetValue
     */
    public val targetValue: SheetValue
        get() = sheetState.targetValue
}

/**
 * Create and remember a [BottomSheetNavigator]
 */
@Composable
public fun rememberBottomSheetNavigator(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
): BottomSheetNavigator {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange
    )

    return remember(sheetState) { BottomSheetNavigator(sheetState) }
}

/**
 * Navigator that drives a [ModalBottomSheetState] for use of [ModalBottomSheetLayout]s
 * with the navigation library. Every destination using this Navigator must set a valid
 * [Composable] by setting it directly on an instantiated [Destination] or calling
 * [navigation.bottomSheet].
 *
 * <b>The [sheetInitializer] [Composable] will always host the latest entry of the back stack. When
 * navigating from a [BottomSheetNavigator.Destination] to another
 * [BottomSheetNavigator.Destination], the content of the sheet will be replaced instead of a
 * new bottom sheet being shown.</b>
 *
 * When the sheet is dismissed by the user, the [state]'s [NavigatorState.backStack] will be popped.
 *
 * The primary constructor is not intended for public use. Please refer to
 * [rememberBottomSheetNavigator] instead.
 *
 * @param sheetState The [ModalBottomSheetState] that the [BottomSheetNavigator] will use to
 * drive the sheet state
 */
@Navigator.Name("bottomSheet")
public class BottomSheetNavigator(
    internal val sheetState: SheetState
) : Navigator<Destination>() {

    internal var sheetEnabled by mutableStateOf(false)
        private set

    private var attached by mutableStateOf(false)


    /**
     * Get the back stack from the [state]. In some cases, the [sheetInitializer] might be composed
     * before the Navigator is attached, so we specifically return an empty flow if we aren't
     * attached yet.
     */
    private val backStack: StateFlow<List<NavBackStackEntry>>
        get() = if (attached) {
            state.backStack
        } else {
            MutableStateFlow(emptyList())
        }

    /**
     * Get the transitionsInProgress from the [state]. In some cases, the [sheetInitializer] might be
     * composed before the Navigator is attached, so we specifically return an empty flow if we
     * aren't attached yet.
     */
    private val transitionsInProgress: StateFlow<Set<NavBackStackEntry>>
        get() = if (attached) {
            state.transitionsInProgress
        } else {
            MutableStateFlow(emptySet())
        }

    /**
     * Access properties of the [ModalBottomSheetLayout]'s [BottomSheetNavigatorSheetState]
     */
    public val navigatorSheetState: BottomSheetNavigatorSheetState =
        BottomSheetNavigatorSheetState(sheetState)

    /**
     * A [Composable] function that hosts the current sheet content. This should be set as
     * sheetContent of your [ModalBottomSheetLayout].
     */

    internal var sheetContent: @Composable ColumnScope.() -> Unit = {}
    internal var onDismissRequest: () -> Unit = {}

    private var animateToDismiss: () -> Unit = {}

    private var animateBack: () -> Unit = {}


    // Inspiration from
    // https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-compose/src/main/java/androidx/navigation/compose/NavHost.kt;l=338;drc=00f6c4d7634026174e04e1d8153b4ad8d9cea3fd
    internal val sheetInitializer: @Composable () -> Unit = {
        val saveableStateHolder = rememberSaveableStateHolder()


        val backStack by backStack.collectAsState()
        var progress by remember { mutableFloatStateOf(0f) }
        var inPredictiveBack by remember { mutableStateOf(false) }

        val visibleEntries by transitionsInProgress.collectAsState()
        val backStackEntry: NavBackStackEntry? = backStack.lastOrNull()

        if (backStackEntry != null) {
            // This is a fix for the sheet not displaying with correct height after activity re-create.
            LaunchedEffect(Unit) {
                sheetEnabled = true
            }
            // Once sheet is enabled again, expand it in case it's stuck.
            LaunchedEffect(sheetEnabled) {
                if (sheetEnabled) sheetState.expand()
            }
        }

        val transitionState = remember {
            // The state returned here cannot be nullable cause it produces the input of the
            // transitionSpec passed into the AnimatedContent and that must match the non-nullable
            // scope exposed by the transitions on the NavHost and composable APIs.
            SeekableTransitionState(backStackEntry)
        }
        val transition = rememberTransition(transitionState, label = "entry")

        if (inPredictiveBack) {
            LaunchedEffect(progress) {
                val previousEntry = backStack[backStack.size - 2]
                transitionState.seekTo(progress, previousEntry)
            }
        } else {
            LaunchedEffect(backStackEntry) {
                // This ensures we don't animate after the back gesture is cancelled and we
                // are already on the current state
                if (transitionState.currentState != backStackEntry) {
                    backStackEntry?.let {
                        transitionState.animateTo(it)
                        state.markTransitionComplete(it)
                    }
                } else {
                    // convert from nanoseconds to milliseconds
                    val totalDuration = transition.totalDurationNanos / 1000000
                    // When the predictive back gesture is cancel, we need to manually animate
                    // the SeekableTransitionState from where it left off, to zero and then
                    // snapTo the final position.
                    animate(
                        transitionState.fraction,
                        0f,
                        animationSpec = tween((transitionState.fraction * totalDuration).toInt())
                    ) { value, _ ->
                        this@LaunchedEffect.launch {
                            if (value > 0) {
                                // Seek the original transition back to the currentState
                                transitionState.seekTo(value)
                            }
                            if (value == 0f) {
                                // Once we animate to the start, we need to snap to the right state.
                                transitionState.snapTo(backStackEntry)
                            }
                        }
                    }
                }
            }
        }

        sheetContent = {
            PredictiveBackHandler(backStack.size > 1) { backEvent ->
                var currentBackStackEntry: NavBackStackEntry? = null
                if (backStack.size > 1) {
                    progress = 0f
                    currentBackStackEntry = backStack.lastOrNull()
                    state.prepareForTransition(currentBackStackEntry!!)
                    val previousEntry = backStack[backStack.size - 2]
                    state.prepareForTransition(previousEntry)
                }
                try {
                    backEvent.collect {
                        if (backStack.size > 1) {
                            inPredictiveBack = true
                            progress = it.progress
                        }
                    }
                    if (backStack.size > 1) {
                        inPredictiveBack = false
                        popBackStack(currentBackStackEntry!!, false)
                    }
                } catch (_: CancellationException) {
                    if (backStack.size > 1) {
                        inPredictiveBack = false
                    }
                }
            }
            transition.AnimatedContent(
                transitionSpec = {
                    // If the initialState of the AnimatedContent is not in visibleEntries, we are in
                    // a case where visible has cleared the old state for some reason, so instead of
                    // attempting to animate away from the initialState, we skip the animation.
                    if (initialState in visibleEntries) {
                        ContentTransform(
                            fadeIn(),
                            fadeOut(),
                            1f,
                            SizeTransform(true)
                        )
                    } else {
                        EnterTransition.None togetherWith ExitTransition.None
                    }
                },
                contentKey = { it?.id }
            ) {
                // In some specific cases, such as clearing your back stack by changing your
                // start destination, AnimatedContent can contain an entry that is no longer
                // part of visible entries since it was cleared from the back stack and is not
                // animating. In these cases the currentEntry will be null, and in those cases,
                // AnimatedContent will just skip attempting to transition the old entry.
                // See https://issuetracker.google.com/238686802
                val isPredictiveBackCancelAnimation = transitionState.currentState == backStackEntry
                val currentEntry =
                    if (inPredictiveBack || isPredictiveBackCancelAnimation) {
                        // We have to do this because the previous entry does not show up in
                        // visibleEntries
                        // even if we prepare it above as part of onBackStackChangeStarted
                        it
                    } else {
                        visibleEntries.lastOrNull { entry -> it == entry }
                    }

                // while in the scope of the composable, we provide the currentEntry as the
                // ViewModelStoreOwner and LifecycleOwner
                currentEntry?.LocalOwnersProvider(saveableStateHolder) {
                    val content = (currentEntry.destination as Destination).content
                    content(currentEntry)
                }
            }
        }

        LaunchedEffect(backStack) {
            onDismissRequest = {
                // Clear state
                backStack.reversed().forEach {
                    state.pop(it, false)
                }
                sheetEnabled = false
            }
        }
        val scope = rememberCoroutineScope()

        animateToDismiss = {
            // The order is important here.
            // First, we mark the current screen as animating using popWithTransition.
            // Then we animate the bottomsheet to hidden state.
            // Once the sheet is hidden we remove it from the composition (sheetEnabled = false)
            // and then mark the animation finished.
            //
            // This is necessary so that navigating from a bottom sheet to a normal
            // destination will work fine. For example, BottomSheetWithClose to Zoom.
            backStackEntry?.let { entry ->
                state.popWithTransition(popUpTo = entry, saveState = false)
                scope
                    .launch { sheetState.hide() }
                    .invokeOnCompletion {
                        sheetEnabled = false
                        state.markTransitionComplete(entry)
                    }
            }
        }

        animateBack = {
            val current = backStack.lastOrNull()
            current?.let {
                state.popWithTransition(current, false)
            }
        }
    }

    override fun onAttach(state: NavigatorState) {
        super.onAttach(state)
        attached = true
    }

    override fun createDestination(): Destination = Destination(
        navigator = this,
        content = {}
    )

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        // Push new item with transition if bottomsheet is already open.
        // If it's not, bottomsheet will animate the appearance anyway.
        if (backStack.value.isNotEmpty()) {
            entries.fastForEach { entry ->
                state.pushWithTransition(entry)
            }
        } else {
            sheetEnabled = true
            entries.fastForEach { entry ->
                state.push(entry)
            }
        }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        val backStack = state.backStack.value
        if (backStack.firstOrNull() == popUpTo) {
            animateToDismiss()
        } else {
            animateBack()
        }
    }

    /**
     * [NavDestination] specific to [BottomSheetNavigator]
     */
    @NavDestination.ClassType(Composable::class)
    public class Destination(
        navigator: BottomSheetNavigator,
        internal val content: @Composable ColumnScope.(NavBackStackEntry) -> Unit
    ) : NavDestination(navigator), FloatingWindow
}
