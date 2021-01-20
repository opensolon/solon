
## 一、配置示例：

```yaml
solon:
  app:
    name: "solon-consul-test"

solon.cloud.nacos:
  server: "localhost:8500"
  username: nacos
  password: nacos

```

## 二、配置说明

| 配置说 | 说明 | 框架：nacos | 框架：consul |
| -------- | -------- |  -------- |   -------- | 
| solon.cloud.@@.server     |      |       |        | 
| solon.cloud.@@.token     |      |       |        | 
| solon.cloud.@@.username     |      |       |        | 
| solon.cloud.@@.password     |      |       |        | 
| | | |
| solon.cloud.@@.config.enable     |      |       |        | 
| solon.cloud.@@.config.loadGroup     |      |       |        | 
| solon.cloud.@@.config.loadKey     |      |       |        | 
| solon.cloud.@@.config.refreshInterval     |      |       |        | 
| | | |
| solon.cloud.@@.discovery.enable     |      |        |        | 
| solon.cloud.@@.discovery.hostname     |      |        |        | 
| solon.cloud.@@.discovery.tags     |      |        |        | 
| solon.cloud.@@.discovery.healthCheckPath     |      |        |        | 
| solon.cloud.@@.discovery.healthCheckInterval     |      |        |        | 
| solon.cloud.@@.discovery.healthDetector     |      |        |        | 
| solon.cloud.@@.discovery.healthDetector     |      |        |        | 
| solon.cloud.@@.discovery.refreshInterval     |      |        |        | 