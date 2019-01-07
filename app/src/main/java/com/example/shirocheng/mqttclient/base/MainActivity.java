package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.shirocheng.mqttclient.base.view.OnMoveAndSwipedListener;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.App;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;

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

    private RecyclerViewAdapter mAdapter;
    private List<Connection> connections;
    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (!loading && linearLayoutManager.getItemCount() == (linearLayoutManager.findLastVisibleItemPosition() + 1)) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (loadTimes < 1) {
                            mAdapter.removeFooter();
                            loading = false;
                            mAdapter.addItems(connections);
                            mAdapter.addFooter();
                            loadTimes++;
                        } else {
                            mAdapter.removeFooter();
                            Snackbar.make(mRecyclerView, getString(R.string.no_more_data), Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    loading = false;
                                }
                            }).show();
                        }
                    }
                }, 1000);

                loading = true;
            }
        }
    };
    private boolean loading;
    private int loadTimes = 1;
    private int color = 0;
    private Box<Connection> connectionBox;

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
        model.getNoteLiveData(connectionBox).observe(this, new Observer<List<Connection>>() {
            @Override
            public void onChanged(@Nullable List<Connection> connections) {
                // updateUI
                if (connections != null) {
                    Logger.w("On Conn Changed", "warn");
                    mAdapter.setItems(connections);
                    mAdapter.addFooter();
                    tvConnNum.setText(String.valueOf(connections.size()));
                }
            }
        });
    }

    private void initView() {

        //使 Toolbar 取代原本的 actionbar
        setSupportActionBar(toolbar);

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
        mAdapter.addHeader();

        // 设置监听回调，当触发 item 滑动时，进行数据库操作
        mAdapter.setOnItemDismissListener(new OnMoveAndSwipedListener() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                return false;
            }

            @Override
            public void onItemDismiss(int position) {
                // 数据库操作
                Connection item = connections.get(position);
                connectionBox = ((App) getApplication()).getBoxStore().boxFor(Connection.class);
                connectionBox.remove(item.getId());
            }
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
        mRecyclerView.addOnScrollListener(scrollListener);
    }
}
