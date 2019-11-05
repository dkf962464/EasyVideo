package com.rocky.mediaplaysurface.util

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


/**
 * create by 2019/10/29
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */
object BaseUtil {
    fun millToMin(mill: Int): String {
        val minutes = (mill % (1000 * 60 * 60) / (1000 * 60)).toLong()
        val seconds = (mill % (1000 * 60) / 1000).toLong()
        val diffTime: String
        if (seconds < 10) {
            diffTime = "$minutes:0$seconds"
        } else {
            diffTime = "$minutes:$seconds"
        }
        return diffTime
    }
    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpVal, context.resources
                .displayMetrics
        ).toInt()
    }

//    fun isServiceRunning(mContext: Context?, className: String?): Boolean {
//        var isRunning = false
//        val activityManager = mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val serviceList = activityManager.getRunningServices(30)
//        if (serviceList.size <= 0) {
//            return false
//        }
//        for (i in serviceList.indices) {
//            if (serviceList[i].service.className == className) {
//                isRunning = true
//                break
//            }
//        }
//        return isRunning
//    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun setLandScreenStatusBarState(activity: Activity) {
        val decorView = activity.window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//隐藏nav栏
                or View.SYSTEM_UI_FLAG_FULLSCREEN//隐藏状态栏

                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun showStatusBar(activity: Activity) {
        val decorView = activity.window.decorView
        //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        //设置状态栏颜色为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
     fun clearAnim(context: Context) {
        val activity:Activity=context as Activity
        val decorView = activity.window.decorView as ViewGroup
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val f = decorView.layoutParams as WindowManager.LayoutParams
        f.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_SEAMLESS
        windowManager.updateViewLayout(decorView, f)
    }
}