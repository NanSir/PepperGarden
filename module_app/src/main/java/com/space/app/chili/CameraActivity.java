package com.space.app.chili;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.widget.MagicCameraView;
import com.sir.app.base.BaseActivity;
import com.sir.app.base.BaseRecyclerAdapter;
import com.sir.app.base.help.OnItemClickListener;
import com.sir.app.base.help.ViewHolder;
import com.space.app.chili.apdater.ModuleAdapter;
import com.space.app.chili.utils.MenuUtils;
import com.space.app.chili.utils.ModuleUtils;
import com.space.app.chili.utils.UpdatesUtils;

import java.io.File;
import java.util.ArrayList;


import butterknife.BindView;
import butterknife.OnClick;
import cn.jarlen.photoedit.operate.ImageObject;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.operate.OperateView;
import cn.jarlen.photoedit.photoframe.PhotoFrame;
import cn.jarlen.photoedit.utils.FileUtils;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * 拍照界面
 * Created by zhuyinan on 2017/6/1.
 */
public class CameraActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.camera_view)
    MagicCameraView cameraView;
    @BindView(R.id.camera_capture)
    ImageButton captureBtn;
    @BindView(R.id.camera_show)
    ImageView cameraShow;
    @BindView(R.id.canvas_layout)
    LinearLayout canvasLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    ModuleAdapter adapter;

    ObjectAnimator animator;
    MediaPlayer shootMP;
    MagicEngine magicEngine;
    File mediaStoragePath;
    String mediaStorageFolder = "Camera";

    CheckBox frame, watermark;
    PhotoFrame mImageFrame;

    OperateView operateView;
    OperateUtils operateUtils;

    int cameraFrame;

    @Override
    public int bindLayout() {
        return R.layout.activity_camera;
    }


    @Override
    public void initView(Bundle savedInstanceState) {
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setCameraMenu(R.id.camera_skin);
        setCameraMenu(R.id.camera_delayed);
        watermark = setCameraMenu(R.id.camera_watermark);
        frame = setCameraMenu(R.id.camera_frame);

        adapter = new ModuleAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        //照片存储目
        mediaStoragePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), mediaStorageFolder);
        //初始化相机
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder.build(cameraView);

        mImageFrame = new PhotoFrame();
    }

    @Override
    public void doBusiness(Context mContext) {
        animator = ObjectAnimator.ofFloat(captureBtn, "rotation", 0, 360);
        animator.setDuration(300);
        animator.setRepeatCount(1);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewHolder holder, int position) {
                if (frame.isChecked()) {
                    if (position == 0) {
                        cameraView.setForeground(null);
                    } else {
                        cameraView.setForeground(getDrawable(ModuleUtils.getPhotoFrame(position)));
                    }
                    cameraFrame = position;
                } else if (watermark.isChecked()) {
                    if (position == 0) {
                        operateView.clear();
                    } else {
                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), adapter.getItem(position));
                        ImageObject imgObject = operateUtils.getImageObject(bmp, operateView, 5, 150, 100);
                        operateView.addItem(imgObject);
                    }
                }
            }
        });

        //检查更新
        new UpdatesUtils().latest(getContext());
    }


    private void initOperateView() {
        if (operateView == null) {
            operateView = new OperateView(this, canvasLayout);
            operateUtils = new OperateUtils(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    canvasLayout.getWidth(), canvasLayout.getHeight());

            operateView.setLayoutParams(layoutParams);
            operateView.setMultiAdd(false);
            canvasLayout.addView(operateView);

            operateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCameraMenu();
                }
            });
        }
    }

    boolean isMenu = true;

    //相机操作菜单
    private CheckBox setCameraMenu(@IdRes int id) {
        CheckBox checkBox = (CheckBox) findViewById(id);
        checkBox.setOnCheckedChangeListener(this);
        return checkBox;
    }

    //显示相机菜单
    public void showCameraMenu() {
        if (isMenu) {
            MenuUtils.hideToolbar(findViewById(R.id.camera_menu), getContext(), 90);
        } else {
            MenuUtils.showToolbar(findViewById(R.id.camera_menu), getContext(), 90);
        }
        isMenu = !isMenu;
    }


    @OnClick({R.id.camera_capture, R.id.camera_switch, R.id.camera_show, R.id.canvas_layout})
    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.camera_capture:
                if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, view.getId());
                } else {
                    takePhoto();
                }
                break;
            case R.id.camera_switch:
                magicEngine.switchCamera();
                break;
            case R.id.camera_show:
                showPhoto();
                break;
            case R.id.canvas_layout:
                showCameraMenu();
                break;

        }
    }

    //显示相机相框菜单
    private void photoFrame(boolean checked) {
        if (checked) {
            recyclerView.setVisibility(View.VISIBLE);
            watermark.setChecked(false);
            adapter.clearAll();
            adapter.addItem(ModuleUtils.getPhotoFramePreview());
            adapter.notifyDataSetChanged();
        } else {
            if (!watermark.isChecked()) {
                recyclerView.setVisibility(View.INVISIBLE);
            }
        }
    }

    //显示相机水印菜单
    private void cameraWatermark(boolean checked) {
        if (checked) {
            initOperateView();
            recyclerView.setVisibility(View.VISIBLE);
            frame.setChecked(false);
            adapter.clearAll();
            adapter.addItem(ModuleUtils.getWatermark());
            adapter.notifyDataSetChanged();
        } else {
            if (!frame.isChecked()) {
                recyclerView.setVisibility(View.INVISIBLE);
            }
        }
    }


    /**
     * 拍照
     */
    private void takePhoto() {
        animator.start();
        shootSound();
        magicEngine.savePicture(FileUtils.getOutputMediaFile(mediaStoragePath), new SavePictureTask.OnPictureSaveListener() {
            @Override
            public void onSaved(String result) {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    PhotoProcessing((String) msg.obj);
                    break;
            }
        }
    };


    /**
     * 处理图片
     *
     * @param filePath
     */
    private void PhotoProcessing(final String filePath) {

        if (operateView != null) {
            operateView.save();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cameraView.getForeground() != null) {
                    Bitmap bitmap = mImageFrame.combineFrame(getContext(), FileUtils.getBitmap(filePath), ModuleUtils.getPhotoFrame(cameraFrame));
                    FileUtils.writeImage(bitmap, filePath, 100); //存储内存
                }

                if (operateView != null && operateView.isImage()) {
                    Bitmap bitmap = mImageFrame.combineFrame(getContext(), FileUtils.getBitmap(filePath), getBitmapByView(operateView));
                    FileUtils.writeImage(bitmap, filePath, 100);
                }
            }
        }).start();
        //更新预览图
        cameraShow.setImageBitmap(FileUtils.ResizeBitmap(FileUtils.getBitmap(filePath), 100, 100));
    }

    // 将模板View的图片转化为Bitmap
    public Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }


    /**
     * 显示照片集
     */
    public void showPhoto() {
        PhotoPicker.builder()
                .setPhotoCount(3)
                .setGridColumnCount(3)
                .setShowPicturesFolder(mediaStorageFolder)
                .start(CameraActivity.this);
    }

    /**
     * 播放系统拍照声音
     */
    public void shootSound() {
        AudioManager audio = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int volume = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (volume != 0) {
            if (shootMP == null) {
                shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            }
            if (shootMP != null) {
                shootMP.start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新预览图
        String file = FileUtils.getLastPhoto(mediaStoragePath);
        if (!TextUtils.isEmpty(file)) {
            cameraShow.setImageBitmap(FileUtils.ResizeBitmap(FileUtils.getBitmap(file), 100, 100));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            ArrayList<String> photos;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                // getOperation().addParameter(photos, "photos").forward(PrintActivity.class);
                Intent mIntent = new Intent();
                mIntent.putStringArrayListExtra("photos", photos);
                mIntent.setClass(this, PrintActivity.class);
                startActivity(mIntent);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        switch (compoundButton.getId()) {
            case R.id.camera_skin:
                magicEngine.setBeautyLevel(checked ? 4 : 0);
                break;
            case R.id.camera_delayed:
                Log.e("TAG", "延时拍摄");
                break;
            case R.id.camera_watermark:
                cameraWatermark(checked);
                break;
            case R.id.camera_frame:
                photoFrame(checked);
                break;
        }
    }
}
