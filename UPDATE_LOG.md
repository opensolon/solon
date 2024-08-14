### 纪年

* v0: 2018 ~ 2019 (2y)
* v1: 2020 ~ 2022 (3y)
* v2: 2023 ~

### v1.x 升到 v2.x 提醒

* 提醒1：之前没有使用弃用接口的，可以直接升级 <br>
* 提醒2：有使用弃用接口的。建议先升级到 1.12.4；替换弃用代码后，再升级到 2.0.0


### 待议
* 新增 solon.boot.vertx 插件?
* 新增 seata-solon-plugin 插件
* 增加 quartz jdbc 及时更新支持（数据库里变了，它马上变） ???
* 调整 solon.config.add 与 solon.config.load 合并，规范格式（同时支持内部与外部） ???
* 调整 使用更多的 slf4j 替换 LogUtil ???
* 调整 取消启动参数简写模式，保持与应用参数相同的名字 ???
* 
* 新增 solon.cloud.gateway 插件?
* 新增 seata-solon-plugin 插件？
* 添加 序列化安全名单接口?
* 优化 拦截体系与 rx 的兼容？

### 2.9.0
* 添加 solon.data 配置节 `solon.dataSources`（用于自动构建数据源）
* 添加 solon.docs 配置节 `solon.docs`（用于自动构建文档摘要）
* 添加 solon.view.prefix 配置项支持 "file:" 前缀（支持体外目录）
* 添加 solon.scheduling.simple SimpleScheduler::isStarted 方法
* 添加 solon `@Condition(onBean, onBeanName)` 条件属性
* 添加 solon.validation ValidUtils 工具类
* 添加 托管类构造参数注入支持 
* 优化 AppContext::beanMake 保持与 beanSacn 相同的类处理
* 优化 solon.serialization.jackson 兼容 @JsonFormat 注解时间格式和时间格式配置并存
* 优化 solon Context::body 的兼容性，避免不可读情况
* 优化 solon 调试模式与 gradle 的兼容性
* 优化 solon.boot FormUrlencodedUtils 预处理把 post 排外
* 优化 solon.web.rx 允许多次渲染输出
* 优化 kafka-solon-cloud-plugin 添加 username, password 简化配置支持
* 优化 solon.boot 413 状态处理
* 优化 solon AppContext 注册和查找时以 rawClz 为主（避免以接口注册时，实例类型查不到）
* 修复 solon.view.thymeleaf 模板不存在时没有输出 500 的问题
* 修复 solon.boot.smarthttp 适配在 chunked 下不能读取 body string 的问题
* 修复 solon-openapi2-knife4j 没有配置时不能启动的问题
* smarthttp 升为 1.5.8
* wood 升为 1.2.15
* socket.d 升为 2.5.11
* zookeeper 升为 3.9.2
* dromara-plugins 升为 0.1.2
* kafka_2.13 升为 3.8.0

* 插件名调整对应表(旧名标为弃用，仍可用)

| 新名                          | 旧名                          | 备注                           |
|-----------------------------|-----------------------------|------------------------------|
| nami-channel-http-hutool    | nami.channel.http.hutool    |                              |
| nami-channel-http-okhttp    | nami.channel.http.okhttp    |                              |
| nami-channel-socketd        | nami.channel.socketd        |                              |
| nami-coder-fastjson         | nami.coder.fastjson         |                              |
| nami-coder-fastjson2        | nami.coder.fastjson2        |                              |
| nami-coder-fury             | nami.coder.fury             |                              |
| nami-coder-hessian          | nami.coder.hessian          |                              |
| nami-coder-jackson          | nami.coder.jackson          |                              |
| nami-coder-protostuff       | nami.coder.protostuff       |                              |
| nami-coder-snack3           | nami.coder.snack3           |                              |
| :: base                     |                             |                              |
| solon-banner                | solon.banner                |                              |
| solon-config-yaml           | solon.config.yaml           |                              |
| solon-config-plus           |                             | 从原 solon.config.yaml 里拆出来    |
| solon-hotplug               | solon.hotplug               |                              |
| solon-i18n                  | solon.i18n                  |                              |
| solon-mvc                   | solon.mvc                   |                              |
| solon-proxy                 | solon.proxy                 |                              |
| solon-validation            | solon.validation            |                              |
| :: boot                     |                             |                              |
| solon-boot-jdkhttp          | solon.boot.jdkhttp          |                              |
| solon-boot-jetty-add-jsp    | solon.boot.jetty.add.jsp    |                              |
| solon-boot-jetty            | solon.boot.jetty            |                              |
| solon-boot-jlhttp           | solon.boot.jlhttp           |                              |
| solon-boot-smarthttp        | solon.boot.smarthttp        |                              |
| solon-boot-socketd          | solon.boot.socketd          |                              |
| solon-boot-undertow-add-jsp | solon.boot.undertow.add.jsp |                              |
| solon-boot-undertow         | solon.boot.undertow         |                              |
| solon-boot-vertx            | solon.boot.vertx            |                              |
| solon-boot-websocket-netty  | solon.boot.websocket.netty  |                              |
| solon-boot-websocket        | solon.boot.websocket        |                              |
| solon-boot                  | solon.boot                  |                              |
| :: cloud                    |                             |                              |
| solon-cloud-eventplus       | solon.cloud.eventplus       |                              |
| solon-cloud-httputils       | solon.cloud.httputils       | 想办法基于 solon.net.httputils 统一 |
| solon-cloud-metrics         | solon.cloud.metrics         |                              |
| solon-cloud-tracing         | solon.cloud.tracing         |                              |
| solon-cloud                 | solon.cloud                 |                              |
| :: data                     |                             |                              |
| solon-cache-caffeine        | solon.cache.caffeine        |                              |
| solon-cache-jedis           | solon.cache.jedis           |                              |
| solon-cache-redisson        | solon.cache.redisson        |                              |
| solon-cache-spymemcached    | solon.cache.spymemcached    |                              |
| solon-data-dynamicds        | solon.data.dynamicds        |                              |
| solon-data-shardingds       | solon.data.shardingds       |                              |
| solon-data                  | solon.data                  |                              |
| :: detector                 |                             |                              |
| solon-health-detector       | solon.health.detector       |                              |
| solon-health                | solon.health                |                              |
| :: docs                     |                             |                              |
| solon-docs-openapi2         | solon.docs.openapi2         |                              |
| solon-docs-openapi3         | solon.docs.openapi3         |                              |
| solon-docs                  | solon.docs                  |                              |
| :: faas                     |                             |                              |
| solon-luffy                 | solon.luffy                 |                              |
| :: logging                  |                             |                              |
| solon-logging-log4j2        | solon.logging.log4j2        |                              |
| solon-logging-logback       | solon.logging.logback       |                              |
| solon-logging-simple        | solon.logging.simple        |                              |
| solon-logging               | solon.logging               |                              |
| :: native                   |                             |                              |
| solon.aot                   | solon-aot                   |                              |
| ::net                       |                             |                              |
| solon-net-httputils         | solon.net.httputils         |                              |
| solon-net-stomp             | solon.net.stomp             |                              |
| solon-net                   | solon.net                   |                              |
| :: scheduling               |                             |                              |
|                             | solon.extend.schedule       | 弃用                           |
| solon-scheduling-quartz     | solon.scheduling.quartz     |                           |
| solon-scheduling-simple     | solon.scheduling.simple     |                              |
| solon-scheduling            | solon.scheduling            |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              ||                             |                             |                                   |
|                             |                             |                              |




### 2.8.6
* 添加 solon Context::outputStreamAsGzip 方法（方便手动 gzip 输出）
* 添加 solon `@Bean(initMethod, destroyMethod)` 配置属性
* 添加 solon ActionLoaderDefault:postActionPath 方法（方便重写）
* 添加 solon.boot `server.request.useRawpath` 配置
* 添加 solon.boot http-server 同端口的 ws 信号注册
* 优化 solon.boot 当有 `server.?.name` 配置时才注册 ws,sd 信号
* 优化 solon MvcFactory:resolveParam 更名为 resolveActionParam（旧名，标为弃用）
* 优化 solon DownloadedFile 取消空构造函数，避免内容为 null 的情况
* 优化 solon UploadedFile::getContentSize 避免可能为 null 的情况
* 优化 solon.proxy AOT 代理增加 接口 default 方法代理支持
* 优化 solon.proxy ASM 代理增加 接口 default 方法代理支持
* 优化 solon.net SubProtocolCapable:getSubProtocols 设计，增加请求子协义校验的支持
* 优化 solon 停止结束语打印
* 修复 solon.sessionstate.local LocalSessionState::sessionKeys 数据获取错误
* 修复 solon.serialization.jackson 的 boolAsInt 配置 对小写 bool 无效的问题
* 修复 solon.serialization.fastjson2 的 boolAsInt 配置无效的问题
* 修复 solon.config.yaml 当配置 key 为数字时会出错的问题
* fastjson2 升为 2.0.52
* snack3 升为 3.2.107
* wood 升为 1.2.13
* socket.d 升为 2.5.10
* folkmq 升为 1.7.6
* smartsocket 升为 1.5.50
* smarthttp 升为 1.5.5

### 2.8.5
* 添加 solon.cache.redisson RedissonClientOriginalSupplier 类
* 添加 solon MethodKey 类
* 添加 solon ClassUtil:resolveClassLoader 方法
* 添加 solon Router::getBy(controllerClz) 方法，用于管理需求
* 添加 solon ChainManager::addExecuteHandler(e,index) 方法，支持执行器排序
* 添加 solon RouterInterceptor::postArguments 提交参数确认（更方便 mvc 参数定制）
* 添加 solon UploadedFile::getContentAsBytes 方法
* 添加 solon DownloadedFile 动态 304 的支持
* 添加 solon Component:registered，控制形态注册
* 添加 solon.data 序列化新实例 JsonSerializer.typedInstance
* 添加 captcha-solon-plugin 图形验证码接口注释,之后使用smart-doc会拥有更完整的文档内容
* 优化 solon `solon.config.load` 支持 `classpath:` 前缀
* 优化 solon.data 的 JsonSerializer,JavabinSerializer 类加载器处理
* 优化 solon.sessionstate.jedis 的 JsonSerializer,JavabinSerializer 类加载器处理
* 优化 solon Serializer 接口定义，更好支持泛型
* 优化 solon CacheService 接口定义，更好支持泛型
* 优化 mybatis-plus-extension-solon-plugin 增加 `@Db IService` 注入支持
* 优化 mybatis-solon-plugin 没有 mappers 时的提示
* 修复 solon.logging forward 时会清掉 mdc 的问题
* 修复 solon 一个注解同时用 beanInterceptorAdd 和 beanExtractorAdd 时 代理失效的问题
* redisx 升为 1.6.5
* snack3 升为 3.2.104
* wood 升为 1.2.12
* socket.d 升为 2.5.9
* folkmq 升为 1.7.4
* water 升为 2.13.3
* nacos2 升为 2.3.2（更好兼容 GraalVM Native）
* smartsocket 升为 1.5.46
* smarthttp 升为 1.5.2

### 2.8.4
* 新增 solon.net.httputils 插件
* 添加 BeanWarp::rawClz，优化 BeanWrapLifecycle 的检测方式（避免 LifecycleBean 重复注册）
* 添加 CloudBreakerException 异常类，用于 CloudBreakerInterceptor
* 添加 CloudStatusException 异常类，用于 Cloud 触发的 4xx 状态异常
* 添加 Router 移除控制器的接口
* 添加 openapi2 支持 action 返回接口类型
* 简化 Router 注册控制器的方式
* 优化 solon.boot.websocket 适配，在重启时端口不能立即复用的问题
* 优化 List[Bean] 注入，支持排序
* 优化 AppContext::beanShapeRegister 改为实例检测（之前为类型检测）
* 调整 429 状态改由 CloudBreakerException 发出（访问量过大时）
* 调整 415 状态改由 StatusException 发出（Consumes 不匹配）
* 调整 用 solon.net.httputils 替换 solon.test 里的 HttpUtils（统一代码）
* 调整 `@Consumes` `@Produces` 作用域，支持加在类上
* 调整 solon-rpc 快捷包去掉 hessian 依赖（如有需要手动引入）
* 调整 CloudBreakerInterceptor 融断时改为 CloudBreakerException（之前为直接设定 429 状态），更方便过滤和拦截
* 修复 `@Consumes` `@Produces` 在 solon.docs 里无效的问题
* fury 升为 0.5.1
* liteflow 升为 2.12.1
* socket.d 升为 2.5.7
* folkmq 升为 1.7.2
* mybatis-flex 升为 1.9.3
* smartsocket 升为 1.5.44
* smarthttp 升为 1.4.3
* undertow 升为  2.2.32.Final

### 2.8.3
* 新增 thrift-solon-cloud-plugin 插件
* 新增 solon.serialization.jackson.xml 插件
* 添加 `@Destroy` 注解（与 `@Init` 呼应）
* 添加 Serializer 接口，统一多处模块的序列化定义
* 添加 BytesSerializerRender 类，对应 StringSerializerRender
* 添加 solon.net.stomp ToStompWebSocketListener 适配 WebSocket 子协议验证
* 添加 solon.net ToSocketdWebSocketListener 适配 WebSocket 子协议验证
* 添加 graphql-solon-plugin GraphqlWebsocket 适配 WebSocket 子协议验证
* 添加 WebSocket 子协议校验支持（smarthttp,jetty,undertow,java-websocket,netty-websocket）
* 添加 应用配置键名二次引用支持
* 添加 folkmq 适配 EventLevel.instance 订阅支持
* 添加 rocketmq5 适配 EventLevel.instance 订阅支持
* 添加 solon.boot.socketd 对 ssl 配置的支持
* 添加 beetl 适配自定义 Tag 注入支持
* 添加 enjoy 适配自定义 Tag 注入支持
* 添加 StatusException 异常类型
* 调整 AuthException 改为扩展自 StatusException（之前为 SolonException）
* 调整 ValidatorException 改为扩展自 StatusException（之前为 SolonException）
* 调整 Action 参数解析异常类型为 StatusException（之前为 IllegalArgumentException）
* 调整 solon.test 默认为 junit5 并简化 SolonTest 体验（不用加 ExtendWith 了），需要 junit4 的需引入 solon-test-junit4
* 优化 CloudClient.event().newTranAndJoin() 增加 inTrans 判断
* 优化 mybatis-solon-plugin 在有 mapper 配置，但无 mapper 注册时的异常提示（原为 warn 日志提示）
* 优化 RouteSelectorExpress 的路由顺序（常量的，优于变量的）
* 优化 kafka 适配的 ack 处理
* 修复 IndexUtil:buildGatherIndex 处理字段时，会出错的问题
* snack3 升为 3.2.100
* fastjson2 升为 2.0.51
* socket.d 升为 2.5.3
* folkmq 升为 1.5.2
* wood 升为 1.2.11
* sqltoy 升为 5.6.10.jre8
* mybatis-flex 升为 1.9.1
* smarthttp 升为 1.4.2
* okhttp 升为 4.12.0
* xxl-job 升为 2.4.1
* graphql 升为 18.3

### 2.8.0
* 添加 `@Bean::injected`
* 添加 `TmplUtil` 工具类（提供简单模板支持）
* 添加 `RoutingDataSource` 对事务管理的支持（即动态数据源支持事务管理）
* 添加 Solon Cloud Event 消息事务支持！
* 优化 Bean 集合注入时，乎略泛型
* 优化 smarthttp 和 jetty 适配，queryString 和 from-data 同时有数据时的处理
* 优化 jetty 适配的临时目录处理
* 优化 solon.serialization.properties 处理，扩大范围
* 优化 onMissingBean 条件与 Bean 集合注入的边界问题！
* 优化 sa-token 的 json 序列化处理
* 优化 配置加载同步，带 - 的不同步到 System
* snack3 升为 3.2.97
* socket.d 升为 2.4.16
* folkmq 升为 1.4.4
* fastjson2 升为 2.0.50
* mybatis-flex 升为 1.8.9
* sqltoy 升为 5.6.5.jre8
* sa-token 升为 1.38.0
* kafka 升为 3.7.0
* water 升为 2.13.2
* luffy 升为 1.7.5

### 2.7.6
* 新增 solon-docs-openapi2-javadoc 插件
* 新增 solon.serialization.properties 插件（支持 ?a.b=1 prop 风格的参数）
* 新增 solon.net.stomp 插件
* 添加 solon.boot.jetty 原生编译支持
* 添加 solon.boot.undertow 原生编译支持
* 添加 solon 对 `classpath*:` 表达式支持（兼容旧的习惯）
* 添加 solon Utils:pid 方法（方便获取进程号）
* 添加 solon.data TranUtils:getDataSourceProxy 方法
* 添加 solon.cloud CloudProps:getNamespace 方法，允许每个中间件有自己 namespace 配置
* 添加 solon.net WebSocketListener::onPing,onPong 方法，允许获得 ping/pong 的监听
* 添加 solon.net WebSocketListenerSupplier 接口，简化 ws 扩展适配
* 添加 solon.validation 校验出错的结果里带上当前校验的名称（该名称是当前校验的参数名称或者是实体对象字段名称）
* 添加 nami json 解码器，对普通 string 的支持（如果返回类型为 string ，则解码失败时以普通 string 返回）
* 添加 "solon.config.load" 对资源表达式的支持（例："config/*.yml"）
* 添加 solon.boot.smarthttp 对参数 key 的 urlDecode 处理
* 优化 solon.net ToSocketdWebSocketListener 防止 websocket 恶意空连
* 优化 非 solon 测试环境下，日志打印添加 pid 显示支持
* 优化 ResourceUtil 扫描类或资源的能力，增加支持 `*Mapper` 表达式
* 优化 Aot 时的函数注册处理（修复 原生运行时组件代理失败的问题）
* 优化 Bean 集合的注入处理
* 优化 StaticResourceHandler 对静态资源的压缩支持，改为先查找压缩文件
* 修复 solon.boot.undertow 的 ws 适配在 window 下异常关闭处理时，无法触发 onClose 事件的问题
* 修复 solon.boot.websocket.netty 接收二进制码时出错的问题
* 修复 nami 在请求参数为空时被转换成GET操作的问题
* 修复 solon.docs.openapi2 没有同步 operation.security 的问题。
* 调整 solon.net websocket 没有路由记录时，将自动关闭（更安全）
* 调整 solon Condition::onMissingBean 标为弃用
* redisx 升为 1.6.3
* fastjson2 升为 2.0.49
* wood 升为 1.2.9
* mybatis-flex 升为 1.8.8
* snack3 升为 3.2.95
* socket.d 升为 2.4.14

### 2.7.5
* 新增 solon.web.rx 插件
* 添加 SolonException 异常基类
* 添加 solon.view 模板渲染器指定视图前缀的构造函数
* 添加 solon.view 模板渲染器的容器注册（提供容器扩展方式）
* 添加 solon.data TranUtils:getConnectionProxy 接口，方便不同的事务对接
* 添加 solon.scheduling 对多个 JobInterceptor 及排序支持
* 添加 solon.cloud 对多个 CloudJobInterceptor 及排序支持
* 添加 solon.validation `@Size` 验证注解支持参数
* 添加 AppContext::removeWrap 接口
* 添加 folkmq-solon-cloud-plugin 对 event:key 支持
* 添加 大写开头的环境变量注入支持
* 调整 bean 的基类注册的排除条件改为“java.”开头（之前为包含）
* 修复 solon.scheduling.simple 在使用单点 cron 表过式时，会出现 npe 的问题
* socket.d 升为 2.4.10
* folkmq 升为 1.4.2
* snack3 升为 3.2.92
* jackson 升为 2.17.0
* fastjson2 升为 2.0.48
* mybatis-flex 升为 1.8.7
* beetlsql 升为 3.30.4-RELEASE
* netty 升为 4.1.107.Final
* vertx 升为 4.5.6
* hutool 升为 5.8.27
* smartsocket 升为 1.5.43
* smarthttp 升为 1.4.1
* aws-java-sdk-s3 升为 1.12.695
* grpc 升为 1.62.2
* thrift 升为 0.20.0
* dubbo3 升为 3.2.11
* freemarker 升为 2.3.32
* beetl 升为 3.16.0.RELEASE
* thymeleaf 升为 3.1.2.RELEASE
* log4j 升为 2.23.1
* slf4j 升为 2.0.12

### 2.7.3
* 增加 java 22 支持
* 添加 solon.threads.virtual.enabled 配置支持
* 添加 solon.cloud.httputils 异步处理接口
* 添加 NamiClient::localFirst 属性，增加本地实现组件优先支持
* 添加 Context::filesDelete 批量删除临时文件方法
* 优化 solon.view 后端模板的编码配置处理
* 优化 非 http/https 协议包的处理（被人刷假包）
* 优化 local-solon-cloud-plugin 本地服务发现的查找处理，避免失败
* 优化 部分锁的处理方式（synchronized 改为 ReentrantLock）
* 优化 jetty 的 multipart 解析处理
* 修复 solon.health HealthIndicator 自动注册无效的问题
* socket.d 升为 2.4.7
* folkmq 升为 1.3.2
* snack3 升为 3.2.90
* sqltoy 升为 5.6.2.jre8
* asm 升为 9.6
* nacos1 升为 1.4.7
* nacos2 升为 2.3.1
* redisson 升为 3.27.2
* undertow 升为 2.2.31.Final

### 2.7.2
* 添加 公共锁 Utils.locker() 给初始化场景使用
* 添加 scheduling 调度任务拦截机制 JobInterceptor
* 添加 UploadedFile:delete 尝试删除临时文件方法
* 添加 server.request.useTempfile 新的配置支持
* 添加 solon.boot.jdkhttp 上传文件缓存的可选功能
* 添加 solon.boot.jlhttp 上传文件缓存的可选功能
* 添加 solon.boot.smarthttp 上传文件缓存的可选功能
* 添加 solon.boot.jetty 上传文件缓存的可选功能
* 添加 日志打印默认格式显示进程号
* 调整 框架内的 ThreadLocal 默认为非继承，通过 FactoryManager 可定制
* 调整 jlhttp 源码独立为 jlhttp 仓库再转依赖引用
* 调整 cron 源码独立为 java-cron 仓库再转依赖引用
* 调整 smarthttp 的内部日志级为 WARN
* 优化 部分锁的处理方式（synchronized 改为 ReentrantLock）
* 优化 solon.boot.smarthttp 适配在空跑时的内存情况
* 优化 solon.boot.smarthttp 的 websocket 闲置超时处理
* 优化 FactoryManager.newThreadLocal 接口设计，方便定制扩展
* 修复 knife4j 适配未鉴权时在 undowtow + 非本地IP情况下 500 的问题
* redisson 调为 3.23.5
* shardingsphere 调为 5.3.2
* socket.d 升为 2.4.6
* folkmq 升为 1.3.1
* sqltoy 升为 5.2.98
* beetlsql 升为 3.30.1-RELEASE
* mybatis-flex 升为 1.8.2
* fastjson2 升为 2.0.47
* smarthttp 升为 1.4.0

### 2.7.1
* 添加 solon LifecycleBean:prestop 生命节点，方便用户做安全停止
* 添加 solon.scheduling IJobManager:jobGetAll 接口
* 添加 folkmq-solon-cloud-plugin 新的 rpc 与 tran 能力适配
* 调整 solon.boot.jetty 适配的静态资源处理
* 优化 solon 外部资源文件的相对位置加载在不同 linux 的兼容性
* 优化 solon 安全停止打印信息
* socket.d 升为 2.4.4
* folkmq 升为 1.2.2
* jetty 升为 9.4.54.v20240208
* undertow 升为 2.2.30.Final
* smarthttp 升为 1.3.9
* smartsocket 升为 1.5.42
* sqltoy 升为 5.2.95
* mybatis-flex 升为 1.7.9
* beetlsql 升为 3.30.0-RELEASE
* activerecord 升为 5.1.3
* shardingsphere 升为 5.4.1
* jetcd 升为 0.7.7
* xxl-job 升为 2.4.0
* thymeleaf 升为 3.0.15.RELEASE
* enjoy 升为 5.1.3
* beetl 升为 3.15.14.RELEASE
* forest 升为 1.5.36

### 2.7.0
* 调整 内核的 mvc 能力实现，独立为 solon.core.mvc 包（为之后拆分作准备）
* 新增 solon.view.jsp.jakarta 插件
* 新增 solon.scheduling 插件对 command 调度的支持（即由命令行参数调度任务）
* 添加 undertow jsp tld 对 templates 目录支持（简化 tld 的使用）
* 添加 jetty jsp tld 对 templates 目录支持（简化 tld 的使用）
* 添加 SocketdProxy 对 socket.d 集群的支持
* 添加 @Addition 注解（用于间接附加注解）
* 添加 相对应用目录的文件获取接口
* 调整 Plugin组件和动态组件注解的弃用提醒级别为 error
* 调整 外部资源文件加载，保持与应用目录的相对位置（不因 user.dir 而变）
* 调整 @Get, @Options 注解到类上时的限定效果，保持与方法上一样（原增量效果 @Addition 注解替代）
* 解除 WEB-INF 的目录依赖，早期是为了支持 jsp tld 文件的自动处理（仍然兼容）
* 修复 QuartzSchedulerProxy::remove 失效的问题（之后调错方法了）
* socket.d 升为 2.4.0
* folkmq 升为 1.1.0
* sqltoy 升为 5.2.93
* mybatis-flex 升为 1.7.8
* dbvisitor 升为 5.4.1
* fastjson2 升为 2.0.46

### 2.6.6
* 添加 ToSocketdWebSocketListener::setListener 接口（更方便定制）
* 添加 ToHandlerListener 对异常反馈的支持
* 添加 WebSocket 支持 war(javax) 部署
* 添加 WebSocket 支持 war(jakarta) 部署
* 添加 OffsetDateTime 请求注入支持
* 添加 solon.boot.undertow 打印 http2 的启用情况
* 添加 solonee.licence 配置支持
* 添加 JacksonActionExecutor 支持设定全新 ObjectMapper
* 添加 smarthttp 适配对 websocket header 的处理
* 调整 enableMd5key 返回（支持链式返回自己）
* 调整 路由拦截器匹配规则采用pathNew方法（原为path）
* 调整 ToHandlerListener 基类改为 EventListener（更方便定制）
* 修复 WebSocket 不能获取 queryString 单字母参数问题
* liteflow 升为 2.11.4.2
* fastjson2 升为 2.0.44
* snack3 升为 3.2.88
* bean-searcher 升为 4.2.7
* sqltoy 升为 5.2.91
* beetlsql 升为 3.29.0-RELEASE
* reactor-netty-http 升为 1.1.15
* reactor-core 升为 3.6.2
* socket.d 升为 2.3.7
* folkmq 升为 1.0.30
* sms4j 升为 3.1.1
* smarthttp 升为 1.3.8

### 2.6.5
* 修复 solon.auth 验证路径被强制转小写的问题
* 添加 war 部署时，multipartConfig 自动配置并与 app.yml 对接
* 添加 thymeleaf 适配对 `@{}` 语法的支持
* 添加 RedisCacheService,RedissonCacheService,MemCacheService::enableMd5key (默认为 true)
* 添加 solon.serialization 对 ZonedDateTime 适配支持
* dubbo3 升为 3.2.10
* hutool 升为 5.8.25
* redisson 升为 3.24.3
* lettuce 升为 6.2.7.RELEASE
* fury 升为 0.4.1
* fastjson2 升为 2.0.45
* slf4j 升为 2.0.11
* knife4j 升为 4.5.0
* forest 升为 1.5.35
* mybatis 升为 3.5.15
* mybatis-flex 升为 1.7.7
* beetlsql 升为 3.27.5-RELEASE
* bean-searcher 升为 4.2.6
* snack3 升为 3.2.87
* folkmq 升为 1.0.28
* socket.d 升为 2.3.4

### 2.6.4
* 新增 graphql-solon-plugin 插件（欢迎试用）
* 修复 @Header 与 @Body 同时注入时，@Header 会失效的问题
* 修复 LocalCacheService 时间过大时会超界的问题
* 添加 local-solon-cloud-plugin 对描述信息的获取
* fastjson2 升为 2.0.44
* log4j 升为 2.22.1
* logback 升为 1.3.14
* sqltoy 升为 5.2.88
* mybatis-flex 升为 1.7.6
* beetlsql 升为 3.27.4-RELEASE
* fastmybatis 升为 2.10.0
* socket.d 升为 2.2.2
* folkmq 升为 1.0.26

### 2.6.3
* 发布 Solon FaaS
* 修复 solon.luffy 插件 JtFunctionLoaderClasspath 可能会出现 null 异常的问题
* 修复 solon.luffy 插件 XFun.callFile 的执行兼容性（添加 file_id 赋值）
* 修复 Solon.cfg().stopSafe() 自动配置失效的问题
* 修复 war 部署时 contextPath  自动识别失效的问题
* 调整 contextPath 配置，支持 '!' 开头（表示强制模式，即不再支持旧的地址请求）
* 调整 solon.net 把 socketd 包改为 provided
* 调整 Utils::mime 添加默认处理
* 添加 ConvertUtil 对 Byte 类型的默认解析
* smart-http 升为 1.3.6
* luffy 升为 1.7.2
* folkmq 升为 1.0.22
* socket.d 升为 2.1.14
* sqltoy 升为 5.2.86
* beetl 升为 3.15.12.RELEASE
* beetlsql 升为 3.27.3-RELEASE
* netty 升为 4.1.101.Final

### 2.6.2
* 修复 solon.logging.logback 对 solon.logging.appender.console.enable 配置无效的问题
* 修复 maven 打包插件，出现 “Unable to rename XXX” 错误
* 修复 jetty 适配，添加多个 http 端口时会受 ssl 配置影响
* 修复 swagger 适配 `@Body` 后 path 参数不能显示的问题
* 调整 solon.data nested 事务微略处理
* socket.d 升为 2.1.7
* folkmq 升为 1.0.18
* nacos2 升为 2.3.0
* sqltoy 升为 5.2.85

### 2.6.1
* 新增 folkmq-solon-cloud-plugin 插件
* 新增 solon.luffy 插件
* 添加 solon.docs 全局参数支持
* 添加 solon.logging 配置提示文件
* 添加 rabbitmq-solon-cloud-plugin 消息并发处理机制
* 添加 RunUtil::asyncAndTry 接口
* 添加 WebSocket::id, attrHas 接口
* 添加 WebSocket::setIdleTimeout 接口
* 添加 NamiClient::name 支持配置表达式
* 调整 solon.auth 取消 AuthUtil 验证路径、权限、角色与登录绑死。由用户适配的处理器决定
* 调整 scheduledPoolSize 默认值多一倍
* 调整 solon.scheduling.simple 过滤中断异常
* 调整 solon.logging 注册日志打印，取消不必要的打印
* 调整 RouterWebSocketListener 更名为 PathWebSocketListener
* 调整 http-server 的 idleTimeout 策略
* 修复 Snack3 不能反序列化 SaSession 的问题
* 修复 solon.logging.config 的配置文件不存在时会异常的问题
* 修复 solon.boot.smarthttp 获取 queryString 会中文乱码的问题
* 移除 `@Dao`,`@Service`,`@Repository` 三个注解（弃用很久了，容易带来误解）
* snack3 升为 3.2.84
* socket.d 升为 2.0.22
* wood 升为 1.2.6
* mysql-flex 升为 1.7.5
* sqltoy 升为 5.2.82
* beetlsql 升为 3.27.2-RELEASE
* smartsocket 升为 1.5.38
* undertow 升为 2.2.28.Final
* netty 升为 4.1.101.Final
* wx-java 升为 4.6.0

### 2.6.0（2023-11-15）
* 设定 smart-http 为 solon-api 快捷组合包的默认 http-server
* 重构 socketd 适配，升为 v2.0
* 重构 websocket 适配，升为 v2.0
* 新增 solon.net 模块用于定义网络接口，分离 websocket 与 socketd 的接口（分开后，用户层面更清爽）
* 新增 solon.boot.socketd 插件
* 新增 sa-token-dao-redisson-jackson 插件
* 添加 SolonApp::filterIfAbsent,routerInterceptorIfAbsent 接口
* 添加 AppContext::getBeansMapOfType 接口
* 添加 websocket context-path 过滤处理机制
* 添加 `@Cache` 缓存注解处理对动态开关的支持（之前，只能在启动时决定）
* 添加 `@Tran` 事务注解处理对动态开关的支持（之前，只能在启动时决定）
* 添加 solon.boot.smarthttp 外部优先级处理（成为默认后，要方便外部替换它）
* 调整 smart-http,jetty,undertow 统一使用 server.http.idleTimeout 配置
* 调整 `@ProxyComponent` 弃用提示为直接提示（之前为 debug 模式下）
* 调整 websocket server 的地址打印
* 移除 AopContext（完成更名 AppContext 的第二步动作）
* 移除 PathLimiter （已无用，留着有误导性）
* 移除 SolonApp::enableWebSocketD,enableWebSocketMvc,enableSocketMvc（已无用，留着有误导性）
* 优化 http context-path 过滤器处理机制
* 优化 solon.test 的 `@Rollback` 注解处理，支持 web 的事务控制
* 优化 solon.scheduling.simple 保持与 jdk 调度服务的策略一致
* 删除 socketd v1.0 相关的 10 多个插件（v2.0 独立仓库）
* jackson 升为 2.15.2
* pagehelper 升为 5.3.3
* liteflow 升为 2.11.3
* activemq 升为 5.16.7
* redisx 升为 1.6.2
* minio8 升为 8.5.3
* sqltoy 升为 5.2.81
* fastjson2 升为 2.0.42
* luffy 升为 1.7.0
* water 升为 2.12.0



### 2.5.12
* 调整 solon.view 插件，默认添加请求上下文 context 对象
* 调整 SnackActionExecutor 默认关闭 className 读取
* 调整 事务监听器 afterCommit 时，转为无事务状态（移除事务状态）
* 优化 SolonApp 构造时日志处理（避免失去样式的可能）
* 优化 容器停止时把 Closeable 接口的 bean 也关掉
* 优化 solon.data 插件 afterCommit 事件之前移除事务状态
* 优化 属性引用表达式，当环境变量无时马上使用默认值（环境变量是启动时决定的，有或无固定了）
* 优化 mqtt-solon-cloud-plugin 重构代码，增加异步发布与并发消费模式（并改为异步接口）
* 优化 mqtt5-solon-cloud-plugin 重构代码，增加异步发布与并发消费模式（并改为异步接口）
* 增加 solon.aot 支持使用命令行设置 构建 native image args
* 增加 Utils::isProxyClass 接口
* 修复 solon.boot.smarthttp 可能会两次触发 close 的问题
* snack3 升为 3.2.82
* sqltoy 升为 5.2.75
* mybatis-flex 升为 1.7.3
* activemq 升为 5.15.12
* sureness 升为 1.0.8
* guava 升为 32.1.3-jre
* pulsar 升为 2.11.2
* beetlsql 升为 3.26.1-RELEASE
* smarthttp 升为 1.3.5

### 2.5.11
* 新增 nami.coder.fury 插件
* 新增 solon.serialization.fury 插件
* 新增 hibernate-solon-plugin 插件（提供标准 jpa 支持）
* 修复 @Import::classes 未被执行的问题
* 修复 socketd.session 关闭时，自动心跳不能停止的问题
* 修复 solon.logging.logback 控制台等级 yml 配置失效的问题
* 优化 Solon::cfg() 多配置加载的校验机制
* 优化 solon.logging.logback 原生编译元信息配置
* 优化 运行时退出勾子的处理，非 aot 情况下强制退出
* 调整 socketd.session::sendHeartbeatAuto 更名为 startHeartbeatAuto （内部接口）
* 调整 @Init 的弃用提示（改为推荐了）
* 添加 @Import::profiles, @Import::profilesIfAbsent 接口
* 添加 DynamicDsKey 类, 替代 DynamicDsHolder
* 添加 socketd.session::stopHeartbeatAuto 接口
* snack3 升为 3.2.81
* liteflow 升为 2.11.2
* sqltoy 升为 5.2.73
* mybatis-flex 升为 1.7.2
* sa-token 升为 1.37.0
* redisx 升为 1.6.1
* smarthttp 升为 1.3.4
* beetlsql 升为 3.26.0-RELEASE
* beetl 升为 3.15.10.RELEASE
* fastjson2 升为 2.0.41
* rabbitmq 升为 5.19.0
* dubbo3 升为 3.2.7
* rocketmq4 升为 2.9.7
* kafka_2.13 升为 3.6.0
* sms4j 升为 3.0.2
* dromara-plugins 升为 0.1.1

### 2.5.10
* 新增 ConverterFactory 接口
* 恢复 @Mapping 函数旧版兼容，改为告警（以后去掉非公有支持）
* 增加 @Mapping 函数非公有告警提醒
* 弃用 SolonBuilder

### 2.5.9
* 修订 CONTRIBUTING.md
* 新增 事务管理的 TranListener 机制支持!!!
* 新增 Mapping 函数对 TypeVariable 参数类型的识别支持!!!
* 新增 Mapping 函数父类继承的支持（仅限 public）!!!
* 新增 FactoryManager 工具类，合并各种工厂管理，替代旧的 Bridge
* 弃用 InitializingBean（简化应用生命周期）
* 删除 SolonApp::onError,::enableErrorAutoprint 接口（已无用，留着有误导性）
* 删除 LogUtil::globalSet 接口（已无用，留着有误导性）
* 调整 Get,Put,Post,Delete,Patch 注解，取消 ElementType.TYPE 目标
* 调整 solon.logging 的 MDC.clear() 时机（安排到最外层）
* 调整 数据源事务管理相关改为线程状态可继承（支持通过 FactoryManager 设置 threadLocalFactory）
* 调整 所有模块的单测都升级为 junit5（落实最新的 CONTRIBUTING 规范）
* 调整 water-solon-cloud-plugin 本地调试时服务注册改为被动检测（之前为主动上报）
* 调整 HttpServerConfigure::enableSsl 允许自己设定 SSLContext（方便国密处理）
* 增加 动态数据源注解 `@DynamicDs("${ddsName}")` 参数模板支持
* 增加 Context::remotePort 接口
* 增加 Context::remoteIp 接口，原 ip 接口标为弃用
* 增加 `@Init` 函数对 AOP 的支持（有时候初始化也要用事务注解之类的）
* 增加 配置提示元文件???
* 修复 solon.socketd.client.websocket 自动重连失效的问题
* sqltoy 升为 5.2.69
* mybatis-flex 升为 1.7.0

### 2.5.8
* 取消 全局未处理异常走总线的机制，转由 Log 框架接收（简化用户体验）

### 2.5.7

* 增加 Context::headerOfResponse 接口
* 增加 http server gzip 整体配置支持
* 增加 solon.web.staticfiles 对 gzip 的配置支持
* 增加 solon.boot.jdkhttp 对 gzip 的配置支持（取消内部的自动处理）
* 调整 `@Import` 替代 `@PropertySource`、`@TestPropertySource`，后者标为弃用
* 调整 `@Rollback` 替代 `@TestRollback`，后者标为弃用
* 调整 SolonTestApp 默认关闭 http 服务；避免与已启动服务端口冲突
* 调整 solon.cache.jedis 的两个序列化实现，转到 solon.data（做为公用）
* 调整 solon.cache.redission RedissonCacheService 增加外部序列化接口支持
* 调整 `@Bean` 函数，参数没带注解的算必须
* 调整 paramsMap 增加 autoMultipart 处理
* 调整 nami,forest,feign 负载均衡的获取方式
* 修复 solon-maven-plugin 在 linux 下因为一些用户角色没有权限导致打包失败问题
* 优化 `VarGather` 检查增加自动排序
* 优化 Props::getMap 处理
* 简化 Props::getProp 处理提升性能
* wood 升为 1.2.2
* mybatis-flex 升为 1.6.8
* fastmybatis 升为 2.9.7
* easy-trans 升为 1.3.0
* sa-token 升为 1.36.0
* fastjson2 升为 2.4.0
* jetty 升为 9.4.52.v20230823
* undertow 升为 2.2.26.Final
* redisx 升为 1.6.0

### 2.5.6
* asm 升为 9.5 （for JDK21）

### 2.5.5
* 完成 JDK21 编译测试，功能单元测试
* 添加 HttpServerConfigure::setExecutor 接口，用于支持虚拟线程池（for JDK21）
* 添加 PropUtil 类。把原来的属性表达式与模板解析独立出来
* 添加 ContextPathListener 类，用于控制 contentPath 对 ws,tpc 的影响
* 添加 ContextPathFilter 一个简化的构造函数
* 添加 MethodHolder::getDeclaringClz、getDeclaringClzAnnotation 接口
* 添加 yaml 多片段支持（即一文件多环境支持）
* 添加 多配置文件交差引用变量支持
* 添加 DownloadedFile(file,name) 构造函数
* 添加 Router 对 405 的支持
* 调整 RunUtil 执行器分离为 parallelExecutor + asyncExecutor（for JDK21）
* 调整 CacheService 接口（增加类型化 get）
* 调整 SessionState 接口（增加类型化 get）
* 调整 Context::session 接口（增加类型化 get）
* 调整 BeanWrap 的 rawSet 改为公有
* 调整 SolonApp::enableWebSocketMvc, enableSocketMvc, 默认为 false
* 调整 SolonApp::enableWebSocket, enableWebSocketD 分离设置，各不相关（前者代表通讯，后者代表协议）
* 调整 CloudConfig 复用主框架的属性表达式与模板解析
* 调整 Listener 去掉 @FunctionalInterface，所有方法标为 default
* 调整 ChainManager::postResult 的执行策略改为包围式（相当于倒序）
* 调整 ValHolder 标为弃用
* 调整 jdkhttp,jlhttp,smarthttp 的 contentLength 适配处理
* 调整 使用更多的 slf4j 替换 bus
* 调整 mybatis-solon-plugin 的会话提交方式，修复二级缓存控制可能失效的问题
* 调整 "solon.config.load" 支持按顺序加载
* 调整 几个特定启动参数的处理方式。改与成 Solon.cfg() 同步，再统一从 Solon.cfg() 取值
* 调整 启动参数与系统属性的同步时机
* wood 升为 1.2.1
* redisx 升为 1.5.0
* mybatis-flex 升为 1.6.5
* sqltoy 升为 5.2.66
* polaris 升为 1.14.1
* slf4j 升为 2.0.9
* lombok 升为 1.18.30（for JDK21）

### 2.5.4
* 增加 AppContext::onEvent 接口
* 调整 paramsMap() 的 List 处理，避免出现只读情况
* 调整 JarClassLoader 更名为 AppClassLoader
* 调整 solon.serialization.fastjson2 转换枚举未匹配时则异常
* 调整 solon.serialization.snack3 转换枚举未匹配时则异常
* 调整 smarthttp,jetty,undertow 的 FORM_URLENCODED 预处理
* 调整 signal server 启动打印信息
* mybatis-flex 升为 1.6.4
* dromara-plugins 升为 0.1.0
* snack3 升为 3.2.80
* redisx 升为 1.4.10
* beetlsql 升为 3.25.4-RELEASE
* fastmybatis 升为 2.9.6
* bean-searcher 升为 4.2.4
* rabbitmq 升为 5.18.0
* kafka_2.13 升为 3.5.1
* dubbo3 升为 3.2.5
* logback 升为 1.3.11
* nacos2 升为 2.2.4
* snakeyaml 升为 2.2
* redisson 升为 3.23.3
* luffy 升为 1.6.7
* water 升为 2.11.3

### 2.5.3
* 增加 `AppContext` 类
* 调整 `AopContext` 标为弃用，由 `AppContext` 替代
* 调整 solon.docs.openapi2 对枚举类型的显示处理
* beetlsql 升为 3.25.2-RELEASE

### 2.5.2（2023-09-02）
* 增加 `@Component` 自动动态代理特性，即自动识别AOP需求并按需启用动态代理
* 调整 `@ProxyComponent` 标为弃用，组件统一使用 `@Component`
* 调整 `@Around` 标为弃用，统一使用 context::beanInterceptorAdd 接口添加拦截器
* liteflow 升为 2.11.0
* activerecord 升为 5.1.2
* enjoy 升为 5.1.2

### 2.4.6
* 增加 http range 分片输出支持
* 增加 IoUtil 工具类，替代旧的 Utils IO功能（旧的标为弃用）
* 增加 `@Tran` 事务监视事件支持，并增加 message 属性（只在最外层触发事件）
* 调整 kafka-solon-cloud-plugin 如果没有订阅，则不启用消费端
* 调整 PathLimiter 的弃用提示方式
* 调整 solon.cloud.metrics 的时间记录单位
* 调整 solon.data.dynamicds 增加默认源配置项
* 优化 solon.serialization.snack3 对根字符串的解码处理
* 优化 solon.scheduling 的 `@Async` 定制能力
* 优化 solon.docs.openapi2 对基础类型列表识别过度的问题
* 优化 solon.docs.openapi2 对实体扩展的字段识别
* 优化 solon.docs.openapi2 的 `@ApiModelProperty` 注解处理
* 优化 solon.data.dynamicds 手动控制能力（添加、移除、获取）
* 优化 sa-token-solon-plugin 两个适配类的处理
* 优化 ConditionUtil 条件工具类处理
* mybatis-flex 升为 1.6.1
* beetlsql 升为 3.25.0-RELEASE
* dbvisitor 升为 5.4.0
* snack3 升为 3.2.79
* forest 升为 1.5.33
* smarthttp 升为 1.3.0

### 2.4.5
* 修复 httputils of okhttp 手动设定超时无效的问题

### 2.4.4
* 新增 mqtt5-solon-cloud-plugin 插件（用于支持 v5 版本）
* 增加 mqtt-solon-cloud-plugin 支持 publishTimeout 配置（默认3秒）
* 增加 mqtt-solon-cloud-plugin 支持 topicFilter 表达式
* 增加 HttpServerConfigure::enableDebug 接口
* 增加 Context::close 强制实现约定
* 增加 JacksonRenderFactory::addFeatures、removeFeatures 接口
* 增加 solon.cloud.eventplus 对事件 qos 传递支持
* 优化 rabbitmq-solon-cloud-plugin 代码实现
* 优化 solon-maven-plugin 打包提示语
* 优化 maven 打包插件版本管理
* beetlsql 升为 3.24.0-RELEASE
* beetl 升为 3.15.8.RELEASE
* mybatis-flex 升为 1.5.8
* fastmybatis 升为 2.9.2
* fastjson2 升为 2.0.39
* snack3 升为 3.2.76
* liteflow 升为 2.10.6
* knife4j 升为 4.2.0

### 2.4.3
* 增加 solon.cloud.metrics 对 MeterBinder 的自动装配处理
* 增加 HandlerLoaderFactory，以实现所有 `@Mapping` 注解解析的重写支持
* 增加 rocketmq-solon-cloud-plugin 插件，对 ak/sk 的支持
* 增加 sa-token 插件，对 SaTokenDaoOfRedisson 实现
* 增加 solon.data.dynamicds 支持各源不同的连接池类型
* 调整 solon.serialization.fastjson 插件，默认增加字段排序特性
* 调整 solon-test 依赖，改成 solon-test-junit4 和 solon-test-junit5 的集合
* 调整 ConvertUtil 内的 LocalDateTime 处理，先通过 Date 中转，统一时间格式
* 调整 FiledWrap 反射权限改为用时再设置
* 调整 RouterInterceptorLimiter 标为弃用，由 RouterInterceptor::pathPatterns 替代
* 调整 ChainManager::getFilterNodes、getInterceptorNodes 输出类型
* 调整 solon-admin 的 server 和 client 通信为有状态格式
* 调整 solon 模块结构
* 调整 solon-admin-server 界面细节
* 修复 solon.docs.openapi2 插件 ApiImplicitParam::paramType 未生效的问题
* 修复 solon.docs.openapi2 插件 ApiParam::hidden 未生效的问题
* sa-token 升为 1.35.0.RC
* mybatis-flex 升为 1.5.7
* redisx 升为 1.4.9

### 2.4.2
* 新增 lettuce-solon-plugin 插件
* 新增 solon.docs.openapi2 插件
* 新增 solon.cloud.metrics 插件
* 升级 solon-maven-plugin 的相关依赖
* 增加 `List<Bean>` 和 `Map<String,Bean>` 注入支持
* 增加 Context::attrOrDefault 接口，原接口标为弃用
* 增加 RouterInterceptor::pathPatterns 接口，原接口标为弃用
* 增加 solon-admin-server 对 basic auth 配置的支持
* 增加 solon-admin-server 对 uiPath 配置的支持
* 增加 solon-admin-client 接口安全控制
* 增加 solon-admin-client 与 server 一起使用时，自动识别 serverUrl
* 增加 solon-admin-client 增加 token 访问控制
* 调整 solon-admin-client ，元信息改为 solon.app 的内容；server 监视日志改为 trace 级别
* 调整 solon.docs 的控制器启动，转到 solon-openapi2-knife4j。方便不同的框架定制自己的服务
* 调整 `@Bean` 函数的 VarGather::requireRun 由 false 改为 true，只要过了条件检测必然运行
* 调整 sa-token-solon-plugin 插件，增加对网关的支持
* 调整 CloudMetricService 接口设计，更适合与 micrometer 对接
* 调整 solon.health.detector 代码实现，增加复用性
* 修复 solon.cloud.eventplus 插件 `@CloudEventSubscribe` 注解在函数会出错的问题
* 修复 solon.docs 插件 `@ApiParam` 注解无效的问题
* mybatis-flex 升为 1.5.6
* beetlsql 升为 3.23.6-RELEASE
* sqltoy 升为 5.2.60
* snack3 升为 3.2.75
* wood 升为 1.1.8
* smarthttp 升为 1.2.9
* smartsocket 升为 1.5.32

### 2.4.1
* 新增 solon.web.servlet.jakarta 插件（用于 war 打包时，支持 tomcat10, jetty11 等 jakarta.servlet 容器）
* 新增 solon-admin-client 插件
* 新增 solon-admin-server 插件
* 新增 solon-admin-server-ui 插件
* 增加 Props::getByKeys、getOrDefault 接口
* 调整 EventBus::push 标为弃用，添加 ::publish 代之
* 调整 AopContext::beanAroundXxx 标为弃用，添加 ::beanInterceptorXxx 代之
* 调整 Around 相关名改为 Interceptor，原名标为弃用
* 优化 ProxyComponent 注解属性调整成与 Component 注解一样，支持用 tags 查找
* 优化 solon.boot 信号启动的执行时机后延
* 优化 solon.scheduling 插件的 retry 的兜底设计
* 优化 EventBus 增加订阅排序支持
* 修复 bean 所有字段只读且无 public 构造器时，会异常的问题
* wood 升为 1.1.7
* snack3 升为 3.2.73
* beetlsql 升为 3.23.5-RELEASE

### 2.4.0（2023-07-20）
* 新增 solon-openapi2-knife4j 插件，替代 solon-swagger2-knife4j
* 增加 server.socket.ssl.* 配置
* 增加 server.websocket.ssl.* 配置
* 增加 Context::isSecure 接口
* 增加 ChainManager::defExecuteHandler 接口
* 增加 ChainManager 对 SessionStateFactory 的管理，原管理方式移除
* 增加 `@Header` String[] xxx 注入支持
* 增加 Converter 体系，一般用于基础类型的通用转换
* 增加 日志服务孵化机制，可在打印前进行完成格式配置
* 增加 solon.boot.socketd.netty 插件对 ssl 的支持
* 增加 solon.boot.websocket.netty 插件对 ssl 的支持
* 优化 RouterListener 取消自己的线程池，改用 RunUtil
* 优化 JsonConverter 标为弃用，统一由 Converter 替代
* 优化 SocketChannelBase 标为弃用，统一由 ChannelBase 替代
* 优化 ResourceUtil 的根路径兼容性
* 优化 @Init 注解逻辑，仅对原始实例有效。保持与 LifecycleBean 相同策略
* 优化 solon.boot.smarthttp 的 ws 适配
* 优化 nacos2-solon-cloud-plugin 的适配，统一 jackson 版本
* 优化 nacos-solon-cloud-plugin 的适配，统一 jackson 版本
* 优化 LogUtil 扩展方式，改为静态扩展方式，原手动方式标为弃用
* 优化 Context::headerValues() 返回类型为 String[]
* 优化 Context::paramValues() 统一 server 相关处理逻辑
* 优化 统一 request 参数与实体字段的注入转换机制，并增加自定义转换机制
* 优化 内核日志打印顺序，由 SolonApp 实例化后再打印。以便日志格式配置先加载
* 优化 当使用 http ssl 时，服务启动打印为 https 地址
* 优化 ActionExecuteHandlerDefault::changeBody 参数结构，方便不同的序列化方案处理
* 优化 Nami 编码器匹配策略及头同步策略，尤其是仅单编码包引入时
* 优化 AbstractRoutingDataSource 的关闭处理
* 优化 ShardingDataSource 增加 Closeable 接口支持
* 调整 beetlsql-solon-plugin 插件，DbConnectionSource 改为公有，调整包结构
* 修复 solon-swagger2-knife4j 插件，递归类型的数据模型会栈溢出的问题
* 修复 solon-swagger2-knife4j 插件，相同 path 不能显示多个 method 的问题
* 修复 solon-swagger2-knife4j 插件，`List<Demo>` 风格参数，不能正常构建 json 示例
* 修复 solon-swagger2-knife4j 插件，`Page<Demo>`、`Result<Page<Demo>>` 等复杂嵌套的临时模型，不能正常构建 json 示例
* dbvisitor 升为 5.3.3
* mybatis-flex 升为 1.5.1
* sqltoy 升为 5.2.59
* fastmybatis 升为 2.8.1
* bean-searcher 升为 4.2.2
* liteflow 升为 2.10.5
* beetl 升为 3.15.7.RELEASE
* beetlsql 升为 3.23.4-RELEASE
* smarthttp 升为 1.2.8
* fastjson2 升为 2.0.35

### 2.3.8
* 增加 HttpServerConfigure::enableHttp2 接口, 默认为 false
* 增加 UploadedFile[] 注入支持
* 调整 solon.view.* 增加引擎提供者获取属性
* 调整 mqtt-solon-cloud-plugin 插件，增加获取原生 client 接口
* 调整 minio-solon-cloud-plugin 插件，minio 降为 8.2.2
* 调整 单元测试项目结构?
* 调整 IpUtil 增加扩展 ip 实现，可替换内部的实现
* 调整 solon.docs 插件，在无参 post 时，不再自动转成 get；增加 @Api::value() 做为 tags
* 调整 Gateway:register 执行时机为容器启动时，使注册时可使用注入字段
* 修复 solon.boot.undertow 在客户端进程关闭时，不能触发 onClose 事件的问题
* 修复 solon.boot.smarthttp 在客户端进程关闭时，不能触发 onClose 事件的问题
* 优化 sqltoy-solon-plugin 适配代码，增加 LightDao 的支持
* 优化 hasor-solon-plugin 插件适配，支持最新状态
* sqltoy 升为 5.2.58
* mybatis-flex 升为 1.4.7


### 2.3.7
* 优化 nami 解码器的渲染要求策略
* 优化 solon.boot.websocket 异步发送机制
* 优化 solon.boot.websocket.netty 异步发送机制
* 调整 RunUtil 增加线程池名
* 调整 StringSerializerRender 开放 serializer 属性
* 调整 app.router().caseSensitive 默认为 true
* 增加 CloudEvent 注解在函数上时，支持 AOP 扩展
* 增加 solon.docs 插件，对网关开发模式的支持
* 增加 solon.boot.socketd.jdksocket 插件，对 ssl 的支持
* 增加 server.http.ssl.* 配置
* 增加 NamiMapping、NamiBody 注解
* 增加 序列化接口（在渲染之外），可以外面复用（从 StringSerializerRender 开放 serializer）
* 增加 Context::headersMap, Context::headerValues 接口
* 修复 solon.serialization.jackson 在某些情况下，序列化 null 会出错的问题
* 修复 solon.boot.jetty 不能使用资源文件做 ssl 密钥文件的问题
* wood 升为 1.1.5
* smarthttp 升为 1.2.6
* smartsocket 升为 1.5.31
* sqltoy 升为 5.2.57
* mybatis-flex 升为 1.4.4
* beetlsql 升为 3.23.2-RELEASE

### 2.3.6
* 修复 异步监听可能为null的问题
* 调整 异步超时默认为30秒（-1L为不限，0L为默认）

### 2.3.5
* 新增 solon.boot.websocket.netty 插件
* 增加 solon.boot.jdkhttp 插件，虚拟异步支持（进而支持响应式接口）
* 增加 solon.boot.jlhttp 插件，虚拟异步支持（进而支持响应式接口）
* 调整 solon.web.flux 插件，出错时自动结束异步
* 调整 Context 异步接口机制，只能被调用一次
* 优化 solon.web.sse 插件，改为纯异步机制（所有 solon.boot.http 已支持异步）
* 优化 SmartHttp Context 异步接口机制
* 优化 Servlet 启动打印信息
* 优化 Context:forward 在有 context-path 时的处理
* 优化 ContextPathFilter 对根地址的映射处理
* 修复 pathNew 多次执行后 ContextPathFilter 会失效的问题

### 2.3.4
* 新增 solon.web.sse 插件（sse: Server Send Events）
* 新增 solon.web.flux 插件，响应式web开发插件（适用于支持异步的 http server）
* 增加 mybatis-plus-solon-plugin 插件，原生编译支持（GraalVM Native Image）
* 增加 solon.scheduling 插件，简单的 Retry 功能
* 增加 solon.validation 一次性验证所有字段的支持
* 增加 solon.docs 插件，支持字段 transient 排除
* 增加 Context 异步控制接口，为响应式web开发提供支持
* 增加 ActionReturnHandler 接口，之后特别的返回结果可定制。为响应式web开发提供支持
* 增加 ActionExecuteHandler 接口（替代旧的 ActionExecutor），并交由 chainManager 管理
* 增加 jetty、undertow 对 Context 异步适配
* 增加 Inject("{xxx:def}") 默认值转集合和数组支持
* 完善 mybatis-solon-plugin  原生编译支持
* 完善 solon.aot 增加 lambda 序列化支持
* 修复 请求路径动态变化后，路径变量获取失败的问题
* guava 升为 32.0.0-jre
* smarthttp 升为 1.2.4
* smartsocket 升为 1.5.30
* dromara-plugins 升为 0.0.9
* forest 升为 1.5.32
* mybatis-flex 升为 1.4.1
* sqltoy 升为 5.2.54
* hutool 升为 5.8.20
* fastjson2 升为 2.0.34
* java-websocket 升为 1.5.3

### 2.3.3
* 调整 solon-cloud-alibaba 快捷包 改用 nacos2,rocketmq5
* 调整 file-s3-solon-cloud-plugin 插件，不排除 aws-java-sdk-s3（之前为排除）
* 添加 dromara-plugins 所有插件的版本管理
* 添加 PathRule 工具类
* 添加 PathLimiter 用于限制 RouterInterceptor 的范围
* 添加 MybaitsAdapter::getMapper 增加缓存处理
* 添加 maxHeaderSize(8k), maxBodySize(2m) 为 server 统一默认配置，不然会出 readToken 错误
* 添加 Context::sessionOrDefault(),headerOrDefault(),paramOrDefault() 接口
* 添加 ChainManager::getFilterNodes(),getInterceptorNodes() 接口
* 优化 AsmProxy 代理类的缓存机制（简化）
* 优化 Utils::firstOrNull 增加 null 判断
* 优化 ClassUtil 的异常处理
* 优化 Context::filesMap() 改抛 IOException 异常
* 优化 Context::param(key,def) 处理
* wood 升为 1.1.3
* nacos2 升为 2.2.3
* rocketmq5 升为 5.0.5
* dubbo3 升为 3.2.2

### 2.3.2
* 调整 mybaits-solon-plugin 插件，添加 configuration.mapperVerifyEnabled 配置（控制是否启用 mapper 校验）
* 调整 solon.docs 插件，将非 `@Body` model 进行字段拆解
* activemq 升为 5.15.9

### 2.3.1
* 新增 solon.data.shardingds 插件
* 新增 redisson-solon-plugin 插件
* 调整 solon-swagger2-knife4j 插件的优先级，以便控制 enableDoc
* 调整 mybatis-solon-plugin 插件，增加 aot 处理适配（支持原生编译了）
* 调整 mybatis 适配增加 isMapper 检测接口
* 调整 开放 bean 内部形态注册的限制，之前只能用普通组件注解
* 调整 应用启动时的事件改由 push 推送（之前是 pushTry）
* 调整 XxxCacheService 增加新的构造函数
* 调整 jlhttp Part 的 body string 大小限制改为 MAX_BODY_SIZE（之前为 MAX_HEADER_SIZE）
* 调整 smarthttp Part 的 body string 大小限制改为 MAX_BODY_SIZE（之前为 MAX_HEADER_SIZE）
* 调整 jdkhttp Part 的 body string 大小限制改为 MAX_BODY_SIZE（之前为 MAX_HEADER_SIZE）
* 增加 Context::filesMap() 接口
* bean-searcher 升为 4.2.0
* sqltoy 升为 5.2.51
* redisson 升为 3.21.0
* netty 升为 4.1.75.Final
* fastjson2 升为 2.0.33
* mybatis-flex 升为 1.3.2
* fastmybatis 升为 2.6.1

### 2.3.0（2023-05-25）
* 升级 日志体系到 slf4j 2.x（如果冲突，排除旧的 1.x）!!!
* 新增 solon.docs 插件!!!
* 新增 solon-swagger2-knife4j 插件!!!
* 新增 zipkin-solon-cloud-plugin 插件
* 新增 etcd-solon-cloud-plugin 插件
* 新增 fastmybatis-solon-plugin 插件
* 弃用 `@Dao` `@Repository` `@Service` （改由 `@ProxyComponent` 替代）
* 增加 ProxyUtil::attach(ctx,clz,obj,handler) 接口
* 增加 aot 对 methodWrap 参数的自动登记处理
* 修复 AopContext::getWrapsOfType 返回结果失真的问题
* 调整 mybatis 按包名扫描只对 `@Mapper` 注解的接口有效（避免其它接口误扫）
* slf4j 升为 2.0.7
* log4j2 升为 2.20.0（基于 slf4j 2.x）
* logback 升为 1.3.7（基于 slf4j 2.x）
* sqltoy 升为  5.2.48
* mybatis-flex 升为 1.2.9
* beetlsql 升为 3.23.1-RELEASE
* wood 升为 1.1.2
* redisx 升为 1.4.8
* water 升为 2.11.0
* protobuf 升为 3.22.3
* jackson 升为 2.14.3
* dubbo3 升为 3.2.1
* grpc 升为 1.54.1
* zookeeper 升为 3.7.1
* nacos2-client 升为 2.2.2
* nacos1-client 升为 1.4.5
* jaeger 升为 1.8.1

### 2.2.20
* 添加 Props::bindTo 接口
* 优化 日志框架，在 window 下的彩色打印支持
* snack3 升为 3.2.72

### 2.2.19
* 调整 日志框架，增加 window 下的彩色打印支持
* 调整 bio maxThreads core x 32
* 调整 maxBodySize,maxFileSize 用大于号做判断 (undertow 之外，maxBodySize 相当于 maxFromContentSize)
* 修复 solon.boot.undertow 的 maxBodySize 配置无效问题
* 修复 solon.boot.smarthttp + ssl 在某些情况下会慢的问题
* 修复 maxFileSize 过大会超界的问题
* smartboot.socket 升为 1.5.28
* smartboot.http 升为 1.2.1
* snack3 升为 3.2.71

### 2.2.18
* 增加 solon.boot.jdkhttp 对 HttpServerConfigure 接口的支持，方便添加端口及ssl的编程控制
* 增加 solon.boot.jlhttp 对 HttpServerConfigure 接口的支持，方便添加端口及ssl的编程控制
* 增加 solon.boot.smarthttp 对 HttpServerConfigure 接口的支持，方便添加端口及ssl的编程控制
* 增加 solon.boot.jetty 对 HttpServerConfigure 接口的支持，方便添加端口及ssl的编程控制
* 增加 solon.boot.undertow 对 HttpServerConfigure 接口的支持，方便添加端口及ssl的编程控制
* 增加 solon.logging.logback 插件，文件扩展名配置（.log, .log.gz）
* 增加 solon.logging.log4j2 插件，文件扩展名配置（.log, .log.gz）
* snack3 升为 3.2.67

### 2.2.17
* 增加 @Inject 注入 bean 的 required 检测支持
* 增加 缓存服务适配类可传入客户端的构建函数
* 增加 DynamicDataSource 无参构造函数，方便定制
* 增加 CloudDiscoveryService 代理类，以支持发现代理的配置
* smart-socket 升级为 1.5.27
* smart-http 升级为 1.2.0
* fastjson2 升为 2.0.31

### 2.2.16
* 增加 ctx:pathAsLower() 接口
* 增加 solon.boot.undertow 原生配置申明
* 增加 solon.sessionstate.jwt 原生配置申明
* 增加 solon.logging.logback 原生配置申明
* 增加 solon.logging.log4j2 原生配置申明
* 增加 solon cloud 发现代理的配置支持（在 k8s 环境，可直接转发到 k8s sev 上）
* 调整 aot 注册时对空类名进行过滤
* 增强 solon.boot.jetty 在原生运行时兼容性
* 增强 solon.boot.undertow 在原生运行时兼容性
* 调整 Context::commit 函数位置，迁移到别处
* 调整 预热工具在 aot 时跳过执行
* 调整 MethodWrap 和 BeanWrap 的两个异常解包处理
* 解决 solon.aot 部分类型不能解析识别的问题
* polaris 升为 1.12.2
* beetl 升为 3.15.4.RELEASE
* beetlsql 升为 3.22.0-RELEASE
* sqltoy 升为 5.2.45
* liteflow 升为 2.10.2
* forest 升为 1.5.31
* dbvisitor 升为 5.3.1

### 2.2.15
* 增加 aot 配置注入实体的自动登记处理
* 增加 aot 函数包装的返回可序列化类型的自动登记处理
* 增加 aot 有注入jdk代理的自动登记处理
* 增加 aot jdbc 驱动的的自动登记处理
* 增加 aot 通用反射代理的自动登记处理
* 调整 aot 完成后的关闭处理方式

### 2.2.14
* 增加 afterInjection() 对注入的检测及非必须注入的支持
* 增加 @Bean demo(...) 对注入的检测及非必须注入的支持
* 增加 okhttp 原生编译支持配置
* 增加 solon.scheduling.quartz 原生编译支持配置
* 增加 water-solon-cloud-plugin 原生编译支持配置
* 增加 solon.aot 功能总体上实现（细节优需优化）
* 增加 VarGater::check 接口，用于在容器启动时做收集检测
* 增加 Router::caseSensitive 接口，用于设定区分大小写
* 调整 solon.test 增加代理类的字段注入
* 调整 solon.proxy.apt 的代理生成能力，转移到 solon.aot
* 调整 solon aot 时，取消 Runtime.getRuntime().addShutdownHook
* 调整 solon cloud 在 aot 时不做注册处理
* 调整 mybatis 适配的环境id规则保持也数据源bean同名
* 调整 Fastjson2Serializer 增加内部的上下文复用
* 调整 solon 路径分析器添加区分大小写的控制
* 调整 solon-maven-plugin 打包时，排除 provided 的包；支持配置 include 和 exclude 配置
* 调整 sa-token-solon-plugin 全局过滤器的 BeforeAuth 认证设为不受 includeList 与 excludeList 的限制，所有请求都会进入
* 修复 native 运行时，可能出现找不到资源文件而报错
* mybatis-flex 升为 1.2.0
* wood 升为 1.1.1

### 2.2.13
* 调整 solon.scheduling.quartz 管理机制
* 调整 solon.scheduling.simple 管理机制
* 调整 solon.aot 将 native 元数据生成到对应主类的包下
* 调整 solon bean 允许有多个其它基础形态
* 调整 EnjoyRender 增加获取 Engine 对象方法
* 调整 aot 时不启动 http 等通讯服务
* 移除 mybatis-flex-solon-plugin 的实现代码，改为引用第三方
* mybatis-flex 升为 1.1.8
* dubbo3 升为 3.2.0
* fastjson2 升为 2.0.29

### 2.2.12
* 新增 solon.aot 插件
* 新增 simplejavamail-solon-plugin 插件
* 新增 sms4j-solon-plugin 插件
* 增加 Utils::isEmpty(Collection s) 接口
* 增加 solon cloud i18n 默认值配置支持
* 修复 @Inject("${demo:hello}") 有默认值的配置注入不能自动更新的问题
* mybatis-flex 升为 1.1.3
* mybatis-plus 升为 3.5.3.1
* undertow 升为 2.2.24.Final
* jetty 升为 9.4.51.v20230217
* fastjson2 升为 2.0.28
* snack3 升为 3.2.66，支持 Charset 类型注入

### 2.2.11
* 新增 pulsar2-solon-plugin 插件
* 新增 drools-solon-plugin 插件
* 新增 solon.web.sdl 插件替代 solon.web.sso（之前名字搞错了）
* 插件 solon.boot.jetty 增加 jetty-servlet 依赖（solon.boot.jetty.add.servlet 就不需要了）
* 插件 solon.boot.jlhttp 优化线程不够时会处理方式
* 插件 solon.boot.socketd.jdksocket 优化线程不够时会处理方式
* 插件 solon.boot.smarthttp 优化二级池线程不够时会处理方式
* 插件 solon-maven-plugin 打包机制，支持 scope system 包编译
* 增加 @Configuration + @Inject 支持配置变更事件的注入
* 增加 solon.cloud/Config:_Props 申明为非序列化字段
* 增加 三个 http server 通过事件扩展的支持，比如定制执行服务
* 调整 sa-token sao 适配代码，优化相同key在多线程下的并发问题
* 调整 InvocationRunnableFactory::create 允许返回为 null，即自己直接执行
* 调整 @Init 的索引策略与 LifecycleBean 相同：+1
* 调整 根路由支持 remove 监听记录
* fastjson2 升为 2.0.27
* beetlsql 升为 3.22.0-RELEASE
* redisx 升为 1.4.7

### 2.2.10
* 新增 mybatis-flex-solon-plugin 插件
* 插件 solon.cloud.tracing 将 traceId 和 spanId 存入日志全局变量，方便在日志中打印
* 插件 solon.scheduling 增加 @Async 运行器创建扩展机制
* 调整 "@Init will be discarded" 打印时机，改由 debug 时打印
* 调整 solon.web.sso 插件的用法
* 调整 mybatis 相关的适配包名，基于2.0规范
* 调整 @SolonTest 注解为可继承
* 优化 mybatis-solon-plugin 去掉关闭连接时的 connection.setAutoCommit(true)。此段代码会导致查询速度增加20~30ms
* 优化 solon.boot.jlhttp 插件 JlHttpServer 类，实现接口公用性!!!
* 优化 solon.boot.jdkhttp 插件 JdkHttpServer 类，实现接口公用性!!!
* 优化 solon.boot.smarthttp 插件 SmHttpServer 类，实现接口公用性!!!
* snack3 升为 3.2.65，支持 File 类型注入

### 2.2.9
* 新增 solon.web.sso 插件
* 新增 wxjava-xxx-solon-plugin 插件
* 插件 solon.serialization.jackson 增加几个默认特性，增强与fastjson的兼容度???
* 插件 solon.serialization.snack3 增加嵌套泛型支持 List<List<Long>>、Map<String,List<Long>>
* 优化 jlhttp 状态码超界的处理
* 优化 solon.cloud.tracing @Tracing 增加全类名的记录
* 优化 时区解析统一由 ZoneId 处理???
* 调整 solon.serialization.snack3 从 solon-lib 移到 solon-api，更方便 json 的选择
* 增加 "/WEB-INF/templates/" 后端模板目录约定支持
* mybatis 升为 3.5.13
* fastjson2 升为 2.0.26
* beetl 升为 3.15.1.RELEASE
* beetlsql 升为 3.21.0-RELEASE
* snack3 升为 3.2.64

### 2.2.8
* 插件 dubbo-solon-plugin 增加 DubboFilterTracing 类
* 插件 solon.scheduling.simple 增加单计划任务的起停控制
* 增加 solon.boot.jlhttp 插件 JlHttpServer 类，提供可复用支持!!!
* 增加 solon.boot.jdkhttp 插件 JdkHttpServer 类，提供可复用支持!!!
* 增加 solon.boot.smarthttp 插件 SmHttpServer 类，提供可复用支持!!!
* 增加 动态代理时异常时的友好提示
* 优化 solon.boot.xxx "Server:main:" 打印
* 优化 solon.cloud.tracing Span 的 name 和 tags
* 优化 LifecycleBean 组件增加自动排位!!!
* 优化 ctx.realIp() 获取算法
* wood 升为 1.1.0 （拆分为： wood 和 wood.plus）
* water 升为 2.20.3 （配套 wood 拆分）

### 2.2.7
* 增加 @Path 替代 @PathVar（简短些），不过这个注解本身意义不大
* 增加 ResourceUtil::findResource 接口(ssl.keyStore 改用此接口)
* 增加 "application.xxx" 配置的弃用警告日志
* 增加 "solon.config" 多文件与内外文件支持
* 增加 "solon.config.add"(添加外部) 配置，用于替代 "solon.config"
* 增加 "solon.config.load"(加载内部) 配置，支持数组配置
* 增加 Props::getMap 接口，替代 Props::getXmap
* 调整 file-s3-solon-cloud-plugin 插件， "file.default"改为动态获取，增加bucket接口
* 调整 maven 版本管理，由 ${xxx.ver} 改为 ${xxx.version}
* 优化 请求参数 required 的提示
* 优化 Props::getProp 减少 forEach 次数
* 修复 solon.scheduling.simple 插件，在启动卡时后不能运行 cron=* * * * * ?
* 修复 AopContext::ProxyComponent 不能被 copyto 的问题
* sqltoy 升为 5.2.41
* wood 升为 1.0.9
* snack3 升为 3.2.62
* hutool 升为 5.8.16
* liteflow 升为 2.10.1

### v2.2.6
* 调整 solon.scheduling.quartz 增加非单例运行支持，method 支持拦截
* 调整 solon.scheduling.simple 增加非单例运行支持，method 支持拦截
* 调整 solon.web.staticfiles 改为固定长度输出
* 调整 CloudJob 增加非单例运行支持，method 支持拦截
* 增加 ModelAndView 操作便利性
* 增加 CloudLoadBalance 对策略机制的支持，可自定义
* 增加 CloudClient::loadBalance() 接口
* 增加 ids=t1,t2,t3 注入 List<T> 的支持
* 增加 插件加载失败日志
* 增加 动态代理注解的兼容检测，并打印警告日志
* 移除 不必要的 @Note 注释，同时缩减内核大小
* liteflow 升为 2.10.0
* fastjson2 升为 2.0.25
* snakeyaml 升为 2.0

### v2.2.5

* 增加 GsonActionExecutor 类
* 修复 FastjsonActionExecutor 配置没启效的问题
* snack3 升为 3.2.61
* wood 升为 1.0.8
* wood 升为 2.10.2
* gson 升为 1.10.1
* jackson 升为 2.14.2

### v2.2.4

* 新增 orika-solon-plugin 插件
* 新增 solon-job 插件
* 新增 solon-web-beetl 插件
* 新增 solon-web-enjoy 插件
* 增加 VaultUtils::guard 接口
* 修复 ColonClient.configLoad(g,k) 不能实时同步配置的问题
* beetlsql 升为 3.20.3-RELEASE
* fastjson2 升为 2.0.25
* snack3 升为 3.2.60
* hutool 升为 5.8.15
* sqltoy 升为 5.2.39
* beetlsql 升为 3.20.3-RELEASE

### v2.2.3

* 新增 easy-trans-solon-plugin 插件（引用）
* 增加 应用元信息 "solon.app.meta" 配置支持
* 增加 应用标签 "solon.app.tags" 配置支持
* 增加 nacos 客户端的自由配置支持（如：clusterName,contextPath）
* 增加 HealthHandler 自动注册支持，即组件模式
* 调整 sa-token 的集成逻辑
* 修正 LOG_SERVER 属性映射缺陷（pr）

### v2.2.2

* 新增 bean-searcher-solon-plugin 插件
* 优化 rocketmq5-solon-cloud-plugin 插件
* 移除 forest-solon-plugin 源码，改引用 com.dtflys.forest:forest-solon-plugin
* 移除 liteflow-solon-plugin 源码，改引用 com.yomahub:liteflow-solon-plugin
* 增加 调试模式下，模板仅在文件模式下才加载源码提供器
* 增加 用属性配置控制 solon.logging.log4j2::File 添加器是否启用！
* 增加 用属性配置控制 solon.logging.logback::File 添加器是否启用！
* 增加 "solon.logging.config" 配置支持，可将日志的配置文件移到外部
* 增加 CloudProps 关于 username/password 与 ak/sk 配置互通的支持！
* 增加 CloudProps 关于 username/password 上下级传导的支持！
* 增加 Solon::stopBlock 接口
* 调整 Solon::stop 时把 app,appMain置为null
* 调整 solon.web.static 的目录优先级（支持两个，但只让一个生效）
* 调整 让注解产生的生命周期，排序晚1个点
* 调整 让默认的 beanOnloaded 生命周期，排序为-1
* activerecord 升级为 5.0.4
* bean-searcher 升级为 4.1.2
* snack3 升级为 3.2.59
* hutool 升级为 5.8.14
* dubbo3 升级为 3.1.7
* forest 升级为 1.5.30
* rocketmq5-client 升级为 5.0.4
* smarthttp 升级为 1.1.22
* smartsocket 升级为 1.5.25
* ons-client 升级为 1.8.8.8.Final

### v2.2.1

* mybatis-solon-plugin，取消 mappers 检测异常，改为警告日志
* ContextPathFilter 增加与 cfg().serverContextPath 同步配置
* LifecycleBean 增加对 InitializingBean 的继承，使用时简便些
* 模板添加 templates 目录支持
* 移除 RenderUtil 类
* 降低内部手动添加 lifecycle 的执行排序，调为 -99 和 -98!!!
* 延迟 captcha-solon-plugin 运行时机
* 添加 SerializationConfig，为渲染器提供统一的配置帮助


### v2.2.0（2023-02-28）

* 概要：
* 1.吸收近期使用需求，增强内核能力（完善 bean 的生命周期；调整 plugin 仅定位为 spi；）
* 2.兼容的同时增强品牌个性化（增加 @SolonMain，@ProxyComponent 注解）
* 3.增加 apt 代理实现，增加 apt 生成 native 元信息配置的机制（为 native 简便打包，埋下好的基础 ）
*
*
* 新增 solon.proxy 插件
* 新增 solon.proxy.apt 插件
* 新增 solon.graalvm 插件
* 新增 solon.graalvm.apt 插件
* 调整 BeanInvocationHandler 内部代码，简化并增加 AptProxy 调用
* 调整 dateAsFormat 配置增加对 LocalDate 和 LocalDateTime 的支持
* 调整 Plugin::Init 标为弃用, 并由 InitializingBean 接口接替
* 调整 Plugin 接口不再做为组件形态，有生命周期需求的可改为 LifecycleBean 接口
* 调整 Plugin Spi 实例化改为 Bean 模式，之前为不能注入的 New 模式
* 调整 AopContext 标注 beanOnloaded 为弃用。事件概念调整为容器内部的生命周期概念
* 调整 AopContext 增加 start(),stop(),lifecycle() 接口；强化生命周期管理概念
* 调整 Lifecycle 增加可异常选择，并标注 @FunctionalInterface
* 调整 调整打包时主函数的提示信息
* 增加 应用属性配置内部引用增加默认值支持及环境变量引用
* 增加 @ProxyComponent 注解，使用时强依赖于 solon.proxy 插件
* 增加 @SolonMain 主解，作为 apt 生成 Graalvm Native 元信息配置的入口
* 增加 apt 代理实现方式（做为 asm 实现的补充），为全功能实现 Graalvm Native 打包提供支持
* 增加 InitializingBean 接口
* 增加 LifecycleBean 接口
* 增加 ClassUtil 工具类
* sqltoy 升级为 5.2.37

### v2.1.4

* 新增 bean-searcher-solon-plugin 插件
* Props::getByParse 增加默认值支持
* Props::getByExpr 增加默认值支持
* Condition 注解重新规范条件属性名
* AopContext 增加 Condition::onMissingBean 条件属性支持
* 升级 snack3 为 3.2.54 ，支持 kotlin data


### v2.1.3

* 插件 solon-maven-plugin 增加 jdk19 支持
* 插件 snowflake-id-solon-cloud-plugin 增加 workId 可配置
* 插件 solon.test 增加 AbstractHttpTester 类，名字比 HttpTestBase 顺眼些
* 插件 solon.test Http 测试器 增加 http(int port) 接口，方便本机所有http端口测试（比如 mock server）
* 插件 solon-test-junit4 增加 mockito-core 依赖，方便 mock 测试
* 增加 新的类存在检测方式 Utils.hasClass(() -> AuthUtil.class)
* 修复 activerecord-solon-pllugin::修复在 ActiveRecordPlugin::start 前不能注入 DbPro 的问题
* 修复 solon.cache.redisson 默认缓存时间没有生效的问题，及缓存键头没用起来的问题
* 调整 solon.cache.jedis 缓存键以':'为间隔
* 优化 solon.test 排除只读类成为代理类
* 优化 Gateway 根据路由记录数量，自动切换主处理
* 优化 http 输出长度模式 与 chunked模式 的自动切换
* 升级 mockito 为 4.11.0
* 升级 beetl 为 3.14.1.RELEASE
* 升级 sqltoy 为 5.2.34


### v2.1.2
* 新增 solon.health.detector 插件
* 增强 detector-solon-plugin 扩展能力
* 增强 mybatis-solon-plugin 的 typeAliases,typeHandlers,mappers 表达式配置能力
* 增加 ResourceUtil 提供资源路径表达式分析能力
* 调整 local-solon-cloud-plugin 本地文件路径规范
* 修复 mybatis-solon-plugin 与 solon-maven-plugin 打包插件的兼容性问题

### v2.1.0（2023-02-10）
* 新增 activemq-solon-cloud-plugin 插件
* 新增 solon.logging.log4j2（复制于 log4j2-solon-plugin）
* 新增 solon.logging.logback（复制于 logback-solon-plugin）
* 插件 beetlsql-solon-plugin 升级 beetlsql 为 3.20.0
* 插件 sqltoy-solon-plugin 升级 sqltoy 为 5.2.32
* 插件 dbvisitor-solon-plugin 升级 dbvisitor 为 5.2.1
* 插件 sa-token-solon-plugin 添加 SaJsonTemplate 实现类
* 增加 @Condition 注解，提供Com类与Bean函数的过滤支持!!!
* 增加 AppPrestopEndEvent，AppPrestopEndEvent 事件!!!
* 增加 配置元信息 solon-configuration-metadata.json 规范与支持
* 增加 EventBus.pushTry 接口
* 增加 solon.view.beetl 对 -debug=1 的支持
* 增加 solon.view.enjoy 对 -debug=1 的支持
* 优化 安全停止与延时的配置(增加新的启动参数：stop.safe,和应用配置：solon.stop.safe)


### v2.0.0（2023-02-01）

* 说明：第一个版只删除弃用代码，不加新功能
*
* 调整 solon//
    * 删除 Aop；由 Solon.context() 替代
    * 删除 Bean:attr，Component:attr
    * 删除 BeanLoadEndEvent，PluginLoadEndEvent；由 AppBeanLoadEndEvent，AppPluginLoadEndEvent 替代
    * 删除 Utils.parallel()...等几个弃用接口；由 RunUtil 替代
    * 删除 Solon.global()；由 Solon.app() 替代
    * 删除 SolonApp::port()；由 Solon.cfg().serverPort() 替代
    * 删除 SolonApp::enableSafeStop()；由 Solon.cfg().enableSafeStop() 替代
    * 删除 AopContext::getProps()；由 ::cfg() 替代
    * 删除 AopContext::getWrapAsyn()；由 ::getWrapAsync() 替代
    * 删除 AopContext::subWrap()；由 ::subWrapsOfType() 替代
    * 删除 AopContext::subBean()；由 ::subBeansOfType() 替代
    * 删除 AopContext::getBeanAsyn()；由::getBeanAsync() 替代
    * 删除 Solon.cfg().version()；由 Solon.version() 替代
    * 删除 EventBus::pushAsyn()；由 pushAsync() 替代
    * 删除 PrintUtil::debug()，::info() 等...；由 LogUtil 替代
    * 删除 @Mapping::before,after,index 属性；由 @Before,@After 或 RouterInterceptor 或 Solon.app().before(),after() 替代
    * 删除 "solon.profiles.active" 应用配置（只在某版临时出现过）；由 "solon.env" 替代
    * 删除 "solon.extend.config" 应用配置（只在某版临时出现过）；由 "solon.config" 替代
    * 删除 "solon.encoding.request" 应用配置（只在某版临时出现过）；由 "server.request.encoding" 替代
    * 删除 "solon.encoding.response" 应用配置（只在某版临时出现过）；由 "server.request.response" 替代
    *
    * 调整 DownloadedFile，UploadedFile 字段改为私有；由属性替代
* 调整 solon.i18n//
    * 删除 I18nBundle::toMap()；由 ::toProp() 替代
* 调整 solon.web.staticfiles//
    * 删除 StaticMappings::add(string1,bool2,repository3) 接口；由 StaticMappings::add(string1,repository2) 替代
    * 说明 string1 ，有'/'结尾表示目录，无'/'结尾表示单文件
* 调整 solon.web.cors//
    * 删除 ..extend.cores 包；由 ..web.cors 包替代
* 调整 solon.cloud//
    * 删除 Media::bodyAsByts()..；由 ::bodyAsBytes() 替代
* 调整 solon.cloud.httputils//
    * 删除 cloud.HttpUtils::asShortHttp()..；由 ::timeout() 替代
* 调整 solon.test//
    * 删除 test.HttpUtils::exec2()..；由 ::execAsCode()..替代
* 调整 solon.boot//
    * 删除 SessionStateBase/cookie[SOLONID2]
* 调整 mybatis-solon-plugin//
    * 删除 org.apache.ibatis.ext.solon.Db；由 ..solon.annotation.Db 替代
* 调整 beetlsql-solon-plugin//
    * 删除 org.beetl.sql.ext.solon.Db；由 ..solon.annotation.Db 替代
* 调整 sa-token-solon-plugin//
    * 删除 SaTokenPathFilter 类，由 SaTokenFilter 替代
    * 删除 SaTokenPathInterceptor 类，由 SaTokenInterceptor 替代
* 删除插件 httputils-solon-cloud-plugin；由 solon.cloud.httputils 替代
* 删除插件 solon.extend.stop；由 solon.web.stop 替代
* 删除插件 solon.extend.async；由 solon.scheduling 替代
* 删除插件 solon.schedule；由 solon.scheduling.simple 替代
* 删除插件 solon.extend.retry
* 删除插件 solon.extend.jsr330
* 删除插件 solon.extend.jsr303
* 删除插件 solon.logging.impl；由 solon.logging.simple 替代
*
* 新增插件 powerjob-solon-plugin
* 新增插件 powerjob-solon-cloud-plugin（支持 solon cloud job 标准）
*
* 调整 solon.scheduling/JobManger 添加更多注册时检测
* 调整 solon.banner/banner.txt 自定义默认机制
* 调整 sa-token-solon-plugin/isPrint 处理机制
* 调整 sa-token-solon-plugin 增加对 sso,oauth2 两模块的适配
* 调整 nami 添加 ContentTypes 类，提供便利的 content-type 常量
