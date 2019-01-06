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

import com.example.shirocheng.mqttclient.MqttHelper;
import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.brief.MsgViewModel;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        ButterKnife.bind(this);

        initView();
    }

    private void updateUI() {
        MsgViewModel model = ViewModelProviders.of(this).get(MsgViewModel.class);
        model.getSubscription(getApplicationContext()).observe(this, jsonValues -> {
            // update UI
            tvReceive.setText(jsonValues);
            Logger.w(jsonValues, "debug mqtt");
            Toast.makeText(this, jsonValues, Toast.LENGTH_SHORT).show();
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
            String ip = intent.getStringExtra("ip");
            String clientId = intent.getStringExtra("clientId");
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

            tvConnIp.setText(ip);
            tvConnName.setText(clientId);

            connection = new Connection();
            connection.setClientId(clientId);
            connection.setServerIp(ip);
            connection.setServerPort("1883");
        }
    }


    @OnClick({R.id.btn_publish, R.id.btn_connect, R.id.btn_subscribe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_publish: {
                String pubMsg = "okokok from MI phone";
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
                if (flag) {
                    Snackbar.make(view, "Success Connect to: " + connection.getServerIp(),
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            }
            case R.id.btn_subscribe: {
                updateUI();
                break;
            }
        }
    }
}
