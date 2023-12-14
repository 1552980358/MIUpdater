package org.ks.chan.mi.updater.ui.screen.login.repository

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.ks.chan.mi.updater.util.EmptyStr
import org.ks.chan.mi.updater.util.coroutine.Default
import org.ks.chan.mi.updater.util.okhttp.httpGet

class XiaomiAccountLoginRepository {

    private companion object {

        const val RepositoryURL = "https://account.xiaomi.com/pass/serviceLogin"

        const val ParameterSign = "_sign"
        const val SignPassportPrefix = "2&V1_passport&"

    }

    val signPassport = flow {
        val signPassport = httpGet(RepositoryURL).request
            .url
            .queryParameter(ParameterSign)
            ?.replace(SignPassportPrefix, EmptyStr)
        emit(signPassport)
    }.flowOn(Default)

}