package com.kankan.androidchatwithh5demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AnotherWebViewActivity extends AppCompatActivity {

    private String url = "http://www.baidu.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_web_view);

        initUserBrowser();
        initEntireActivity();
    }

    private void initUserBrowser() {
        RxView.clicks(this.findViewById(R.id.userBrowser))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        AnotherWebViewActivity.this.startActivity(intent);
                    }
                });
    }

    private void initEntireActivity() {
        RxView.clicks(this.findViewById(R.id.entireActivity))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        AnotherWebViewActivity.this.startActivity(new Intent(AnotherWebViewActivity.this, EntireWebViewActivity.class));
                    }
                });
    }
}
