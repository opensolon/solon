### 单测说明：

* 启动 App1Main（作为功能服务）
* 启动 Gateway1Test 单测（如果有需要，修改 gateway1.yml 配置）



### 配置风格：

```yaml
solon.cloud.gateway:
  httpClient: #?可选
    connectTimeout: 10 #?可选
    requestTimeout: 10 #?可选
    responseTimeout: 1800 #?可选
  discover:
    enabled: true
    excludedServices:
      - "user-service"
  routes: #!必选
    - target: "http://localhost:8080" # 或 "lb://user-service"
      predicates: #?可选
        - "Path=/demo/**"
      filters: #?可选
        - "StripPrefix=1"
      timeout: #?可选
        connectTimeout: 10 #?可选
        requestTimeout: 10 #?可选
        responseTimeout: 1800 #?可选
```

