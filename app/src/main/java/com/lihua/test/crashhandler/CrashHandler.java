package com.lihua.test.crashhandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath()
            + "/CrashTest/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    public CrashHandler() {
    }

    public static CrashHandler getInstance(){
        return sInstance;
    }

    public void init(Context context){
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //导出异常信息到sdcard
        dumpExceptionToSDcard(e);
        //上传异常到服务器，便于开发人员解决
        uploadExceptionToServer();

        e.printStackTrace();
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则由自己结束自己
        if (mDefaultCrashHandler != null){
            mDefaultCrashHandler.uncaughtException(t,e);
        }else {
            Process.killProcess(Process.myPid());
            if (BuildConfig.DEBUG) Log.d(TAG, "有异常处理，不需要再这个线程处理");
        }
    }

    private void dumpExceptionToSDcard(Throwable e) {
        //如果sdcard不存在或无法使用，则无法把异常信息写人到sdcard
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "sdcard unmounted ,skip dump exception!");
            return;
        }

        File dir = new File("/storage/emulated/0/CrashTest/log/");//new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            e.printStackTrace(pw);
            pw.close();
        }catch (Exception e1){
            e1.printStackTrace();
            Log.e(TAG, "dumpExceptionToSDcard failed ");
        }

    }

    private void dumpPhoneInfo(PrintWriter pw) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
            //app version
            pw.print("App version : ");
            pw.print(packageInfo.versionName);
            pw.print("_");
            pw.println(packageInfo.versionCode);

            //android version
            pw.print("OS vesion: ");
            pw.print(Build.VERSION.RELEASE);
            pw.print("_");
            pw.println(Build.VERSION.SDK_INT);

            //手机制造商
            pw.print("Vendor: ");
            pw.println(Build.MANUFACTURER);

            //手机型号
            pw.print("Model: ");
            pw.println(Build.MODEL);

            //cpu架构
            pw.print("CPU ABI: ");
            pw.println(Build.CPU_ABI);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void uploadExceptionToServer() {
        //todo upload Exception Message to your web server
    }
}
