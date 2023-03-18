[![Maven Central](https://img.shields.io/maven-central/v/org.noear/solon-swagger-knife4j.svg)](https://search.maven.org/search?q=g:org.noear%20AND%20solon-swagger-knife4j)
[![Apache 2.0](https://img.shields.io/:license-Apache2-blue.svg)](https://license.coscl.org.cn/Apache2/)
[![JDK-8+](https://img.shields.io/badge/JDK-8+-green.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![QQ交流群](https://img.shields.io/badge/QQ交流群-22200020-orange)](https://jq.qq.com/?_wv=1027&k=kjB5JNiC)



solon 集成 knife4j , 按UI规范生成相应的 Swagger Json。目前处于试验中


与 solon 版本对应

| solon-swagger-knife4j | solon   | 备注  |
|-----------------------|---------|-----|
| 1.0.0-M1              | 1.7.3   |     |
| 1.0.1-M2              | 1.10.13 |     |
| 1.0.1-M3              | 1.11.6  |     |
| 1.0.1-M4              | 2.0.0   |     |


使用方式：
maven引入
```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-swagger-knife4j</artifactId>
    <version>1.0.1-M4</version>
</dependency>

<!-- 需配合 solon 2.0.0 或更高版本使用 -->
```

启用示例（启动后打开：`/swagger`）

```java
@EnableSwagger
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}
```

初始化配置文件，copy 到工程 resources/swagger.properties 目录下

```properties
# 配置 swagger

# 启用文档
enable = true

# 当前swagger的版本号
swaggerVersion = 2.0

# 文档名称
info_title = 在线文档
# 文档说明
info_description = 在线API文档
# 服务条件
info_termsOfService = https://gitee.com/noear/solon/blob/master/LICENSE
# 许可
info_license_name = demo
info_license_url = https://gitee.com/noear/solon
# 联系方式
info_contact_name = demo
info_contact_email = demo@qq.com
# 文档版本号
info_version = 1.0

# 访问地址,默认指向当前服务器
# host = 127.0.0.1:8080

# 访问前缀,默认为空,需与solon配置的controllerKey前缀一致
basePath = /

# 访问许可
schemes = http, https

# 链接外部文档
externalDocs_description = Find out more about Swagger
externalDocs_url = https://swagger.io/

# 接口分组 分组名#包名 多个分组使用逗号拼接
swagger_resources = admin端接口#com.swagger.demo.admin, app端接口#com.swagger.demo.app


# 提供全局参数Debug功能,目前默认提供header(请求头)、query(form)两种方式的入参.Debug调试tab页会带上该参数
# 格式  name#in   多个全局参数使用逗号拼接
# name  参数名.
# in    header(请求头) | query(form)
globalSecurityParameters = token#header, testPara#query


#############################  扩展增强设置  #############################
# 禁用OpenApi结构显示(默认显示)
# enableOpenApi = false
# 禁用UI搜索框(默认显示)
# enableSearch = false
# 禁用调试(默认显示)
# enableDebug = false
# 调试Tab是否显示AfterScript功能(默认显示)
# enableAfterScript = false
# 是否显示界面中SwaggerModel功能(默认显示)
# enableSwaggerModels = false
# 自定义Swagger Models名称(默认显示Swagger Models)
# swaggerModelName = 我是自定义的Model名称
# 是否显示Footer(默认显示)
# enableFooter = false
# 是否启用自定义Footer
# enableFooterCustom = true
# 自定义Footer内容,支持Markdown语法. 需enableFooter=false&&enableFooterCustom=true时生效
# footerCustomContent = Apache License 2.0 | Copyright  2019-[浙江八一菜刀股份有限公司](https://gitee.com/xiaoym/knife4j)

```
