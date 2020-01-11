package com.media.kvideo.surfaceview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.media.kvideo.R
import com.media.kvideo.util.BaseUtil
import java.lang.Math.abs

/**
 * create by 2019/10/29
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */

class EasyVideo : ConstraintLayout {
    //java new
    private var progressDrawable: Drawable? = null
    private var thumb: Drawable? = null
    private var fullScreen: Drawable? = null
    private var exitFullScreen: Drawable? = null
    private var playPauseIcon: Drawable? = null
    private var startTimeColor: Int = 0
    private var endTimeColor: Int = 0
    private var autoPlay: Int = 1
    var radius = 0f
    var topLeftRadius = 0f
    var topRightRadius = 0f
    var bottomRightRadius = 0f
    var bottomLeftRadius = 0f
    private var isShowSeekbar: Boolean = true
    private var playUrl: String? = null
    //一定要设置此view的透明度为0.5f
    private var bottomBackgroundColor: Int = 0
    private var seekBarHeight: Float? = 1f
    var jvavIsplay: Boolean = false
    private var inflater: LayoutInflater? = null
    private var reduceAdd: TextView? = null
    private var allTime: TextView? = null
    private var currentTime: TextView? = null
    private var playPause: ImageView? = null
    private var fullScreenView: ImageView? = null
    private var seekBar: SeekBar? = null
    private var view: View? = null
    private var mediaPlaySurfaceView: MediaPlaySurfaceView? = null
    private var rootView: ConstraintLayout? = null
    private var mHandler: Handler
    private var UPDATE_PLAY_ICON: Int = 0
    private var UPDATE_PAUSE_ICON: Int = 1
    private var PLAY_URL: Int = 3
    private var HIDE_CONTROL = 4
    private var params: LayoutParams? = null
    private var current: LayoutParams? = null
    private var progressbarLayout: LayoutParams? = null
    private var loading: ProgressBar? = null
    private var surfaceViewParams: LayoutParams? = null
    private var pg: Int = 0
    private var YMargin:Float=0f
    companion object {
        var ISTOUNCHUP: Boolean = true
        var isTounch: Boolean = true
    }

    constructor(context: Context?) : super(context) {
        setWillNotDraw(false)
        initData()
    }

    constructor(context: Context?, attr: AttributeSet?) : super(context, attr) {
        context!!
        setWillNotDraw(false)
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.EasyVideo)
        progressDrawable = typedArray.getDrawable(R.styleable.EasyVideo_progressDrawable)
        thumb = typedArray.getDrawable(R.styleable.EasyVideo_thumb)
        fullScreen = typedArray.getDrawable(R.styleable.EasyVideo_fullScreen)
        exitFullScreen = typedArray.getDrawable(R.styleable.EasyVideo_exitFullScreen)
        playPauseIcon = typedArray.getDrawable(R.styleable.EasyVideo_playPauseIcon)
        startTimeColor = typedArray.getColor(R.styleable.EasyVideo_startTimeColor, Color.WHITE)
        endTimeColor = typedArray.getColor(R.styleable.EasyVideo_endTimeColor, Color.WHITE)
        bottomBackgroundColor = typedArray.getColor(R.styleable.EasyVideo_bottomBackgroundColor, Color.BLACK)
        seekBarHeight = typedArray.getDimension(R.styleable.EasyVideo_seekBarHeight, 1f)
        autoPlay = typedArray.getInt(R.styleable.EasyVideo_autoPlay, 1)
        isTounch = typedArray.getBoolean(R.styleable.EasyVideo_isTounch, true)
        isShowSeekbar = typedArray.getBoolean(R.styleable.EasyVideo_isShowSeekbarController, true)
        playUrl = typedArray.getString(R.styleable.EasyVideo_playUrl)
        radius = typedArray.getDimension(R.styleable.EasyVideo_radius, 0f)
        topLeftRadius = typedArray.getDimension(R.styleable.EasyVideo_topLeftRadius, 0f)
        topRightRadius = typedArray.getDimension(R.styleable.EasyVideo_topRightRadius, 0f)
        bottomRightRadius = typedArray.getDimension(R.styleable.EasyVideo_bottomRightRadius, 0f)
        bottomLeftRadius = typedArray.getDimension(R.styleable.EasyVideo_bottomLeftRadius, 0f)
        typedArray.recycle()
        initData()
    }

    //横竖屏切换
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("ObsoleteSdkInt")
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        seekBar
        BaseUtil.clearAnim(context) //清除横竖屏切换的动画，避免卡顿
        val currentParams: LayoutParams = currentTime!!.layoutParams as LayoutParams
        val allTimeParams: LayoutParams = allTime!!.layoutParams as LayoutParams
        val playPauseParams: LayoutParams = playPause!!.layoutParams as LayoutParams
        val fullScreenViewParams: LayoutParams = fullScreenView!!.layoutParams as LayoutParams

        if (newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("marginTopIsJl", "ORIENTATION_LANDSCAPE$y")
            setWeight(currentParams, currentTime, 0.2f)
            setWeight(allTimeParams, allTime, 0.2f)
            setWeight(playPauseParams, playPause, 0.15f)
            setWeight(fullScreenViewParams, fullScreenView, 0.15f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                BaseUtil.setLandScreenStatusBarState(context as Activity)
            }
            if (null != mediaPlaySurfaceView) {
                mediaPlaySurfaceView!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
            //设置easyvideo在横屏的时候，绝对位置为0，防止出现控件移位问题
            y=0f
            top=0
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("marginTopIsJl", "onConfigurationChanged$y")
            BaseUtil.showStatusBar(context as Activity)
            setWeight(currentParams, currentTime, 0.5f)
            setWeight(allTimeParams, allTime, 0.5f)
            setWeight(playPauseParams, playPause, 0.2f)
            setWeight(fullScreenViewParams, fullScreenView, 0.2f)
            if (null != mediaPlaySurfaceView) {
                mediaPlaySurfaceView!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
            layoutParams =LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            //在竖屏的时候，设置easyview的绝对位置为第一次测量时候的值，防止出现控件还原到位置的0,0坐标
            if (y==0f){
                y= abs(YMargin)
            }
        }

    }

    init {
        if (null == inflater) {
            inflater = LayoutInflater.from(context)
        }
        mHandler = initHandler()
        settingForceAble(context)
        view = inflater!!.inflate(R.layout.default_video_controller, this, false)
        currentTime = view!!.findViewById(R.id.current_time)
        allTime = view!!.findViewById(R.id.all_time)
        playPause = view!!.findViewById(R.id.play_pause_but)
        fullScreenView = view!!.findViewById(R.id.full_screen)
        seekBar = view!!.findViewById(R.id.seek_bar)
        rootView = view!!.findViewById(R.id.root_view)

        mediaPlaySurfaceView = MediaPlaySurfaceView(context)
        mediaPlaySurfaceView!!.id = R.id.medially_view_id
        surfaceViewParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mediaPlaySurfaceView!!.layoutParams = surfaceViewParams
        playPause!!.setOnClickListener {
            if (mediaPlaySurfaceView!!.isPlaying()) {
                pauseVideo()
                Log.e("video'sRun", "pauseVideo")
            } else {
                playVideo()
                Log.e("video'sRun", "playVideo")
            }
        }
        settingSeekBarSlideTouch(seekBar)
        isFocusableInTouchMode = true

    }

    private fun initData() {
        settingPlayIcon()

            val value: Activity = context as Activity
            currentTime!!.setTextColor(startTimeColor)
            allTime!!.setTextColor(endTimeColor)
            rootView!!.setBackgroundColor(bottomBackgroundColor)

            fullScreenView!!.setOnClickListener {
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    value.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                } else {
                    value.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                }
            }
            if (null == playPauseIcon) {
                BaseUtil.setDrawable(context, playPause, R.drawable.play_pause_selecter)
            } else {
                playPause!!.setImageDrawable(playPauseIcon)
            }
            if (null == progressDrawable) {
                seekBar!!.progressDrawable = ContextCompat.getDrawable(context, R.drawable.seekbar_style)
            } else seekBar!!.progressDrawable = progressDrawable
            if (null == thumb) {
                seekBar!!.thumb = ContextCompat.getDrawable(context, R.drawable.seekbar_thume)
            } else
                seekBar!!.thumb = thumb
            removeAllViews()
            addView(mediaPlaySurfaceView)
            params = view!!.layoutParams as LayoutParams
            params!!.bottomToBottom = R.id.medially_view_id
            view!!.layoutParams = params
            if (!isShowSeekbar) {
                view!!.visibility = View.GONE
            }
            view!!.setBackgroundColor(Color.TRANSPARENT)
            addView(view)

            reduceAdd = TextView(context)
            reduceAdd!!.id = R.id.rd_id
            current = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            current!!.leftToLeft = R.id.medially_view_id
            current!!.rightToRight = R.id.medially_view_id
            current!!.topToTop = R.id.medially_view_id
            current!!.topMargin = BaseUtil.dp2px(context, 40f)
            reduceAdd!!.layoutParams = current
            addView(reduceAdd)
            loading = ProgressBar(context)
            progressbarLayout = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            progressbarLayout!!.leftToLeft = R.id.medially_view_id
            progressbarLayout!!.rightToRight = R.id.medially_view_id
            progressbarLayout!!.topToTop = R.id.medially_view_id
            progressbarLayout!!.bottomToBottom = R.id.medially_view_id
            loading!!.indeterminateDrawable = ContextCompat.getDrawable(context, R.drawable.loading_rote_anim)
            loading!!.layoutParams = progressbarLayout
            loading!!.visibility = View.GONE
            addView(loading)
            mHandler.sendEmptyMessage(PLAY_URL)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val resolveWidth = mediaPlaySurfaceView!!.resolveSize(measuredWidth, widthMeasureSpec, 0)
        val resolveHeight = mediaPlaySurfaceView!!.resolveSize(measuredHeight, heightMeasureSpec, 1)
        setMeasuredDimension(resolveWidth!!, resolveHeight!!)
        Log.e("marginTopIsJl","onMeasure\t$top")
        //测量控件的结果在此记录，方便在横竖屏的时候使用
        if (y!=0f){
            YMargin=y
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        Log.e("marginTopIsJl","onDraw\t$top")

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        clip(canvas, radius, radius, rect)
        super.onDraw(canvas)
    }

    fun loadVideo(playUrl: String) {
        if (null != mediaPlaySurfaceView) {
            mHandler.sendEmptyMessage(UPDATE_PLAY_ICON)
            mediaPlaySurfaceView!!.playVideo(playUrl, currentTime, allTime, seekBar, loading, view)
        }
    }

    private fun clip(canvas: Canvas?, radiusX: Float, RadiusY: Float, rectF: RectF) {
        val path = Path()
        if (radius <= 0) {
            val floatArray = floatArrayOf(
                topLeftRadius,
                topLeftRadius,
                topRightRadius,
                topRightRadius,
                bottomRightRadius,
                bottomRightRadius,
                bottomLeftRadius,
                bottomLeftRadius
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                path.addRoundRect(rectF.left, rectF.top, rectF.width(), rectF.height(), floatArray, Path.Direction.CCW)
            }
        } else {
            path.addRoundRect(rectF, radiusX, RadiusY, Path.Direction.CCW)
        }

        if (Build.VERSION.SDK_INT >= 26) {
            canvas!!.clipPath(path)
        } else {
            canvas!!.clipPath(path, Region.Op.REPLACE)
        }

    }

    private fun playVideo() {
        mediaPlaySurfaceView!!.play()
        mediaPlaySurfaceView!!.updateProgress()
        mHandler.sendEmptyMessage(UPDATE_PLAY_ICON)
    }

    private fun pauseVideo() {
        mediaPlaySurfaceView!!.pause()
        mHandler.sendEmptyMessage(UPDATE_PAUSE_ICON)

    }

    private fun setWeight(layoutParams: LayoutParams?, view: View?, weight: Float?) {
        layoutParams!!.horizontalWeight = weight!!
        view!!.layoutParams = layoutParams
    }

    fun destroyMedially() {
        if (null != mediaPlaySurfaceView) mediaPlaySurfaceView!!.destroy()
    }

    @SuppressWarnings("WeakerAccess, unused,unchecked,all")
    fun invalidateVideo() {
        if (null != mediaPlaySurfaceView) {
            mediaPlaySurfaceView!!.invalidateVideo()
        }
    }

    private fun initHandler(): Handler {
        return Handler(Looper.getMainLooper()) {
            when (it.what) {
                UPDATE_PLAY_ICON -> {
                    playPause!!.isSelected = true
                    playPause!!.isPressed = true
                    playPause!!.isFocusable = true
                }
                UPDATE_PAUSE_ICON -> {
                    playPause!!.isSelected = false
                    playPause!!.isPressed = false
                    playPause!!.isFocusable = false
                }
                PLAY_URL -> {
                    if (autoPlay == 0) {
                        if (null != playUrl) {
                            loadVideo(playUrl!!)
                        } else {
                            Toast.makeText(
                                context,
                                " auto play  failure , because play url is null ! Do you really set the url property in xml ?",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                HIDE_CONTROL -> {
                    if (!ISTOUNCHUP) {
                        BaseUtil.translationAnim(view!!, false)
                    }
                }
            }
            false
        }
    }

    private fun settingSeekBarSlideTouch(seekBar: SeekBar?) {
        seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                pg = progress
                ISTOUNCHUP = false
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar!!.progress = pg
                mediaPlaySurfaceView!!.seekTo(pg)
                mediaPlaySurfaceView!!.play()
                if (null != mediaPlaySurfaceView) {
                    mediaPlaySurfaceView!!.updateProgress()
                }
                mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 3000)
                ISTOUNCHUP = true
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val activity: Activity = context as Activity
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun settingForceAble(context: Context?) {
        val activity: Activity = context as Activity
        val decorView = activity.window.decorView
        decorView.isFocusable = true
        decorView.isFocusableInTouchMode = true
        decorView.requestFocus()
    }

    private fun settingPlayIcon() {
        if (null == fullScreen || null == exitFullScreen) {
            if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                BaseUtil.setDrawable(context, fullScreenView, R.drawable.full)
            } else {
                BaseUtil.setDrawable(context, fullScreenView, R.drawable.exit_full)
            }
        } else {
            if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                fullScreenView!!.setImageDrawable(fullScreen)
            } else {
                fullScreenView!!.setImageDrawable(exitFullScreen)
            }
        }
    }
}