package com.example.shirocheng.mqttclient.base.add;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.bean.Subscription;
import com.example.shirocheng.mqttclient.db.App;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class AddSubActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_back)
    Toolbar toolbarBack;
    @BindView(R.id.et_sub_name)
    TextInputEditText etSubName;
    @BindView(R.id.til_sub_name)
    TextInputLayout tilSubName;
    @BindView(R.id.et_sub_topic)
    TextInputEditText etSubTopic;
    @BindView(R.id.til_sub_topic)
    TextInputLayout tilSubTopic;
    @BindView(R.id.et_sub_key)
    TextInputEditText etSubKey;
    @BindView(R.id.til_sub_key)
    TextInputLayout tilSubKey;
    @BindView(R.id.btn_sub_create)
    MaterialButton btnSubCreate;
    @BindView(R.id.cb_is_number)
    AppCompatCheckBox cbIsNumber;

    private Box<Subscription> subscriptionBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);
        ButterKnife.bind(this);

        initView();
    }

    @OnClick(R.id.btn_sub_create)
    public void onViewClicked(View view) {
        Subscription insertData = getData();

        // 写入数据库
        if (insertData != null) {
            //todo
            subscriptionBox = ((App) getApplication()).getBoxStore().boxFor(Subscription.class);
            subscriptionBox.put(insertData);
            Snackbar.make(view, "Success create",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            Snackbar.make(view, "Failed create",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }

    private void initView() {
        setSupportActionBar(toolbarBack);
        toolbarBack.setTitle("add Sub");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private Subscription getData() {
        Subscription sub = new Subscription();

        sub.setName(etSubName.getText().toString());
        sub.setTopic(etSubTopic.getText().toString());
        sub.setIsNumber(cbIsNumber.isChecked());
        sub.setJsonKey(etSubKey.getText().toString());

        return sub;
    }
}
