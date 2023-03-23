

### 添加https证书支持

通过应用配置文件（app.yml）进行配置（支持 .jks 或 .pfx）

```yml
server.ssl:
  keyStore: "/demo.jks" #或者应用内资源文件 "classpath:demo.jks"
  keyPassword: "demo"
```

通过启动命令增加配置

```shell
java -Dserver.ssl.keyStore=/demo.jks 
     -Dserver.ssl.keyPassword=demo 
     -jar  demo.jar
```


### 启用 http2 示例

添加证书配置

```yml
server.ssl:
  keyStore: "/demo.jks" #或者应用内资源文件 "classpath:demo.jks"
  keyPassword: "demo"
```

启用 http2

```java
public class Http2 {
    public static void main(String[] args) {
        Solon.start(Http2.class, args, app -> {
            app.onEvent(Undertow.Builder.class, e -> {
                //启用 http2
                e.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
                //再加个 http 监听（server.port 被 https 占了）//如果不需要，则不加
                e.addHttpListener(8081, "0.0.0.0");
            });
        });
    }
}
```

