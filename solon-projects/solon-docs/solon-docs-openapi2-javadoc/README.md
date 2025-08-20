# 接口文档配置文件说明:

## 配置如下
```java
@Configuration
public class DocConfig {
    /**
     * 简单点的
     */
    @Managed("appApi")
    public DocDocket appApi() {
        //根据情况增加 "knife4j.setting" （可选）
        return new DocDocket()
                .groupName("app端接口")
                .schemes(Scheme.HTTP)
                .apis("com.swagger.demo.controller.app");

    }

    /**
     * 丰富点的
     */
    @Managed("adminApi")
    public DocDocket adminApi() {
        //根据情况增加 "knife4j.setting" （可选）
        return new DocDocket()
                .groupName("admin端接口")
                .info(new ApiInfo().title("在线文档")
                        .description("在线API文档")
                        .termsOfService("https://gitee.com/noear/solon")
                        .contact(new ApiContact().name("demo")
                                .url("https://gitee.com/noear/solon")
                                .email("demo@foxmail.com"))
                        .version("1.0"))
                .schemes(Scheme.HTTP, Scheme.HTTPS)
                .globalResponseInData(true)
                .globalResult(Result.class)
                .apis("com.swagger.demo.controller.admin"); //可以加多条，以包名为单位

    }
}
```

# 使用方式
1. 引入solon-docs-openapi2-javadoc依赖
2. 按照上方配置进行配置
3. 访问文档接口 http://localhost:8080/swagger/v2?group=1.%E6%B5%8B%E8%AF%95%E6%A8%A1%E5%9D%97
4. 没有 UI 界面，使用Apifox 等工具进行导入，按照下图方式导入。导入完成后再接口管理中查看进行管理。

<img src="option.png">