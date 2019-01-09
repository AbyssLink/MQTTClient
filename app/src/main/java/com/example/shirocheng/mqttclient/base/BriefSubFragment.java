package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.add.AddSubActivity;
import com.example.shirocheng.mqttclient.base.model.MsgViewModel;
import com.example.shirocheng.mqttclient.base.model.SubViewModel;
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.bean.Msg;
import com.example.shirocheng.mqttclient.bean.Subscription;
import com.example.shirocheng.mqttclient.db.App;
import com.example.shirocheng.mqttclient.mqtt.MqttHelper;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class BriefSubFragment extends Fragment {


    private static Connection connection = null;
    @BindView(R.id.recycler_sub)
    RecyclerView recyclerSub;
    @BindView(R.id.fab_sub_add)
    FloatingActionButton fabSubAdd;
    private RecyclerSubAdapter mAdapter;
    private Box<Subscription> subscriptionBox;
    private Box<Msg> msgBox;
    private Boolean onDelete = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);
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
        Logger.addLogAdapter(new AndroidLogAdapter());
        recyclerSub.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerSubAdapter(getContext());
        recyclerSub.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(sub -> updateMsgUI(sub));

    }

    /**
     * 使用 Model 观察数据变化更新 UI
     */
    public void updateUI() {
        subscriptionBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Subscription.class);
        SubViewModel model = ViewModelProviders.of(this).get(SubViewModel.class);
        model.getSubLiveData(subscriptionBox).observe(this, new Observer<List<Subscription>>() {
            @Override
            public void onChanged(@Nullable List<Subscription> subscriptions) {
                if (onDelete) {
                    onDelete = false;
                } else if (subscriptions != null) {
                    mAdapter.setItems(subscriptions);
                }
            }
        });
    }


    @OnClick(R.id.fab_sub_add)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), AddSubActivity.class);
        startActivity(intent);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private void initMqttConnection() {
        MqttHelper.getInstance().createConnect(getContext(), connection);
        MqttHelper.getInstance().doConnect();
        MqttHelper.getInstance().setCallBack(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // 数据库操作
                msgBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Msg.class);
                Msg msg = new Msg();
                msg.setMsg(message.toString());
                msgBox.put(msg);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void updateMsgUI(Subscription sub) {

        MqttHelper.getInstance().subscribeTopic(sub.getTopic());

        // 观察数据变化
        msgBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Msg.class);
        MsgViewModel model = ViewModelProviders.of(this).get(MsgViewModel.class);
        model.getMsgLiveData(msgBox).observe(this, new Observer<List<Msg>>() {
            @Override
            public void onChanged(@Nullable List<Msg> msgs) {
                if (msgs != null) {
                    if (sub.getJsonKey() != null) {
                        Snackbar.make(recyclerSub, msgs.get(msgs.size() - 1).getMsg(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
