package com.example.shirocheng.mqttclient.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Msg {
    @Id
    Long id;

    String msg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
