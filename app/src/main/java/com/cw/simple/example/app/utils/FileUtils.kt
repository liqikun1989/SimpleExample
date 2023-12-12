package com.cw.simple.example.app.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.cw.simple.example.app.log.KLog
import org.apache.tools.zip.ZipFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*

object FileUtils {
    @JvmStatic
    fun deleteFile(file: File): Boolean {
        if (file.exists()) {
            if (file.isFile) {
                file.delete()
                return true
            } else if (file.isDirectory) {
                val files = file.listFiles()
                if (files != null) {
                    for (i in files.indices) {
                        deleteFile(files[i])
                    }
                }
                file.delete()
            }
            return true
        }
        return true
    }

    @SuppressLint("DiscouragedPrivateApi")
    fun getExternalStorageDirectory(application: Application): File {
        return try {
            Environment.getExternalStorageDirectory()
        } catch (e: Throwable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                    val list = storageManager.storageVolumes
                    for (item in list) {
                        val mPath = StorageVolume::class.java.getDeclaredField("mPath")
                        mPath.isAccessible = true
                        val file = mPath[item] as File
                        if (file.exists()) {
                            return file
                        }
                    }
                } catch (e1: Throwable) {
                    e1.printStackTrace()
                }
            }
            try {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).parentFile
            } catch (ee: java.lang.Exception) {
                File("/storage/emulated/0")
            }
        }
    }

    fun unzipFile(zipFilePath: String, outPath: String?): Boolean {
        val zipFile = File(zipFilePath)
        if (!zipFile.exists() || outPath.isNullOrEmpty()) {
            return false
        }
        val outFile = File(outPath)
        if (!outFile.exists()) {
            outFile.mkdirs()
        }
        try {
            ZipFile(zipFile).use { z ->
                val files: Enumeration<org.apache.tools.zip.ZipEntry> = z.entries
                var ff: File
                while (files.hasMoreElements()) {
                    val entry: org.apache.tools.zip.ZipEntry = files.nextElement()
                    if ("../".equals(entry.name, ignoreCase = true)) {
                        continue
                    }
                    ff = File(outPath + File.separator + entry.name)
                    if (!ff.parentFile.exists()) {
                        ff.parentFile.mkdirs()
                    }
                    if (entry.isDirectory) {
                        ff.mkdirs()
                        continue
                    }
                    val zis: InputStream = z.getInputStream(entry)
                    writeData(ff, zis)
                }
                return true
            }
        } catch (e: Exception) {
            KLog.e("unzip error $zipFilePath", e)
            return false
        }
    }

    /***
     * 将数据流写入到文件中
     * @param outfile 输出文件
     * @param inputStream 输出数据流
     * @param inputStream 输出数据流
     * @return
     */
    private fun writeData(outfile: File, inputStream: InputStream): Boolean {
        var len = 0
        val buffer = ByteArray(1024)
        try {
            FileOutputStream(outfile).use { fos ->
                val channel = fos.channel
                while (inputStream.read(buffer).also { len = it } > 0) {
                    try {
                        channel.write(ByteBuffer.wrap(buffer, 0, len))
                    } catch (e: IOException) {
                        if (e.message!!.contains("EAGAIN")) {
                            KLog.e("unzip file fail=" + outfile.absolutePath)
                        }
                    }
                }
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getFileNameFromPath(path: String?): String? {
        if (path.isNullOrEmpty()) {
            return null
        }
        return try {
            path.substring(path.lastIndexOf("/") + 1)
        } catch (e: Exception) {
            null
        }
    }
}