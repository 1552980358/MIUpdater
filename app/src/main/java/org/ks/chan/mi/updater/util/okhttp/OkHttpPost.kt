package org.ks.chan.mi.updater.util.okhttp

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.ks.chan.mi.updater.util.coroutine.ioBlocking

suspend fun httpPost(
    url: String,
    postBody: String,
    contentType: MediaType,
): Response {
    val requestBody: RequestBody = postBody.toRequestBody(
        contentType = contentType
    )

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    return ioBlocking {
        okHttpClient.newCall(request)
            .execute()
    }
}