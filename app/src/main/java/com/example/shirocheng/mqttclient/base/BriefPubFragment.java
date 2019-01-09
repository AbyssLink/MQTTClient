package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.add.AddPubActivity;
import com.example.shirocheng.mqttclient.base.model.PubViewModel;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.bean.Publishing;
import com.example.shirocheng.mqttclient.db.App;
import com.example.shirocheng.mqttclient.mqtt.MqttHelper;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class BriefPubFragment extends Fragment {

    @BindView(R.id.recycler_pub)
    RecyclerView recyclerPub;
    @BindView(R.id.fab_pub_add)
    FloatingActionButton fabPubAdd;

    private RecyclerPubAdapter mAdapter;
    private Box<Publishing> publishingBox;
    private Boolean onDelete = false;
    private static Connection connection = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pulish, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMqttConnection();
        initView();
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerPub.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerPubAdapter(getContext());
        recyclerPub.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(pub -> MqttHelper.getInstance().publishTopic(pub.getTopic(), pub.getMsg()));
    }

    /**
     * 使用 Model 观察数据变化更新 UI
     */
    public void updateUI() {
        publishingBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Publishing.class);
        PubViewModel model = ViewModelProviders.of(this).get(PubViewModel.class);
        model.getPubLiveData(publishingBox).observe(this, new Observer<List<Publishing>>() {
            @Override
            public void onChanged(@Nullable List<Publishing> publishings) {
                if (onDelete) {
                    onDelete = false;
                } else if (publishings != null) {
                    Logger.w("On Conn Changed", "warn");
                    mAdapter.setItems(publishings);
                }
            }
        });
    }

    private void initMqttConnection() {
        MqttHelper.getInstance().createConnect(getContext(), connection);
        MqttHelper.getInstance().doConnect();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @OnClick(R.id.fab_pub_add)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), AddPubActivity.class);
        startActivity(intent);
    }
}
