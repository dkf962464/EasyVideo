package com.rocky.kotlinplaysurface

import android.os.Handler
import android.os.Looper
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 2017/3/31.
 */

class OkHttpManager {
    private val mHandler = Handler(Looper.getMainLooper())
    private val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build()

    interface OnCallback {
        fun onError(e: IOException)

        fun onSuccess(response: Response)
    }

    operator fun get(url: String, params: Map<String, String>?, onCallback: OnCallback) {
        val buffer = StringBuffer()
        buffer.append(url)
        // 构建参数
        if (params != null && params.size > 0) {
            buffer.append("?")
            for ((key, value) in params) {
                try {
                    buffer.append(key).append("=").append(URLEncoder.encode(value, "utf-8"))
                    buffer.append("&")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

            }
            // 删除最后一个 &
            buffer.deleteCharAt(buffer.length - 1)
        }

        // 构建请求对象
        val request = Request.Builder()
            .get()
            .url(buffer.toString())
            .build()
        // 发起异步请求
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // 将子线程任务运行到主线程
                mHandler.post { onCallback.onError(e) }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                //                mHandler.post(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        onCallback.onSuccess(response);
                //                    }
                //                });
                onCallback.onSuccess(response)
            }
        })
    }


    companion object {
        private val TAG = "OkHttpUtils"
        private val params: HashMap<String, String>? = null
        private var mOkHttpUtils: OkHttpManager? = null

        val instances: OkHttpManager
            get() {
                if (null == mOkHttpUtils) {
                    mOkHttpUtils = OkHttpManager()
                }
                return mOkHttpUtils as OkHttpManager
            }
    }
}
