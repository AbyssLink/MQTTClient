package com.example.shirocheng.mqttclient.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.mqtt.MqttHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddPublishFragment extends Fragment {

    @BindView(R.id.btn_publish)
    MaterialButton btnPublish;
    Unbinder unbinder;
    @BindView(R.id.et_pub_topic)
    TextInputEditText etPubTopic;
    @BindView(R.id.til_pub_topic)
    TextInputLayout tilPubTopic;
    @BindView(R.id.et_pub_msg)
    TextInputEditText etPubMsg;
    @BindView(R.id.til_pub_msg)
    TextInputLayout tilPubMsg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pulish, container, false);
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

    @OnClick(R.id.btn_publish)
    public void onViewClicked(View view) {
        String pubTopic = etPubTopic.getText().toString();
        String pubMsg = etPubMsg.getText().toString();

        Boolean flag = MqttHelper.getInstance().publishTopic(pubTopic, pubMsg);

        if (flag) {
            Snackbar.make(view, "Publish: " + pubMsg,
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            Snackbar.make(view, "Failed to Publish",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }


}
