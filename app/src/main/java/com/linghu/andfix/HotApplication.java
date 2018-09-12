package com.linghu.andfix;

import android.app.Application;
import android.util.Log;

import com.alipay.euler.andfix.patch.PatchManager;

import java.io.IOException;

/**
 * Created by linghu on 2018/9/12.
 * 网址参考：https://blog.csdn.net/AndroidMsky/article/details/54377806
 *           http://blog.csdn.net/AndroidMsky/article/details/54377806
 *
 */
public class HotApplication extends Application {
    public static final String Tag = "andfix";
    static PatchManager patchManager;

    @Override
    public void onCreate() {
        super.onCreate();

        patchManager = new PatchManager(HotApplication.this);
        patchManager.init("1.0");//current version //String appversion=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        patchManager.loadPatch();
    }


    public static void load() {
        try {
            Log.d(Tag, "=======PatchManager========");
            //adb push  out.apatch  /sdcard/linghu/
            patchManager.addPatch("/sdcard/linghu/out.apatch");
            Log.d(Tag, "=======PatchManager.addPatch success");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Tag, "=======PatchManager.addPatch fail");
        }
    }

    public static void remove() {
        patchManager.removeAllPatch();
        Log.d(Tag, "=======PatchManager removePatch");
    }

}