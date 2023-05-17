
怀旧风格 old:

%d{yyyy-MM-dd HH:mm:ss} %-5level %magenta(${PID:-}) %white(---) %-20(%yellow([%20.20thread])) %-55(%cyan(%.32logger{30}:%L)) %msg%n

怀旧风格 new:

%white(%d{yyyy-MM-dd HH:mm:ss}) %highlight(%-5level) %magenta(${PID:-}) %white(---) %-20(%yellow([%20.20thread])) %-55(%cyan(%.32logger{30}:%L)) %white(:) %msg%n


```yaml
solon.logging.appender:
  console:
    pattern: "%d{yyyy-MM-dd HH:mm:SSS} %highlight(%-5level) %magenta(${PID:-}) --- %-20(%yellow([%20.20thread])) %-56(%cyan(%-40.40logger{39}%L)) : %msg%n"
  file:
    pattern: "%d{yyyy-MM-dd HH:mm:ss} %-5level ${PID:-} --- %-20([%20.20thread]) %-56(%-40.40logger{39}%L) : %msg%n"
```


怀旧风格 new2(无效):

%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n

