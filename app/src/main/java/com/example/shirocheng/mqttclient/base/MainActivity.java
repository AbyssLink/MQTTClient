package com.example.shirocheng.mqttclient.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shirocheng.mqttclient.MqttHelper;
import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.data.Connection;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;

    @BindView(R.id.text_receive)
    TextView textReceive;

    private Connection connection;
    private String serverUri = "tcp://120.78.134.26:1883";
    private String clientId = "ExampleAndroidClient";
    private String subscribeTopic = "sensor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());

        startMqtt();
    }

    private void startMqtt() {
        // init mqttHelper
        mqttHelper = new MqttHelper(getApplicationContext(), serverUri, clientId, subscribeTopic);
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
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                Logger.w(message, "Debug");

                // 通知主线程
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = message;
                handler.sendMessage(msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    //    Handler 在主线程中创建，自动绑定主线程，通知主线程更新 UI：
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    textReceive.setText(msg.obj.toString());
                    break;
                case 2:
                    textReceive.setText("Disconnect");
                    break;
            }
        }
    };

}
