package com.example.shirocheng.mqttclient.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Subscription {

    @Id(autoincrement = true)
    private Long id;    // 主键自增

    private Long connId;    // 所属连接的主键
    private String name;    // 订阅名称
    private String topic;   //主题
    private Boolean isNumber;   // 是否为数字
    private String jsonKey;     // json解包后的键，用于取值

    @Generated(hash = 1800298428)
    public Subscription() {
    }

    @Generated(hash = 460237087)
    public Subscription(Long id, Long connId, String name, String topic, Boolean isNumber, String jsonKey) {
        this.id = id;
        this.connId = connId;
        this.name = name;
        this.topic = topic;
        this.isNumber = isNumber;
        this.jsonKey = jsonKey;
    }

    public Long getConnId() {
        return connId;
    }

    public void setConnId(Long connId) {
        this.connId = connId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getNumber() {
        return isNumber;
    }

    public void setNumber(Boolean number) {
        isNumber = number;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public void setJsonKey(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public Boolean getIsNumber() {
        return this.isNumber;
    }

    public void setIsNumber(Boolean isNumber) {
        this.isNumber = isNumber;
    }

}
