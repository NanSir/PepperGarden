package com.space.app.chili.upload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.space.app.chili.common.BusEvent;
import com.space.app.chili.common.BusProvider;
import com.space.app.chili.utils.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 上传服务
 * Created by zhuyinan on 2017/6/12.
 */
public class UploadService extends Service {

    String TAG = UploadService.class.getSimpleName();
    int upOption;
    String uploadUrl = "http://192.168.40.228/chilicity/web/index.php/api/default/image-up";
    //String uploadUrl = "http://192.168.40.15:8080/hello/doUploads";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<String> urls = intent.getStringArrayListExtra("fileUrls");
        RequestParams params = new RequestParams();
        for (int i = 0; i < urls.size(); i++) {
            try {
                params.put("files[" + i + "]", new File(urls.get(i)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        postFile(params);
        return super.onStartCommand(intent, flags, startId);
    }


    private void postFile(RequestParams params) {
        HttpUtil.post(getApplication(), uploadUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, new String(responseBody));
                BusProvider.getBusInstance().post(new BusEvent(101));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    Log.e(TAG, new String(responseBody));
                }
                BusProvider.getBusInstance().post(new BusEvent(102));
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                if (upOption != count) {
                    upOption = count;
                    BusProvider.getBusInstance().post(new BusEvent(100, count));
                }
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }
}
