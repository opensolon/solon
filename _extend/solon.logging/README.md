
## demo

```yaml
solon.logging.appender:
  console:
    level: TRACE
    enable: true
  cloud:
    level: INFO
    enable: true
  file:
    class: org.xxx.xxx.LogFileAppender
    level: INFO

solon.logging.logger:
  "org.xxx.xxx.*":
    level: INFO
  "org.xxx.xxx.yyy":
    level: INFO
```
