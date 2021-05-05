
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
    class: org.xx.xx.LogFileAppender
    level: INFO

solon.logging.logger:
  "xxx.xxx.xxx.*":
    level: INFO
  "xxx.xxx.xxx.yyy":
    level: INFO
```