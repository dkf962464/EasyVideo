package com.rocky.mediaplaysurface.surfaceview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.rocky.mediaplaysurface.R
import com.rocky.mediaplaysurface.util.BaseUtil
import com.rocky.mediaplaysurface.util.StatusBarUtil
import com.rocky.mediaplaysurface.util.StatusBarUtil.immersive

/**
 * create by 2019/10/29
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */
class SurfaceViewLayout : ConstraintLayout {
    //java new
    var progressDrawable: Drawable? = null
    var thumb: Drawable? = null
    var fullScreen: Drawable? = null
    var exitFullScreen: Drawable? = null
    var playPauseIcon: Drawable? = null
    var startTimeColor: Int? = 0
    var endTimeColor: Int? = 0
    //一定要设置此view的透明度为0.5f
    var bottomBackgroundColor: Int? = 0
    var seekBarHeight: Float? = 1f
    private var inflater: LayoutInflater? = null
    private var currentTime: TextView? = null
    private var allTime: TextView? = null
    private var playPause: ImageView? = null
    private var fullScreenView: ImageView? = null
    private var seekBar: SeekBar? = null
    private var view: View? = null
    private var mediaPlaySurfaceView: MediaPlaySurfaceView? = null
    private var rootView: ConstraintLayout? = null
    private var mHandler: Handler
    private var UPDATE_PLAY_ICON: Int = 0
    private var UPDATE_PAUSE_ICON: Int = 1
    private var params: LayoutParams? = null
    private var surfaceViewParams: LayoutParams? = null
    private var pg:Int=0
    constructor(context: Context?) : super(context) {
        Log.e("aaa666", "constructor")
    }


    constructor(context: Context?, attr: AttributeSet?) : super(context, attr) {
        context!!
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.SurfaceViewLayout)
        progressDrawable = typedArray.getDrawable(R.styleable.SurfaceViewLayout_progressDrawable)
        thumb = typedArray.getDrawable(R.styleable.SurfaceViewLayout_thumb)
        fullScreen = typedArray.getDrawable(R.styleable.SurfaceViewLayout_fullScreen)
        exitFullScreen = typedArray.getDrawable(R.styleable.SurfaceViewLayout_exitFullScreen)
        playPauseIcon = typedArray.getDrawable(R.styleable.SurfaceViewLayout_playPauseIcon)
        startTimeColor = typedArray.getColor(R.styleable.SurfaceViewLayout_startTimeColor, Color.WHITE)
        endTimeColor = typedArray.getColor(R.styleable.SurfaceViewLayout_endTimeColor, Color.WHITE)
        bottomBackgroundColor = typedArray.getColor(R.styleable.SurfaceViewLayout_bottomBackgroundColor, Color.BLACK)
        seekBarHeight = typedArray.getDimension(R.styleable.SurfaceViewLayout_seekBarHeight, 1f)
        typedArray.recycle()
    }

    //横竖屏切换
    @SuppressLint("ObsoleteSdkInt")
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val currentParams: LayoutParams = currentTime!!.layoutParams as LayoutParams
        val allTimeParams: LayoutParams = allTime!!.layoutParams as LayoutParams
        val playPauseParams: LayoutParams = playPause!!.layoutParams as LayoutParams
        val fullScreenViewParams: LayoutParams = fullScreenView!!.layoutParams as LayoutParams
        if (newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setWeight(currentParams, currentTime, 0.1f)
            setWeight(allTimeParams, allTime, 0.1f)
            setWeight(playPauseParams, playPause, 0.15f)
            setWeight(fullScreenViewParams, fullScreenView, 0.15f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                BaseUtil.setLandScreenStatusBarState(context as Activity)
            }
            surfaceViewParams=LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            if (null!=mediaPlaySurfaceView){
                mediaPlaySurfaceView!!.layoutParams=surfaceViewParams
            }
            layoutParams=LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            BaseUtil.showStatusBar(context as Activity)
            setWeight(currentParams, currentTime, 0.4f)
            setWeight(allTimeParams, allTime, 0.4f)
            setWeight(playPauseParams, playPause, 0.2f)
            setWeight(fullScreenViewParams, fullScreenView, 0.2f)
            surfaceViewParams=LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            if (null!=mediaPlaySurfaceView){
                mediaPlaySurfaceView!!.layoutParams=surfaceViewParams
            }
        }
    }

    init {
        if (null == inflater) {
            inflater = LayoutInflater.from(context)
        }
        mHandler = initHandler()

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
            if (mediaPlaySurfaceView!!.getBinder().isPlaying()) {
                pauseVideo()
                Log.e("videoisRun", "pauseVideo")
            } else {
                playVideo()
                Log.e("videoisRun", "playVideo")
            }
        }
        settingSeekBarSlideTounch(seekBar)
        isFocusableInTouchMode=true
    }
    private fun initData() {
        val value: Activity =  context as Activity
        currentTime!!.setTextColor(startTimeColor!!)
        allTime!!.setTextColor(endTimeColor!!)
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullScreenView!!.setImageDrawable(fullScreen)
        } else {
            fullScreenView!!.setImageDrawable(exitFullScreen)
        }
        fullScreenView!!.setOnClickListener {
            if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                value.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                value.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }
        playPause!!.setImageDrawable(playPauseIcon)
        seekBar!!.progressDrawable = progressDrawable
        rootView!!.setBackgroundColor(bottomBackgroundColor!!)
        seekBar!!.thumb = thumb
        removeAllViews()
        addView(mediaPlaySurfaceView)
        params = view!!.layoutParams as LayoutParams
        params!!.bottomToBottom = R.id.medially_view_id
        view!!.layoutParams = params
        addView(view)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val resolveWidth = mediaPlaySurfaceView!!.resolveSize(measuredWidth, widthMeasureSpec, 0)
        val resolveHeight = mediaPlaySurfaceView!!.resolveSize(measuredHeight, heightMeasureSpec, 1)
        setMeasuredDimension(resolveWidth!!, resolveHeight!!)
        initData()
        BaseUtil.clearAnim(context) //清除横竖屏切换的动画，避免卡顿
    }

    fun loadVideo(playUrl: String) {
        if (null != mediaPlaySurfaceView) {
            mHandler.sendEmptyMessage(UPDATE_PLAY_ICON)
            mediaPlaySurfaceView!!.playVideo(playUrl, currentTime, allTime, seekBar)
        }
    }

    private fun playVideo() {
        mediaPlaySurfaceView!!.getBinder().play()
        mediaPlaySurfaceView!!.updateProgress()
        mHandler.sendEmptyMessage(UPDATE_PLAY_ICON)
    }

    private fun pauseVideo() {
        mediaPlaySurfaceView!!.getBinder().pause()
        mHandler.sendEmptyMessage(UPDATE_PAUSE_ICON)

    }

    private fun setWeight(layoutParams: LayoutParams?, view: View?, weight: Float?) {
        layoutParams!!.horizontalWeight = weight!!
        view!!.layoutParams = layoutParams
    }

    fun destroyMedially() {
        if (null != mediaPlaySurfaceView) mediaPlaySurfaceView!!.destory()
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
            }
            false
        }
    }

    private fun settingSeekBarSlideTounch(seekBar: SeekBar?){
        seekBar!!.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                pg=progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar!!.progress=pg
                mediaPlaySurfaceView!!.getBinder().seekTo(pg)
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            val activity:Activity= context as Activity
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}