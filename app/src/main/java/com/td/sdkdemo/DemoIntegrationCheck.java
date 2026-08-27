package com.td.sdkdemo;

import android.util.Log;
import com.td.ads.base.common.TDIntegration;

public final class DemoIntegrationCheck {
    private static final String TAG = "TDIntegration";

    private DemoIntegrationCheck() {}

    public static String dump() {
        StringBuilder sb = new StringBuilder("== TD integration check ==\n");
        probe(sb, "adapter", "jdsdk", "com.td.ads.jdsdk.JdsdkReward");
        probe(sb, "adapter", "adgain", "com.td.ads.adgain.AdgainReward");
        probe(sb, "adapter", "ltmb", "com.td.ads.ltmb.LtmbReward");
        probe(sb, "peer", "jdsdk", "com.jd.ad.polyunion.view.AdView");
        probe(sb, "peer", "adgain", "com.adgain.sdk.AdGainSdk");
        probe(sb, "peer", "ltmb", "com.ltmb.ltsdk.core.LTAdSdk");
        String text = sb.toString();
        Log.i(TAG, text);
        return text;
    }

    private static void probe(StringBuilder sb, String kind, String name, String className) {
        boolean ok = TDIntegration.isClassPresent(className);
        sb.append(kind).append(' ').append(name).append(' ').append(className)
                .append(' ').append(ok ? "LINKED" : "missing").append('\n');
    }
}
