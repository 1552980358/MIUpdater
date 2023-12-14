package org.ks.chan.mi.updater.util.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

val falseState: MutableState<Boolean>
    get() = mutableStateOf(false)