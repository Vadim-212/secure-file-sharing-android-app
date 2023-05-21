package com.vadim212.securefilesharingapp.domain.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipFilesHelper: Helper {
    companion object {
        @Throws(IOException::class)
        fun zipFiles(filesUris: List<Uri>, archiveSavePath: File, contentResolver: ContentResolver) {
            val BUFFER_SIZE = 6 * 1024
            var origin: BufferedInputStream? = null
            val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(archiveSavePath)))
            try {
                val data = ByteArray(BUFFER_SIZE)
                for (i in filesUris.indices) {
                    val parcelFileDescriptor = contentResolver.openFileDescriptor(filesUris[i], "r")
                    val fi = FileInputStream(parcelFileDescriptor?.fileDescriptor)
                    origin = BufferedInputStream(fi, BUFFER_SIZE)
                    try {
                        val entry = ZipEntry(contentResolver.getFileName(filesUris[i]))
                        out.putNextEntry(entry)
                        var count = origin.read(data, 0, BUFFER_SIZE)
                        while (count != -1) {
                            out.write(data, 0, count)
                            count = origin.read(data, 0, BUFFER_SIZE)
                        }
                    } finally {
                        origin.close()
                    }
                    parcelFileDescriptor?.close()
                }
            } finally {
                out.close()
            }
        }

        @Throws(IOException::class)
        fun unzipFiles(archivePath: File, saveDir: String) {
            val BUFFER_SIZE = 6 * 1024
            try {
                val f = File(saveDir)
                if (!f.isDirectory) {
                    f.mkdirs()
                }
                val zin = ZipInputStream(FileInputStream(archivePath))
                try {
                    var ze = zin.nextEntry
                    while (ze != null) {
                        val path = saveDir + File.separator + ze.name
                        if (ze.isDirectory) {
                            val unzipFile = File(path)
                            if(!unzipFile.isDirectory) {
                                unzipFile.mkdirs()
                            }
                        } else {
                            val fout = FileOutputStream(path, false)
                            try {
                                var c = zin.read()
                                while (c != -1) {
                                    fout.write(c)
                                    c = zin.read()
                                }
                                zin.closeEntry()
                            } finally {
                                fout.close()
                            }
                        }
                        ze = zin.nextEntry
                    }

                } finally {
                    zin.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ZIP", "Unzip exception", e)
            }
        }

        @Throws(IOException::class)
        fun unzipFilesToUri(archivePath: File, saveDirUri: Uri, context: Context) {
            try {
                val dir = DocumentFile.fromTreeUri(context, saveDirUri)
                val zin = ZipInputStream(FileInputStream(archivePath))
                try {
                    var ze = zin.nextEntry
                    while (ze != null) {
                        val file = dir?.createFile("text/*", ze.name)
                        val parcelFileDescriptor =
                            context.contentResolver.openFileDescriptor(file?.uri!!, "w")
                        val fout = FileOutputStream(parcelFileDescriptor?.fileDescriptor)
                        fout.apply {
                            write(zin.readBytes())
                            flush()
                            close()
                        }
                        ze = zin.nextEntry
                        parcelFileDescriptor?.close()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("ZIP", "Unzip (ZipInputStream) exception", e)
                }
                finally {
                    zin.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ZIP", "Unzip exception", e)
            }
        }
    }
}