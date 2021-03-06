package com.media.kvideo.surfaceview

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.media.kvideo.R
import com.media.kvideo.services.VideoServices
import com.media.kvideo.util.BaseUtil
import org.w3c.dom.Text
import kotlin.math.abs
import kotlin.math.max

/**
 * create by 2019/10/24
 *
 * author: wgl
 *
 * Believe in yourself, you can do it.
 */
class MediaPlaySurfaceView : SurfaceView {
    //SeekBar的progressDrawable属性
    private var ms: Int? = 0
    //播放器
    private var player: MediaPlayer? = null
    private var binder: VideoServices.VideoBinder? = null
    //播放视频
    private var VIDEO: Int = 0
    //缓冲区
    private var BUFFER_PROGRESS = 2
    private var CURRENT_TIME = 1
    //快进快退位移
    private var FASTFORWARD = 3
    //定时处理seekbar隐藏
    private var HIDESEEKBAR=4
    //快进快退视图
    private var textView: TextView? = null
    private var mHandler: Handler
    private var playerUrl: String? = null

    private var connection: ServiceConnection? = null
    private var current: TextView? = null
    private var allTime: TextView? = null
    private var seekBar: SeekBar? = null
    private var videoAllTime: Int? = 0
    private var downX: Float? = 0f
    private var downY: Float? = 0f
    private var moveX: Float? = 0f
    private var moveY: Float? = 0f
    private var JULI: Float = 10f
    private var currentProgress: Int = 0
    private var reduce: String? = null
    //0是快进 1是快退
    private var type: Int = 0
    private var beforeMoveDistance: Float = 0f
    private var progressBar: ProgressBar? = null
    private var beforTime: Int = 0
    private var secondProgress: Int = 0
    private var roootControlView: View? = null
    private var isMoveEvent = true
    private var fastForawrd: Boolean = false

    //java new
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attr: AttributeSet?) : super(context, attr)

    init {
//        if (!BaseUtil.isServiceRunning(context, "com.media.kvideo.services.VideoServices")) {
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder = service as VideoServices.VideoBinder?
                Log.e("service", "启动成功" + (null == binder))
            }
        }
        this.context.bindService(
            Intent(this.context, VideoServices::class.java),
            connection!!,
            Context.BIND_AUTO_CREATE
        )
//        }
        mHandler = initHandler()
        setOnTouchListener { _, event ->
            if (EasyVideo.isTounch) {
                textView = rootView.findViewById(R.id.rd_id)
                val seekBar = rootView.findViewById<SeekBar>(R.id.seek_bar)
                val durationTime: String = BaseUtil.millToMin(binder!!.getDuration())
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        moveX = event.rawX
                        moveY = event.rawY
                        Log.e("isMovent", "" + (moveY!!) + "\t" + downY!! + "\t" + moveX)
                        if (moveX!! - downX!! > 0) {
                            type = 0
                            currentProgress += 150
                            val current: String = BaseUtil.millToMin(binder!!.getCurrenPostion() + currentProgress)
                            when {
                                current.length <= 4 -> settingStyle(textView!!, durationTime, current, 5)
                                current.length <= 5 -> settingStyle(textView!!, durationTime, current, 6)
                                else -> settingStyle(textView!!, durationTime, current, 7)
                            }
                        } else {
                            type = 1
                            currentProgress -= 150
                            reduce = BaseUtil.millToMin(binder!!.getCurrenPostion() - abs(currentProgress))
                            if (binder!!.getCurrenPostion() - abs(currentProgress) <= 0) {
                                reduce = when {
                                    durationTime.length <= 4 -> "0:00"
                                    durationTime.length <= 5 -> "00:00"
                                    else -> "00:00"
                                }
                            }
                            when {
                                durationTime.length <= 4 -> settingStyle(textView!!, durationTime, reduce!!, 5)
                                durationTime.length <= 5 -> settingStyle(textView!!, durationTime, reduce!!, 6)
                                else -> settingStyle(textView!!, durationTime, reduce!!, 7)
                            }
                        }
                        beforeMoveDistance = moveX!!
                        if (isMoveEvent&&abs(currentProgress) >300 ) {
                            textView!!.visibility = View.VISIBLE
                            mHandler.sendEmptyMessage(FASTFORWARD)
                            Log.e("isMoventxxxx", "ve\t$beforeMoveDistance")
                            isMoveEvent = false
                        }
                    }
                    MotionEvent.ACTION_DOWN -> {
                        if (null != roootControlView) {
                            if (BaseUtil.isShowControl) {
                                BaseUtil.translationAnim(roootControlView!!, false)
                            } else {
                                BaseUtil.translationAnim(roootControlView!!, true)
                            }
                        }
                        downX = event.rawX
                        downY = event.rawY
                    }
                    MotionEvent.ACTION_UP -> {
                        Log.e("actionEvent", "ACTION_UP")
                        if (!isMoveEvent&&abs(currentProgress) >300){
                            BaseUtil.fastForward(textView!!, false)
                            isMoveEvent=true
                        }
                        if (abs(currentProgress) > 300) {
                            if (type == 0) {
                                seekBar!!.progress = binder!!.getCurrenPostion() + currentProgress
                                binder!!.seekTo(binder!!.getCurrenPostion() + currentProgress)
                            } else if (type == 1) {
                                seekBar!!.progress = binder!!.getCurrenPostion() - abs(currentProgress)
                                binder!!.seekTo(binder!!.getCurrenPostion() - abs(currentProgress))
                            }

                        }
                        mHandler.sendEmptyMessageDelayed(HIDESEEKBAR,4000)
                        currentProgress = 0
                    }
                }
            }
            true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val resolveWidth = resolveSize(measuredWidth, widthMeasureSpec, 0)
        val resolveHeight = resolveSize(measuredHeight, heightMeasureSpec, 1)
        setMeasuredDimension(resolveWidth!!, resolveHeight!!)
    }

    fun invalidateVideo() {
        visibility = View.GONE
        visibility = View.VISIBLE
    }

    /**
     * type: 0 width 1 height
     * */
    fun resolveSize(measureSize: Int?, measureSpec: Int?, type: Int?): Int? {
        measureSpec!!
        measureSize!!
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        when (mode) {
            MeasureSpec.EXACTLY -> {
                ms = size
                Log.e("EXACTLY", "EXACTLY")
            }
            MeasureSpec.AT_MOST -> {
                if (type == 0) {
                    val metrics = resources.displayMetrics
                    ms = metrics.widthPixels
                } else if (type == 1) {
                    ms = BaseUtil.dp2px(context, 220f)
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                ms = max(measureSize, size)
            }
        }
        return ms
    }

    fun playVideo(
        playUrl: String?,
        current: TextView?,
        allTime: TextView?,
        seekBar: SeekBar?,
        progressBar: ProgressBar?,
        view: View?
    ) {
        playerUrl = playUrl
        this.current = current
        this.allTime = allTime
        this.seekBar = seekBar
        this.progressBar = progressBar
        this.roootControlView = view
        if (null != playUrl) {
            mHandler.sendEmptyMessage(VIDEO)
        } else {
            throw NullPointerException("playUrl is not null")
        }
    }

    private fun initHandler(): Handler {
        return Handler(Looper.getMainLooper()) {
            when (it.what) {
                VIDEO -> {
                    startVideo()
                }
                CURRENT_TIME -> {
                    if (beforTime == binder!!.getCurrenPostion()) {
                        progressBar!!.visibility = View.VISIBLE
                    } else {
                        progressBar!!.visibility = View.GONE
                    }
                    if (binder!!.getCurrenPostion() <= videoAllTime!!) {
                        current!!.text = BaseUtil.millToMin(binder!!.getCurrenPostion())
                    } else {
                        current!!.text = BaseUtil.millToMin(videoAllTime!!)
                    }
                    seekBar!!.progress = binder!!.getCurrenPostion()

                    beforTime = binder!!.getCurrenPostion()
                    mHandler.sendEmptyMessageDelayed(CURRENT_TIME, 500)
                }
                BUFFER_PROGRESS -> {
                    Log.e("testLastTime", "$secondProgress*2500")
                    seekBar!!.secondaryProgress = secondProgress
                }
                FASTFORWARD -> {
                    BaseUtil.fastForward(textView!!, true)
                }
                HIDESEEKBAR->{
                    if (BaseUtil.isShowControl) {
                        BaseUtil.translationAnim(roootControlView!!, false)
                    }
                }
            }
            false
        }
    }


    private fun startVideo() {
        if (null != binder) {
            addCallBack()
            mHandler.removeMessages(VIDEO)
            player = binder!!.getPlayer()
            if (playerUrl!!.contains("http")) {
                player!!.setDataSource(playerUrl)
            } else {
                player!!.setDataSource(this.context, Uri.parse(playerUrl))
            }
            player!!.prepareAsync()
            player!!.setOnPreparedListener {
                allTime!!.text = BaseUtil.millToMin(binder!!.getDuration())
                videoAllTime = binder!!.getDuration()
                seekBar!!.max = videoAllTime!!
                player!!.setDisplay(holder)
                play()
            }

            player!!.setOnCompletionListener {
                mHandler.removeMessages(CURRENT_TIME)
                Log.e("removemessage", "ok")
            }

            player!!.setOnBufferingUpdateListener { _, percent ->
                secondProgress = percent
                mHandler.sendEmptyMessageDelayed(BUFFER_PROGRESS, 1000)
            }

        } else {
            mHandler.sendEmptyMessageDelayed(VIDEO, 500)
        }
    }

    fun invalidateSurfaceView() {
        visibility = View.GONE
        visibility = View.VISIBLE
    }

    fun destroy() {
        context.unbindService(connection!!)
        this.destroy()
    }

    fun updateProgress() {
        if (!mHandler.hasMessages(CURRENT_TIME))
            mHandler.sendEmptyMessage(CURRENT_TIME)
    }

    private fun addCallBack() {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                if (null != binder) {
                    binder!!.pause()
                }
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (null != binder) {
                    binder!!.setDisplay(holder!!)
                    binder!!.play()
                    updateProgress()
                }
            }
        })
    }

    fun pause() {
        if (null != binder) {
            binder!!.pause()
            mHandler.removeMessages(CURRENT_TIME)
        }
    }

    fun play() {
        if (null != binder) {
            if (!binder!!.isPlaying()) {
                binder!!.play()
                mHandler.sendEmptyMessage(CURRENT_TIME)
            }
        }
    }

    fun isPlaying(): Boolean {
        if (null != binder) return binder!!.isPlaying()
        return false
    }

    fun seekTo(progress: Int) {
        if (null != binder) {
            binder!!.seekTo(progress)
        }
    }

    private fun settingStyle(textView: TextView, durationTime: String, currentTime: String, position: Int) {
        BaseUtil.setTextColor(
            textView,
            "$currentTime/$durationTime",
            Color.WHITE,
            Color.GREEN,
            position
        )
    }
}