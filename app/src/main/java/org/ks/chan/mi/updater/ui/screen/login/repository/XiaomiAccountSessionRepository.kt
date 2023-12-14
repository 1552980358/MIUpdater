package org.ks.chan.mi.updater.ui.screen.login.repository

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.ks.chan.mi.updater.util.coroutine.Default
import org.ks.chan.mi.updater.util.okhttp.httpGet
import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class XiaomiAccountSessionRepository(location: String, security: String, nonce: Long) {

    private companion object {

        const val SHA1 = "SHA-1"

        const val SetCookie = "Set-Cookie"
        const val SemiColon = ';'
        const val ServiceToken = "serviceToken"
    }

    private fun buildClientSignMessage(nonce: Long, security: String): ByteArray {
        return "nonce=${nonce}&$security".toByteArray()
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun buildClientSignParam(nonce: Long, security: String): String {
        val clientSignMessage = buildClientSignMessage(nonce, security)
        val digest = MessageDigest.getInstance(SHA1)
            .apply { update(clientSignMessage) }
            .digest()
        return Base64.encode(digest)
    }

    private fun buildRequestUrl(location: String, clientSign: String): String {
        return "${location}&_userIdNeedEncrypt=true&clientSign=${clientSign}"
    }

    val serviceToken = flow {
        val clientSign = buildClientSignParam(nonce, security)

        val response = httpGet(buildRequestUrl(location, clientSign))
        val sessionToken = response.headers(SetCookie).find { it.contains(ServiceToken) }
            ?.run {
                val startIndex = indexOf(ServiceToken) + ServiceToken.length
                val endIndex = indexOf(SemiColon, startIndex)
                substring(startIndex, endIndex)
            }
        emit(sessionToken)
    }.flowOn(Default)

}