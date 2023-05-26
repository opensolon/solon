
* 实现 slf4j 接口对接，为分布式日志服务提供入口.
* solon.cloud 不再直接依赖 solon.logging.impl


日志服务将由：

* log4j2-solon-plugin //本地日志
* logback-solon-plugin  //本地日志
* water-solon-plugin //分布式日志服务