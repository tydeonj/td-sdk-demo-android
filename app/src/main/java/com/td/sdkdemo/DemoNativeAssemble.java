package com.td.sdkdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.td.ads.base.adapter.TDNativeMaterial;
import com.td.ads.base.adapter.TDRenderType;
import com.td.ads.open.TDAdInfo;
import java.io.InputStream;
import java.net.URL;

/**
 * Demo / 自动化共用：按信息流大图卡片拼自渲染（图标+标题+广告标 / 主图 / CTA），再 Show。
 * 模板广告不要调用。空容器 Show 会 {@code invalidParam("nativeLayout")}。
 */
final class DemoNativeAssemble {
    private DemoNativeAssemble() {}

    static boolean assemble(Context ctx, ViewGroup container, TDAdInfo info) {
        if (ctx == null || container == null || info == null
                || info.renderType != TDRenderType.SELF_RENDER) {
            return false;
        }
        TDNativeMaterial m = info.nativeMaterial;
        container.removeAllViews();
        int pad = dp(ctx, 12);
        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(pad, pad, pad, pad);
        card.setBackground(roundRect(Color.WHITE, dp(ctx, 8), Color.parseColor("#E8E8E8"), 1));

        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        ImageView icon = new ImageView(ctx);
        icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        icon.setClickable(true);
        icon.setTag(TDNativeMaterial.TAG_ICON);
        icon.setBackground(roundRect(Color.parseColor("#EEEEEE"), dp(ctx, 6), 0, 0));
        clipRound(icon, dp(ctx, 6));
        int iconSize = dp(ctx, 40);
        header.addView(icon, new LinearLayout.LayoutParams(iconSize, iconSize));
        if (m != null && notEmpty(m.iconUrl)) {
            bindRemoteImage(icon, m.iconUrl);
        }

        LinearLayout texts = new LinearLayout(ctx);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textsLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textsLp.leftMargin = dp(ctx, 10);
        header.addView(texts, textsLp);

        LinearLayout titleRow = new LinearLayout(ctx);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = new TextView(ctx);
        title.setText(m == null || m.title == null ? "" : m.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        title.setTextColor(Color.parseColor("#212121"));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setMaxLines(2);
        title.setClickable(true);
        title.setTag(TDNativeMaterial.TAG_TITLE);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleRow.addView(title, titleLp);
        TextView badge = new TextView(ctx);
        badge.setText("广告");
        badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        badge.setTextColor(Color.parseColor("#9E9E9E"));
        badge.setPadding(dp(ctx, 4), dp(ctx, 1), dp(ctx, 4), dp(ctx, 1));
        badge.setBackground(roundRect(Color.parseColor("#F3F3F3"), dp(ctx, 2), 0, 0));
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeLp.leftMargin = dp(ctx, 6);
        titleRow.addView(badge, badgeLp);
        TextView close = new TextView(ctx);
        close.setText("✕");
        close.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        close.setTextColor(Color.parseColor("#737373"));
        close.setGravity(Gravity.CENTER);
        close.setClickable(true);
        close.setTag(TDNativeMaterial.TAG_CLOSE);
        LinearLayout.LayoutParams closeLp = new LinearLayout.LayoutParams(dp(ctx, 22), dp(ctx, 22));
        closeLp.leftMargin = dp(ctx, 6);
        titleRow.addView(close, closeLp);
        texts.addView(titleRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        String descText = m == null || m.desc == null ? "" : m.desc;
        if (descText.length() > 0) {
            TextView desc = new TextView(ctx);
            desc.setText(descText);
            desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            desc.setTextColor(Color.parseColor("#757575"));
            desc.setMaxLines(2);
            desc.setClickable(true);
            desc.setTag(TDNativeMaterial.TAG_DESC);
            LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            descLp.topMargin = dp(ctx, 2);
            texts.addView(desc, descLp);
        }
        card.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (m != null && notEmpty(m.imageUrl)) {
            ImageView img = new ImageView(ctx);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setClickable(true);
            img.setTag(TDNativeMaterial.TAG_IMAGE);
            img.setBackgroundColor(Color.parseColor("#F0F0F0"));
            clipRound(img, dp(ctx, 6));
            bindRemoteImage(img, m.imageUrl);
            LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, imageHeight(ctx, container));
            imgLp.topMargin = dp(ctx, 10);
            card.addView(img, imgLp);
        }

        TextView cta = new TextView(ctx);
        cta.setText(m == null || m.cta == null || m.cta.length() == 0 ? "查看详情" : m.cta);
        cta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
        cta.setTextColor(Color.WHITE);
        cta.setTypeface(Typeface.DEFAULT_BOLD);
        cta.setGravity(Gravity.CENTER);
        cta.setClickable(true);
        cta.setTag(TDNativeMaterial.TAG_CTA);
        cta.setBackground(roundRect(Color.parseColor("#1565C0"), dp(ctx, 6), 0, 0));
        LinearLayout.LayoutParams ctaLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 40));
        ctaLp.topMargin = dp(ctx, 10);
        card.addView(cta, ctaLp);

        container.addView(card, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return true;
    }

    private static int imageHeight(Context ctx, ViewGroup container) {
        int w = container.getWidth();
        if (w <= 0) {
            w = ctx.getResources().getDisplayMetrics().widthPixels - dp(ctx, 32);
        }
        int inner = Math.max(dp(ctx, 200), w - dp(ctx, 24));
        int h = Math.round(inner * 9f / 16f);
        return Math.max(dp(ctx, 140), Math.min(dp(ctx, 200), h));
    }

    private static GradientDrawable roundRect(int fill, int radius, int stroke, int strokeW) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(radius);
        if (strokeW > 0) {
            d.setStroke(strokeW, stroke);
        }
        return d;
    }

    private static void clipRound(View v, final int radius) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        v.setClipToOutline(true);
        v.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
    }

    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }

    private static boolean notEmpty(String s) {
        return s != null && s.length() > 0;
    }

    private static void bindRemoteImage(final ImageView img, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                try {
                    in = new URL(url).openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(in);
                    if (bm == null) {
                        return;
                    }
                    img.post(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageBitmap(bm);
                        }
                    });
                } catch (Throwable ignore) {
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Throwable ignore) {
                        }
                    }
                }
            }
        }, "td-demo-img").start();
    }
}
