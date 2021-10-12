<h1 align="center">Solon Health Checks</h1>

<div align="center">
Author noear，iYarnFog
</div>

## ✨ 特性

- 🌈 一行代码添加检查点，侵入性极低。
- 📦 开箱即用的高质量组件。

## 📦 安装

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.extend.health</artifactId>
</dependency>
```

## ⚙️ 配置

```yaml
# No configuration.
```

## 🔨 示例

```java
@Configuration
public class Config {
    @Bean
    public void initHealthCheckPoint() {
        //test...
        HealthChecker.addPoint("preflight", Result::succeed);
        HealthChecker.addPoint("test", Result::failure);
        HealthChecker.addPoint("boom", () -> {
            throw new IllegalStateException();
        });
    }
}
```

```text
GET /healthz
Response Code:
200 : Everything is fine
503 : At least one procedure has reported a non-healthy state
500 : One procedure has thrown an error or has not reported a status in time
Response:
{"code":"ERROR","details":[{"name":"test","code":"UP"},{"name":"boom","code":"DOWN"},{"name":"preflight","code":"ERROR"}]}
```