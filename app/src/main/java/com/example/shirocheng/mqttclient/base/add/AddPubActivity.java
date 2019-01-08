package com.example.shirocheng.mqttclient.base.add;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.shirocheng.mqttclient.R;
import com.example.shirocheng.mqttclient.bean.Publishing;
import com.example.shirocheng.mqttclient.db.App;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class AddPubActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_back)
    Toolbar toolbarBack;
    @BindView(R.id.et_pub_name)
    TextInputEditText etPubName;
    @BindView(R.id.til_pub_name)
    TextInputLayout tilPubName;
    @BindView(R.id.et_pub_topic)
    TextInputEditText etPubTopic;
    @BindView(R.id.til_pub_topic)
    TextInputLayout tilPubTopic;
    @BindView(R.id.btn_pub_create)
    MaterialButton btnPubCreate;

    private Box<Publishing> publishingBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pub);
        ButterKnife.bind(this);

        initView();
    }


    @OnClick(R.id.btn_pub_create)
    public void onViewClicked(View view) {
        Publishing insertData = getData();

        // 写入数据库
        if (insertData != null) {
            publishingBox = ((App) getApplication()).getBoxStore().boxFor(Publishing.class);
            publishingBox.put(insertData);
            Snackbar.make(view, "Success create",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            Snackbar.make(view, "Failed create",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }

    private void initView() {
        setSupportActionBar(toolbarBack);
        toolbarBack.setTitle("addConn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private Publishing getData() {
        Publishing pub = new Publishing();

        pub.setName(etPubName.getText().toString());
        pub.setTopic(etPubTopic.getText().toString());

        return pub;
    }
}
