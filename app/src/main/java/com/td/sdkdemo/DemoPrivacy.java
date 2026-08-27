package com.td.sdkdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.td.ads.base.privacy.TDPrivacyController;
import com.td.ads.base.privacy.TDPrivacyDeviceInfo;
import com.td.ads.open.TDAdsSDK;
import java.util.ArrayList;
import java.util.List;

public final class DemoPrivacy {
    private static final String TAG = "DemoPrivacy";
    private static final String PREF = "td_sdkdemo_privacy";
    public static final String KEY_AGREED = "privacy_agreed";
    public static final String KEY_PERSONALIZED = "personalized";
    public static final String KEY_OAID = "oaid";
    public static final String KEY_DENY_LOCATION = "deny_location";
    public static final String KEY_DENY_INSTALL = "deny_install_list";

    private DemoPrivacy() {
    }

    public static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static boolean isAgreed(Context ctx) {
        return prefs(ctx).getBoolean(KEY_AGREED, false);
    }

    public static void setAgreed(Context ctx, boolean v) {
        prefs(ctx).edit().putBoolean(KEY_AGREED, v).apply();
        if (v && oaid(ctx)) {
            TDAdsSDK.setAuthUID(ctx, true);
        }
    }

    public static boolean personalized(Context ctx) {
        return prefs(ctx).getBoolean(KEY_PERSONALIZED, true);
    }

    public static boolean oaid(Context ctx) {
        return prefs(ctx).getBoolean(KEY_OAID, true);
    }

    public static boolean denyLocation(Context ctx) {
        return prefs(ctx).getBoolean(KEY_DENY_LOCATION, false);
    }

    public static boolean denyInstall(Context ctx) {
        return prefs(ctx).getBoolean(KEY_DENY_INSTALL, false);
    }

    public static void put(Context ctx, String key, boolean v) {
        prefs(ctx).edit().putBoolean(key, v).apply();
    }

    public static void applySampleDefaults(Context ctx) {
        setAgreed(ctx, true);
        put(ctx, KEY_PERSONALIZED, true);
        put(ctx, KEY_OAID, true);
        put(ctx, KEY_DENY_LOCATION, false);
        put(ctx, KEY_DENY_INSTALL, false);
    }

    public static void applyBeforeInit(Context ctx) {
        List<String> deny = new ArrayList<>();
        if (denyLocation(ctx)) {
            deny.add(TDPrivacyDeviceInfo.LOCATION);
        }
        if (denyInstall(ctx)) {
            deny.add(TDPrivacyDeviceInfo.APP_INSTALL_LIST);
        }
        if (!deny.isEmpty()) {
            TDAdsSDK.deniedUploadDeviceInfo(deny.toArray(new String[0]));
        }
        TDAdsSDK.setAuthUID(ctx, oaid(ctx));
        TDAdsSDK.setOpenPersonalizedAd(personalized(ctx));
        TDAdsSDK.setPrivacyController(new TDPrivacyController() {
            @Override
            public boolean isCanUseLocation() {
                return !denyLocation(ctx);
            }

            @Override
            public boolean alist() {
                return !denyInstall(ctx);
            }

            @Override
            public boolean isCanUseOaid() {
                return oaid(ctx);
            }
        });
        Log.i(TAG, "applyBeforeInit deny=" + deny + " oaid=" + oaid(ctx));
    }

    public static void applyAfterInit(Context ctx) {
        TDAdsSDK.setPrivacyUserAgree(isAgreed(ctx));
        TDAdsSDK.setOpenPersonalizedAd(personalized(ctx));
        TDAdsSDK.setAuthUID(ctx, oaid(ctx));
        Log.i(TAG, "applyAfterInit agree=" + isAgreed(ctx)
                + " personalized=" + personalized(ctx)
                + " oaid=" + oaid(ctx)
                + " denied=" + TDAdsSDK.getDeniedUploadDeviceInfo());
    }

    public static String statusLine(Context ctx) {
        return "agree=" + isAgreed(ctx)
                + " personal=" + personalized(ctx)
                + " oaid=" + oaid(ctx)
                + " denyLoc=" + denyLocation(ctx)
                + " denyList=" + denyInstall(ctx);
    }
}
