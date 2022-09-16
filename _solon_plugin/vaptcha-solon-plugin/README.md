<h1 align="center">Vaptcha for solon</h1>

<div align="center">
Author noear，iYarnFog
</div>

## ✨ 特性

- 🌈 注解式验证，无侵入性。
- 📦 开箱即用的高质量组件。

## 📦 安装

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>vaptcha-solon-plugin</artifactId>
</dependency>
```

前端适配教程请参考 [Vaptcha 官网](https://www.vaptcha.com/document/install.html#pc-%E7%BD%91%E9%A1%B5%E9%83%A8%E7%BD%B2)

## ⚙️ 配置

```yaml
vaptcha:
  vid: xxx
  key: xxx
  # 是否本地调试？本地调试会自动注入真实IP，127.0.0.1这样的无法通过验证
  local: true
```

## 🔨 示例

```java
import org.noear.solon.extend.vaptcha.http.request.validators.Vaptcha;

public class Request {
    @Vaptcha
    iVaptcha vaptcha;
}
```