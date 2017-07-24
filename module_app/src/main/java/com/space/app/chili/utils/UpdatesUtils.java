package com.space.app.chili.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;


import com.google.gson.Gson;
import com.space.app.chili.R;
import com.space.app.chili.common.UpdateInfo;
import com.space.app.chili.download.DownLoadService;
import com.space.app.chili.download.DownloadInfo;

import java.io.File;


import cn.pedant.SweetAlert.SweetAlertDialog;
import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

/**
 * 检查APK更新
 * Created by zhuyinan on 2017/5/5.
 */
public class UpdatesUtils {

    SweetAlertDialog alertDialog;

    String TAG = UpdatesUtils.class.getSimpleName();

    String firToken = "c6c9ef298336f1197454dc9d749d0617";

    private void showCheckUpdates(Context context) {
        alertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("检查更新")
                .setCancelText("取消")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public void latest(final Context context) {
        FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
            @Override
            public void onSuccess(final String json) {
                Log.i(TAG, "check from fir.im success!" + "\n" + json);
                UpdateInfo apk = new Gson().fromJson(json, UpdateInfo.class);
                int mVersion_code = DeviceUtils.getVersionCode(context);// 当前的版本号
                if (mVersion_code < Integer.parseInt(apk.getVersion())) {
                    // 显示提示对话
                    showNoticeDialog(context, setUpdate(apk));
                } else {
                    Log.i(TAG, "check from fir.im success!" + "\n" + "当前最新版本");
                }
            }

            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "check from fir.im onFail!" + "\n" + e.toString());
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
        });
    }


    public void checkUpdates(final Context context) {
        FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
            @Override
            public void onSuccess(final String json) {
                UpdateInfo apk = new Gson().fromJson(json, UpdateInfo.class);
                int mVersion_code = DeviceUtils.getVersionCode(context);// 当前的版本号
                if (mVersion_code < Integer.parseInt(apk.getVersion())) {
                    // 显示提示对话
                    showNoticeDialog(context, setUpdate(apk));
                } else {
                    alertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    alertDialog.showCancelButton(false);
                    alertDialog.setTitleText("已经是最新版本");
                }
            }

            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "check fir.im fail! " + "\n" + e.getMessage());
                alertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                alertDialog.setContentText(e.getMessage());
            }

            @Override
            public void onStart() {
                showCheckUpdates(context);
            }

            @Override
            public void onFinish() {

            }
        });
    }


    private DownloadInfo setUpdate(UpdateInfo apk) {
        String fileName = apk.getName() + "_v" + apk.getVersion() + ".apk";
        String destFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        Log.i(TAG, "下载APK存储路径:" + destFileDir);
        DownloadInfo loadingBean = new DownloadInfo(apk.getBinary().getFsize(), 0);
        loadingBean.setLogo(R.mipmap.ic_launcher);
        loadingBean.setName(fileName);
        loadingBean.setContent(apk.getChangelog());
        loadingBean.setLoadUrl(apk.getInstallUrl());
        loadingBean.setSaveUrl(destFileDir);
        loadingBean.setVersionName(apk.getVersionShort());
        loadingBean.setCode(Integer.parseInt(apk.getVersion()));
        return loadingBean;
    }

    private boolean checkLocal(DownloadInfo info) {
        File file = new File(info.getSaveUrl(), info.getName());
        if (file.exists()) {
            Log.i(TAG, file.getAbsolutePath() + "\nThe APK size:" + file.length());
            Log.i(TAG, "Download Info APK Size:" + info.getTotal());
            return file.length() == info.getTotal(); //文件存在，大小一样不用重新下
        }
        return false;
    }

    private File getLocal(DownloadInfo info) {
        return new File(info.getSaveUrl(), info.getName());
    }

    /**
     * 显示更新对话框
     */
    private void showNoticeDialog(final Context context, final DownloadInfo info) {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("更新提示");
        builder.setMessage(info.getContent());
        // 更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (checkLocal(info)) {//检查本地是否已经下载
                    Log.i(TAG, "检查本到地已经下载APK");
                    DeviceUtils.installApk(context, getLocal(info));
                } else {
                    Intent intent = new Intent(context, DownLoadService.class);
                    intent.putExtra("DownloadInfo", info);
                    context.startService(intent);
                }
            }
        });
        // 稍后更新
        builder.setNegativeButton("以后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
}
