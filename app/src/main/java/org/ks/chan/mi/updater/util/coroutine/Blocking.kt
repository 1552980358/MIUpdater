package org.ks.chan.mi.updater.util.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

suspend fun <T> ioBlocking(block: suspend CoroutineScope.() -> T) =
    withContext(context = IO, block = block)