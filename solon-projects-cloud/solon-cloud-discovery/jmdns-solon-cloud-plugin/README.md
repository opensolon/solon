
#### 1. 简要配置示例

```yml
solon.app:
  group: demo       # 配置服务使用的默认组
  name: helloapp    # 发现服务使用的应用名

solon.cloud.jmdns:
  server: localhost # 不需要端口号，JmDNS监听该IP进行服务发现 写 localhost 或某个本地 IP
```

#### 2. 完整配置示例

```yml
solon.app:
  group: demo
  name: helloapp

solon.cloud.jmdns:
  server: localhost
  discovery:
    enable: true                    # 是否启用（默认为: 启用）
```