
#### udp
```yaml
solon.cloud.jaeger:
  server: "localhost:6831"
  trace:
    enable: true                    #是否启用（默认：true）
    exclude: "/health,/_run/check/" #排除路径
```

#### http

```yaml
solon.cloud.jaeger:
  server: "http://localhost:xxxx"
  username: user
  password: 1234
  trace:
    enable: true                    #是否启用（默认：true）
    exclude: "/health,/_run/check/" #排除路径
```

```yaml
solon.cloud.jaeger:
  server: "http://localhost:xxxx"
  token: xxxx
  trace:
    enable: true                    #是否启用（默认：true）
    exclude: "/health,/_run/check/" #排除路径
```