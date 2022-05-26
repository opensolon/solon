
#### 1. 简要配置示例

```yml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.zookeeper:
  server: localhost      #服务地址
  config:
    load: "helloapp.yml"

```

#### 2. 完整配置示例

```yml
solon.app:
  group: "demo"
  name: "helloapp"

solon.cloud.zookeeper:
  server: "localhost"
  discovery:
    server: "localhost"             #发现与注册服务地址（默认为: server）
    enable: true                    #是否启用（默认为: 启用）
    tags: "test"                    #添加服务标签
  config:
    server: "localhost"         #配置服务地址（默认为: server）
    enable: true                #是否启用（默认为: 启用）
    load: "helloapp.yml"        #启动时加载配置
```