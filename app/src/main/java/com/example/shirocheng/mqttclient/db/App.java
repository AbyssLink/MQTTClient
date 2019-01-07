package com.example.shirocheng.mqttclient.db;

import android.app.Application;

import com.example.shirocheng.mqttclient.BuildConfig;
import com.example.shirocheng.mqttclient.bean.MyObjectBox;
import com.facebook.stetho.Stetho;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class App extends Application {


    public static final String TAG = "ObjectBoxExample";
    public static final boolean EXTERNAL_DIR = false;

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(App.this).build();
        Stetho.initializeWithDefaults(this);    // 网络数据库调试
        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(this);
        }
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
