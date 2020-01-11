package com.rocky.kotlinplaysurface

import android.animation.ValueAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.media.kvideo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var onMyWay: String =
        "http://m.kugou.com/app/i/mv.php?cmd=100&hash=cec895f084995e79068dc1a040d7dc58&ismp3=1&ext=mp4"
    private var starSky: String =
        "http://m.kugou.com/app/i/mv.php?cmd=100&hash=eb6bb33f2f2dc58b996bc67fbc86ec85&ismp3=1&ext=mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.immersive(this, Color.TRANSPARENT, 0.5f)
        setContentView(R.layout.activity_main)
//        video.loadVideo("http://vipmp4i.vodfile.m1905.com/202001071410/8b5aa5b4429872d8ffad16e0cd500f51/movie/2020/01/03/m20200103E930C8L6PJO4F7MM/E59643F51FB145C3F3A4BD49C.mp4")
        requestMv(onMyWay)
//        val string: File =Environment.getExternalStorageDirectory()
//        val list =  string.listFiles()
//        Log.e("FileName","$"+list.size)
//        for (str in list){
//            Log.e("FileName",""+str.name+"\t"+str.path)
//        }
    }

    fun click(v: View) {
        if (topleft.isChecked){
            video.topLeftRadius=35f
        }else{
            video.topLeftRadius=0f
        }
        if (topright.isChecked){
            video.topRightRadius=35f
        }else{
            video.topRightRadius=0f
        }
        if (bottomleft.isChecked){
            video.bottomLeftRadius=35f
        }else{
            video.bottomLeftRadius=0f
        }
        if (bottomright.isChecked){
            video.bottomRightRadius=35f
        }else{
            video.bottomRightRadius=0f
        }
        if (radius.isChecked){
            video.radius=35f
        }else{
            video.radius=0f
        }
        video.invalidate()
//        requestMv(onMyWay)
//        v.alpha=0f
//        v.visibility=View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        //在ondestroy调用这个方法，避免内存不足崩掉
        video!!.destroyMedially()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }
    private fun settingAlph() {
        runOnUiThread {
            val ofInt = ValueAnimator.ofFloat(0f, 1f)
            ofInt.duration = 2000
            ofInt.repeatCount = 0
            ofInt.addUpdateListener {
                val any: Float = it.animatedValue as Float
//                video!!.alpha = any
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
