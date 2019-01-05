package com.example.shirocheng.mqttclient.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.shirocheng.mqttclient.db.dao.DaoMaster;
import com.example.shirocheng.mqttclient.db.dao.DaoSession;
import com.facebook.stetho.Stetho;

public class MyApplication extends Application {
    private DaoSession daoSession;
    private static Context mContext;

    public static Context getInstance() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initGreenDao();
        Stetho.initializeWithDefaults(this);    // 网络数据库查看

        mContext = getApplicationContext();
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "MQTTClient.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    /**
     * 获取 DaoSession
     *
     * @return
     */
    public DaoSession getDaoSession() {
        return daoSession;
    }
}
