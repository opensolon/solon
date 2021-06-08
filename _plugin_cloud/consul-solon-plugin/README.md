
#### 1. 简要配置示例

```yml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.nacos:
  server: localhost      #服务地址
  config:
    load: "helloapp.yml"

```

#### 2. 完整配置示例

```yml
solon.app:
  group: "demo"
  name: "helloapp"

solon.cloud.nacos:
  server: "localhost"
  discovery:
    server: "localhost"             #发现与注册服务地址（默认为: server）
    enable: true                    #是否启用（默认为: 启用）
    unstable: true                  #不稳定ip?
    tags: "test"                    #添加服务标签
    healthCheckPath: "/run/check/"  #健康检测路径（默认为：/run/check/）
    healthCheckInterval: "5s"       #健康检测时间间隔（默认为：5s）
    healthDetector: ""              #自定义探测器
    refreshInterval: "5s"           #发现刷新时间间隔（默认为：5s）
  config:
    server: "localhost"         #配置服务地址（默认为: server）
    enable: true                #是否启用（默认为: 启用）
    load: "helloapp.yml"        #启动时加载配置
    refreshInterval: "5s"       #配置刷新时间隔（默认为：5s）
```