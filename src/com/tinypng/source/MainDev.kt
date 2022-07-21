package com.tinypng.source

import com.tinify.AccountException
import com.tinify.TiniPng
import java.io.File
import kotlin.collections.ArrayList


interface LogCall {
    fun onLog(log: String) {
    }
}

object MainDev {

    private var mListener: LogCall? = null
    private var mProjectConfig: TinyPngConfig? = null
    private val md5s = ArrayList<String>()
    private var mRecordPath = ""

    @JvmStatic
    fun start(configPath: String, recordPath: String, listener: LogCall? = null) {
        mListener = listener
        mRecordPath = recordPath
        mProjectConfig = ConfigUtils.loadProjectConfig(configPath)
        Thread {
            loadTinyFileMD5()
            setTinypngKey()
            startTinypngAll()
            mListener?.onLog("压缩结束")
        }.start()
    }

    private fun startTinypngAll() {
        mProjectConfig?.paths?.forEach { root ->
            scanImageFile(root) { file ->
                executeIO {
                    tinypng(file)
                }
            }
        }
    }

    private fun setTinypngKey(): Boolean {
        if ((mProjectConfig?.keys?.size ?: 0) > 0) {
            val key = mProjectConfig?.keys?.get(0)
            mProjectConfig?.keys?.removeAt(0)
            mListener?.onLog("设置Key:${key}")
            TiniPng.setKey(key)
            return true
        }
        return false
    }

    private fun loadTinyFileMD5() {
        val config = ConfigUtils.loadFileMD5Record(mRecordPath)
        val split = config.split(",")
        md5s.addAll(split)
    }

    private fun tinypng(path: String) {
        try {
            mListener?.onLog("开始压缩:${path}")
            val md5 = MD5Util.generateMD5(path)
            if (md5s.contains(md5)) {
                mListener?.onLog("已经压缩过:${path}")
                return
            }
            TiniPng.startTiniPng(path, path)
            tinyOK(path)
        } catch (e: Exception) {
            if (e is AccountException) {
                if (setTinypngKey()) {
                    tinypng(path)
                } else {
                    mListener?.onLog("已经没有可用的key")
                }
            } else {
                e.printStackTrace()
            }
        }
    }

    private fun tinyOK(path: String) {
        mListener?.onLog("压缩成功:${path}")
        val md5 = MD5Util.generateMD5(path)
        ConfigUtils.appendFileMD5Record(mRecordPath, md5)
    }

    private fun executeIO(runnable: () -> Unit) {
        runnable.invoke()
    }

    private fun scanImageFile(root: String, next: (path: String) -> Unit) {
        val rootFile = File(root)
        val files = rootFile.listFiles()
        files?.forEach { file ->
            val pathFile = file.absolutePath.lowercase()
            if (pathFile.endsWith(".png") || pathFile.endsWith(".jpg")) {
                next.invoke(file.absolutePath)
            } else {
                if (file.isDirectory) {
                    scanImageFile(file.absolutePath, next)
                }
            }
        }
    }
}