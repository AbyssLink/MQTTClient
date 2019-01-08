package com.example.shirocheng.mqttclient.base.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.shirocheng.mqttclient.mqtt.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 该 Model 获取实时的 Mqtt 消息
 */
public class MsgViewModel extends ViewModel {
    private MutableLiveData<String> jsonValues;

    public LiveData<String> getSubscription() {
        if (jsonValues == null) {
            jsonValues = new MutableLiveData<>();
            loadJsonValues();
        }
        return jsonValues;
    }

    private void loadJsonValues() {
        // Do an asynchronous operation to fetch jsonValues.
        startMqtt();
    }

    private void startMqtt() {
        new Thread(() -> {
            MqttHelper.getInstance().setCallBack(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                }

                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) {
                    String message = mqttMessage.toString();
                    jsonValues.postValue(message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });


        }).start();
    }
}
