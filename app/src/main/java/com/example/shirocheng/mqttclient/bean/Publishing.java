package com.example.shirocheng.mqttclient.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Publishing {

    @Id
    private Long id;    // 主键自增

    private Long connId;    // 所属连接的主键
    private String name;    // 订阅名称
    private String topic;   //主题

    public Publishing(Long connId, String name, String topic) {
        this.connId = connId;
        this.name = name;
        this.topic = topic;
    }

    public Publishing() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConnId() {
        return connId;
    }

    public void setConnId(Long connId) {
        this.connId = connId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
