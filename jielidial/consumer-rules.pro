# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#指定压缩级别
-optimizationpasses 5

#不跳过library中的非public的类
-dontskipnonpubliclibraryclasses

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
# ============忽略警告，否则打包可能会不成功=============
-ignorewarnings

-keepattributes *Annotation*,InnerClasses #保留注解不混淆
-keepattributes Signature # 避免混淆泛型
-keepattributes SourceFile,LineNumberTable# 抛出异常时保留代码行号


#混淆时不使用大小写混合类名
-dontusemixedcaseclassnames

#打印混淆的详细信息
-verbose

#保留native方法的类名和方法名

-keepclasseswithmembernames class * {
    native <methods>;
}


#--------------------------------------------- 公共配置 End ----------------------------------------#


#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#所有实体类都保持住
-keep public class com.ido.alexa.bean.** {*;}
-keep public class com.ido.alexa.data.** {*;}
-keep public class com.ido.alexa.callbacks.** {*;}
-keep public class com.ido.alexa.log.** {*;}
-keep public class com.ido.record.** {*;}
-keep public class com.ido.alexa.AlexaApi {*;}
-keep public class com.ido.alexa.AlexaApp {*;}
-keep public class com.ido.alexa.AlexaConstant{*;}
-keep class com.amazon.identity.auth.device.**{*;}
-keep class com.ido.alexa.service.** {*;}
-keep class com.ido.alexa.manager.** {*;}
-keep class com.ido.alexa.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#--------------------------2.第三方包-------------------------------

#Gson
-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}