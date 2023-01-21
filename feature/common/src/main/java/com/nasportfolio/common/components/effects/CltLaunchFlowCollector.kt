package com.nasportfolio.common.components.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

@Composable
fun <T> CltLaunchFlowCollector(
    keys: Array<Any> = arrayOf(true),
    lifecycleOwner: LifecycleOwner,
    flow: Flow<T>,
    collector: FlowCollector<T>
) {
    LaunchedEffect(*keys) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(collector = collector)
            }
        }
    }
}