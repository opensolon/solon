
#### 1. 简要配置示例

```yml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.water:
  server: water      #服务地址
  config:
    load: "helloapp.yml"

```

#### 2. 完整配置示例

```yml
solon.app:
  group: "demo"
  name: "helloapp"

solon.cloud.water:
  server: water
  discovery:
    enable: true                #是否启用（默认为: 启用）
    server: "water"             #发现与注册服务地址（默认为: server）
    tags: "test"                #注册标签
  config:
    enable: true                #是否启用（默认为: 启用）
    load: "helloapp.yml"        #启动时加载配置
  log:
    default: "helloapp_log"
  trace:
    enable: true                #是否启用（默认为: 启用）
  metric:
    enable: true                #是否启用（默认为: 启用）
  job:
    enable: true                #是否启用（默认为: 启用）
  event:
    enable: true                #是否启用（默认为: 启用）
    receive: "@helloapp_host"   #事件接收域名
  lock:
    enable: true                #是否启用（默认为: 启用）
  list:
    enable: true                #是否启用（默认为: 启用）
```