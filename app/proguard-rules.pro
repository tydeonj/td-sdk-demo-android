# 对外 Demo 宿主若开 minify：只 keep TD 聚合（与对接文档 4.1.6 对齐）。对方 SDK 不管。
-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod
-keep public class com.td.ads.open.** { *; }
-keep public interface com.td.ads.open.** { *; }
-keep public class com.td.ads.core.** { *; }
-keep public interface com.td.ads.core.** { *; }
-keep public class com.td.ads.base.** { *; }
-keep public interface com.td.ads.base.** { *; }
-keep public class com.td.ads.crash.** { *; }
-keep public class com.td.ads.jdsdk.** { *; }
-keep public class com.td.ads.adgain.** { *; }
-keep public class com.td.ads.ltmb.** { *; }
-keep public class com.td.ads.sigmob.** { *; }
-keep public class com.td.ads.mintegral.** { *; }
-keepclassmembers class * extends com.td.ads.base.adapter.TDBaseAdapter {
    public <init>();
}
