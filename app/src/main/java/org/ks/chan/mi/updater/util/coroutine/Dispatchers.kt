package org.ks.chan.mi.updater.util.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

val Main: CoroutineDispatcher
    get() = Dispatchers.Main

val Default: CoroutineDispatcher
    get() = Dispatchers.Default

val IO: CoroutineDispatcher
    get() = Dispatchers.IO