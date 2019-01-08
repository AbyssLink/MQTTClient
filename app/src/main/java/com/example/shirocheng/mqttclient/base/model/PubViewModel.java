package com.example.shirocheng.mqttclient.base.model;

import android.arch.lifecycle.ViewModel;

import com.example.shirocheng.mqttclient.bean.Publishing;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

/**
 * 绑定 Pub 变化
 */
public class PubViewModel extends ViewModel {

    private ObjectBoxLiveData<Publishing> pubLiveData;

    public ObjectBoxLiveData<Publishing> getPubLiveData(Box<Publishing> publishingBox) {
        if (pubLiveData == null) {
            pubLiveData = new ObjectBoxLiveData<>(publishingBox.query().build());
        }
        return pubLiveData;
    }
}
