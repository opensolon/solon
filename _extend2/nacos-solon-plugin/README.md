```yaml
solon.app:
  name: "solon-consul-test"

solon.cloud.nacos:
  server: "localhost:8500"
  username: nacos
  password: nacos
  discovery:
    enable: true
    hostname: "12.12.12:12"
  config:
    enable: true
    load: "test/app"

```