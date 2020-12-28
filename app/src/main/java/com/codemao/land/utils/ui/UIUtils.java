package com.codemao.land.utils.ui;

import com.codemao.land.activity.BCMApplication;

public class UIUtils {

    public static int px2dp(float pxValue) {
        return (int) (pxValue / BCMApplication.Companion.getScreenDensity() + 0.5f);
    }

    public static int dp2px(float dpValue) {
        return (int) (dpValue * BCMApplication.Companion.getScreenDensity() + 0.5f);
    }

    public static int px2sp(float pxValue) {
        return (int) (pxValue / BCMApplication.Companion.getScaledDensity());
    }

    public static int sp2px(float spValue) {
        return (int) (spValue * BCMApplication.Companion.getScaledDensity());
    }

}
