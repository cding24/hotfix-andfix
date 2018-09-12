#AndFix-Demo
A demo about using AndFix 0.5.0
AndFix的集成使用。


一、andfix使用
  1. 首先添加依赖
      compile 'com.alipay.euler:andfix:0.3.1@aar'

   然后在Application.onCreate()中添加以下代码
      patchManager = new PatchManager(context);
      patchManager.init(appversion); //String appversion= getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
      patchManager.loadPatch();
  注意每次appversion变更都会导致所有补丁被删除,如果appversion没有改变，则会加载已经保存的所有补丁。
  然后在需要的地方调用PatchManager的addPatch方法加载新补丁，比如可以在下载补丁文件之后调用。
  
  混淆需要加入：
      -keep class * extends java.lang.annotation.Annotation
      -keepclasseswithmembernames class * {
          native <methods>;
      }
      -keep class com.alipay.euler.andfix.** { *; }

2. 打补丁，首先生成一个apk文件，然后更改代码，在修复bug后生成另一个apk。
   通过官方提供的工具apkpatch生成一个.apatch格式的补丁文件，需要提供原apk，修复后的apk，以及一个签名文件。
   可以直接使用命令apkpatch查看具体的使用方法。
   使用示例：apkpatch -o D:/Patch/ -k debug.keystore -p android -a androiddebugkey -e android f bug-fix.apk t release.apk

3. 通过网络传输或者adb push的方式将apatch文件传到手机上;
4. 然后运行到patchManager.addPatch的时候就会加载补丁。
   加载过的补丁会被保存到data/packagename/files/apatch_opt目录下，所以下载过来的补丁用过一次就可以删除了。

注意：每次产生的apatch文件的名字如果是相同的，结果会导致只有第一次的补丁能生效。只有每次名字不同才能加载，log中应该也有提示。

二、andfix原理：
    apkpatch将两个apk做一次对比，然后找出不同的部分。可以看到生成的apatch了文件，后缀改成zip再解压开，里面有一个dex文件。通过jadx查看一下源码，里面就是被修复的代码所在的类文件,这些更改过的类都加上了一个_CF的后缀，并且变动的方法都被加上了一个叫@MethodReplace的annotation，通过clazz和method指定了需要替换的方法。然后客户端sdk得到补丁文件后就会根据annotation来寻找需要替换的方法。最后由JNI层完成方法的替换。如果本地保存了多个补丁，那么AndFix会按照补丁生成的时间顺序加载补丁。具体是根据.apatch文件中的PATCH.MF的字段Created-Time。
 安全验证：
    文件的签名验证：AndFix对apatch文件签名做了是否和应用的签名是同一个的验证，不是的话就不加载该补丁。
    指纹验证：为了防止有人替换掉本地保存的补丁文件，所以要验证MD5码。SecurityChecker类里面也已经做了验证处理，但是这个MD5码是保存在sharedpreference里面，如果手机已经root那么还是可以被访问的。
    
  三、优缺点：
  优点：
    1. 不需要重启APP即可应用补丁，对用户透明无感知；
    2. 能满足快速修复问题。
  缺点：
    1. 无法添加新类和新的字段；
    2. 需要使用加固前的apk制作补丁，但是补丁文件很容易被反编译，也就是修改过的类源码容易泄露。使用加固平台可能会使热补丁功能失效（在360加固提了这个问题，未验证）。
