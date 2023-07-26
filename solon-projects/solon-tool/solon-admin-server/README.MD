# Solon Admin Server

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-admin-server</artifactId>
</dependency>
```

## 1. 描述

**需配合 Solon Admin Client 一起使用**

Solon Admin 是一款基于 Solon 的轻量级应用监视器，可用于监视 Solon 应用的运行状态。

## 2. 使用

引入包后，启动类添加注解：`@EnableAdminServer`

```java
@EnableAdminServer
@SolonMain
public class Main {

    public static void main(String[] args) {
        Solon.start(Main.class, args);
    }
}
```

之后启动应用程序，访问 `http://localhost:8080`（默认地址）即可查看相关信息。

## 3. 配置

```yaml
solon.admin.server:
  enabled: true #是否启用 Solon Admin Server
  mode: "local" #模式：local 本地模式，cloud 云模式
  heartbeatInterval: 10000 #心跳速率，单位：毫秒
  clientMonitorPeriod: 2000 #客户端监控周期，单位：毫秒
  connectTimeout: 5000 #连接超时，单位：毫秒
  readTimeout: 5000 #读取超时，单位：毫秒
  uiPath: "/"
  basicAuth:
    admin: 123456
```

## 4. 配置中心

Solon Admin Server 支持连接到配置中心，只需将 `mode` 设置为 `cloud`，并在 Solon 中配置配置中心相关信息即可启用。