package com.example.shirocheng.mqttclient.base.brief;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.example.shirocheng.mqttclient.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 该 Model 获取实时的 Mqtt 消息
 */
public class MsgViewModel extends ViewModel {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                MqttHelper.getInstance().subscribeTopic("DHT11");
            }
        }).start();
    }
}
