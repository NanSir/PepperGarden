package com.space.app.chili.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.space.app.chili.utils.DeviceUtils;

import java.io.File;

/**
 * Created by zhuyinan on 2017/5/6.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals("notification_clicked")) {
            //处理点击事件
            String absoluteFile = intent.getStringExtra("filePath");
            DeviceUtils.installApk(context, new File(absoluteFile));
        }

        if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
            Log.e("TAG", "notification_cancelled");
        }
    }
}
