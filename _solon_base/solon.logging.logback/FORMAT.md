
怀旧风格 old:

%d{yyyy-MM-dd HH:mm:ss} %-5level %magenta(${PID:-}) %white(---) %-20(%yellow([%20.20thread])) %-55(%cyan(%.32logger{30}:%L)) %msg%n

怀旧风格 new:

%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n

