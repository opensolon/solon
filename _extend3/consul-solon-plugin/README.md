
### 配置示例

* properties 配置

```properties
solon.app.name=test-consul-api
solon.app.group=test

consul.host=localhost

#consul.discovery.enable=true
#consul.discovery.hostname=12.12.12:12
#consul.discovery.tags=dev
#consul.discovery.healthCheckInterval=10s
#consul.discovery.healthCheckPath=/run/check/

#consul.locator.enable=true
#consul.locator.interval=10000

#consul.config.enable=true
#consul.config.key=test
#consul.config.interval=10000
```

* yml 配置

```yaml
solon:
  app:
    name: "test-consul-api"
    group: "test"

consul:
  host: "localhost"
#  discovery:
#    enable: true
#    hostname: "12.12.12:12"
#    tags: "dev"
#    healthCheckInterval: "10s"
#    healthCheckPath: "/run/check/"
#  locator:
#    enable: true
#    interval: "10s"
#  config:
#    key: "test"
#    enable: true
#    interval: "10s"

```