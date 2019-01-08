package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.model.ConnViewModel;
import com.example.shirocheng.mqttclient.base.view.ItemTouchHelperCallback;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.App;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton fabAddConn;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout_recycler_view)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_conn_num)
    TextView tvConnNum;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private RecyclerViewAdapter mAdapter;
    private int color = 0;
    private Box<Connection> connectionBox;
    private Boolean onDelete = false;


    @OnClick({R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(MainActivity.this, AddConnActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());

        initView();

        updateUI();
    }

    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    /**
     * 使用 Model 观察数据变化，更新 UI
     */
    public void updateUI() {
        connectionBox = ((App) getApplication()).getBoxStore().boxFor(Connection.class);
        ConnViewModel model = ViewModelProviders.of(this).get(ConnViewModel.class);
        model.getConnectionLiveData(connectionBox).observe(this, connections -> {
            // updateUI
            if (onDelete) {           // 若为删除，则recyclerview已有动效，无需更新
                onDelete = false;
                tvConnNum.setText(String.valueOf(connections.size()));
            } else if (connections != null) {
                Logger.w("On Conn Changed", "warn");
                mAdapter.setItems(connections);
                tvConnNum.setText(String.valueOf(connections.size()));
            }
        });
    }

    private void initView() {

        //使 Toolbar 取代原本的 actionbar
        setSupportActionBar(toolbar);

        // Set up the navigation drawer.
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        if (navView != null) {
            setupDrawerContent(navView);
        }

        // 注册左滑布局触发器
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 颜色改变效果
        if (getScreenWidthDp() >= 1200) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (getScreenWidthDp() >= 800) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }

        mAdapter = new RecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // 通过接口回调执行数据库操作
        mAdapter.setOnItemDismissListener(position -> {
            onDelete = true;
            connectionBox = ((App) getApplication()).getBoxStore().boxFor(Connection.class);
            connectionBox.remove(connectionBox.getAll().get(position).getId());
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (color > 4) {
                            color = 0;
                        }
                        mAdapter.setColor(++color);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.setting_navigation_menu_item: {
                            // Do nothing
                            break;
                        }
                        case R.id.backup_navigation_menu_item: {
                            Intent intent =
                                    new Intent(MainActivity.this, AddConnActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case R.id.about_navigation_menu_item: {
                            break;
                        }
                        case R.id.history_navigation_menu_item: {
                            break;
                        }
                        default:
                            break;
                    }
                    // Close the navigation drawer when an item is selected.
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                });
    }
}
