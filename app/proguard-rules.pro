# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
##---------------Begin: proguard configuration common for all Android apps ----------
#https://stackoverflow.com/questions/36762834/getting-enclosingmethod-errors-on-building-in-android-studio-2
-keepattributes EnclosingMethod
#---------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''

-keep class com.google.**
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.support.v4.content.WakefulBroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-dontwarn android.support.**
-dontwarn com.google.**
-dontwarn org.slf4j.**
-dontwarn org.json.*
-dontwarn org.mortbay.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.codec.binary.**
-dontwarn javax.xml.**
-dontwarn javax.management.**
-dontwarn java.lang.management.**
-dontwarn android.support.**
-dontwarn com.google.code.**
-dontwarn oauth.signpost.**

-keep class javax.** { *; }
-keep class org.** { *; }
-keep class javax.xml.**{ *; }
-keep class android.support.**{ *; }
-keep class com.google.code.**{ *; }
-keep class oauth.signpost.**{ *; }
-keep class com.vn.ntsc.utils.LogUtils { *; }
-keep class android.util.Log {
  public static *** d(...);
  public static *** v(...);
  public static *** w(...);
  public static *** e(...);
  public static *** i(...);
}

# keep server request and server response
-keep class com.vn.ntsc.base.model.BaseBean { *; }
-keep class com.vn.ntsc.base.model.ServerRequest { *; }
-keep class com.vn.ntsc.base.model.ServerResponse { *; }
-keep public class * extends com.vn.ntsc.base.model.BaseBean { *; }
-keep public class * extends com.vn.ntsc.base.model.ServerRequest { *; }
-keep public class * extends com.vn.ntsc.base.model.ServerResponse { *; }

#Delete all Log.v(), Log.i()...
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** v(...);
#    public static *** w(...);
#    public static *** e(...);
#    public static *** i(...);
#}

#nankai set Scroll
-keepattributes *Annotation*
-keep public class * extends android.support.design.widget.CoordinatorLayout.Behavior { *; }
-keep public class * extends android.support.design.widget.ViewOffsetBehavior { *; }
#end
#chooser on webview
-keepclassmembers class * extends android.webkit.WebChromeClient {
   public void openFileChooser(...);
}
#end
-keepclassmembers public class * extends android.view.View {
  void set*(***);
  *** get*();
}

-keepclassmembers class * {
    public void actionSearch(android.view.View);
    public void actionAFeature(android.view.View);
    public void actionBFeature(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
	public static <fields>;
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
	native <methods>;
}

#-keep class * extends AbstractDao
-keep public class de.**{ *; }
-keep public class android.*

-keep public class com.jeremyfeinstein.slidingmenu.lib.**{*;}
-keep public class com.portsip.**{*;}
-keep public class com.google.**{ *; }

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep class javax.inject.** { *; }
##---------------End: proguard configuration common for all Android apps ----------

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Retrofit 2.X
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

#-keepattributes Signature
-keep class com.facebook.** { *; }

##----------------Begin Google Play Service--------------------------
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

# Keep the names of classes/members we need for client functionality.
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Needed for Parcelable/SafeParcelable Creators to not get stripped
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}


#Enter your proguard-rules here
# autovalue
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**
-dontwarn autovalue.shaded.com.**
-dontwarn com.google.auto.value.**

# autovalue gson extension
-keep class **.AutoParcelGson_*
-keepnames @auto.parcelgson.AutoParcelGson class *

# cache
-keep class com.google.**
-dontwarn com.google.**
-dontwarn sun.misc.**

#RxJava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#Crashlytics
-dontwarn com.crashlytics.sdk.android.**

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#com.github.bumptech.glide:okhttp3-integration:1.5.0@aar
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# for leakcanary
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification

#-keepresourcexmlattributenames vector/*
-keep public class * implements com.bumptech.glide.module.GlideModule

# chat lib
-keep class com.tuanthitluoc.** { *; }
-dontwarn com.tuanthitluoc.**
-keep class vn.com.ntqsolution.** { *; }
-dontwarn vn.com.ntqsolution.**


#-keep class org.webrtc.** {*;}
-keep class org.webrtc.CameraSession**
-keep interface org.webrtc.CameraSession$* {*;}
-keep interface org.webrtc.CameraSession$Events {*;}
#-keepattributes Signature
-keep class org.webrtc.** { *; }
-keepclassmembers class org.webrtc { *; }
-dontwarn org.webrtc.**

-dontwarn android.net.Network
-keep class org.appspot.apprtc.**  { *; }
-keep class de.tavendo.autobahn.**  { *; }

#jackson
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class your.class.** {
    public void set*(***);
    public *** get*();
}

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**


-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#ShortcutBadger
-keep class me.leolin.shortcutbadger.impl.AdwHomeBadger { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.ApexHomeBadger { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.AsusHomeLauncher { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.DefaultBadger { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.NewHtcHomeBadger { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.NovaHomeBadger { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.SolidHomeBadger { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.SonyHomeBadger { <init>(...); }
-keep class me.leolin.shortcutbadger.impl.XiaomiHomeBadger { <init>(...); }

# Dagger ProGuard rules.
# https://github.com/square/dagger

-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}

-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection