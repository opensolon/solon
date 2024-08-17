solon.cloud.gateway 为“半”响应式架构

配置风格：

```yaml
solon.cloud.gateway:
  routes:
    - target: "http://localhost:8080" # 或 "lb://user-service"
      predicates:
        - "Path=/demo/**"
        - "xxx.xxx.demo1=ccc" #全类名 base:RoutePredicate
      filters:
        - "StripPrefix=1"
        - "xxx.xxx.demo2=test" #全类名 base:RouteFilter
```


目前支持：

| 框架         | 支持情况                         | 备注                         |
|------------|------------------------------|----------------------------|
| jetty      | 推荐                           |                            |
| undertow   | 推荐                           |                            |
| jdkhttp    | 支持                           |                            |
| jlhttp     | 支持                           |                            |
| smart-http | 不支持（表单提前解析后，不能做 stream copy） | 换掉 SimpleRouteHandler 后可支持 |

