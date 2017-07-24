/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.jarlen.photoedit.photoframe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import java.util.List;

import static android.R.attr.bitmap;


public class PhotoFrame {

    /**
     * 资源ID的相框合成
     *
     * @return
     */
    public Bitmap combineFrame(Context context, Bitmap backdrop, int res) {
        return combinateFrame(context, backdrop, decodeBitmap(context, res));
    }

    /**
     * 资源ID的相框合成
     *
     * @return
     */
    public Bitmap combineFrame(Context context, Bitmap backdrop, Bitmap bitmap) {
        return combinateFrame(context, backdrop, bitmap);
    }

    /**
     * 资源ID的相框合成
     *
     * @return
     */
    public Bitmap combineFrameBitmap(Context context, Bitmap backdrop, String pathName) {
        return combinateFrame(context, backdrop, decodeBitmap(pathName));
    }


    /**
     * @return 路径图片的合成相框
     */
    public Bitmap combineFramePathRes(Bitmap backdrop, List<String> mFramePathListRes) {
        return combinateFrame(backdrop, mFramePathListRes);
    }


    /**
     * 添加边框
     *
     * @return
     */
    private Bitmap combinateFrame(Context context, Bitmap backdrop, Bitmap bitmap) {
        Drawable[] array = new Drawable[2];
        array[0] = new BitmapDrawable(context.getResources(), backdrop);
        Bitmap b = resize(bitmap, backdrop.getWidth(), backdrop.getHeight());
        array[1] = new BitmapDrawable(context.getResources(), b);
        LayerDrawable layer = new LayerDrawable(array);
        return decodeBitmap(layer);
    }


    /**
     * 将Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap decodeBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将R.drawable.*转换成Bitmap
     *
     * @param res
     * @return
     */
    private Bitmap decodeBitmap(Context context, int res) {
        return BitmapFactory.decodeResource(context.getResources(), res);
    }


    /**
     * 将图片路径String 转化为bitmap
     *
     * @param pathName
     * @return
     */
    private Bitmap decodeBitmap(String pathName) {
        return BitmapFactory.decodeFile(pathName);
    }

    /**
     * 图片缩放
     *
     * @param bm
     * @param w
     * @param h
     * @return
     */
    public Bitmap resize(Bitmap bm, int w, int h) {
        Bitmap BitmapOrg = bm;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }


    /**
     * @param res 边框资源ID数组
     * @return 合成
     */
    private Bitmap combinateFrame(Context context, Bitmap backdrop, int[] res) {
        Bitmap bmp = decodeBitmap(context, res[0]);
        // 边框的宽高
        final int smallW = bmp.getWidth();
        final int smallH = bmp.getHeight();

        // 原图片的宽高
        final int bigW = backdrop.getWidth();
        final int bigH = backdrop.getHeight();

        int wCount = (int) Math.ceil(bigW * 1.0 / smallW);
        int hCount = (int) Math.ceil(bigH * 1.0 / smallH);

        // 组合后图片的宽高
        int newW = (wCount + 2) * smallW;
        int newH = (hCount + 2) * smallH;

        // 重新定义大小
        Bitmap newBitmap = Bitmap.createBitmap(newW, newH, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint p = new Paint();
        p.setColor(Color.TRANSPARENT);
        canvas.drawRect(new Rect(0, 0, newW, newH), p);

        Rect rect = new Rect(smallW, smallH, newW - smallW, newH - smallH);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);

        // 绘原图
        canvas.drawBitmap(backdrop,
                (newW - bigW - 2 * smallW) / 2 + smallW,
                (newH - bigH - 2 * smallH) / 2 + smallH, null);
        // 绘边框
        // 绘四个角
        int startW = newW - smallW;
        int startH = newH - smallH;
        Bitmap leftTopBm = decodeBitmap(context, res[0]); // 左上角
        Bitmap leftBottomBm = decodeBitmap(context, res[2]); // 左下角
        Bitmap rightBottomBm = decodeBitmap(context, res[4]); // 右下角
        Bitmap rightTopBm = decodeBitmap(context, res[6]); // 右上角

        canvas.drawBitmap(leftTopBm, 0, 0, null);
        canvas.drawBitmap(leftBottomBm, 0, startH, null);
        canvas.drawBitmap(rightBottomBm, startW, startH, null);
        canvas.drawBitmap(rightTopBm, startW, 0, null);

        leftTopBm.recycle();
        leftTopBm = null;
        leftBottomBm.recycle();
        leftBottomBm = null;
        rightBottomBm.recycle();
        rightBottomBm = null;
        rightTopBm.recycle();
        rightTopBm = null;

        // 绘左右边框
        Bitmap leftBm = decodeBitmap(context, res[1]);
        Bitmap rightBm = decodeBitmap(context, res[5]);
        for (int i = 0, length = hCount; i < length; i++) {
            int h = smallH * (i + 1);
            canvas.drawBitmap(leftBm, 0, h, null);
            canvas.drawBitmap(rightBm, startW, h, null);
        }

        leftBm.recycle();
        leftBm = null;
        rightBm.recycle();
        rightBm = null;

        // 绘上下边框
        Bitmap bottomBm = decodeBitmap(context, res[3]);
        Bitmap topBm = decodeBitmap(context, res[7]);
        for (int i = 0, length = wCount; i < length; i++) {
            int w = smallW * (i + 1);
            canvas.drawBitmap(bottomBm, w, startH, null);
            canvas.drawBitmap(topBm, w, 0, null);
        }

        bottomBm.recycle();
        bottomBm = null;
        topBm.recycle();
        topBm = null;

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return newBitmap;
    }

    private Bitmap combinateFrame(Bitmap backdrop, List<String> res) {
        Bitmap bmp = decodeBitmap(res.get(0));
        // 边框的宽高
        final int smallW = bmp.getWidth();
        final int smallH = bmp.getHeight();

        // 原图片的宽高
        final int bigW = backdrop.getWidth();
        final int bigH = backdrop.getHeight();

        int wCount = (int) Math.ceil(bigW * 1.0 / smallW);
        int hCount = (int) Math.ceil(bigH * 1.0 / smallH);

        // 组合后图片的宽高
        int newW = (wCount + 2) * smallW;
        int newH = (hCount + 2) * smallH;

        // 重新定义大小
        Bitmap newBitmap = Bitmap.createBitmap(newW, newH, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint p = new Paint();
        p.setColor(Color.TRANSPARENT);
        canvas.drawRect(new Rect(0, 0, newW, newH), p);

        Rect rect = new Rect(smallW, smallH, newW - smallW, newH - smallH);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);

        // 绘原图
        canvas.drawBitmap(backdrop,
                (newW - bigW - 2 * smallW) / 2 + smallW,
                (newH - bigH - 2 * smallH) / 2 + smallH, null);
        // 绘边框
        // 绘四个角
        int startW = newW - smallW;
        int startH = newH - smallH;
        Bitmap leftTopBm = decodeBitmap(res.get(0)); // 左上角
        Bitmap leftBottomBm = decodeBitmap(res.get(2)); // 左下角
        Bitmap rightBottomBm = decodeBitmap(res.get(4)); // 右下角
        Bitmap rightTopBm = decodeBitmap(res.get(6)); // 右上角

        canvas.drawBitmap(leftTopBm, 0, 0, null);
        canvas.drawBitmap(leftBottomBm, 0, startH, null);
        canvas.drawBitmap(rightBottomBm, startW, startH, null);
        canvas.drawBitmap(rightTopBm, startW, 0, null);

        leftTopBm.recycle();
        leftTopBm = null;
        leftBottomBm.recycle();
        leftBottomBm = null;
        rightBottomBm.recycle();
        rightBottomBm = null;
        rightTopBm.recycle();
        rightTopBm = null;

        // 绘左右边框
        Bitmap leftBm = decodeBitmap(res.get(1));
        Bitmap rightBm = decodeBitmap(res.get(5));
        for (int i = 0, length = hCount; i < length; i++) {
            int h = smallH * (i + 1);
            canvas.drawBitmap(leftBm, 0, h, null);
            canvas.drawBitmap(rightBm, startW, h, null);
        }

        leftBm.recycle();
        leftBm = null;
        rightBm.recycle();
        rightBm = null;

        // 绘上下边框
        Bitmap bottomBm = decodeBitmap(res.get(3));
        Bitmap topBm = decodeBitmap(res.get(7));
        for (int i = 0, length = wCount; i < length; i++) {
            int w = smallW * (i + 1);
            canvas.drawBitmap(bottomBm, w, startH, null);
            canvas.drawBitmap(topBm, w, 0, null);
        }

        bottomBm.recycle();
        bottomBm = null;
        topBm.recycle();
        topBm = null;

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return newBitmap;
    }

}
