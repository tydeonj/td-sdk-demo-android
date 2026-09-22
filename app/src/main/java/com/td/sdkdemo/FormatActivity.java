package com.td.sdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.td.ads.base.adapter.TDNativeMaterial;
import com.td.ads.base.adapter.TDRenderType;
import com.td.ads.base.common.TDError;
import com.td.ads.open.TDAdInfo;
import com.td.ads.open.TDBanner;
import com.td.ads.open.TDBannerListener;
import com.td.ads.open.TDInterstitial;
import com.td.ads.open.TDInterstitialListener;
import com.td.ads.open.TDNative;
import com.td.ads.open.TDNativeListener;
import com.td.ads.open.TDReward;
import com.td.ads.open.TDRewardListener;
import com.td.ads.open.TDSplash;
import com.td.ads.open.TDSplashListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FormatActivity extends AppCompatActivity {
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_UNIT = "unit";
    public static final String TYPE_REWARD = "reward";
    public static final String TYPE_INTERSTITIAL = "interstitial";
    public static final String TYPE_SPLASH = "splash";
    public static final String TYPE_BANNER = "banner";
    public static final String TYPE_NATIVE = "native";

    private String type;
    private EditText etUnit;
    private EditText etFloor;
    private TextView tvLog;
    private FrameLayout container;
    private ViewGroup splashOverlay;
    private FrameLayout splashAdArea;

    private TDReward reward;
    private TDInterstitial interstitial;
    private TDSplash splash;
    private TDBanner banner;
    private TDNative nativeAd;
    private TDAdInfo lastNativeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format);
        type = getIntent().getStringExtra(EXTRA_TYPE);
        long unit = getIntent().getLongExtra(EXTRA_UNIT, 0L);

        TextView title = findViewById(R.id.tv_format_title);
        title.setText(titleOf(type));
        etUnit = findViewById(R.id.et_unit);
        etFloor = findViewById(R.id.et_floor);
        if (unit > 0L) etUnit.setText(String.valueOf(unit));
        tvLog = findViewById(R.id.tv_log);
        container = findViewById(R.id.ad_container);
        splashOverlay = findViewById(R.id.splash_overlay);
        splashAdArea = findViewById(R.id.splash_ad_area);
        applyContainerForType();

        findViewById(R.id.btn_load).setOnClickListener(v -> doLoad());
        findViewById(R.id.btn_show).setOnClickListener(v -> doShow());
        findViewById(R.id.btn_ready).setOnClickListener(v -> log("isReady=" + isReady()));

        createAd();
    }

    private static String titleOf(String type) {
        if (TYPE_REWARD.equals(type)) return "激励视频";
        if (TYPE_INTERSTITIAL.equals(type)) return "插屏";
        if (TYPE_SPLASH.equals(type)) return "开屏";
        if (TYPE_BANNER.equals(type)) return "横幅 Banner";
        if (TYPE_NATIVE.equals(type)) return "原生";
        return type == null ? "广告" : type;
    }

    private long unitId() {
        try {
            return Long.parseLong(etUnit.getText().toString().trim());
        } catch (Exception e) {
            return 0L;
        }
    }

    private void createAd() {
        destroyAds();
        long u = unitId();
        switch (type) {
            case TYPE_REWARD:
                reward = new TDReward(this, u);
                reward.setAdListener(new TDRewardListener() {
                    @Override public void onAdLoaded(TDAdInfo info) { log("onAdLoaded " + info); }
                    @Override public void onAdLoadFailed(TDError error) { log("onAdLoadFailed " + error); }
                    @Override public void onAdImpression(TDAdInfo info) { log("onAdImpression"); }
                    @Override public void onAdShowFailed(TDError error) { log("onAdShowFailed " + error); }
                    @Override public void onVideoStart(TDAdInfo info) { log("onVideoStart"); }
                    @Override public void onVideoComplete(TDAdInfo info) { log("onVideoComplete"); }
                    @Override public void onAdClicked(TDAdInfo info) { log("onAdClicked"); }
                    @Override public void onAdReward(TDAdInfo info) { log("onAdReward"); }
                    @Override public void onAdClosed(TDAdInfo info) { log("onAdClosed"); }
                    @Override public void onBiddingStart(TDAdInfo info) { log("onBiddingStart " + info); }
                    @Override public void onBiddingEnd(TDAdInfo info, TDError error) { logBidEnd(info, error); }
                    @Override public void onAdAllLoaded(boolean isSuccess) { log("onAdAllLoaded " + isSuccess); }
                    @Override public void onAdIsLoading() { log("onAdIsLoading"); }
                });
                break;
            case TYPE_INTERSTITIAL:
                interstitial = new TDInterstitial(this, u);
                interstitial.setAdListener(simpleInter());
                break;
            case TYPE_SPLASH:
                splash = new TDSplash(this, u);
                splash.setAdListener(simpleSplash());
                break;
            case TYPE_BANNER:
                banner = new TDBanner(this, u);
                banner.setAdListener(simpleBanner());
                break;
            case TYPE_NATIVE:
                nativeAd = new TDNative(this, u);
                nativeAd.setAdListener(simpleNative());
                break;
        }
        applyDemoUserId();
        log("created unit=" + u);
    }

    /** 空不调；0 清除；大于 0 本次 Load 有效，单位分 */
    private void applyBidFloor() {
        String raw = etFloor == null ? "" : etFloor.getText().toString().trim();
        if (raw.isEmpty()) {
            return;
        }
        int fen;
        try {
            fen = Integer.parseInt(raw);
        } catch (Exception e) {
            log("setBidFloor invalid: " + raw);
            return;
        }
        if (reward != null) reward.setBidFloor(fen);
        else if (interstitial != null) interstitial.setBidFloor(fen);
        else if (splash != null) splash.setBidFloor(fen);
        else if (banner != null) banner.setBidFloor(fen);
        else if (nativeAd != null) nativeAd.setBidFloor(fen);
        log("setBidFloor " + fen);
    }

    /** 透传示例：userId 给 LTMB/Sigmob/京东激励；channel 给京东 Android Init */
    private void applyDemoUserId() {
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("userId", "td_demo_user");
        p.put("channel", "td_demo");
        if (reward != null) reward.setCustomParams(p);
        else if (interstitial != null) interstitial.setCustomParams(p);
        else if (splash != null) splash.setCustomParams(p);
        else if (banner != null) banner.setCustomParams(p);
        else if (nativeAd != null) nativeAd.setCustomParams(p);
    }

    private void doLoad() {
        if (!com.td.ads.open.TDAdsSDK.isInit()) {
            log("请先初始化 SDK");
            return;
        }
        createAd();
        applyBidFloor();
        if (reward != null) reward.loadAd();
        else if (interstitial != null) interstitial.loadAd();
        else if (splash != null) splash.loadAd();
        else if (banner != null) banner.loadAd();
        else if (nativeAd != null) nativeAd.loadAd();
    }

    private void doShow() {
        if (!com.td.ads.open.TDAdsSDK.isInit()) {
            log("请先初始化 SDK");
            return;
        }
        Activity act = this;
        String scene = "demo_scene";
        if (reward != null) {
            reward.entryAdScenario(scene);
            reward.showAd(act, scene);
        } else if (interstitial != null) {
            interstitial.entryAdScenario(scene);
            interstitial.showAd(act, scene);
        }
        else if (splash != null) {
            ViewGroup box = showContainer();
            splash.entryAdScenario(scene);
            box.post(() -> splash.showAd(act, box, scene));
        }
        else if (banner != null) {
            banner.entryAdScenario(scene);
            banner.showAd(act, container, scene);
        } else if (nativeAd != null) {
            assembleNativeIfNeeded(lastNativeInfo);
            nativeAd.entryAdScenario(scene);
            nativeAd.showAd(act, container, scene);
        }
    }

    private boolean isReady() {
        if (reward != null) return reward.isReady();
        if (interstitial != null) return interstitial.isReady();
        if (splash != null) return splash.isReady();
        if (banner != null) return banner.isReady();
        if (nativeAd != null) return nativeAd.isReady();
        return false;
    }

    private void destroyAds() {
        if (reward != null) reward.onDestroy();
        if (interstitial != null) interstitial.onDestroy();
        if (splash != null) splash.onDestroy();
        if (banner != null) banner.onDestroy();
        if (nativeAd != null) nativeAd.onDestroy();
        reward = null; interstitial = null; splash = null; banner = null; nativeAd = null;
        lastNativeInfo = null;
        hideSplashOverlay();
    }

    private void applyContainerForType() {
        hideSplashOverlay();
        android.view.View hint = findViewById(R.id.tv_splash_hint);
        android.view.View preview = findViewById(R.id.splash_preview);
        boolean splash = TYPE_SPLASH.equals(type);
        if (hint != null) hint.setVisibility(splash ? android.view.View.VISIBLE : android.view.View.GONE);
        if (preview != null) preview.setVisibility(splash ? android.view.View.VISIBLE : android.view.View.GONE);
        if (splash) {
            container.setVisibility(android.view.View.GONE);
            return;
        }
        if (TYPE_BANNER.equals(type)) {
            container.setVisibility(android.view.View.VISIBLE);
            setContainerHeightDp(200);
            return;
        }
        if (TYPE_NATIVE.equals(type)) {
            container.setVisibility(android.view.View.VISIBLE);
            setContainerHeightDp(360);
            return;
        }
        container.setVisibility(android.view.View.GONE);
    }

    private void setContainerHeightDp(int dp) {
        ViewGroup.LayoutParams lp = container.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = Math.round(dp * getResources().getDisplayMetrics().density);
        container.setLayoutParams(lp);
    }

    private ViewGroup showContainer() {
        splashOverlay.setVisibility(android.view.View.VISIBLE);
        return splashAdArea != null ? splashAdArea : splashOverlay;
    }

    private void hideSplashOverlay() {
        if (splashOverlay == null) return;
        if (splashAdArea != null) splashAdArea.removeAllViews();
        splashOverlay.setVisibility(android.view.View.INVISIBLE);
    }

    /** Banner / 原生关闭后拆掉容器里的广告。 */
    private void clearAdContainer() {
        if (container == null) return;
        container.removeAllViews();
    }

    private void log(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(new Date());
        String line = time + "  " + msg + "\n";
        runOnUiThread(() -> {
            tvLog.append(line);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void logBidEnd(TDAdInfo info, TDError error) {
        log("onBiddingEnd " + info + (error == null ? " ok" : " " + error));
    }

    private TDInterstitialListener simpleInter() {
        return new TDInterstitialListener() {
            @Override public void onAdLoaded(TDAdInfo info) { log("onAdLoaded " + info); }
            @Override public void onAdLoadFailed(TDError error) { log("onAdLoadFailed " + error); }
            @Override public void onAdImpression(TDAdInfo info) { log("onAdImpression"); }
            @Override public void onAdClicked(TDAdInfo info) { log("onAdClicked"); }
            @Override public void onAdClosed(TDAdInfo info) { log("onAdClosed"); }
            @Override public void onAdShowFailed(TDError error) { log("onAdShowFailed " + error); }
            @Override public void onBiddingStart(TDAdInfo info) { log("onBiddingStart " + info); }
            @Override public void onBiddingEnd(TDAdInfo info, TDError error) { logBidEnd(info, error); }
            @Override public void onAdAllLoaded(boolean isSuccess) { log("onAdAllLoaded " + isSuccess); }
            @Override public void onAdIsLoading() { log("onAdIsLoading"); }
        };
    }
    private TDSplashListener simpleSplash() {
        return new TDSplashListener() {
            @Override public void onAdLoaded(TDAdInfo info) { log("onAdLoaded " + info); }
            @Override public void onAdLoadFailed(TDError error) { log("onAdLoadFailed " + error); }
            @Override public void onAdImpression(TDAdInfo info) { log("onAdImpression"); }
            @Override public void onAdClicked(TDAdInfo info) { log("onAdClicked"); }
            @Override public void onAdClosed(TDAdInfo info) {
                hideSplashOverlay();
                log("onAdClosed");
            }
            @Override public void onAdShowFailed(TDError error) {
                hideSplashOverlay();
                log("onAdShowFailed " + error);
            }
            @Override public void onBiddingStart(TDAdInfo info) { log("onBiddingStart " + info); }
            @Override public void onBiddingEnd(TDAdInfo info, TDError error) { logBidEnd(info, error); }
            @Override public void onAdAllLoaded(boolean isSuccess) { log("onAdAllLoaded " + isSuccess); }
            @Override public void onAdIsLoading() { log("onAdIsLoading"); }
        };
    }
    private TDBannerListener simpleBanner() {
        return new TDBannerListener() {
            @Override public void onAdLoaded(TDAdInfo info) { log("onAdLoaded " + info); }
            @Override public void onAdLoadFailed(TDError error) { log("onAdLoadFailed " + error); }
            @Override public void onAdImpression(TDAdInfo info) { log("onAdImpression"); }
            @Override public void onAdClicked(TDAdInfo info) { log("onAdClicked"); }
            @Override public void onAdClosed(TDAdInfo info) {
                clearAdContainer();
                log("onAdClosed");
            }
            @Override public void onAdShowFailed(TDError error) { log("onAdShowFailed " + error); }
            @Override public void onBiddingStart(TDAdInfo info) { log("onBiddingStart " + info); }
            @Override public void onBiddingEnd(TDAdInfo info, TDError error) { logBidEnd(info, error); }
            @Override public void onAdAllLoaded(boolean isSuccess) { log("onAdAllLoaded " + isSuccess); }
            @Override public void onAdIsLoading() { log("onAdIsLoading"); }
        };
    }
    private TDNativeListener simpleNative() {
        return new TDNativeListener() {
            @Override public void onAdLoaded(TDAdInfo info) {
                lastNativeInfo = info;
                log("onAdLoaded " + info + " renderType=" + TDRenderType.name(info == null ? TDRenderType.UNKNOWN : info.renderType));
            }
            @Override public void onAdLoadFailed(TDError error) { log("onAdLoadFailed " + error); }
            @Override public void onAdImpression(TDAdInfo info) { log("onAdImpression"); }
            @Override public void onAdClicked(TDAdInfo info) { log("onAdClicked"); }
            @Override public void onAdClosed(TDAdInfo info) {
                clearAdContainer();
                log("onAdClosed");
            }
            @Override public void onAdShowFailed(TDError error) { log("onAdShowFailed " + error); }
            @Override public void onBiddingStart(TDAdInfo info) { log("onBiddingStart " + info); }
            @Override public void onBiddingEnd(TDAdInfo info, TDError error) { logBidEnd(info, error); }
            @Override public void onAdAllLoaded(boolean isSuccess) { log("onAdAllLoaded " + isSuccess); }
            @Override public void onAdIsLoading() { log("onAdIsLoading"); }
        };
    }

    private void assembleNativeIfNeeded(TDAdInfo info) {
        if (DemoNativeAssemble.assemble(this, container, info)) {
            TDNativeMaterial m = info == null ? null : info.nativeMaterial;
            log("assembled self_render title=" + (m == null ? "" : m.title)
                    + " img=" + (m == null || m.imageUrl == null ? "" : m.imageUrl));
        }
    }

    @Override protected void onDestroy() {
        destroyAds();
        super.onDestroy();
    }
}
