package com.example.huilee.okhttpdemo;

import android.util.Log;

/**
 * Created by huilee on 2017/4/9.
 */

public class LogUtil {

    private static final String TAG = "imooc_okhttp";
    private static boolean debug = true;

    public static void e(String msg){
        if(debug){
            Log.e(TAG,msg);
        }
    }

}
