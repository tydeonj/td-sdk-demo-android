package com.td.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.td.ads.base.common.TDError;
import com.td.ads.open.TDInitListener;
import com.td.ads.open.TDAdsSDK;

public class MainActivity extends AppCompatActivity {
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = findViewById(R.id.tv_status);
        ((TextView) findViewById(R.id.tv_title)).setText("TD Android SDK  " + TDAdsSDK.getSdkVersion());

        findViewById(R.id.btn_init).setOnClickListener(v -> doInit());
        bindFormat(R.id.btn_reward, FormatActivity.TYPE_REWARD, DemoApp.unitForFormat(FormatActivity.TYPE_REWARD));
        bindFormat(R.id.btn_interstitial, FormatActivity.TYPE_INTERSTITIAL, DemoApp.unitForFormat(FormatActivity.TYPE_INTERSTITIAL));
        bindFormat(R.id.btn_splash, FormatActivity.TYPE_SPLASH, DemoApp.unitForFormat(FormatActivity.TYPE_SPLASH));
        bindFormat(R.id.btn_banner, FormatActivity.TYPE_BANNER, DemoApp.unitForFormat(FormatActivity.TYPE_BANNER));
        bindFormat(R.id.btn_native, FormatActivity.TYPE_NATIVE, DemoApp.unitForFormat(FormatActivity.TYPE_NATIVE));
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            for (long unit : DemoApp.allUnits()) {
                TDAdsSDK.clearCache(unit);
            }
            toast("已清除缓存");
        });
        refreshStatus();
    }

    private void bindFormat(int id, String type, long unit) {
        findViewById(id).setOnClickListener(v -> {
            Intent i = new Intent(this, FormatActivity.class);
            i.putExtra(FormatActivity.EXTRA_TYPE, type);
            i.putExtra(FormatActivity.EXTRA_UNIT, unit);
            startActivity(i);
        });
    }

    private void doInit() {
        tvStatus.setText("初始化中…");
        DemoPrivacy.applySampleDefaults(this);
        DemoPrivacy.applyBeforeInit(this);
        TDAdsSDK.initSdk(this, DemoApp.APP_ID, new TDInitListener() {
            @Override
            public void onSuccess() {
                DemoPrivacy.applyAfterInit(MainActivity.this);
                DemoIntegrationCheck.dump();
                refreshStatus();
                toast("初始化成功");
            }

            @Override
            public void onFailed(TDError error) {
                tvStatus.setText("初始化失败  " + error);
                toast("初始化失败");
            }
        });
    }

    private void refreshStatus() {
        tvStatus.setText(TDAdsSDK.isInit()
                ? ("已初始化  v" + TDAdsSDK.getSdkVersion())
                : "未初始化");
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
