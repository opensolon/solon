[![Maven Central](https://img.shields.io/maven-central/v/org.noear/solon-swagger-knife4j.svg)](https://search.maven.org/search?q=g:org.noear%20AND%20solon-swagger-knife4j)
[![Apache 2.0](https://img.shields.io/:license-Apache2-blue.svg)](https://license.coscl.org.cn/Apache2/)
[![JDK-8+](https://img.shields.io/badge/JDK-8+-green.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![QQ交流群](https://img.shields.io/badge/QQ交流群-22200020-orange)](https://jq.qq.com/?_wv=1027&k=kjB5JNiC)


solon 集成 knife4j , 按UI规范生成相应的 Swagger Json。目前处于试验中

使用方式：

启用示例（启动后打开：`/doc.html`）

```java
@EnableSwagger
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
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
  info:
    title: "在线文档"
    description: "在线API文档"
    termsOfService: "https://gitee.com/noear/solon"
    version: 1.0
    contact:
      name: "demo"
      email: "demo@qq.com"
    license:
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

    /**
     * 基于代码构建
     */
    @Bean("appApi")
    public DocDocket appApi() {
        return new DocDocket()
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

