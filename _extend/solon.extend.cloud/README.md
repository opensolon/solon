```yaml
solon:
  app:
    name: "solon-consul-test"
    group: "test"

solon.cloud:
  type: "consul"
  server: "localhost:8500"
  discovery:
#    server: "localhost:8500"
#    enable: true
    hostname: "12.12.12:12"
    tags: "dev"
    healthCheckInterval: "10s"
    healthCheckPath: "/run/check/"
    healthDetector: ""
  config:
#    server: "localhost:8500"
#    enable: true
    key: "test/app"
#    interval: "10s"
#    watch: ""
  locator:
#    enable: true
    interval: "10s"

```