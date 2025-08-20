
#### 基于配置进行构建

前端打开，跟本地的一样

```yaml
solon.docs:
  discover:  #(发现配置，需要引入 solon cloud 发现服务插件)，可选
    enabled: true
    syncStatus: true                          #同步上下线状态
    uriPattern: "swagger/v2?group={service}" #上游路径模式（要么带变量 {service}，要么用统一固定值）
    contextPathPattern: "/{service}"
    basicAuth:
      admin: "123456"
      user: "654321"
    excludedServices:                                 #排除服务名
      - "user-api"
    includedServices:                                 #包函服务名
      - "order-api"
  routes:
    - id: appApi   #(远程接口文档，即分布式服务或微服务)，配置风格
      groupName: "app端接口"
      upstream:
        target: "lb://app-api"
        contextPath: "/app"
        uri: "/xxx"
    - id: adminApi  #(本地接口文档)，配置风格
      groupName: "admin端接口"
      globalResponseInData: true
      basicAuth:
        admin: "123456"
        user: "654321"
      apis:
        - basePackage: "com.swagger.demo.controller.admin"
      info: #可选
        title: "在线文档"
        description: "在线API文档"
        termsOfService: "https://gitee.com/noear/solon"
        version: 1.0
        contact: #可选
          name: "demo"
          email: "demo@qq.com"
        license: #可选
          name: "demo"
          url: "https://gitee.com/noear/solon/blob/master/LICENSE"
```

#### 2、基于代码构建

```java
@Configuration
public class DocConfig {

    @Inject
    OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * 基于代码构建
     */
    @Managed("appApi")
    public DocDocket appApi() {
        //根据情况增加 "knife4j.setting" （可选）
        return new DocDocket()
                .vendorExtensions(openApiExtensionResolver.getExtension())
                .groupName("app端接口")
                .schemes(Scheme.HTTP)
                .globalResult(Result.class)
                .globalResponseInData(true)
                .apis("com.swagger.demo.controller.app")
                .securityDefinitionInHeader("token");

    }

    @Managed("adminApi")
    public DocDocket adminApi() {
        return new DocDocket()
                .groupName("admin端接口")
                .info(new ApiInfo().title("在线文档")
                        .description("在线API文档")
                        .termsOfService("https://gitee.com/noear/solon")
                        .contact(new Contact().name("demo")
                                .url("https://gitee.com/noear/solon")
                                .email("demo@foxmail.com"))
                        .version("1.0"))
                .schemes(Scheme.HTTP, Scheme.HTTPS)
                .globalResponseInData(true)
                .globalResult(Result.class)
                .apis("com.swagger.demo.controller.admin")
                .securityDefinitionInHeader("token");

    }
}
```

