```yaml
solon:
  app:
    name: "solon-consul-test"

solon.cloud.nacos:
  server: "localhost:8500"
  username: nacos
  password: nacos
  discovery:
    enable: true
    hostname: "12.12.12:12"
    healthCheckInterval: "5s"
    healthCheckPath: "/run/check/"
    healthDetector: ""
  config:
#    server: "localhost:8500"
    enable: true
    load: "test/app"
#    interval: "5s"
#    watch: ""
  locator:
#    enable: true
    interval: "5s"

```