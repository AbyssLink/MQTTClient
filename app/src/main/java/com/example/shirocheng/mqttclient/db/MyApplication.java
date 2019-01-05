package com.example.shirocheng.mqttclient.db;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.shirocheng.mqttclient.db.dao.DaoMaster;
import com.example.shirocheng.mqttclient.db.dao.DaoSession;

public class MyApplication extends Application {
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        initGreenDao();
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "local.db");
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
