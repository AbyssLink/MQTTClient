package com.example.shirocheng.mqttclient.base.brief;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.shirocheng.mqttclient.bean.Connection;
import com.example.shirocheng.mqttclient.db.DaoHelper;

import java.util.List;

/**
 * 该 Model 用于绑定 Conn 变化
 */
public class ConnViewModel extends ViewModel {

    private MutableLiveData<List<Connection>> conns;

    public LiveData<List<Connection>> getConnections() {
        if (conns == null) {
            conns = new MutableLiveData<>();
            loadConnections();
        }
        return conns;
    }

    private void loadConnections() {
        // Do an asynchronous operation to fetch data.
        conns.postValue(DaoHelper.getInstance().loadAllConn());
    }

    /**
     * 用于外部通知更新
     */
    public void refresh() {
        conns.setValue(DaoHelper.getInstance().loadAllConn());
    }

}
