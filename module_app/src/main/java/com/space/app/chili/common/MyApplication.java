package com.space.app.chili.common;


import com.sir.app.base.BaseApplication;

import im.fir.sdk.FIR;


/**
 * Created by zhuyinan on 2017/6/1.
 */

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
        //日志收集
        Thread.setDefaultUncaughtExceptionHandler(ErrorLogCollector.getInstance().getUncaughtException());
    }
}
