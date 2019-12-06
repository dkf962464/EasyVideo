package com.rocky.kotlinplaysurface

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.google.gson.Gson
import com.media.kvideo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.immersive(this, Color.TRANSPARENT, 0.5f)
        setContentView(R.layout.activity_main)
        requestMv()
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
//        Toast.makeText(this,"开始播放第五人格,爱你呦",Toast.LENGTH_LONG).show()
//        val reader = LyricsReader()
//        reader.loadLrc()
//        lrc.lyricsReader= reader
//        video!!.pauseVideo()
//        ManyLyricsView(this).play()
    }

    override fun onDestroy() {
        super.onDestroy()
        video!!.destroyMedially()
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
    private fun requestMv() {
        OkHttpManager.instances["http://m.kugou.com/app/i/mv.php?cmd=100&hash=cec895f084995e79068dc1a040d7dc58&ismp3=1&ext=mp4", null, object :
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
                        val playUrl = mvBean.mvdata!!.rq!!.downurl
                        Log.e("playurl", "mvUrl\t$playUrl")
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
