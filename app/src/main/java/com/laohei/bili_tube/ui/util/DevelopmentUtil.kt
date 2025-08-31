package com.laohei.bili_tube.ui.util

import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun underDevelopment(scope: CoroutineScope){
    scope.launch {
        EventBus.send(Event.AppEvent.ToastEvent(R.string.str_under_development))
    }
}