#-flattenpackagehierarchy
#-allowaccessmodification
#-keepattributes Exceptions,InnerClasses,Signature,SourceFile,LineNumberTable
#-dontwarn
#-ignorewarnings
##kotlin
#
#
#-dontwarn
#-keepclassmembers class **$WhenMappings {
#    <fields>;
#}
# -keep class com.rocky.mediaplaysurface.surfaceview.EasyVideoublic void loadVideo();
# }
#  -keep class com.media.kvideo.surfaceview.MediaPlaySurfaceView{
#      public void playVideo();
#          public void resolveSize();
#  }
#
#-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
#    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
#}
#
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#-keepclassmembers class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}
#-keep class **.R$* {*;}
#-keepclassmembers enum * { *;}
#
##mars
#-keep class com.tencent.mars.** { *; }
#
##rx
#-keep class rx.internal.util.unsafe.** { *; }
#-keep class android.databinding.** { *; }
#
##Gson
#-keepclassmembers public class com.google.gson.**
#-keepclassmembers public class com.google.gson.** {public private protected *;}
#-keepclassmembers public class com.project.mocha_patient.login.SignResponseData { private *; }
#-keepclassmembers class sun.misc.Unsafe { *; }
#-keep @interface com.google.gson.annotations.SerializedName
#-keepclassmembers class * {
#    @com.google.gson.annotations.SerializedName <fields>;
#}
#
#
##greenDAO
#-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
#public static java.lang.String TABLENAME;
#}
#-keep class **$Properties {*;}
#
##Glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
#-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}
## for DexGuard only
##-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#-dontwarn com.bumptech.glide.**
#
##Qiniu SDK
#-keep class com.qiniu.**{*;}
#-keep class com.qiniu.**{public <init>();}
#-ignorewarnings
#
#
#
#
## ProGuard configurations for Bugtags
#-keepattributes LineNumberTable,SourceFile
#-keep class com.bugtags.library.** {*;}
#-dontwarn com.bugtags.library.**
#-keep class io.bugtags.** {*;}
#-dontwarn io.bugtags.**
#-dontwarn org.apache.http.**
#-dontwarn android.net.http.AndroidHttpClient