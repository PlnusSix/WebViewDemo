package com.kankan.androidchatwithh5demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Random;

public class EntireWebViewActivity extends AppCompatActivity {

    private String url = "http://www.baidu.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        setContentView(webView);
        if (new Random().nextInt(2) == 0) {
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Toast.makeText(EntireWebViewActivity.this, "Oh,no!" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    EntireWebViewActivity.this.setProgress(newProgress);
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    //titleView.setText(title);
                    //titleView是一个TextView
                }
            });
        } else {
            String summary = "<html><body>You scored <b>192</b> points.</body></html>";
            webView.loadData(summary, "text/html", null);
        }
    }
}
