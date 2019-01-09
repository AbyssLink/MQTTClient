package com.example.shirocheng.mqttclient.base.model;

import android.arch.lifecycle.ViewModel;

import com.example.shirocheng.mqttclient.bean.Msg;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

/**
 * 该 Model 获取实时的 Mqtt 消息
 */
public class MsgViewModel extends ViewModel {
    private ObjectBoxLiveData<Msg> msgLiveData;

    public ObjectBoxLiveData<Msg> getMsgLiveData(Box<Msg> msgBox) {
        if (msgLiveData == null) {
            // query all notes, sorted a-z by their text (https://docs.objectbox.io/queries)
            msgLiveData = new ObjectBoxLiveData<>(msgBox.query().build());
        }
        return msgLiveData;
    }

}
