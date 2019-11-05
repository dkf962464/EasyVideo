package com.rocky.kotlinplaysurface

/**
 * create by 2019/10/11
 *
 *
 * author: wgl
 *
 *
 * Believe in yourself, you can do it.
 */
class MvBean {


    /**
     * errcode : 0
     * error :
     * hash : 7376CFD1F173057435F57CFA799248A7
     * id : 596581
     * is_publish : 1
     * mp3data : {"bitrate":128,"filesize":4399565,"hash":"73f5bd0843e0bf3f8342489cd5b712b6","timelength":275}
     * mvdata : {"le":{"backupdownurl":["http://fs.mv.web2.kugou.com/201910111417/a1669f1d221128a37b0ac63e683f3a62/G093/M03/03/03/nQ0DAFiGt_GAHYfqARzAC7-4EaM815.mp4"],"bitrate":541976,"downurl":"http://fs.mv.web.kugou.com/201910111417/a31be9998275d5c2696fa4a1a17b6e13/G093/M03/03/03/nQ0DAFiGt_GAHYfqARzAC7-4EaM815.mp4","filesize":18661387,"hash":"7376cfd1f173057435f57cfa799248a7","timelength":275435},"rq":{},"sq":{}}
     * mvicon : http://imge.kugou.com/mvhdpic/{size}/20170124/20170124101322929987.jpg
     * play_count : 4055787
     * remark :
     * singer : 周杰伦
     * songname : 霍元甲
     * status : 1
     * timelength : 275435
     * track : 3
     * type : 2
     */

    var errcode: Int = 0
    var error: String? = null
    var hash: String? = null
    var id: Int = 0
    var is_publish: Int = 0
    var mp3data: Mp3dataBean? = null
    var mvdata: MvdataBean? = null
    var mvicon: String? = null
    var play_count: Int = 0
    var remark: String? = null
    var singer: String? = null
    var songname: String? = null
    var status: Int = 0
    var timelength: Int = 0
    var track: Int = 0
    var type: Int = 0

    class Mp3dataBean {
        /**
         * bitrate : 128
         * filesize : 4399565
         * hash : 73f5bd0843e0bf3f8342489cd5b712b6
         * timelength : 275
         */

        var bitrate: Int = 0
        var filesize: Int = 0
        var hash: String? = null
        var timelength: Int = 0
    }

    class MvdataBean {
        /**
         * le : {"backupdownurl":["http://fs.mv.web2.kugou.com/201910111417/a1669f1d221128a37b0ac63e683f3a62/G093/M03/03/03/nQ0DAFiGt_GAHYfqARzAC7-4EaM815.mp4"],"bitrate":541976,"downurl":"http://fs.mv.web.kugou.com/201910111417/a31be9998275d5c2696fa4a1a17b6e13/G093/M03/03/03/nQ0DAFiGt_GAHYfqARzAC7-4EaM815.mp4","filesize":18661387,"hash":"7376cfd1f173057435f57cfa799248a7","timelength":275435}
         * rq : {}
         * sq : {}
         */

        var le: LeBean? = null
        var rq: RqBean? = null
        var sq: SqBean? = null

        class LeBean {
            /**
             * backupdownurl : ["http://fs.mv.web2.kugou.com/201910111417/a1669f1d221128a37b0ac63e683f3a62/G093/M03/03/03/nQ0DAFiGt_GAHYfqARzAC7-4EaM815.mp4"]
             * bitrate : 541976
             * downurl : http://fs.mv.web.kugou.com/201910111417/a31be9998275d5c2696fa4a1a17b6e13/G093/M03/03/03/nQ0DAFiGt_GAHYfqARzAC7-4EaM815.mp4
             * filesize : 18661387
             * hash : 7376cfd1f173057435f57cfa799248a7
             * timelength : 275435
             */

            var bitrate: Int = 0
            var downurl: String? = null
            var filesize: Int = 0
            var hash: String? = null
            var timelength: Int = 0
            var backupdownurl: List<String>? = null
        }

        class RqBean {
            var bitrate: Int = 0
            var downurl: String? = null
            var filesize: Int = 0
            var hash: String? = null
            var timelength: Int = 0
            var backupdownurl: List<String>? = null
        }

        class SqBean {
            var bitrate: Int = 0
            var downurl: String? = null
            var filesize: Int = 0
            var hash: String? = null
            var timelength: Int = 0
            var backupdownurl: List<String>? = null
        }
    }
}
