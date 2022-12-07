
#### 配置示例

```yml

solon:
  app:
    namespace: default
    group: demo
    name: demoapp
    
solon.cloud.polaris:
  server: 127.0.0.1:8091
  discovery:
    enable: true
  config:
    enable: true
    load: "server1.yml"
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

