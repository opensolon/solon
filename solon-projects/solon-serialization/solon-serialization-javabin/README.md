solon-serialization-javabin
==========

基于 Java 原生 `ObjectInputStream` / `ObjectOutputStream` 的 javabin 序列化实现。
反序列化时带类过滤。

## 使用

```java
JavabinSerializer ser = new JavabinSerializer();
ser.classFilter().allow("com.yourapp.");
redisCacheService.serializer(ser);
```

也可以通过配置放宽：

```yaml
solon.serialization.javabin:
  allow:
    - "com.yourapp."
  deny:
    - "com.yourapp.internal."
  unrestricted: false
```

默认过滤器允许常见 JDK 类型，可通过 `allow(...)` / `deny(...)` 调整；如需平滑迁移，也可以配合 `unrestricted: true` 先按黑名单模式接入。
