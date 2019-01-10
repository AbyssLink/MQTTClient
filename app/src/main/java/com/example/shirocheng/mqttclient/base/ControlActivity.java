package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.model.ConnViewModel;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.App;
import com.example.shirocheng.mqttclient.mqtt.MqttHelper;

import org.eclipse.paho.client.mqttv3.MqttException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

public class ControlActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static Connection connection;
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
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.navigation_bottom)
    BottomNavigationView navigationBottom;
    @BindView(R.id.card_layout)
    LinearLayout cardLayout;

    private Box<Connection> connectionBox;
    private BriefPubFragment briefPubFragment = new BriefPubFragment();
    private BriefSubFragment briefSubFragment = new BriefSubFragment();
    private BriefDashFragment briefDashFragment = new BriefDashFragment();
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            //点击BottomNavigationView的Item项，切换ViewPager页面
            //menu/navigation.xml里加的android:orderInCategory属性就是下面item.getOrder()取的值
            viewPager.setCurrentItem(menuItem.getOrder());
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);

        updateUI();
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("addConn");
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
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        if (id != null) {
            connection = connectionBox.get(Long.parseLong(id));

            briefSubFragment.setConnection(connection); // 传数据给fragment
            briefPubFragment.setConnection(connection);

            tvConnIp.setText(connection.getServerIp());
            tvConnName.setText(connection.getClientId());
            if (connection.isActivate()) {
                tvConnActivate.setText("ON");
                tvConnActivate.setBackground(getResources().getDrawable(R.drawable.tv_round_green));
            } else {
                tvConnActivate.setText("OFF");
                tvConnActivate.setBackground(getResources().getDrawable(R.drawable.tv_round_red));
            }
        }
    }

    private void updateUI() {
        connectionBox = ((App) getApplication()).getBoxStore().boxFor(Connection.class);
        ConnViewModel connViewModel = ViewModelProviders.of(this).get(ConnViewModel.class);
        connViewModel.getConnectionLiveData(connectionBox).observe(this, connections -> {
            //update UI
            Intent intent = getIntent();
            String id = intent.getStringExtra("id");
            if (id != null) {
                connection = connectionBox.get(Long.parseLong(id));

                briefPubFragment.setConnection(connection);
                briefSubFragment.setConnection(connection); // 传数据给fragment

                tvConnIp.setText(connection.getServerIp());
                tvConnName.setText(connection.getClientId());
                if (connection.isActivate()) {
                    tvConnActivate.setText("ON");
                    tvConnActivate.setBackground(getResources().getDrawable(R.drawable.tv_round_green));
                } else {
                    tvConnActivate.setText("OFF");
                    tvConnActivate.setBackground(getResources().getDrawable(R.drawable.tv_round_red));
                }
            }
        });
    }

    // bottom navigation + viewpager 参考：https://tomoya92.github.io/2017/04/05/android-bottomnavigationview-viewpager-fragment/
    private void initViewPager(ViewPager viewPager) {

        //添加viewPager事件监听（很容易忘）
        viewPager.addOnPageChangeListener(this);
        navigationBottom.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0:
                        return briefSubFragment;
                    case 1:
                        return briefPubFragment;
                    case 2:
                        return briefDashFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connect, menu);
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
                briefSubFragment.setConnection(connection);
                briefPubFragment.setConnection(connection);
                Snackbar.make(tvConnName, "Success Connect to: " + connection.getServerIp(),
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } else {        //连接失败
                // 更新连接状态
                connection.setActivate(false);
                connectionBox.put(connection);
                Snackbar.make(tvConnName, "Connect Failed, Check IP Address or Network",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }

        if (id == R.id.action_disconnect) {
            try {
                MqttHelper.getInstance().disConnect();
                // 更新连接状态
                connection.setActivate(false);
                connectionBox.put(connection);
                Snackbar.make(tvConnName, "Disconnected",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        //页面滑动的时候，改变BottomNavigationView的Item高亮
        navigationBottom.getMenu().getItem(i).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


}
