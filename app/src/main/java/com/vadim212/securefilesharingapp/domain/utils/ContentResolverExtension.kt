package com.vadim212.securefilesharingapp.domain.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

fun ContentResolver.getFileName(fileUri : Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

fun ContentResolver.getFileSize(fileUri : Uri): Long {
    var size: Long = 0
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        size = returnCursor.getLong(sizeIndex)
        returnCursor.close()
    }
    return size
}
