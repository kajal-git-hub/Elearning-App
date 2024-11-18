# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep the BuildConfig class
-keep class xyz.penpencil.competishun.BuildConfig { *; }
# Preserve the Google Pay Wallet classes
-keep class com.google.android.gms.wallet.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.android.apps.nbu.paisa.** { *; }
-dontwarn com.google.android.apps.nbu.paisa.**

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable
# If you keep the line number information, uncomment this to hide the original source file name.
#-renamesourcefileattribute SourceFile
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
# Razorpay specific ProGuard rules
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
    @com.google.common.eventbus.Subscribe <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.razorpay.**
-keep class com.razorpay.** { *; }

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}

# Add any additional rules specific to your project here
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# Application classes that will be serialized/deserialized over Gson
-keep class com.myapplication.model.api.** { *; }

# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature

# This is also needed for R8 in compat mode since multiple
# optimizations will remove the generic signature such as class
# merging and argument removal. See:
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations
-keep class com.google.gson.reflect.TypeToken { <fields>; }
-keepclassmembers class **$TypeAdapterFactory { <fields>; }

