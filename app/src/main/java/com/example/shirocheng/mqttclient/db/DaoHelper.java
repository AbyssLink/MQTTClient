package com.example.shirocheng.mqttclient.db;

import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.dao.ConnectionDao;
import com.example.shirocheng.mqttclient.db.dao.DaoSession;

import java.util.ArrayList;
import java.util.List;

public class DaoHelper {

    private static DaoHelper daoHelper;
    private ConnectionDao connectionDao;
    private List<Connection> connections;

    public static DaoHelper getInstance() {
        if (daoHelper == null) {
            daoHelper = new DaoHelper();
            return daoHelper;
        }
        return daoHelper;
    }

    private void initSession() {
        MyApplication myApp = (MyApplication) MyApplication.getInstance();
        DaoSession daoSession = myApp.getDaoSession();
        connectionDao = daoSession.getConnectionDao();

    }

    public void addConnection(Connection conn) {
        initSession();
        connectionDao.insert(conn);
    }

    public List<Connection> loadAllConn() {
        initSession();
        connections = new ArrayList<>();
        connections = connectionDao.loadAll();

        return connections;
    }

    public List<Connection> loadConnById(long id) {
        initSession();
        connections = new ArrayList<>();
        connectionDao.load(id);

        return connections;
    }

    public void removeAllConn() {
        initSession();
        connectionDao.deleteAll();
    }

    public void removeConnById(long id) {
        initSession();
        connectionDao.deleteByKey(id);
    }


}
