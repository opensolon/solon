
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
    server: localhost:8093   #polaris配置服务地址
    load: "demoapp.yml"
  discovery:
    server: localhost:8091   #polaris发现服务地址
```

需要更复杂的配置，请通过资源 resources/polaris.yml 进行配置
具体参考：https://github.com/polarismesh/polaris-java/blob/main/polaris-common/polaris-config-default/src/main/resources/conf/default-config.yml

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

