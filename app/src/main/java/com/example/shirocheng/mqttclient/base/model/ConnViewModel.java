package com.example.shirocheng.mqttclient.base.model;

import android.arch.lifecycle.ViewModel;

import com.example.shirocheng.mqttclient.bean.Connection;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

/**
 * 绑定 Conn 变化
 */
public class ConnViewModel extends ViewModel {

    private ObjectBoxLiveData<Connection> connLiveData;

    public ObjectBoxLiveData<Connection> getConnectionLiveData(Box<Connection> connectionBox) {
        if (connLiveData == null) {
            // query all notes, sorted a-z by their text (https://docs.objectbox.io/queries)
            connLiveData = new ObjectBoxLiveData<>(connectionBox.query().build());
        }
        return connLiveData;
    }

}
