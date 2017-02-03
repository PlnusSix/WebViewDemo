package com.kankan.androidchatwithh5demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jakewharton.rxbinding.view.RxView;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView mWebView_assets;
    private String url = "http://www.baidu.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWebView_assets();

        initUrl();
        /**
         * 如果只是mWebView.loadUrl()这样的话
         * 那么点击打开入口的时候，页面将在手机默认的浏览器上打开如果想要在
         * APP内打开的话，就地设置setWebViewClient
         */
        initUrlWithClient();
        turnToAnotherDemo();
    }

    /**
     * 加载assets文件夹下的homepage.html
     */
    private void initWebView_assets() {
        mWebView_assets = (WebView) this.findViewById(R.id.webView_assets);
        mWebView_assets.loadUrl("file:///android_asset/homepage.html");
    }

    /**
     * 加载普通url
     */
    private void initUrl() {
        RxView.clicks((this.findViewById(R.id.enter_Url)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "加载前");
                        new WebView(MainActivity.this).loadUrl(url);
                        Log.e(TAG, "加载后");
                    }
                });
    }

    private void initUrlWithClient() {
        RxView.clicks(this.findViewById(R.id.enter_UrlWithClient))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        WebView webView = (WebView) MainActivity.this.findViewById(R.id.webViewWithClient);
                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }
                        });
                        webView.loadUrl(url);
                    }
                });
    }

    private void turnToAnotherDemo() {
        RxView.clicks(this.findViewById(R.id.turnToAnotherDemo))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        MainActivity.this.startActivity(new Intent(MainActivity.this, AnotherWebViewActivity.class));
                        MainActivity.this.finish();
                    }
                });
    }
}
