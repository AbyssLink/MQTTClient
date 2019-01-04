package com.example.shirocheng.mqttclient.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.shirocheng.mqttclient.R;

public class AddConnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conn);

        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_back);
        //使 Toolbar 取代原本的 actionbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("addConn");
        // add a left arrow to back to parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
