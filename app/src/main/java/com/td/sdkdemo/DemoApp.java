package com.td.sdkdemo;

import android.app.Application;
import com.td.ads.open.TDAdsSDK;

public class DemoApp extends Application {
    public static final String APP_ID = AdConfig.APP_ID;
    public static final long UNIT_INTERSTITIAL = AdConfig.UNIT_INTERSTITIAL;
    public static final long UNIT_BANNER = AdConfig.UNIT_BANNER;
    public static final long UNIT_SPLASH = AdConfig.UNIT_SPLASH;
    public static final long UNIT_NATIVE = AdConfig.UNIT_NATIVE;
    public static final long UNIT_REWARD = AdConfig.UNIT_REWARD;

    public static long unitForFormat(String type) {
        return AdConfig.unitForFormat(type);
    }

    public static long[] allUnits() {
        return AdConfig.allUnits();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TDAdsSDK.setDebugMode(true);
        TDAdsSDK.setLocalMockMode(false);
        TDAdsSDK.setTestMode(false);
        if (DemoPrivacy.isAgreed(this) && DemoPrivacy.oaid(this)) {
            TDAdsSDK.setAuthUID(this, true);
        }
    }
}
