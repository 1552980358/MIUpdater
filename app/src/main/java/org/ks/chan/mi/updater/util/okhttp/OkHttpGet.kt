package org.ks.chan.mi.updater.util.okhttp

import okhttp3.Request
import okhttp3.Response
import org.ks.chan.mi.updater.util.coroutine.ioBlocking

suspend fun httpGet(
    url: String
): Response {
    val request = Request.Builder()
        .url(url)
        .build()

    return ioBlocking {
        okHttpClient.newCall(request)
            .execute()
    }
}