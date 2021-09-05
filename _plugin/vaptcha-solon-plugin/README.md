<h1 align="center">Ant Design</h1>

## ✨ 特性

- 🌈 注解式验证，无侵入性。
- 📦 开箱即用的高质量组件。

## 📦 安装

```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>vaptcha-solon-plugin</artifactId>
    </parent>
```

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