package com.space.app.chili.common;

import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 错误日志工具
 * Created by zhuyinan on 2017/6/14.
 */
public class ErrorLogCollector {

    private static ErrorLogCollector errorLog;

    private ErrorLogCollector(){

    }

    public static ErrorLogCollector getInstance() {
        if (errorLog == null) {
            errorLog = new ErrorLogCollector();
        }
        return errorLog;
    }

    public Thread.UncaughtExceptionHandler getUncaughtException() {
        return uncaughtExceptionHandler;
    }

    /**
     * 记录错误崩溃日志
     */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            String info = null;
            ByteArrayOutputStream byteOut = null;
            PrintStream printStream = null;
            try {
                byteOut = new ByteArrayOutputStream();
                printStream = new PrintStream(byteOut);
                ex.printStackTrace(printStream);
                byte[] data = byteOut.toByteArray();
                info = new String(data);
                data = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (printStream != null) {
                        printStream.close();
                    }
                    if (byteOut != null) {
                        byteOut.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("TAG", info);
            writeErrorLog(info);
            System.exit(10);
        }
    };

    /**
     * 写入错误日志
     *
     * @param info
     */
    private void writeErrorLog(String info) {
        info = getCurrentDateString() + "\n" + info;
        File dir = new File(Environment.getExternalStorageDirectory() + "/ErrorLog/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "ChiliLog.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    private String getCurrentDateString() {
        String timeString;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date nowDate = new Date();
        timeString = sdf.format(nowDate);
        return timeString;
    }
}
