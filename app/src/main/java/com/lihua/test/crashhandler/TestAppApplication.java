package com.lihua.test.crashhandler;

import android.app.Application;

public class TestAppApplication extends Application {
    private static TestAppApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //这里为应用设置异常处理，然后程序才能够获取未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static TestAppApplication getInstance(){
        return sInstance;
    }
}
