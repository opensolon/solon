### 纪年

* v0: 2018 ~ 2019 (2y)
* v1: 2020 ~ 2022 (3y)
* v2: 2023 ~ 2024 (2y)
* v3: 2024 ~ 

### v2.x 升到 v3.x 提醒

* 提醒1：之前没有使用弃用接口的，可以直接升级 <br>
* 提醒2：有使用弃用接口的。建议先升级到 2.9.4；替换弃用代码后，再升级到 3.0.0


### 待议
* 新增 seata-solon-plugin 插件
* 增加 quartz jdbc 及时更新支持（数据库里变了，它马上变） ???
* 调整 solon.config.add 与 solon.config.load 合并，规范格式（同时支持内部与外部） ???
* 调整 使用更多的 slf4j 替换 LogUtil ???
* 调整 取消启动参数简写模式，保持与应用参数相同的名字 ???
* 
* 新增 seata-solon-plugin 插件？
* 添加 序列化安全名单接口?
* 优化 拦截体系与 rx 的兼容？

### v3.0.4

* 添加 solon SolonMain.exclude 属性，排除插件
* smart-http 升为 2.1

### v3.0.3
* 新增 nami-coder-kryo 插件
* 新增 solon-serialization-kryo 插件
* 添加 solon Condition:onProperty 多条件支持
* 添加 solon-net-stomp 简单事务支持，并添加统一凭据答复
* 添加 solon-net-httputils 对 data + get 请求的支持
* 添加 solon `@Controller` 和 `@Remoting` “非 Mapping” 函数支持 “拦截注解”
* 添加 solon @Bean 函数，非 public 的警告日志
* 添加 solon AppContext:beanInterceptorHas 检测方法
* 添加 solon AppContext:subWrapsOfType(baseType, callback, index) 方法
* 添加 solon ClassUtil::findPublicMethods 缓存效果
* 添加 solon-net-httputils 工厂扩展方式
* 添加 solon-data-sqlutils 工厂扩展方式
* 添加 solon 路由器二级索引排序支持
* 添加 solon SimpleSolonApp 类，方便局部测试用
* 修复 solon-docs-openapi2 分布式文档，地址转发出错的问题
* 修复 solon-config-plus 配置注入与 HikariCP 6.0 的兼容问题
* 修复 solon GenericUtil 在泛型变量名在传递过程中改名后，无法还原的问题。（不过，改名会有潜在问题）
* 优化 solon AppContext.beanRegister name 注册时，增加泛型绑定。以简化泛型集合注入的条件
* 优化 nami 简化编解码适配处理
* 优化 nami 的 get 识别处理
* 优化 nami http 请求，不再强制编码。仅当有 `@NamiBody` 注解，或指定编码器才编码（可兼容更多的后端框架）
* 优化 solon BeanWrap.Proxy 接口简化
* 优化 solon BeanWrap:nameSet, indexSet, tagSet, typedSet 改为 public，并由 isDoned() 控制是否可修改
* 优化 solon-view 渲染器的 app.shared 绑定，移到插件 start 时处理。避免热插拨时带入了不同 classloader 的类
* 
* freemarker 升为 2.3.33
* velocity 升为 2.4.1
* redisson 升为 3.37.0
* snack3 升为 3.2.120
* wood 升为 1.3.14
* liquor 升为 1.3.7
* undertow 升为 2.2.37.Final
* jetty 升为 9.4.56.v20240826
* java-websocket 升为 1.5.7
* java-websocket-ns 升为 1.2
* jackson 升为 2.18.1
* asm 升为 9.7.1
* smart-socket 升为 1.5.54
* smart-http 升为 2.0

### v3.0.2
* 添加 solon SerializerManager 及序列化可选注册机制
* 添加 solon Serializer::mimeType,dataType 可选属性，具有自我描述性
* 添加 solon AppContext::app() 属性
* 添加 solon-cloud CloudFileService 添加 getTempUrl(Duration) 方法
* 调整 solon-boot 的 junit-jupiter 依赖标为 scope=test
* 调整 solon VarHolderOfParam:getFullName 的显示，原 "@" 改为 "''"
* 调整 solon mvc 代码独立为 solon-mvc
* 修复 solon Utils.appFolder() 在 jar in jar 打包时，失效的问题
* 修复 solon TmplUtil 当模型参数为 null 会出现 "null"（改为用空替代）
* 修复 solon-boot-smarthttp 适配，在 contentLength(long) 时会精度丢失的问题
* 优化 solon-data-sqlutils 设计（进一步提搞性能和适用性）
* 优化 solon-net-httputils 设计 支持快捷序列化
* 优化 solon-net-stomp 设计 
* 优化 solon-scheduling-simple cron 调度实现
* 优化 local-solon-cloud-plugin job cron 调度实现
* 优化 solon-data 事务管理，支持 RoutingDataSource 深度查找
* 优化 solon-serialization 序列化器的泛型反序列化处理
* 优化 solon-serialization-jackson LocalDateTime,LocalDate,LocalTime 时间反序列化处理
* 优化 solon-security-auth 允许 AuthAdapter 直接支持“多套账号体系鉴权”处理（简化了）。AuthAdapterSupplier 标为弃用 
* cxf-webservices 升为 3.5.9
* beetlsql 升为 3.30.14-RELEASE
* mybatis-flex 升为 1.9.8
* hutool 升为 5.8.32
* liteflow 升为 2.12.4
* snack3 升为 3.2.119
* wood 升为 1.3.12
* redisx 升为 1.6.7
* fastjson2 升为 2.0.53

### v3.0.1

* 新增 solon-web-webservices 插件
* 新增 solon-net-stomp 插件
* 新增 nami-channel-http（基于 httputils 实现） 替代 nami-channel-http-okhttp
  * nami-channel-http 默认使用 URLConnection 适配（更小） 
  * nami-channel-http-okhttp 使用 okhttp 适配
* 修复 solon-boot-vertx 没有启用 websocket 时，无法接收请求的问题
* 修复 solon `@Param` 注解部分失效的问题
* 修复 solon 本地网关可能会死循环的问题
* 调整 solon-serialization Fastjson2 适配，时间默认为 dateTicks 模式（保持与其它序列化的统一）
* 调整 solon-net-httputils 默认使用 URLConnection 适配（引入 okhttp 后，自动切换） 
* 调整 solon Constants.ATTR_CONTROLLER,ATTR_MAIN_HANDLER,ATTR_MAIN_STATUS 内部常量值，避免与用户 attr 冲突
* 调整 solon ActionReturnHandler 匹配增加 ctx 入参，提供更多扩展可能
* 添加 solon 基于上下文的返回类型处理控制（Constants.ATTR_RETURN_HANDLER）
* 优化 solon-serialization dateAsTicks（并为默认），支持 longAsString 时的字符串转换
* snack3 升为 3.2.113

### v3.0.0 (2024-10-01)

* 新增 solon-data-sqlutils 插件
* 移除 solon `Before`、`After` 体系，（统一为 `Filter` 体系） // ok（化敏为简）
* 移除 solon `@ProxyComponent` (统一为 `@Component`) //ok（化敏为简）
* 移除 solon `Bean:registered` 由 `:delivered` 替代
* 移除 solon `Component:registered` 由  `:delivered` 替代
* 移除 无关配置的弃用代码（具体参考对应表）
* 移除 部分事件扩展（具体参考对应表）
* 调整 solon-net `WebSocket:paramMap` 改为 MultiMap 类型
* 调整 solon-net `WebSocket:send` 改为 Future<Void>
* 调整 solon Context::forward 改为“不经过”过滤器
* 调整 solon `Solon.app().handler()` 替代原来的 handlerGet() 和 HandletSet(x)；并简化 StaticResourceHandler 接入处理
* 调整 solon `warp.*` 部分方法设计（内部类）
* 修复 代理类中无法获取到泛型参数的BUG
* 修复 打散包时外部相对文件定位失识问题（优化 `Utils::appFolder`）
* 优化 solon Gateway 在过滤之前构建 `ctx.action()`
* 优化 solon-web 统一 `Context::cookeMap` 解析
* 添加 solon `AppContext:getBeanFuture,getBeansFutureOfType,getBeansMapFutureOfType`
* 添加 solon-cloud `Event:broadcast` 属性
* 添加 solon `Context:cookieSet(Cookie)` 方法，支持 httponly, secure 配置
* 添加 solon GenericUtil:reviewType 方法，实现 ParamWrap 和 FieldWrap 的泛型审查
* 添加 solon 泛型集合注入支持
* 添加 solon 静态字段注入支持
* 添加 solon-test `@SolonTest:scanning` 控制是否自动扫描（关闭时，需要通过 `@Import`）
* 添加 liquor-eval 框架收集
* smart-http 升为 1.6.0
* smartsocket 升为 1.5.52
* noear-jlhttp 升为 1.0.1
* wood 升为 1.3.7
* folkmq 升为 1.7.8
* socket.d 升为 1.5.12
* nacos1 升为 1.4.8
* easy-trans 升为 1.3.1
* sa-token 升为 1.39.0