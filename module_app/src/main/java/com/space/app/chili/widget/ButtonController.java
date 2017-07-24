package com.space.app.chili.widget;


public interface ButtonController {

    int getPressedColor(int color);

    int getLighterColor(int color);

    int getDarkerColor(int color);

    boolean enablePress();

    boolean enableGradient();

}

