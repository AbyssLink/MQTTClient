package com.example.shirocheng.mqttclient.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Connection {

    @Id(autoincrement = true)
    private Long id;    // 主键自增

    private String clientId;    // 设备uuid, 也作为名称在视图显示
    private String serverIp;   // 主机ip
    private String serverPort;  // 主机端口
    private String userName;    // 用户名
    private String password;    // 用户密码

    @Generated(hash = 1735801874)
    public Connection() {
    }

    @Generated(hash = 1789591368)
    public Connection(Long id, String clientId, String serverIp, String serverPort, String userName,
                      String password) {
        this.id = id;
        this.clientId = clientId;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.userName = userName;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
