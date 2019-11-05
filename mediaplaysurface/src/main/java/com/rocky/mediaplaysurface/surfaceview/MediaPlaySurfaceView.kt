package com.rocky.mediaplaysurface.surfaceview

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.rocky.mediaplaysurface.services.VideoServices
import com.rocky.mediaplaysurface.util.BaseUtil
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
    private var CURRENT_TIME = 1
    private var mHandler: Handler
    private var playerUrl: String? = null
    private var connection: ServiceConnection
    private var current: TextView? = null
    private var allTime: TextView? = null
    private var seekBar: SeekBar? = null
    private var videoAllTime: Int? = 0

    //java new
    constructor(context: Context?) : super(context) {
        Log.e("constructor", "sss")
    }

    //xml
    constructor(context: Context?, attr: AttributeSet?) : super(context, attr) {
        //在设置属性的时候进行判断，是否为空
    }

    init {
//        if (!BaseUtil.isServiceRunning(context, "com.rocky.mediaplaysurface.services.VideoServices")) {
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
                connection,
                Context.BIND_AUTO_CREATE
            )
//        }
        mHandler = initHandler()
        addCallBack()

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val resolveWidth = resolveSize(measuredWidth, widthMeasureSpec, 0)
        val resolveHeight = resolveSize(measuredHeight, heightMeasureSpec, 1)
        setMeasuredDimension(resolveWidth!!, resolveHeight!!)
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
                Log.e("EXACTLY","EXACTLY")
            }
            MeasureSpec.AT_MOST -> {
                if (type == 0) {
                    val metrics = resources.displayMetrics
                    ms = metrics.widthPixels
                } else if(type==1) {
                    ms = BaseUtil.dp2px(context, 220f)
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                ms = max(measureSize, size)
            }
        }
        return ms
    }

    fun playVideo(playUrl: String?, current: TextView?, allTime: TextView?, seekBar: SeekBar?) {
        playerUrl = playUrl
        this.current = current
        this.allTime = allTime
        this.seekBar = seekBar
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
                    if (binder!!.getCurrenPostion() <= videoAllTime!!) {
                        current!!.text = BaseUtil.millToMin(binder!!.getCurrenPostion())
                    } else {
                        current!!.text = BaseUtil.millToMin(videoAllTime!!)
                    }
                    seekBar!!.progress = binder!!.getCurrenPostion()
                    mHandler.sendEmptyMessageDelayed(CURRENT_TIME, 500)
                }
            }
            false
        }
    }

    private fun startVideo() {
        if (null != binder) {
            mHandler.removeMessages(VIDEO)
            player = binder!!.getPlayer()
            player!!.reset()
            if (playerUrl!!.contains("http")) {
                player!!.setDataSource(playerUrl)
            } else {
                player!!.setDataSource(this.context, Uri.parse(playerUrl))
            }
            player!!.prepareAsync()
            player!!.setOnPreparedListener {
                player!!.start()
                player!!.setDisplay(holder)
                allTime!!.text = BaseUtil.millToMin(binder!!.getDuration())
                videoAllTime = binder!!.getDuration()
                seekBar!!.max = videoAllTime!!
                mHandler.sendEmptyMessage(CURRENT_TIME)
            }
            player!!.setOnCompletionListener {
                mHandler.removeMessages(CURRENT_TIME)
                Log.e("removemessage", "ok")
            }

        } else {
            mHandler.sendEmptyMessageDelayed(VIDEO, 500)
        }
    }

    fun invalidateSurfaceView() {
        visibility = View.GONE
        visibility = View.VISIBLE
    }

     fun destory() {
        context.unbindService(connection!!)
    }

    fun updateProgress() {
        if (!mHandler.hasMessages(CURRENT_TIME))
            mHandler.sendEmptyMessage(CURRENT_TIME)
    }

    fun getBinder(): VideoServices.VideoBinder {
        return binder!!
    }

    private fun addCallBack() {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                if (null != binder) {
                    binder!!.pause()
                    Log.e("init", "surfaceDestroyed")
                }
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (null != binder) {
                    Log.e("init", "surfaceCreated")
                    binder!!.setDisplay(holder!!)
                    binder!!.play()
                }
            }
        })
    }
}