
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
  "org.apache.zookeeper.*":
    level: "WARN"
  "org.eclipse.jetty.*":
    level: "WARN"

```
