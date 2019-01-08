package com.example.shirocheng.mqttclient.base.model;

import android.arch.lifecycle.ViewModel;

import com.example.shirocheng.mqttclient.bean.Subscription;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

/**
 * 绑定 Sub 变化
 */
public class SubViewModel extends ViewModel {
    private ObjectBoxLiveData<Subscription> subLiveData;

    public ObjectBoxLiveData<Subscription> getSubLiveData(Box<Subscription> subscriptionBox) {
        if (subLiveData == null) {
            subLiveData = new ObjectBoxLiveData<>(subscriptionBox.query().build());
        }
        return subLiveData;
    }
}
