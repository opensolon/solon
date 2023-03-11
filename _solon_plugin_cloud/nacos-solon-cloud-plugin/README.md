
#### 1. 简要配置示例

```yml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.nacos:
  server: localhost:8848      #服务地址
  username: nacos             #链接账号
  password: nacos             #链接密码
  config:
    load: "helloapp.yml"

```

#### 2. 更多配置示例

```yml
solon.app:
  group: "demo"
  name: "helloapp"
  meta:
    version: "v12" #添加元信息配置

solon.cloud.nacos:
  username: nacos
  password: nacos
  discovery:
    server: "localhost:8848,localhost:8847"    #发现与注册服务地址（默认为: server）
    contextPath: "nacos2" #nacos 上下文配置
  config:
    server: "localhost:8831"    #配置服务地址（默认为: server）
    contextPath: "nacos2"
```