package com.rocky.kotlinplaysurface

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rocky.mediaplaysurface.surfaceview.SurfaceViewLayout

class MainActivity : AppCompatActivity() {
    private var video:SurfaceViewLayout?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        video=findViewById(R.id.video)
        video!!.loadVideo("http://fs.mv.web.kugou.com/201911011520/9681b920561b685e20c559ecbebe05c1/G166/M07/03/05/hpQEAF0u3O6ARzfrB0zyu51EJJM877.mp4")
    }

    fun click(v: View){
        val url= "android.resource://$packageName/raw/"+R.raw.zhangdechouhuodejiu
        video!!.loadVideo(url)
//        video!!.pauseVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        video!!.destroyMedially()
    }
}
