package com.laohei.bili_tube.core.runtime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberBroadcastReceiver(
    vararg actions: String,
    context: Context = LocalContext.current
): State<Intent?> {
    val intentState = remember { mutableStateOf<Intent?>(null) }

    DisposableEffect(context, actions) {
        val filter = IntentFilter().apply {
            actions.forEach { addAction(it) }
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                intentState.value = intent
            }
        }
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    return intentState
}
