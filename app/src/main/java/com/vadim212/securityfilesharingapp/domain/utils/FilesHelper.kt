package com.vadim212.securityfilesharingapp.domain.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Base64
import java.io.File
import java.util.*

class FilesHelper(private val context: Context, private val contentResolver: ContentResolver): Helper {


    fun generateFileNameWithUUIDAndInitializationVector(initializationVector: ByteArray): String {
        return UUID.randomUUID().toString().replace("-","") + "_" + Base64.encodeToString(initializationVector, Base64.DEFAULT)
    }

    fun parseUUIDFromGeneratedFileName(filename: String): String {
        return filename.split("_").first()
    }

    fun parseInitializationVectorFromGeneratedFileName(filename: String): ByteArray {
        return Base64.decode(filename.split("_").last(), Base64.DEFAULT)
    }

    fun saveBytesToFileDir(dataBytes: ByteArray, filename: String, fileSaveDir: File) {//, contextWrapperDir: String) {
        //val fileToWriteDir = ContextWrapper(context)
        //    .getDir(contextWrapperDir, Context.MODE_PRIVATE)
        val decryptedFile = File(fileSaveDir, filename)
        decryptedFile.outputStream().apply {
            write(dataBytes)
            flush()
            close()
        }
    }

    fun getBytesFromFile(file: File): ByteArray {
        return file.inputStream().readBytes()
    }

    fun getBytesFromFilePath(filePath: String): ByteArray {
        return File(filePath).inputStream().readBytes()
    }

    fun readBytesFromUri(uri: Uri): ByteArray{
        var bytes: ByteArray? = null
        contentResolver.openInputStream(uri)?.use { inputStream ->
            bytes = inputStream.readBytes()
        }
        return bytes!!
    }

}