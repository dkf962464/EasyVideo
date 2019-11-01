package com.rocky.mediaplaysurface.services
import android.app.Service
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.SurfaceHolder
import android.widget.ImageView
import android.widget.TextView
import com.rocky.mediaplaysurface.interce.FixedThreadInterface
import com.rocky.mediaplaysurface.util.BaseUtil
import com.rocky.mediaplaysurface.util.FixedThread
import java.util.HashMap

class VideoServices : Service() {
    private var player: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder {
        return VideoBinder(player!!)
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer()
    }

    class VideoBinder(private var player: MediaPlayer) : Binder() {
        fun getVideoFrame(imageView: ImageView?, aVideoUri: Uri?, url: String?, million: Int) {
            FixedThread.getThread(object : FixedThreadInterface {
                override fun run() {

                    val retriever = MediaMetadataRetriever()
                    if (null != url && "" != url) {
                        retriever.setDataSource(url, HashMap<String, String>())
                    } else {
                        if (null != aVideoUri)
                            retriever.setDataSource(imageView!!.context, aVideoUri)
                    }
                    val bitmap = retriever
                        .getFrameAtTime(million.toLong(), MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
                    val drawable = BitmapDrawable(imageView!!.context.resources, bitmap)
                    imageView.post {
                        imageView.setImageDrawable(drawable)
                        retriever.release()//释放资源
                    }
                }
            })
        }

        fun setPlayerSpeed(speed: Float) {
            //快进的方法一定要放在player准备好之后再执行
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (player.isPlaying) {
                    player.playbackParams = player.playbackParams.setSpeed(speed)
                } else {
                    player.playbackParams = player.playbackParams.setSpeed(speed)
                    player.pause()
                }
            }
        }

        fun getVideoDurationTime(textView: TextView?, uri: Uri?, url: String?) {
            FixedThread.getThread(object : FixedThreadInterface {
                override fun run() {
                    val mmr = MediaMetadataRetriever()
                    if (null != url && "" != url) {
                        mmr.setDataSource(url, HashMap<String, String>())
                    } else {
                        if (null != uri)
                            mmr.setDataSource(textView!!.context, uri)
                    }
                    val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    textView!!.post {
                        if (null != uri)
                            textView.text = BaseUtil.millToMin(Integer.valueOf(duration))
                        mmr.release()
                    }
                }

            })
        }

        fun pause() {
            if (player.isPlaying) {
                player.pause()
            }
        }
        fun stop() {
            player.stop()
        }
        fun getPlayer():MediaPlayer{
            player.stop()
            player.release()
            player= MediaPlayer()
            return player
        }
        fun play() {
            if (!player.isPlaying) {
                player.start()
            }
        }

        fun getDuration(): Int {
            Log.e("timeDuration", "" + player.duration)
            return player.duration
        }

        fun isVideoHeightGreaterWidth():Boolean{
            return player.videoHeight>player.videoWidth
        }

        fun getCurrenPostion(): Int {
            return player.currentPosition
        }

        fun seekTo(mesc: Int) {
            player.seekTo(mesc)
        }

        fun isLoop(isloop: Boolean) {

            player.isLooping = isloop
        }

        fun setDisplay(surfaceView: SurfaceHolder) {
            player.setDisplay(surfaceView)
        }
        fun isPlaying():Boolean{
            return player.isPlaying
        }

        fun destoryMediaPlayer() {
            try {
                player.setOnCompletionListener(null)
                player.setOnPreparedListener(null)
                player.reset()
                player.release()
            } catch (e: Exception) {
                e.message
            }

        }
    }
}
