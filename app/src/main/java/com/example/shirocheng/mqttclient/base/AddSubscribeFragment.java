package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.model.MsgViewModel;
import com.example.shirocheng.mqttclient.bean.Subscription;
import com.example.shirocheng.mqttclient.mqtt.MqttHelper;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddSubscribeFragment extends Fragment {

    @BindView(R.id.btn_subscribe)
    MaterialButton btnSubscribe;
    @BindView(R.id.tv_receive)
    TextView tvReceive;
    Unbinder unbinder;
    @BindView(R.id.et_sub_topic)
    TextInputEditText etSubTopic;
    @BindView(R.id.til_sub_topic)
    TextInputLayout tilSubTopic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_subscribe)
    public void onViewClicked() {
        Subscription sub = getSubscription();

        MqttHelper.getInstance().subscribeTopic(sub.getTopic());
        updateMsgUI();
    }

    private Subscription getSubscription() {
        Subscription subscription = new Subscription();
        String subTopic = etSubTopic.getText().toString();
        subscription.setTopic(subTopic);

        return subscription;
    }

    private void updateMsgUI() {
        MsgViewModel model = ViewModelProviders.of(this).get(MsgViewModel.class);
        model.getSubscription().observe(this, jsonValues -> {
            // update UI
            tvReceive.setText(jsonValues);
            Logger.w(jsonValues, "debug mqtt");
            Toast.makeText(getContext(), jsonValues, Toast.LENGTH_SHORT).show();
        });
    }


}
