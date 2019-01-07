package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.model.ConnViewModel;
import com.example.shirocheng.mqttclient.base.model.MsgViewModel;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.App;
import com.example.shirocheng.mqttclient.mqtt.MqttHelper;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class DetailViewActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_back)
    Toolbar toolbar;
    @BindView(R.id.tv_share_view_tip)
    TextView tvShareViewTip;
    @BindView(R.id.rela_round_big)
    RelativeLayout relaRoundBig;
    @BindView(R.id.tv_conn_ip)
    TextView tvConnIp;
    @BindView(R.id.tv_conn_name)
    TextView tvConnName;
    @BindView(R.id.tv_receive)
    TextView tvReceive;
    @BindView(R.id.btn_connect)
    MaterialButton btnConnect;
    @BindView(R.id.btn_subscribe)
    MaterialButton btnSubscribe;
    @BindView(R.id.btn_publish)
    MaterialButton btnPublish;
    @BindView(R.id.tv_conn_activate)
    TextView tvConnActivate;

    private Connection connection;
    private Box<Connection> connectionBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        ButterKnife.bind(this);

        updateUI();
        initView();
    }

    private void updateMsgUI() {

        MsgViewModel model = ViewModelProviders.of(this).get(MsgViewModel.class);
        model.getSubscription(getApplicationContext()).observe(this, jsonValues -> {
            // update UI
            tvReceive.setText(jsonValues);
            Logger.w(jsonValues, "debug mqtt");
            Toast.makeText(this, jsonValues, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI() {
        connectionBox = ((App) getApplication()).getBoxStore().boxFor(Connection.class);
        ConnViewModel connViewModel = ViewModelProviders.of(this).get(ConnViewModel.class);
        connViewModel.getConnectionLiveData(connectionBox).observe(this, connections -> {
            //update UI
            Intent intent = getIntent();
            String id = intent.getStringExtra("id");
            if (!id.equals("")) {
                connection = connectionBox.get(Long.parseLong(id));
                tvConnIp.setText(connection.getServerIp());
                tvConnName.setText(connection.getClientId());
                if (connection.isActivate()) {
                    tvConnActivate.setText("YES");
                    tvConnActivate.setTextColor(getResources().getColor(R.color.google_green));
                } else {
                    tvConnActivate.setText("NO");
                    tvConnActivate.setTextColor(getResources().getColor(R.color.google_red));
                }
            }
        });
    }

    private void initView() {
        //使 Toolbar 取代原本的 actionbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("addConn");
        // add a left arrow to back to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            Intent intent = getIntent();
            int color = intent.getIntExtra("color", 1);
            if (color == 1) {
                relaRoundBig.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_green)));
            } else if (color == 2) {
                relaRoundBig.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_yellow)));
            } else if (color == 3) {
                relaRoundBig.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_red)));
            } else if (color == 4) {
                relaRoundBig.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.google_blue)));
            }
        }
    }


    @OnClick({R.id.btn_publish, R.id.btn_connect, R.id.btn_subscribe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_publish: {
                String pubMsg = "OK OK from MI phone";
                Boolean flag = MqttHelper.getInstance()
                        .publishTopic("hello", pubMsg);
                if (flag) {
                    Snackbar.make(view, "Publish: " + pubMsg,
                            Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                break;
            }
            case R.id.btn_connect: {
                MqttHelper.getInstance().createConnect(getApplicationContext(), connection);
                Boolean flag = MqttHelper.getInstance().doConnect();
                if (flag) {     //连接成功
                    // 更新连接状态
                    connection.setActivate(true);
                    connectionBox.put(connection);
                    Snackbar.make(view, "Success Connect to: " + connection.getServerIp(),
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {        //连接失败
                    // 更新连接状态
                    connection.setActivate(false);
                    connectionBox.put(connection);
                    Snackbar.make(view, "Connect failed, check ip address",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            }
            case R.id.btn_subscribe: {
                updateMsgUI();
                break;
            }
        }
    }
}
