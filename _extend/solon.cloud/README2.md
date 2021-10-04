### 本地发现的配置补充

本地发布服务配置(group='')，可与云端发布服务同存，并优于云端（一般用于本地测试或调试；也可用于k8s之类的服务名固定的场景）

```yaml
solon.cloud.local:
  discovery:
    service:
      helloapi: 
        - "http://localhost:8081"
      userapi:
        - "http://userapi"
```

### 本地熔断的配置补充

```yaml
solon.cloud.local:
  breaker:
    test: 1
```