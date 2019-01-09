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
import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.App;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;

public class AddConnActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_back)
    Toolbar toolbar;
    @BindView(R.id.edit_text_clientid)
    TextInputEditText editTextClientid;
    @BindView(R.id.edit_text_serverip)
    TextInputEditText editTextServerip;
    @BindView(R.id.edit_text_port)
    TextInputEditText editTextPort;
    @BindView(R.id.edit_text_username)
    TextInputEditText editTextUsername;
    @BindView(R.id.edit_text_password)
    TextInputEditText editTextPassword;
    @BindView(R.id.submit_button)
    MaterialButton submitButton;
    @BindView(R.id.til_clientid)
    TextInputLayout tilClientid;
    @BindView(R.id.til_serverip)
    TextInputLayout tilServerip;
    @BindView(R.id.til_port)
    TextInputLayout tilPort;

    private Box<Connection> connectionBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conn);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("addConn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.submit_button)
    public void onViewClicked(View view) {
        Connection insertData = getConnection();

        // 写入数据库
        if (insertData != null) {
            //todo
            connectionBox = ((App) getApplication()).getBoxStore().boxFor(Connection.class);
            connectionBox.put(getConnection());
            Snackbar.make(view, "Success create connection",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            Snackbar.make(view, "Failed create connection",
                    Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }

    private Connection getConnection() {
        Connection conn = new Connection();

        String clientId = editTextClientid.getText().toString();
        String serverIp = editTextServerip.getText().toString();
        String port = editTextPort.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        tilClientid.setErrorEnabled(false);
        tilServerip.setErrorEnabled(false);
        tilPort.setErrorEnabled(false);

        if (validateInput(clientId, serverIp, port)) {
            conn.setClientId(clientId);
            conn.setServerIp(serverIp);
            conn.setServerPort(port);
            conn.setUserName(username);
            conn.setPassword(password);
            conn.setActivate(false);    //新创建时默认为未激活

            return conn;
        } else {
            return null;
        }

    }

    private boolean validateInput(String clientId, String serverIp, String port) {
        if (clientId.isEmpty()) {
            showError(tilClientid, "can't be empty");
            return false;
        } else if (serverIp.isEmpty()) {
            showError(tilServerip, "can't be empty");
            return false;
        } else if (port.isEmpty()) {
            showError(tilPort, "can't be empty");
            return false;
        }
        return true;
    }

    //显示错误提示，并获取焦点
    private void showError(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
        textInputLayout.getEditText().setFocusable(true);
        textInputLayout.getEditText().setFocusableInTouchMode(true);
        textInputLayout.getEditText().requestFocus();
    }

}