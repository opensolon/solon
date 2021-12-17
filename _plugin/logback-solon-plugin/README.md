

```yaml
solon.app:
  name: demoapp

# 以下为默认值，如果没有变化，不用加
solon.logging.appender:
  console:
    pattern: "%highlight(%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} [-%thread][*%mdc{traceId}]%tags[%logger{20}]:) %n%msg%n"
  file:
    name: "logs/${solon.app.name}"
    pattern: "%-5level %d{yyyy-MM-dd HH:mm:ss.SSS} [-%thread][*%mdc{traceId}]%tags[%logger{20}]: %n%msg%n"
  
```