package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.brief.MyViewModel;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_receive)
    TextView textReceive;
    @BindView(R.id.fab)
    FloatingActionButton fabAddConn;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        initToolBar();
        updateUI();
    }

    private void initToolBar() {
        //使 Toolbar 取代原本的 actionbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("MqttClient Home");
    }

    private void updateUI() {
        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        model.getSubscription(getApplicationContext()).observe(this, jsonValues -> {
            // update UI
            textReceive.setText(jsonValues);
            Logger.w(jsonValues, "debug mqtt");
            Toast.makeText(this, jsonValues, Toast.LENGTH_SHORT).show();
        });
    }

/*    //    Handler 在主线程中创建，自动绑定主线程，通知主线程更新 UI：
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
    };*/

/*    // 通知主线程
    Message msg = handler.obtainMessage();
    msg.what = 1;
    msg.obj = message;
                handler.sendMessage(msg);*/

    @OnClick(R.id.fab)
    public void onViewClicked(View view) {
//        Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        Intent intent = new Intent(MainActivity.this, AddConnActivity.class);
        startActivity(intent);
    }
}
