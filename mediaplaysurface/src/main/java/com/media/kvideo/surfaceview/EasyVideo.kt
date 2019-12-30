package com.media.kvideo.surfaceview

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
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.media.kvideo.R
import com.media.kvideo.util.BaseUtil

/**
 * create by 2019/10/29
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */

class EasyVideo : ConstraintLayout {
    //java new
    var progressDrawable: Drawable? = null
    var thumb: Drawable? = null
    var fullScreen: Drawable? = null
    var exitFullScreen: Drawable? = null
    var playPauseIcon: Drawable? = null
    var startTimeColor: Int = 0
    var endTimeColor: Int = 0
    var autoPlay:Int =1

    var isShowSeekbar:Boolean=true
    var playUrl:String?=null
    //一定要设置此view的透明度为0.5f
    var bottomBackgroundColor: Int = 0
    var seekBarHeight: Float? = 1f
    var jvavIsplay:Boolean=false
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
    private var PLAY_URL:Int=3
    private var params: LayoutParams? = null
    private var current: LayoutParams? = null
    private var progressbarLayout: LayoutParams? = null
    private var loading: ProgressBar? = null
    private var surfaceViewParams: LayoutParams? = null
    private var pg: Int = 0

    companion object{
         var ISTOUNCHUP:Boolean=true
        var isTounch:Boolean=true
    }
    constructor(context: Context?) : super(context) {
        Log.e("aaa666", "constructor")
    }

    constructor(context: Context?, attr: AttributeSet?) : super(context, attr) {
        context!!
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
        autoPlay=typedArray.getInt(R.styleable.EasyVideo_isAutoPlay,1)
        isTounch=typedArray.getBoolean(R.styleable.EasyVideo_isTounch,true)
        isShowSeekbar=typedArray.getBoolean(R.styleable.EasyVideo_isShowSeekbarController,true)
        playUrl=typedArray.getString(R.styleable.EasyVideo_playUrl);
        typedArray.recycle()
    }

    //横竖屏切换
    @SuppressLint("ObsoleteSdkInt")
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        seekBar
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
            if (null != mediaPlaySurfaceView) {
                mediaPlaySurfaceView!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            BaseUtil.showStatusBar(context as Activity)
            setWeight(currentParams, currentTime, 0.4f)
            setWeight(allTimeParams, allTime, 0.4f)
            setWeight(playPauseParams, playPause, 0.2f)
            setWeight(fullScreenViewParams, fullScreenView, 0.2f)
            if (null != mediaPlaySurfaceView) {
                mediaPlaySurfaceView!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
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
        //因为横竖屏的时候需要重绘全屏按钮，所以把这个方法暴露子外以用来更新
        settingPlayIcon()
        //防止多次重绘，横竖屏切换会黑屏的问题
        if (childCount <= 0) {
            BaseUtil.clearAnim(context) //清除横竖屏切换的动画，避免卡顿
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
            if (!isShowSeekbar){
               view!!.visibility= View.GONE
            }
            addView(view)

            reduceAdd = TextView(context)
            reduceAdd!!.id = R.id.rd_id
            current = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            current!!.leftToLeft = R.id.medially_view_id
            current!!.rightToRight = R.id.medially_view_id
            current!!.topToTop = R.id.medially_view_id
            current!!.topMargin = BaseUtil.dp2px(context, 20f)
            reduceAdd!!.layoutParams = current
            addView(reduceAdd)
            loading= ProgressBar(context)
            progressbarLayout = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            progressbarLayout!!.leftToLeft = R.id.medially_view_id
            progressbarLayout!!.rightToRight = R.id.medially_view_id
            progressbarLayout!!.topToTop = R.id.medially_view_id
            progressbarLayout!!.bottomToBottom = R.id.medially_view_id
            loading!!.indeterminateDrawable = ContextCompat.getDrawable(context, R.drawable.loading_rote_anim)
            loading!!.layoutParams = progressbarLayout
            loading!!.visibility= View.GONE
            addView(loading)
            mHandler.sendEmptyMessage(PLAY_URL)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initData()
        val resolveWidth = mediaPlaySurfaceView!!.resolveSize(measuredWidth, widthMeasureSpec, 0)
        val resolveHeight = mediaPlaySurfaceView!!.resolveSize(measuredHeight, heightMeasureSpec, 1)
        setMeasuredDimension(resolveWidth!!, resolveHeight!!)
    }

    fun loadVideo(playUrl: String) {
        if (null != mediaPlaySurfaceView) {
            mHandler.sendEmptyMessage(UPDATE_PLAY_ICON)
            mediaPlaySurfaceView!!.playVideo(playUrl, currentTime, allTime, seekBar,loading)
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
                PLAY_URL->{
                    if (autoPlay==0){
                        if (null!=playUrl){
                            loadVideo(playUrl!!)
                        }else{
                            Toast.makeText(context,"auto play  failure , beacuse play url is null ! Do you really set the url property in xml ?",Toast.LENGTH_LONG).show()
                        }
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
                ISTOUNCHUP=false
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
                ISTOUNCHUP=true
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