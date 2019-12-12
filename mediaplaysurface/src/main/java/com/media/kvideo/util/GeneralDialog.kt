package com.media.kvideo.util

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.StyleRes

/**
 * create by 2019/12/10
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */
class GeneralDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    private var isOutSide = false
    private var mWindow: Window? = null
    fun getView(id: Int): View {
        val rootView = mWindow!!.decorView
        return rootView.findViewById(id)
    }

    init {
        mWindow = window
    }

    private fun setCanceledOnTouchOutside() {
        setCanceledOnTouchOutside(isOutSide)
    }
     class Builder(mContext: Context, style: Int) {
        private val mGeneralDialog: GeneralDialog = GeneralDialog(mContext, style)
        private val mLayout: WindowManager.LayoutParams
        private var window:Window?=null
        init {
            mLayout = mGeneralDialog.mWindow!!.attributes
            window=mGeneralDialog.mWindow
        }
        fun setGravity(gravity: Int): Builder {
            window!!.setGravity(gravity)
            return this
        }

        fun addView(resId: Int): Builder {
            window!!.setContentView(resId)
            return this
        }

        fun setCanceledOnTouchOutside(touchOutside: Boolean): Builder {
            mGeneralDialog.isOutSide = touchOutside
            return this
        }

        fun setAnimationStyle(@StyleRes animationStyle: Int): Builder {
            window!!.setWindowAnimations(animationStyle)
            return this
        }

        fun setAlpha(alpha: Float): Builder {
            mLayout.alpha = alpha

            return this
        }

        fun setDimAmount(dimAmount: Float): Builder {
            window!!.setDimAmount(dimAmount)
            return this
        }

        fun setHorizontalMargin(horizontalMargin: Int): Builder {
            mLayout.horizontalMargin = horizontalMargin.toFloat()
            return this
        }

        fun setVerticalMargin(verticalMargin: Int): Builder {
            mLayout.verticalMargin = verticalMargin.toFloat()
            return this
        }

        fun create(): GeneralDialog {
            window!!.attributes = mLayout
            mGeneralDialog.setCanceledOnTouchOutside()
            return mGeneralDialog
        }
    }
}