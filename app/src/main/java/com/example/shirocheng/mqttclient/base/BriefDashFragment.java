package com.example.shirocheng.mqttclient.base;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.base.model.MsgViewModel;
import com.example.shirocheng.mqttclient.bean.Msg;
import com.example.shirocheng.mqttclient.db.App;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.objectbox.Box;

public class BriefDashFragment extends Fragment {

    @BindView(R.id.chart)
    LineChart chart;
    Unbinder unbinder;

    private Box<Msg> msgBox;
    private static BriefDashFragment mInstance = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();
    }

    public static BriefDashFragment getInstance() {
        if (mInstance == null) {
            mInstance = new BriefDashFragment();
        }
        return mInstance;
    }

    public void updateUI() {

        // FIXME: 对不同主题的消息进行过滤，分别可视化
        msgBox = (App.getInstance()).getBoxStore().boxFor(Msg.class);
        MsgViewModel model = ViewModelProviders.of(this).get(MsgViewModel.class);
        model.getMsgLiveData(msgBox).observe(this, msgs -> {
            // update UI
            if (msgs != null) {
                List<Entry> entries = new ArrayList<Entry>();
                try {
                    float heart = 0;
                    for (Msg msg : msgs) {
                        // 校验消息的 topic
                        if (msg.getSubTopic().equals("DHT11")) {
                            JsonObject jsonObject = (JsonObject) new JsonParser().parse(msg.getMsg());
                            String data = jsonObject.get("temp").getAsString();
                            // turn your data into Entry objects
                            entries.add(new Entry(heart, Float.parseFloat(data)));
                            heart = heart + 1;
                        }
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
                    LineData lineData = new LineData(dataSet);
                    chart.setData(lineData);
                    chart.invalidate(); // refresh

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
