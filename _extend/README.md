


### 插件


| 插件 | 说明 |
| --- | --- |
| boot插件:: | 说明 |
| org.noear:solon.boot.jlhttp* | boot插件,对`jlhttp`适配,提供`http`服务（不自带session state） |
| org.noear:solon.boot.jetty* | boot插件,对`jetty`适配,提供`http`服务（网友@khb提供） |
| org.noear:solon.boot.undertow* | boot插件,对`undertow`适配,提供`http`服务（网友@tyk提供） |
| org.noear:solon.boot.websocket | boot插件,对`java-websocket`适配，提供`websocket`服务 |
| org.noear:solon.boot.smartsocket | boot插件,对`smart-bsocket`适配，提供`socket`服务 |
| org.noear:solon.extend.jetty.jsp | 扩展插件,为`jetty`添加`jsp`支持（不建议使用jsp）（网友@khb提供） |
| org.noear:solon.extend.undertow.jsp | 扩展插件,为`undertow`添加`jsp`支持（不建议使用jsp）（网友@tyk提供） |
| | |
| 静态文件支持插件:: | 说明 |
| org.noear:solon.extend.staticfiles | 扩展插件,添加静态文件支持（监视 resources/static 文件夹） |
| | |
| 切面支持插件:: | 说明 |
| org.noear:solon.extend.aspect | 扩展插件,添加XDao、XService注解支持；进而支持事务和缓存注解 |
| | |
| 数据操作支持插件:: | 说明 |
| org.noear:solon.extend.data | 扩展插件,实现事务和缓存的注解支持 |
| | |
| 验证支持插件:: | 说明 |
| org.noear:solon.extend.validation | 扩展插件,实现验证的注解支持 |
| | |
| Yaml配置支持插件:: | 说明 |
| org.noear:solon.extend.properties.yaml | 扩展插件,添加yml配置文件支持 |
| | |
| Session插件:: | 说明（可将boot插件的session state服务，自动换掉） |
| org.noear:solon.extend.sessionstate.local | 扩展插件,本地`session` |
| org.noear:solon.extend.sessionstate.redis | 扩展插件,分布式`session`（其于`redis`构建） |
| | |
| 序列化插件:: | 说明 |
| org.noear:solon.serialization.fastjson* | 序列化插件，对 `fastjson` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.snack3* | 序列化插件，对 `snack3` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.hession* | 序列化插件，对 `hession` 适配，提供 `hession` 序列化输出 |
| org.noear:solon.serialization.jackson | 序列化插件，对 `jackson` 适配，提供`json`视图输出 或 序列化输出 |
| | |
| 视图插件:: | 说明（可置多个视图插件） |
| org.noear:solon.view.freemarker* | 视图插件，对 `freemarker` 适配，提供`html`视图输出 |
| org.noear:solon.view.jsp | 视图插件，对 `jsp` 适配，提供`html`视图输出 |
| org.noear:solon.view.velocity | 视图插件，对 `velocity` 适配，提供`html`视图输出 |
| org.noear:solon.view.thymeleaf | 视图插件，对 `thymeleaf` 适配，提供`html`视图输出 |
| org.noear:solon.view.beetl | 视图插件，对 `beetl` 适配，提供`html`视图输出 |
| org.noear:solon.view.enjoy | 视图插件，对 `enjoy` 适配，提供`html`视图输出 |
| | |
| 外部框架适配:: | 说明 |
| org.noear:cron4j-solon-plugin | cron4j 适配插件 |
| org.noear:dubbo-solon-plugin | dubbo 适配插件|
| org.noear:mybatis-solon-plugin | mybatis 适配插件|
| org.noear:weed3-solon-plugin | weed3 适配插件|
