

默认xml配置，支持应用配置
```yaml
solon.app:
  name: demoapp

# 以下为默认值，如果没有变化，不用加（与 solon.logging 插件配置统一）(支持日志服务配置)
solon.logging.appender:
  console:
    level: TRACE
    pattern: "%highlight(%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} [-%thread][*%mdc{traceId}]%tags[%logger{20}]:) %n%msg%n"
  file:
    name: "logs/${solon.app.name}"
    level: INFO
    pattern: "%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} [-%thread][*%mdc{traceId}]%tags[%logger{20}]: %n%msg%n"
  cloud:
    level: INFO
  
```

用户自定义xml配置
```yaml
logback-solon.xml 默认
logback-solon-{env}.xml 环镜切换
```