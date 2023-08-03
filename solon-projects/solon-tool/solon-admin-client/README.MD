# Solon Admin Client

```xml

<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-admin-client</artifactId>
</dependency>
```

## 1. 描述

**需配合 Solon Admin Server 一起使用**

Solon Admin 是一款基于 Solon 的轻量级应用监视器，可用于监视 Solon 应用的运行状态。

## 2. 使用

引入包后，启动类添加注解：`@EnableAdminServer`

```java

@EnableAdminClient
@SolonMain
public class Main {

    public static void main(String[] args) {
        Solon.start(Main.class, args);
    }
}
```

之后启动应用程序，访问 Solon Admin Server 实例的地址即可使用。

## 3. 配置

```yaml
solon.admin.client:
  enabled: true #是否启用 Solon Admin Client
  mode: "local" #模式：local 本地模式，cloud 云模式
  token: "3C41D632-A070-060C-40D2-6D84B3C07094" #令牌：访问接口时需要
  serverUrl: "http://localhost:8080" #Solon Admin Server 实例地址
  connectTimeout: 5000 #连接超时，单位：毫秒
  readTimeout: 5000 #读取超时，单位：毫秒
  metadata: "" #自定义元数据
  showSecretInformation: false #是否向服务端发送敏感信息，如环境变量等
```

## 4. 配置中心

Solon Admin Client 支持连接到配置中心，只需将 `mode` 设置为 `cloud`，并在 Solon 中配置配置中心相关信息即可启用。