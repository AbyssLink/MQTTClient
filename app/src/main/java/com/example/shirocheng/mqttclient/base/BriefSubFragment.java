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
import com.example.shirocheng.mqttclient.base.add.AddSubActivity;
import com.example.shirocheng.mqttclient.base.model.SubViewModel;
import com.example.shirocheng.mqttclient.bean.Subscription;
import com.example.shirocheng.mqttclient.db.App;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class BriefSubFragment extends Fragment {


    @BindView(R.id.recycler_sub)
    RecyclerView recyclerSub;
    @BindView(R.id.fab_sub_add)
    FloatingActionButton fabSubAdd;

    private RecyclerSubAdapter mAdapter;
    private Box<Subscription> subscriptionBox;
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
        initView();
        updateUI();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerSub.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerSubAdapter(getContext());
        recyclerSub.setAdapter(mAdapter);

        // 通过接口回调执行数据库操作
        mAdapter.setOnItemDismissListener(position -> {
            onDelete = true;
            subscriptionBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Subscription.class);
            subscriptionBox.remove(subscriptionBox.getAll().get(position).getId());
        });
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
                    Logger.w("On Conn Changed", "warn");
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

}
