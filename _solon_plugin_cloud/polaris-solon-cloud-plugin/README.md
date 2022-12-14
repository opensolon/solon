
#### 配置示例

```yml
solon.app:
  name: "demoapp"
  group: "demo"
  namespace: "default"

solon.cloud.polaris:
  username: polaris        #polaris链接账号
  password: polaris        #polaris链接密码
  config:
    load: "demoapp.yml"
    server: localhost:8093   #polaris配置服务地址
    serverConnector: # 映射 ConnectorConfigImpl 的字段
      persistEnable: false
  discovery:
    server: localhost:8091   #polaris发现服务地址
    serverConnector: # 映射 ServerConnectorConfigImpl 的字段
      protocol: "grpc"
```

#### 已完成
+ 配置中心 读取,监听
+ 注册中心 监听,拉取,注册,删除
+ 差异性记录（这个应该不存在了...by noear）
    + 在 polaris 中的group指的是配置文件的文件组,属于一个namespace,里面可有多文件,yml/xml/txt/properties等等
    + 相对的 namespace 更像是 nacos 的group
+ solon例子


#### TODO 
+ 配置中心: 如下方式获取的对象为null
``` 
    @CloudConfig(name = "user", autoRefreshed = true)
    User user;
```

+ 配置中心: 不支持更新/新增/删除配置

