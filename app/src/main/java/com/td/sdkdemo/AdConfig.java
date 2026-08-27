package com.td.sdkdemo;

/** 后台 Android 对外 Demo（td外部demo / com.td.sdkdemo）。对内见 project/android/td-demo。 */
public final class AdConfig {
    public static final String APP_ID = "31014";
    public static final long UNIT_INTERSTITIAL = 621314L;
    public static final long UNIT_BANNER = 621311L;
    public static final long UNIT_SPLASH = 621313L;
    public static final long UNIT_NATIVE = 621312L;
    public static final long UNIT_REWARD = 621315L;

    private AdConfig() {}

    public static long unitForFormat(String type) {
        if (type == null) {
            return UNIT_REWARD;
        }
        switch (type) {
            case "interstitial":
                return UNIT_INTERSTITIAL;
            case "banner":
                return UNIT_BANNER;
            case "splash":
                return UNIT_SPLASH;
            case "native":
                return UNIT_NATIVE;
            default:
                return UNIT_REWARD;
        }
    }

    public static long[] allUnits() {
        return new long[] { UNIT_INTERSTITIAL, UNIT_BANNER, UNIT_SPLASH, UNIT_NATIVE, UNIT_REWARD };
    }
}
