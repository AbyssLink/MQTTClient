package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.model.ConnViewModel;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.App;
import com.example.shirocheng.mqttclient.mqtt.MqttHelper;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

public class ControlActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_back)
    Toolbar toolbar;
    @BindView(R.id.rela_round_big)
    RelativeLayout relaRoundBig;
    @BindView(R.id.tv_conn_ip)
    TextView tvConnIp;
    @BindView(R.id.tv_conn_name)
    TextView tvConnName;
    @BindView(R.id.tv_conn_activate)
    TextView tvConnActivate;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private Connection connection;
    private Box<Connection> connectionBox;
    private List<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);

        updateUI();
        initView();
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
                    tvConnActivate.setBackground(getResources().getDrawable(R.drawable.tv_round_green));
                } else {
                    tvConnActivate.setText("NO");
                    tvConnActivate.setBackground(getResources().getDrawable(R.drawable.tv_round_red));
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

        initViewPager(viewPager);
    }

    private void initViewPager(ViewPager viewPager) {

        tabLayout.setupWithViewPager(viewPager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new AddSubscribeFragment());
        fragments.add(new AddPublishFragment());

        titles = new ArrayList<>();
        titles.add("Subscribe");
        titles.add("Publish");
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connect_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_connect) {
            MqttHelper.getInstance().createConnect(getApplicationContext(), connection);
            Boolean flag = MqttHelper.getInstance().doConnect();
            if (flag) {     //连接成功
                // 更新连接状态
                connection.setActivate(true);
                connectionBox.put(connection);
                Snackbar.make(tabLayout, "Success Connect to: " + connection.getServerIp(),
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {        //连接失败
                // 更新连接状态
                connection.setActivate(false);
                connectionBox.put(connection);
                Snackbar.make(tabLayout, "Connect Failed, Check IP Address or Network",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }

        if (id == R.id.action_disconnect) {
            try {
                MqttHelper.getInstance().disConnect();
                // 更新连接状态
                connection.setActivate(false);
                connectionBox.put(connection);
                Snackbar.make(tabLayout, "Disconnected",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
