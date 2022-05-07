
#### udp
```yaml
solon.cloud.jaeger:
  server: "localhost:6831"
  trace:
    enable: true #默认为启用
```

#### http

```yaml
solon.cloud.jaeger:
  server: "http://localhost:xxxx"
  username: user
  password: 1234
  trace:
    enable: true #默认为启用
```

```yaml
solon.cloud.jaeger:
  server: "http://localhost:xxxx"
  token: xxxx
  trace:
    enable: true #默认为启用
```