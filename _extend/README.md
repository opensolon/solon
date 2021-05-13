
### Nami 插件
| Nami 插件（Solon rpc client） | 说明 |
| --- | --- |
| nami插件:: | 说明 |
| nami.coder.fastjson | 对`fastjson`的编解码适配 |
| nami.coder.hessian | 对`hessian`的编解码适配 |
| nami.coder.snack3 | 对`snack3`的编解码适配 |
| nami.channel.http.okhttp | 对`okhttp`的通道适配 |
| nami.channel.socketd | 对`socketd`的通道适配（适配后，可使用org.noear:solon.sockted.client.* 做为客户端） |

### Solon 插件

| Solon 插件 | 说明 |
| --- | --- |
| boot插件:: | 说明 |
| org.noear:solon.boot.jlhttp* | boot插件,对`jlhttp`适配,提供`http`服务（不自带session state） |
| org.noear:solon.boot.jetty* | boot插件,对`jetty`适配,提供`http`服务（网友@khb提供） |
| org.noear:solon.extend.jetty.jsp | 扩展插件,为`jetty`添加`jsp`支持（不建议使用jsp）（网友@khb提供） |
| org.noear:solon.boot.undertow* | boot插件,对`undertow`适配,提供`http`服务（网友@tyk提供） |
| org.noear:solon.boot.websocket | boot插件,对`java-websocket`适配，提供`websocket`服务 |
| org.noear:solon.extend.undertow.jsp | 扩展插件,为`undertow`添加`jsp`支持（不建议使用jsp）（网友@tyk提供） |
| org.noear:solon.boot.socketd.jdksocket | sockted boot插件,对`jdksocket`适配，提供`socketd`服务 |
| org.noear:solon.boot.socketd.netty | sockted boot插件,对`netty`适配，提供`socketd`服务 |
| org.noear:solon.boot.socketd.rsocket | sockted boot插件,对`rsocket`适配，提供`socketd`服务 |
| org.noear:solon.boot.socketd.smartsocket | sockted boot插件,对`smart-socket`适配，提供`socketd`服务 |
| org.noear:solon.boot.socketd.websocket | sockted boot插件,对`websocket`适配，提供`socketd`服务 |
| | |
| 静态文件支持插件:: | 说明 |
| org.noear:solon.extend.staticfiles | 扩展插件,添加静态文件支持（监视 resources/static 文件夹） |
| | |
| 切面支持插件:: | 说明 |
| org.noear:solon.extend.aspect | 扩展插件,添加Dao、Service注解支持；进而支持事务和缓存注解 |
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
| Data插件:: | 说明 |
| org.noear:solon.extend.data | 扩展插件,添加事件（@Tran）、缓存（@Cache）支持 |
| | |
| Cloud插件:: | 说明 |
| org.noear:solon.cloud | 扩展插件, 添加Solon Cloud 的接口定义及配置规范 |
| | |
| Validation插件:: | 说明 |
| org.noear:solon.extend.validation | 扩展插件,添加验证（@Valid）支持 |
| | |
| Cache插件:: | 说明 |
| org.noear:solon.cache.spymemcached | 扩展插件,完成memcached的缓存服务适配 |
| org.noear:solon.solon.cache.jedis | 扩展插件,完成redis的缓存服务适配 |
| | |
| jsr插件:: | 说明 |
| org.noear:solon.extend.jsr303 | 扩展插件,完成jsr303 bean 验证支持 |
| org.noear:solon.extend.jsr330 | 扩展插件,完成jsr330 组件与注入支持 |
| | |
| 跨域插件:: | 说明 |
| org.noear:solon.extend.cors | 扩展插件,完成web跨域注解支持 |
| | |
| Session插件:: | 说明（可将boot插件的session state服务，自动换掉） |
| org.noear:solon.extend.sessionstate.jwt | 扩展插件,分布式`session`（基于`jwt`构建） |
| org.noear:solon.extend.sessionstate.local | 扩展插件,本地`session` |
| org.noear:solon.extend.sessionstate.redis | 扩展插件,分布式`session`（其于`redis`构建） |
| | |
| 日志插件:: | 说明 |
| org.noear:solon.logging | 扩展插件,添加日志支持 |
| org.noear:solon.logging.impl | 扩展插件,添加Slf4j日志支持 |
| | |
| Socketd 插件:: | 说明 |
| org.noear:solon.sockted | 扩展插件,sockted 协议的编解码、会话等基础支持 |
| org.noear:solon.sockted.client.jdksocket | 扩展插件,sockted 协议的 jdksocket 客户端适配 |
| org.noear:solon.sockted.client.netty | 扩展插件,sockted 协议的 netty 客户端适配 |
| org.noear:solon.sockted.smartsocket | 扩展插件,sockted 协议的 smartsocket 客户端适配 |
| org.noear:solon.sockted.websocket | 扩展插件,sockted 协议的 websocket 客户端适配 |
| | |
| 序列化插件:: | 说明 |
| org.noear:solon.serialization.fastjson* | 序列化插件，对 `fastjson` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.snack3* | 序列化插件，对 `snack3` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.hession* | 序列化插件，对 `hession` 适配，提供 `hession` 序列化输出 |
| org.noear:solon.serialization.jackson | 序列化插件，对 `jackson` 适配，提供`json`视图输出 或 序列化输出 |
| org.noear:solon.serialization.protostuff | 序列化插件，对 `protostuff` 适配，提供`protostuff`视图输出 或 序列化输出 |
| | |
| 视图插件:: | 说明（可置多个视图插件） |
| org.noear:solon.view.freemarker* | 视图插件，对 `freemarker` 适配，提供`html`视图输出 |
| org.noear:solon.view.jsp | 视图插件，对 `jsp` 适配，提供`html`视图输出 |
| org.noear:solon.view.velocity | 视图插件，对 `velocity` 适配，提供`html`视图输出 |
| org.noear:solon.view.thymeleaf | 视图插件，对 `thymeleaf` 适配，提供`html`视图输出 |
| org.noear:solon.view.beetl | 视图插件，对 `beetl` 适配，提供`html`视图输出 |
| org.noear:solon.view.enjoy | 视图插件，对 `enjoy` 适配，提供`html`视图输出 |

