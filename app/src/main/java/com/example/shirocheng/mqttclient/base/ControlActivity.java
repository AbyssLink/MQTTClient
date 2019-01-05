package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shirocheng.mqttclient.MqttHelper;
import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.brief.MsgViewModel;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ControlActivity extends AppCompatActivity {

    @BindView(R.id.text_receive)
    TextView textReceive;
    @BindView(R.id.toolbar_back)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.btnPublish)
    MaterialButton btnPublish;
    @BindView(R.id.btnConnect)
    MaterialButton btnConnect;
    @BindView(R.id.btnSubscribe)
    MaterialButton btnSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        initToolBar();
    }

    private void updateUI() {
        MsgViewModel model = ViewModelProviders.of(this).get(MsgViewModel.class);
        model.getSubscription(getApplicationContext()).observe(this, jsonValues -> {
            // update UI
            textReceive.setText(jsonValues);
            Logger.w(jsonValues, "debug mqtt");
            Toast.makeText(this, jsonValues, Toast.LENGTH_SHORT).show();
        });
    }


    @OnClick({R.id.btnPublish, R.id.btnConnect, R.id.btnSubscribe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPublish: {
                String pubMsg = "okokok from MI phone";
                Boolean flag = MqttHelper.getInstance()
                        .publishTopic("hello", pubMsg);
                if (flag) {
                    Snackbar.make(view, "Publish: " + pubMsg,
                            Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                break;
            }
            case R.id.btnConnect: {
                Connection connection = getConnection();
                MqttHelper.getInstance().createConnect(getApplicationContext(), connection);
                Boolean flag = MqttHelper.getInstance().doConnect();
                if (flag) {
                    Snackbar.make(view, "Success Connect to: " + connection.getServerIp(),
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            }
            case R.id.btnSubscribe: {
                updateUI();
                break;
            }
        }
    }

    private void initToolBar() {
        //使 Toolbar 取代原本的 actionbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("Control");
        // add a left arrow to back to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private Connection getConnection() {
        String serverIp = "120.78.134.26";
        String serverPort = "1883";
        String clientId = "ExampleAndroidClient";
        String username = "shiro";
        String password = "shiro";

        Connection connection = new Connection();
        connection.setClientId(clientId);
        connection.setServerIp(serverIp);
        connection.setServerPort(serverPort);
        connection.setUserName(username);
        connection.setPassword(password);

        return connection;

    }
}
