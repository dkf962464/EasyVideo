package com.rocky.kotlinplaysurface

import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import com.google.gson.Gson
import com.media.kvideo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var onMyWay: String =
        "http://m.kugou.com/app/i/mv.php?cmd=100&hash=cec895f084995e79068dc1a040d7dc58&ismp3=1&ext=mp4"
    private var starSky: String =
        "http://m.kugou.com/app/i/mv.php?cmd=100&hash=752a44c3e8b157936cba9eb2f461cd3c&ismp3=1&ext=mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.immersive(this, Color.TRANSPARENT, 0.5f)
        setContentView(R.layout.activity_main)
//        requestMv(starSky)
//        val string: File =Environment.getExternalStorageDirectory()
//        val list =  string.listFiles()
//        Log.e("FileName","$"+list.size)
//        for (str in list){
//            Log.e("FileName",""+str.name+"\t"+str.path)
//        }
    }

    fun click(v: View) {
//        Log.e("lrcPath",File(Environment.getExternalStorageDirectory().absolutePath+"/at","oh.krc").absolutePath)
//        v.visibility=View.GONE
//        val reader = LyricsReader()
//        reader.loadLrc()
//        lrc.lyricsReader= reader
//        video!!.pauseVideo()
//        ManyLyricsView(this).play()
        requestMv(onMyWay)
        v.alpha=0f
        v.visibility=View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

        video!!.destroyMedially()
        //如果只有声音没有图像的话，调用此方法即可
//        video.invalidateVideo()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig!!.orientation==Configuration.ORIENTATION_LANDSCAPE){
            but.visibility=View.GONE
        }else{
            but.visibility=View.VISIBLE
        }
    }
    private fun settingAlph() {
        runOnUiThread {
            val ofInt = ValueAnimator.ofFloat(0f, 1f)
            ofInt.duration = 2000
            ofInt.repeatCount = 0
            ofInt.addUpdateListener {
                val any: Float = it.animatedValue as Float
                video!!.alpha = any
            }
            ofInt.interpolator = LinearInterpolator()
            ofInt.start()
        }
    }

    //http://fs.mv.web.kugou.com/201912061024/bc56643600ab41020db8c39367258664/G168/M04/03/03/SIcBAF0S2l6AcudBBje74CZFDcI800.mp4
    private fun requestMv(requestUrl: String?) {
        var playUrl: String? = null
        OkHttpManager.instances[requestUrl!!, null, object :
            OkHttpManager.OnCallback {
            override fun onError(e: IOException) {
            }

            override fun onSuccess(response: Response) {
                if (response.code() == 200) {
                    try {
                        assert(response.body() != null)
                        val mvJson = response.body()!!.string()
                        val gson = Gson()
                        val mvBean = gson.fromJson(mvJson, MvBean::class.java)
                        playUrl = mvBean.mvdata!!.sq!!.downurl
                        if (null == playUrl) {
                            playUrl = mvBean.mvdata!!.rq!!.downurl
                            if (null==playUrl){
                                playUrl= mvBean.mvdata!!.le!!.downurl
                            }
                        }
                        Log.e("playUrl", "mvUrl\t$playUrl")
                        video!!.loadVideo(playUrl!!)
//                        settingAlph()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }]
    }

}
