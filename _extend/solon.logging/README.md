
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
    class: org.noear.solon.logging.LogFileAppender
    level: INFO

solon.logging.logger:
  "xxx.xxx.xxx.*":
    level: INFO
  "xxx.xxx.xxx.yyy":
    level: INFO
```