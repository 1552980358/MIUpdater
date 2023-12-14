package org.ks.chan.mi.updater

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.ks.chan.mi.updater.storage.XiaomiCredential
import org.ks.chan.mi.updater.storage.xiaomiCredentialDataStore
import org.ks.chan.mi.updater.util.coroutine.Default
import org.ks.chan.mi.updater.util.coroutine.io

class MainViewModel(
    private val xiaomiCredentialDataStore: DataStore<XiaomiCredential>
): ViewModel() {

    companion object Factory {
        @Composable
        fun MainActivity.mainViewModel(): MainViewModel {
            return viewModel(
                factory = viewModelFactory {
                    initializer {
                        MainViewModel(xiaomiCredentialDataStore = xiaomiCredentialDataStore)
                    }
                }
            )
        }

    }

    val isSetup = flow {
        val isSetup = xiaomiCredentialDataStore.data
            .map { xiaomiCredential -> xiaomiCredential.isSetup }
            .first()
        emit(isSetup)
    }.flowOn(Default)

    fun setupChecked() {
        viewModelScope.io {
            xiaomiCredentialDataStore.updateData { xiaomiCredential ->
                xiaomiCredential.toBuilder()
                    .setIsSetup(true)
                    .build()
            }
        }
    }

}