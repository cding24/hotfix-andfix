#AndFix-Demo
A demo about using AndFix 0.5.0
AndFix的集成使用。


1. 首先添加依赖
      compile 'com.alipay.euler:andfix:0.3.1@aar'

   然后在Application.onCreate()中添加以下代码
      patchManager = new PatchManager(context);
      patchManager.init(appversion); //String appversion= getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
      patchManager.loadPatch();
  注意每次appversion变更都会导致所有补丁被删除,如果appversion没有改变，则会加载已经保存的所有补丁。
  然后在需要的地方调用PatchManager的addPatch方法加载新补丁，比如可以在下载补丁文件之后调用。

2. 打补丁，首先生成一个apk文件，然后更改代码，在修复bug后生成另一个apk。
   通过官方提供的工具apkpatch生成一个.apatch格式的补丁文件，需要提供原apk，修复后的apk，以及一个签名文件。
   可以直接使用命令apkpatch查看具体的使用方法。
   使用示例：apkpatch -o D:/Patch/ -k debug.keystore -p android -a androiddebugkey -e android f bug-fix.apk t release.apk

3. 通过网络传输或者adb push的方式将apatch文件传到手机上，
4. 然后运行到patchManager.addPatch的时候就会加载补丁。
   加载过的补丁会被保存到data/packagename/files/apatch_opt目录下，所以下载过来的补丁用过一次就可以删除了。
