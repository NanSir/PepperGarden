package com.space.app.chili.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.view.View;


/**
 * 菜单帮助类
 * Created by zhuyinan on 2017/6/8.
 */
public class MenuUtils {

    public static void hideToolbar(final View v, Activity activity, int values) {
        ValueAnimator animator;
        animator = ValueAnimator.ofFloat(0, -(CommonUtil.dip2px(activity, values) + CommonUtil.getStatusHeight(activity)));
        animator.setTarget(v);
        animator.setDuration(400).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }

    public static void showToolbar(final View v, Activity activity, int values) {
        ValueAnimator animator;
        if (Build.VERSION.SDK_INT >= 19) {
            animator = ValueAnimator.ofFloat(-(CommonUtil.dip2px(activity, values) + CommonUtil.getStatusHeight(activity)), 0);
        } else {
            animator = ValueAnimator.ofFloat(-CommonUtil.dip2px(activity, values), 0);
        }
        animator.setTarget(v);
        animator.setDuration(500).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }


    public static void hideFloor(final View v, Activity activity, int values) {
        ValueAnimator animator;
        if (Build.VERSION.SDK_INT >= 19) {
            animator = ValueAnimator.ofFloat(0, (CommonUtil.dip2px(activity, values) + CommonUtil.getStatusHeight(activity)));
        } else {
            animator = ValueAnimator.ofFloat(0, CommonUtil.dip2px(activity, values));
        }
        animator.setTarget(v);
        animator.setDuration(400).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }

    public static void showFloor(final View v, Activity activity, int values) {
        ValueAnimator animator;
        animator = ValueAnimator.ofFloat(CommonUtil.dip2px(activity, values), 0);
        animator.setTarget(v);
        animator.setDuration(400).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }
}
