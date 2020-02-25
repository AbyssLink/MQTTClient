# MQTTClient
实现一个订阅与发布 mqtt 协议消息的 Android 客户端。

支持多个连接，支持一个连接内的订阅（Subscribe）与发布（Publish）多个消息。



#### 截图

| 添加连接                                                     | 连接页面                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](https://raw.githubusercontent.com/ShiroCheng/pic/master/Screenshot_1582626446.png) | ![](https://raw.githubusercontent.com/ShiroCheng/pic/master/Screenshot_1582626387.png) |



#### 问题解决

1. RecyclerView 的 Adapter 触发点击事件后，通知对应 Activity。

由于 MqttHelper 的限制，Mqtt 的消息处理只能在 Activity 中进行，因此需要完成 adapter 与 activity 之间的通信。我使用接口回调实现，如：点击某一个订阅的item，触发父 activity 处理消息并更新视图：

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

几个成熟的解决方案：

- livedata + room + viewmodel (Google 官方)
- Greendao + Rxjava
- Objectbox 

因为 Objectbox 将数据库 dao 操作与监听数据变化等操作都集成了，我选择了 ObjectBox，为每一个 java bean 类都设置了 objectBox 提供的观察者模型，观察到数据库有变化时更新 UI，使视图刷新更灵活。



初始化 ObjectBox 

```java
// App.java
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



使用 Model 观察 subscription 数据变化更新 UI 

```java
// BriefSubFragment.java
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



mqtt 收到新信息 msg 更新视图

```java
// BriefSubFragment.java
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



#### 文件说明

```bash
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

#### 依赖

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



