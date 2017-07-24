package com.space.app.chili.common;

/**
 * 公共事件
 * Created by zhuyinan on 2017/6/13.
 */
public class BusEvent {

    public BusEvent() {

    }

    public BusEvent(int id) {
        this.id = id;
    }

    public BusEvent(int id, String json) {
        this.id = id;
        this.jsonStr = json;
    }

    public BusEvent(int id, int digit) {
        this.id = id;
        this.digit = digit;
    }

    public int id;

    public String jsonStr;

    public int digit;

}
