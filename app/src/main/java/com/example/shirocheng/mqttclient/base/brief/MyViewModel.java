package com.example.shirocheng.mqttclient.base.brief;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.example.shirocheng.mqttclient.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyViewModel extends ViewModel {
    private MutableLiveData<String> jsonValues;

    public LiveData<String> getSubscription(Context mContext) {
        if (jsonValues == null) {
            jsonValues = new MutableLiveData<>();
            loadJsonValues(mContext);
        }
        return jsonValues;
    }

    private void loadJsonValues(Context mContext) {
        // Do an asynchronous operation to fetch jsonValues.
        startMqtt(mContext);
    }

    private void startMqtt(Context mContext) {
        String serverUri = "tcp://120.78.134.26:1883";
        String clientId = "ExampleAndroidClient";
        String subscribeTopic = "sensor";

        // init mqttHelper
        MqttHelper mqttHelper;
        mqttHelper = new MqttHelper(mContext, serverUri, clientId, subscribeTopic);
        mqttHelper.setCallBack(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }

            @Override
            public void connectionLost(Throwable throwable) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                String message = mqttMessage.toString();
                jsonValues.setValue(message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
