
#### 1. 简要配置

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

#### 2. 完整配置

```yml
solon.app:
  group: "demo"
  name: "helloapp"

solon.cloud.nacos:
  server: "localhost:8848"
  username: nacos
  password: nacos
  discovery:
    server: "localhost:8848"    #发现与注册服务地址（默认为: server）
    enable: true                #是否启用（默认为: 启用）
    unstable: true              #不稳定ip?
    tags: "test"                #添加服务标签
  config:
    server: "localhost:8848"    #配置服务地址（默认为: server）
    enable: true                #是否启用（默认为: 启用）
    load: "helloapp.yml"
```