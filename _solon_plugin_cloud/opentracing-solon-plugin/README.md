

```yaml
solon.cloud.opentracing:
  server: "udp://localhost:6831"
  trace:
    enable: true                    #是否启用（默认：true）
    exclude: "/healthz,/_run/check/" #排除路径
```