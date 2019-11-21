package cn.ccsu.learning.ui.web;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ccsu.learning.R;
import cn.ccsu.learning.base.BaseActivity;

public class WebViewActivity  extends BaseActivity {
    private static final String TAG = "WebViewActivity";

    @BindView(R.id.imgbtn_back)
    ImageButton imgbtnBack;
    @BindView(R.id.imgbtn_close)
    ImageButton imgbtnClose;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        if (getIntent() != null) {
            String url = getIntent().getStringExtra("url");
            Log.e(TAG, "initData: " + url);
            //清除网页访问留下的缓存
            //由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
            mWebView.clearCache(true);
            //清除当前webview访问的历史记录
            //只会webview访问历史记录里的所有记录除了当前访问记录
            mWebView.clearHistory();
            //这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
            mWebView.clearFormData();
            mWebView.getSettings().setJavaScriptEnabled(true);
            //方式1. 加载一个网页：
            mWebView.loadUrl(url);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    mWebView.loadUrl(url);
                    return true;
                }

                // 页面开始加载
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    // 显示进度
                    progressBar.setVisibility(View.VISIBLE);
                }

                // 页面加载完成
                @Override
                public void onPageFinished(WebView view, String url) {
                    // 隐藏进度
                    progressBar.setVisibility(View.GONE);
                }
            });

            //设置WebChromeClient类
            mWebView.setWebChromeClient(new WebChromeClient() {


                //获取网站标题
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    if (!TextUtils.isEmpty(getIntent().getStringExtra("title"))) {
                        tvTitle.setText(getIntent().getStringExtra("title"));
                    } else {
                        tvTitle.setText(title);
                    }
                }

                // 设置网页加载的进度条
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    // 加载进度
                    progressBar.setProgress(newProgress);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        //恢复pauseTimers状态
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        //它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
        mWebView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
        //销毁Webview
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initData();
    }

    @OnClick({R.id.imgbtn_back, R.id.imgbtn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_back:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.imgbtn_close:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { // 表示按返回键
                mWebView.goBack(); // 后退
                // 前进
                // webview.goForward();
                return true; // 已处理
            } else {
                finish();
            }
        }
        return false;
    }
}