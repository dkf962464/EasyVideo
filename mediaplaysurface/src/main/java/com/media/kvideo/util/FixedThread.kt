package com.media.kvideo.util

import android.util.Log
import com.media.kvideo.interce.FixedThreadInterface
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * create by 2019/10/29
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */
object FixedThread {
    fun getThread(threadInterface: FixedThreadInterface): ExecutorService {
        var mFixedThreadPool: ExecutorService? = null
        if (null == mFixedThreadPool) {
            mFixedThreadPool = Executors.newFixedThreadPool(3)

        }
        mFixedThreadPool!!.execute {
            Log.e("execute666", "run: " + FixedThread::class.java)
            threadInterface.run()
        }
        return mFixedThreadPool
    }
}