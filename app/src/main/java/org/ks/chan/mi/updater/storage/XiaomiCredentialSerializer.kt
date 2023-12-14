package org.ks.chan.mi.updater.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import java.io.InputStream
import java.io.OutputStream

object XiaomiCredentialSerializer: Serializer<XiaomiCredential> {

    override val defaultValue: XiaomiCredential
        get() = XiaomiCredential.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): XiaomiCredential {
        XiaomiCredential.newBuilder()
            .build()
        return XiaomiCredential.parseFrom(input)
    }

    override suspend fun writeTo(t: XiaomiCredential, output: OutputStream) {
        t.writeTo(output)
    }

}

private const val FileName = "XiaomiCredential.pb"
val Context.xiaomiCredentialDataStore: DataStore<XiaomiCredential> by dataStore(
    fileName = FileName, serializer = XiaomiCredentialSerializer
)