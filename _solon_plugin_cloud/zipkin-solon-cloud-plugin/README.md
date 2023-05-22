

#### http

```yaml
solon.cloud.zipkin:
  server: "http://localhost:xxxx/api/v2/spans" #根据server的版本选择正确的上报入口
  trace:
    enable: true                    #是否启用（默认：true）
    exclude: "/healthz,/_run/check/" #排除路径
```
