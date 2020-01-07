package com.media.kvideo.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat


/**
 * create by 2019/10/29
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */
object BaseUtil {
    var isShowControl: Boolean = true
    fun millToMin(mill: Int): String {
        val hours = (mill % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60).toLong()
        val minutes = (mill % (1000 * 60 * 60) / (1000 * 60)).toLong()
        val seconds = (mill % (1000 * 60) / 1000).toLong()
        val diffTime: String
        diffTime = if (hours <= 0) {
            if (seconds < 10) {
                "$minutes:0$seconds"
            } else {
                "$minutes:$seconds"
            }
        } else {
            if (minutes < 10) {
                "$hours:0$minutes:$seconds"
            } else {
                "$hours:$minutes:$seconds"
            }
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

    fun setLandScreenStatusBarState(activity: Activity) {
        val decorView = activity.window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//隐藏nav栏
                or View.SYSTEM_UI_FLAG_FULLSCREEN//隐藏状态栏

                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }


    fun showStatusBar(activity: Activity) {
        val decorView = activity.window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun clearAnim(context: Context) {
        val activity: Activity = context as Activity
        val decorView = activity.window.decorView as ViewGroup
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val f = decorView.layoutParams as WindowManager.LayoutParams
        f.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_SEAMLESS
        windowManager.updateViewLayout(decorView, f)
    }

    fun setDrawable(context: Context?, imageView: ImageView?, drawable: Int) {
        imageView!!.setImageDrawable(ContextCompat.getDrawable(context!!, drawable))
    }

    fun setTextColor(textView: TextView, string: String, firstColor: Int, secondColor: Int, endPosition: Int) {
        val spannableString = SpannableString(string)
        //设置颜色
        spannableString.setSpan(ForegroundColorSpan(secondColor), 0, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //设置字体大小，true表示前面的字体大小20单位为dip
        spannableString.setSpan(AbsoluteSizeSpan(17, true), 0, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            AbsoluteSizeSpan(17, true),
            endPosition,
            string.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //设置字体，BOLD为粗体
        spannableString.setSpan(
            ForegroundColorSpan(firstColor),
            endPosition,
            string.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), endPosition, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
    }

    fun translationAnim(view: View, isshow: Boolean) {
        val holder: PropertyValuesHolder? = if (isshow) {
            isShowControl = true
            PropertyValuesHolder.ofFloat("translationY", view.height.toFloat(), 0f)

        } else {
            isShowControl = false
            PropertyValuesHolder.ofFloat("translationY", 0f, view.height.toFloat())
        }
        Log.e("animIsRunning", "run")
        ObjectAnimator.ofPropertyValuesHolder(view, holder).start()
    }

    fun fastForward(view: View, isUp: Boolean) {
        val translationX: ObjectAnimator = if (!isUp) {
            ObjectAnimator.ofFloat(view, "translationX", view.width.toFloat()-150, 0f)

        } else {
            ObjectAnimator.ofFloat(view, "translationX", 0f, view.width.toFloat()-150)
        }
        translationX.repeatCount = 0
        translationX.duration = 1500
        val mAnimatorSet = AnimatorSet()
        mAnimatorSet.duration = 1500
        mAnimatorSet.interpolator = LinearInterpolator()
        mAnimatorSet.playTogether(translationX)
        mAnimatorSet.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!isUp){
                    view.visibility=View.GONE
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        mAnimatorSet.start()
    }
}