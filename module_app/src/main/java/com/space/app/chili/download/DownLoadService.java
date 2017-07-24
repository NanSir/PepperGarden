package com.space.app.chili.download;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;


import com.space.app.chili.utils.HttpUtil;

import java.io.File;

/**
 * 下载服务
 * Created by zs on 2016/7/8.
 */
public class DownLoadService extends Service {

    private int preProgress = 0;

    private int NOTIFY_ID = 1000;

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownloadInfo bean = (DownloadInfo) intent.getSerializableExtra("DownloadInfo");
        //开始下载文件
        downloadFile(bean);
        return super.onStartCommand(intent, flags, startId);
    }


    private void downloadFile(DownloadInfo bean) {
        initNotification(bean.getLogo(), bean.getTitle(), bean.getContent());
        HttpUtil.download(bean.getLoadUrl(), bean.getSaveUrl(), bean.getName(), new HttpUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                updateNotification(100);
                setContentIntent(file.getAbsolutePath());
                installApk(getApplication(), file);
            }

            @Override
            public void onDownloading(int progress) {
                updateNotification(progress);
            }

            @Override
            public void onDownloadFailed(String e) {
                Log.e("TAG", "onDownloadFailed\n" + e);
            }
        });
    }


    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName(), file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    private void setContentIntent(String filePath) {
        Intent intentClick = new Intent(this, NotificationReceiver.class);
        intentClick.setAction("notification_clicked");
        intentClick.putExtra("filePath", filePath);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntentClick);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }


    /**
     * 初始化Notification通知
     */
    public void initNotification(int icon, String title, String content) {
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentText(content)
                .setContentTitle(title)
                .setProgress(100, 0, false);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 更新通知
     */
    public void updateNotification(long progress) {
        int currProgress = (int) progress;
        if (preProgress < currProgress) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, (int) progress, false);
            notificationManager.notify(NOTIFY_ID, builder.build());
        }
        preProgress = (int) progress;
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        notificationManager.cancel(NOTIFY_ID);
    }
}
