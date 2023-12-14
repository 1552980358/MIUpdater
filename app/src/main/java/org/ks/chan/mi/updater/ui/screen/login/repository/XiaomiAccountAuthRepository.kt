package org.ks.chan.mi.updater.ui.screen.login.repository

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.ks.chan.mi.updater.util.EmptyStr
import org.ks.chan.mi.updater.util.coroutine.Default
import org.ks.chan.mi.updater.util.okhttp.httpPost
import java.security.MessageDigest

class XiaomiAccountAuthRepository(
    account: String,
    password: String,
    signPassport: String
) {

    private companion object {

        const val RepositoryURL = "https://account.xiaomi.com/pass/serviceLoginAuth2"

        const val MD5 = "MD5"
        const val PasswordFormatPattern = "%02x"

        const val RequestContentType = "application/x-www-form-urlencoded"

        const val ResponseBodyPrefix = "&&&START&&&"

    }

    private fun buildPostBody(
        account: String, passwordHash: String, signPassport: String
    ): String {
        return "_json=true&" +
                "bizDeviceType=&" +
                "user=$account&" +
                "hash=$passwordHash&" +
                "sid=miuiromota&" +
                "_sign=$signPassport&" +
                "_locale=zh_CN"
    }

    @Serializable
    data class ResponseBody(
        @SerialName("code")
        val code: Int,
        @SerialName("location")
        val location: String,
        @SerialName("ssecurity")
        val security: String = EmptyStr,
        @SerialName("userId")
        val userid: Long = -1,
        @SerialName("nonce")
        val nonce: Long = -1,
        @SerialName("description")
        val description: String,
    ) {

        companion object {
            private const val SuccessCode = 0
        }

        @Transient
        val isSuccess: Boolean
            get() = code == SuccessCode

    }

    val auth = flow {
        val passwordHash = MessageDigest.getInstance(MD5)
            .apply { update(password.toByteArray()) }
            .digest()
            .joinToString(separator = EmptyStr, transform = { PasswordFormatPattern.format(it) })
            .uppercase()

        val postBody = buildPostBody(account, passwordHash, signPassport)
        val contentType = RequestContentType.toMediaType()

        val json = Json { ignoreUnknownKeys = true }
        val response = httpPost(url = RepositoryURL, postBody = postBody, contentType = contentType)
            .body
            ?.string()
            ?.replace(ResponseBodyPrefix, EmptyStr)
            ?.run { json.decodeFromString<ResponseBody>(this) }
        emit(response)
    }.flowOn(Default)

}