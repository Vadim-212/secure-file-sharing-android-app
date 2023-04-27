package com.vadim212.securityfilesharingapp.domain.utils

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipFilesHelper: Helper {
    @Throws(IOException::class)
    fun zipFiles(files: List<String>, archiveSavePath: File) {
        val BUFFER_SIZE = 6 * 1024
        var origin: BufferedInputStream? = null
        val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(archiveSavePath)))
        try {
            val data = ByteArray(BUFFER_SIZE)
            for (i in files.indices) {
                val fi = FileInputStream(files[i])
                origin = BufferedInputStream(fi, BUFFER_SIZE)
                try {
                    val entry = ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1))
                    out.putNextEntry(entry)
                    var count = origin.read(data, 0, BUFFER_SIZE)
                    while (count != -1) {
                        out.write(data, 0, count)
                        count = origin.read(data, 0, BUFFER_SIZE)
                    }
                } finally {
                    origin.close()
                }
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
}