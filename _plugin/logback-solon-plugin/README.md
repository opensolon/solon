

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

用户自定义xml配置，及环境切换
```yaml
logback-solon.xml 默认
logback-solon-{env}.xml 环镜切换
```

Spring boot 怀旧风格
```yaml
solon.app:
  name: demoapp

solon.logging.appender:
  console:
    pattern: "%d{yyyy-MM-dd HH:mm:ss} %-5level %magenta(${PID:-}) %white(-&#45;&#45;) %-20(%yellow([%20.20thread])) %-55(%cyan(%.32logger{30}:%L)) %msg%n"
  file:
    pattern: "%d{yyyy-MM-dd HH:mm:ss} %-5level %magenta(${PID:-}) %white(-&#45;&#45;) %-20(%yellow([%20.20thread])) %-55(%cyan(%.32logger{30}:%L)) %msg%n"
  
```