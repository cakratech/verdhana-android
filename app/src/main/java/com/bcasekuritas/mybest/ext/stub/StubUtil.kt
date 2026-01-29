package com.bcasekuritas.mybest.ext.stub

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import java.io.IOException
import java.nio.charset.Charset
import javax.inject.Inject

class StubUtil @Inject constructor(private val gson: Gson) {
    fun getJsonFromRaw(context: Context, @RawRes rawFileId: Int): String? {
        return try {
            val `is` = context.resources.openRawResource(rawFileId)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun <T> parseInto(jsonString: String?, classOfT:Class<T>, defaultObject: T? = null): T? {
        return try {
            gson.fromJson<T>(jsonString, classOfT)
        } catch (exception: Exception) {
            defaultObject
        }
    }
}