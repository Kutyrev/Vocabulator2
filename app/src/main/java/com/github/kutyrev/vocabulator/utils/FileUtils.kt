package com.github.kutyrev.vocabulator.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

private const val SCHEME = "content"

fun getFileName(context: Context, uri: Uri): String? {
    if (uri.scheme == SCHEME) {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (cursor != null) {
                if(cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if(columnIndex > 0) {
                        return cursor.getString(columnIndex)
                    }
                }
            }
        }
    }

    return uri.path?.substring(uri.path!!.lastIndexOf('/') + 1)
}
