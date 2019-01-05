package com.example.shirocheng.mqttclient;

import android.content.Context;

import com.example.shirocheng.mqttclient.bean.Connection;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

/**
 * Ref: https://wildanmsyah.wordpress.com/2017/05/11/mqtt-android-client-tutorial/
 * Ref2: https://github.com/LichFaker/MqttClientAndroid
 */
public class MqttHelper {
    private static MqttHelper mInstance = null;     // todo: use instance to free ram;

    private String username;
    private String password;
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    private String subTopic;
    private String pubTopic;


    /**
     * 获取单例
     *
     * @return
     */
    public static MqttHelper getInstance() {
        if (null == mInstance) {
            mInstance = new MqttHelper();
        }
        return mInstance;
    }

    /**
     * 释放单例
     */
    public static void release() {
        try {
            if (mInstance != null) {
                mInstance.disConnect();
                mInstance = null;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // 初始化 Mqtt 连接
    private void initMqttClient() {

    }

    // 设置回调
    public void setCallBack(MqttCallbackExtended callBack) {
        mqttAndroidClient.setCallback(callBack);
    }

    /**
     * 创建 MQTT 连接
     *
     * @param context
     * @param connection 获取连接的属性
     */
    public void createConnect(Context context, Connection connection) {

        String serverURI = "tcp://" + connection.getServerIp() + ":" + connection.getServerPort();

        mqttAndroidClient = new MqttAndroidClient(context, serverURI, connection.getClientId());
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        if (username != null) {
            mqttConnectOptions.setUserName(username);

        }
        if (password != null) {
            mqttConnectOptions.setPassword(password.toCharArray());

        }

    }


    /**
     * 开始连接
     *
     * @return
     */
    public boolean doConnect() {
        boolean flag = false;
        if (mqttAndroidClient != null) {
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        Logger.w("Success to connect to broker", "Mqtt");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Logger.w("Failed to connect .... Exception: "
                                + exception.toString(), "Mqtt");

                    }
                });
                flag = true;
            } catch (MqttException ex) {
                ex.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 订阅消息
     *
     * @param subscribeTopic
     * @return
     */
    public boolean subscribeTopic(String subscribeTopic) {
        boolean flag = false;

        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.subscribe(subscribeTopic, 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Logger.w("subscribed!", "Mqtt");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Logger.w("Mqtt", "subscribed failed!");
                    }
                });

                flag = true;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    /**
     * 发布消息
     *
     * @param pubTopic
     * @return
     */
    public boolean publishTopic(String pubTopic, String message) {
        boolean flag = false;

        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(0);
                mqttMessage.setPayload(message.getBytes());

                mqttAndroidClient.publish(pubTopic, mqttMessage);

                Logger.w("Publish Data!" + mqttMessage.toString(), "publish");
                flag = true;
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
                Logger.w("Failed publish Data!", "publish");
            } catch (MqttException e) {
                e.printStackTrace();
                Logger.w("Failed Publish Data!", "publish");
            }

        }
        return flag;
    }

    /**
     * 取消连接
     *
     * @throws MqttException
     */
    public void disConnect() throws MqttException {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            mqttAndroidClient.disconnect();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

}
