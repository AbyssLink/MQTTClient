# MQTTClient
物联网与安卓课大作业，实现一个基于mqtt 的安卓程序

# MQTT DashBoard 答辩说明

实现了一个订阅与发布 mqtt 协议消息的 Android 客户端，可以添加与配置物联网节点的属性。支持多连接，单连接内的多消息订阅与发布。

### **项目名称**

MQTT DashBoard

## 项目概述

###  java 文件结构

```shell
$tree -a
.
├── base	// 基本组件
│   ├── add
│   │   ├── AddConnActivity.java
│   │   ├── AddPubActivity.java
│   │   └── AddSubActivity.java
│   ├── BriefDashFragment.java
│   ├── BriefPubFragment.java
│   ├── BriefSubFragment.java
│   ├── ControlActivity.java
│   ├── MainActivity.java
│   ├── model	// model层，实现数据实时更新
│   │   ├── ConnViewModel.java
│   │   ├── MsgViewModel.java
│   │   ├── PubViewModel.java
│   │   └── SubViewModel.java
│   ├── RecyclerConnAdapter.java
│   ├── RecyclerPubAdapter.java
│   ├── RecyclerSubAdapter.java
│   └── view	// view 的帮助类，如监听接口
│       ├── ItemTouchHelperCallback.java
│       ├── NoScrollViewPager.java
│       └── OnMoveAndSwipedListener.java
├── bean	// 基本数据结构
│   ├── Connection.java		// 连接属性
│   ├── Msg.java			// 收到的mqtt消息
│   ├── Publishing.java		// 发布属性
│   └── Subscription.java	// 订阅属性
├── db		// objectBox 定义
│   └── App.java
├── mqtt	// 封装mqtt消息方法
│   └── MqttHelper.java
└── smartconfig		// smartConfig 功能
    ├── EsptouchDemoActivity.java
    └── EspUtils.java

```



### 项目依赖

```protobuf
implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'    
// Mqtt client : Eclipse Paho
implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'    
// Mqtt service    
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0-alpha'
// 图表显示
implementation 'com.google.code.gson:gson:2.8.5'
// 解析json格式数据

classpath "io.objectbox:objectbox-gradle-plugin:$objectboxVersion"
// 数据持久化并观察变化
```



## 问题解决

1. RecyclerView 的 Adapter 触发点击事件后，通知其 Activity 进行操作。

   由于 MqttHelper 的限制，Mqtt 的消息处理只能在 Activity 中进行，因此需要完成 adapter 与 activity 之间的通信。我选用的方式是通过接口回调实现，如：点击某一个订阅的item，触发父 activity 处理消息并更新视图：

   ```java
   // RecyclerSubAdapter.java
   
   // 接口定义
   public interface onItemClickListener {
       void onSubscribe(Subscription sub);
   }
   
   // 接口设置
   public void setOnItemClickListener(onItemClickListener listener) {
       this.onItemClickListener = listener;
   }
   
   @Override
   public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
   
       if (viewHolder instanceof RecyclerViewHolder) {
           final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;
   
           recyclerViewHolder.mView.setOnClickListener(view -> {
               Snackbar.make(parentView, "Start Subscribe", Snackbar.LENGTH_SHORT)
                   .setAction("Action", null).show();
               if (onItemClickListener != null) {
                   // 接口方法
                   onItemClickListener.onSubscribe(sub);
               }
           });
       }
   }
   
   // BriefSubFragment.java
   // 实现接口方法
   mAdapter.setOnItemClickListener(sub -> updateMsgUI(sub));
   ```

2. 视图随数据同步更新

google 官方提供了 livedata + room + viewmodel 的实现方式，greendao 和 rxjava 也是应用非常广泛的方式，而 objectBox 则将数据库 dao 操作与 监听数据变化等操作都集成了。最终我选择使用 objectBox 库，为每一个 java bean 类都设置了 objectBox 提供的观察者模型，观察到数据库有变化时自动更新 UI，加强了视图更新的灵活性并简化了不少操作，使用如下：

```java
// App.java
// 初始化 ObjectBox
public class App extends Application {

    public static final String TAG = "ObjectBoxExample";
    public static final boolean EXTERNAL_DIR = false;

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(App.this).build();
        Stetho.initializeWithDefaults(this);    // 网络数据库调试
        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(this);
        }
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}

```



```java
// BriefSubFragment.java
/**
 * 使用 Model 观察 subscription 数据变化更新 UI
*/
public void updateUI() {
    subscriptionBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Subscription.class);
    SubViewModel model = ViewModelProviders.of(this).get(SubViewModel.class);
    model.getSubLiveData(subscriptionBox).observe(this, new Observer<List<Subscription>>() {
        @Override
        public void onChanged(@Nullable List<Subscription> subscriptions) {
            if (onDelete) {
                onDelete = false;
            } else if (subscriptions != null) {
                mAdapter.setItems(subscriptions);
            }
        }
    });
}
```



```java
// BriefSubFragment.java
// mqtt 收到新信息 msg 更新视图
public void updateMsgUI(Subscription sub) {

    MqttHelper.getInstance().subscribeTopic(sub.getTopic());
    subscriptionBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Subscription.class);

    // 观察数据变化
    msgBox = ((App) getActivity().getApplication()).getBoxStore().boxFor(Msg.class);
    MsgViewModel model = ViewModelProviders.of(this).get(MsgViewModel.class);
    model.getMsgLiveData(msgBox).observe(this, msgs -> {
        if (msgs.size() >= 2) {
            String msg = msgs.get(msgs.size() - 1).getMsg();
            sub.setMsg(msg);
            // 数据库操作
            subscriptionBox.put(sub);
            if (sub.getJsonKey() == null) {
                Snackbar.make(recyclerSub, msg, Snackbar.LENGTH_SHORT).show();
            } else {
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(msg);
                Snackbar.make(recyclerSub, jsonObject.get(sub.getJsonKey()).getAsString(), Snackbar.LENGTH_SHORT).show();
            }
        }
    });
}
```



3. 智能配网

   物联网节点在初始时需要配置其无线网络，我在项目中集成了官方提供的功能，使用其协议自带的 smartconfig 字段，将 android 客户端连接的无线网络 ssid 和 password 通过广播或多播的方式发送给当前网段下的所有设备，mqtt 物联网节点收到消息后会自主连接上该网络并将信息保存到内置存储中。

```java
// 发送广播，判断 wifi 状态改变
private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
            .getSystemService(WIFI_SERVICE);
        assert wifiManager != null;

        switch (action) {
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                WifiInfo wifiInfo;
                if (intent.hasExtra(WifiManager.EXTRA_WIFI_INFO)) {
                    wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                } else {
                    wifiInfo = wifiManager.getConnectionInfo();
                }
                onWifiChanged(wifiInfo);
                break;
            case LocationManager.PROVIDERS_CHANGED_ACTION:
                onWifiChanged(wifiManager.getConnectionInfo());
                onLocationChanged();
                break;
        }
    }
};
```

