package com.degradators.degradators.common.lifecircle

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun LifecycleOwner.observeLifecycleIn(observer: LifecycleObserver) =
    lifecycle.addObserver(observer)