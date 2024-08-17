solon.cloud.gateway 为“半”响应式架构

配置风格：

```yaml
solon.cloud.gateway:
  routes:
    - upstream: "http://localhost:8080" # 或 "lb://user-service"
      predicates:
        - "PATH=/demo/**"
      filters:
        - "StripPrefix=1"
```