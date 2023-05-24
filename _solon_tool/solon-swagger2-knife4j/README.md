solon 集成 swagger2 + knife4j , 按UI规范生成相应的 Swagger Json。目前处于试验中

使用方式：

启用示例（启动后打开：`/doc.html`），具体参考 src/test 示例

```java
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            //开发模式，或调试模式下才开启文档（或者自己定义配置控制）//或者一直开着（默认是开着的）
            app.enableDoc(app.cfg().isDebugMode() || app.cfg().isFilesMode());
        });
    }
}
```

#### 1、基于配置进行构建

```yaml
swagger.adminApi:
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

```java
@Configuration
public class DocConfig {
    /**
     * 基于配置构建
     */
    @Bean("adminApi")
    public DocDocket adminApi(@Inject("${swagger.adminApi}") DocDocket docket) {
        docket.globalResult(Result.class);
        docket.securityDefinitionInHeader("token");
        return docket;
    }
}
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
    @Bean("appApi")
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

    @Bean("adminApi")
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

