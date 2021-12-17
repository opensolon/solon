

```yaml
solon.app:
  name: demoapp

# 以下为默认值，如果没有变化，不用加（与solon.logging 插件配置统一起来）
solon.logging.appender:
  console:
    pattern: "%highlight(%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} [-%thread][*%mdc{traceId}]%tags[%logger{20}]:) %n%msg%n"
  file:
    name: "logs/${solon.app.name}"
    level: INFO
    pattern: "%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} [-%thread][*%mdc{traceId}]%tags[%logger{20}]: %n%msg%n"
  
```

用户自定义xml配置
```yaml
logback-app.xml 默认
logback-app-{env}.xml 环镜切换
```