package com.example.shirocheng.mqttclient.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.shirocheng.mqttclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddConnActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_back)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conn);
        ButterKnife.bind(this);

        initToolBar();
    }

    private void initToolBar() {
        //使 Toolbar 取代原本的 actionbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("addConn");
        // add a left arrow to back to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
