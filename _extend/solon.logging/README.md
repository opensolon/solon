
## demo

```yaml
solon.logging.appender:
  console:
    level: INFO
  file:
    class: org.noear.solon.logging.LogFileAppender
    level: INFO

solon.logging.logger:
  "xxx.xxx.xxx.*":
    level: INFO
  "xxx.xxx.xxx.yyy":
    level: INFO
```