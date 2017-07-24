package com.space.app.chili.utils;

import com.space.app.chili.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyinan on 2017/6/8.
 */
public class ModuleUtils {


    public static List<Integer> getWatermark() {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.ic_not_have);
        list.add(R.drawable.ic_watermark_a);
        list.add(R.drawable.ic_watermark_b);
        list.add(R.drawable.ic_watermark_c);
        list.add(R.drawable.ic_watermark_d);
        list.add(R.drawable.ic_watermark_e);
        list.add(R.drawable.ic_watermark_f);
        list.add(R.drawable.ic_watermark_g);
        list.add(R.drawable.ic_watermark_h);
        list.add(R.drawable.ic_watermark_i);
        list.add(R.drawable.ic_watermark_j);
        list.add(R.drawable.ic_watermark_k);
        list.add(R.drawable.ic_watermark_l);
        return list;
    }

    public static List<Integer> getPhotoFramePreview() {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.ic_not_have);
        list.add(R.drawable.ic_frame_preview_a);
        return list;
    }

    public static Integer getPhotoFrame(int i) {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.ic_not_have);
        list.add(R.drawable.ic_frame_a);
        return list.get(i);
    }



}
