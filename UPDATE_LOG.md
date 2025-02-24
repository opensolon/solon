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
* 调整 使用更多的 slf4j 替换 LogUtil ???
* 调整 取消启动参数简写模式，保持与应用参数相同的名字 ???
* 
* 新增 seata-solon-plugin 插件？
* 添加 序列化安全名单接口?
* 优化 拦截体系与 rx 的兼容？

### v3.1.0

* 新增 solon-ai 插件
* 新增 solon-ai-repo-milvus 插件
* 新增 solon-ai-load-markdown 插件
* 新增 solon-ai-load-pdf 插件
* 新增 solon-ai-load-html 插件
* 定义 响应式过滤和拦截体系？？？
* 优化 拦截体系与 rx 的兼容？？？
* 优化 规范插件命名？？？
* 优化 solon-auth，AuthRuleHandler 的 Filter 实现转到 AuthAdapter 身上，方便用户控制 index
* 优化 solon-security-validation BeanValidator 的设定方式
* 添加 solon BeanWrap:isNullOrGenericFrom 方法
* 添加 solon AppContext:: getBeanOrDefault 方法
* 添加 solon subWrapsOfType, subBeansOfType, getBeansOfType, getBeansMapOfType genericType 过滤参数
* 添加 solon ParameterizedTypeImpl:toString 缓存支持
* 添加 solon-flow FlowEngine:load(uri) 方法
* 添加 solon-flow Chain:parseByText 方法
* 添加 solon-data-sqlutils SqlExecutor:updateBatchReturnKeys 接口，支持批处理后返回主键
* 添加 solon-data-sqlutils SimpleSqlExecutor:onCommandPost、onCommandAfter 事件支持
* 添加 solon-net-httputils HttpUtils:proxy 接口，支持 http 代理
* 添加 solon-net-httputils HttpUtils:execAsTextStream 文本流读取接口（可用于 dnjson 和 sse-stream）
* 添加 solon-web-rx 过滤体系
* 移除 solon-data-sqlutils Row,RowList 弃用接口
* 移除 solon-auth AuthAdapterSupplier 弃用接口
* 调整 solon-docs-openapi2 合并 solon-docs-openapi2-javadoc?
* 调整 solon-flow 用 layout 替代 nodes 配置（标为弃用）
* 调整 solon-rx Completable:doOnXxx 构建策略（可重复添加）
* 修复 solon-web-rx ActionReturnRxHandler 在接收异步发布器时，会结束输出的问题 
* 测试 虚拟线程、异步、响应式并优化性能？？？
* socket.d 升为 2.5.15
* fastjson2 升为 2.0.55
* jackson 升为 2.18.2
* gson 升为 2.12.1
* fury 升为 0.10.0
* kryo 升为 5.6.2
* sa-token 升为 1.40.0
* redisson 升为 3.45.0
* lettuce 升为 6.5.4.RELEASE
* hutool 升为 5.8.36
* grpc 升为 1.69.1
* vertx 升为 4.5.13
* netty 升为 4.1.118.Final
* liteflow 2.12.4.5
* forest 升为 1.6.3
* wx-java 升为 4.7.2.B

### v3.0.9
* 修复 solon-docs-openapi2 @Body 注解识别失灵的问题
* 修复 solon-data nested 事务策略单独回滚失效的问题
* 调整 solon-web-staticfiles 自动弃用带 `../` 和 `..\` 符号的路径匹配
* 调整 solon-flow 用 layout 替代 nodes 配置（旧的仍可用，标为弃用）
* liquor 升为 1.3.11

### v3.0.8
* 添加 solon-web-sse SseRender 渲染器，支持 "text/event-stream" 的任意类型处理
* 添加 solon-flow FlowEngine:chains 方法
* 添加 solon-flow ChainContext:run(script) 接口（支持在脚本中运行动态脚本）
* 优化 solon-flow 简化模式可以没有 start,end 节点，自动识别出开始节点
* 优化 solon ActionReturnHandler 增加排序支持
* 优化 solon-web-sse SseEmitter 添加事件缓存支持（初始化前就可发事件，之前不能）
* 优化 solon-web-rx 结构设计，方便支持其它响应式框架(比如 mutiny)
* 优化 solon-web-rx 可支持所有 reactivestreams 实现框架
* 优化 solon beanExtractOrProxy 内部改为先代理再提取，解决提取时对象未代理
* 调整 solon-web-rx 调整 Flux collectList 策略为由 mimeType 是否为流决定
* 修复 solon 集合bean注入时，可能产生两次执行的问题
* 修复 solon-logging-logback 的 "solon.logging.appender.file.maxHistory" 配置无效的问题
* 修复 solon RoutingTableDefault 路由可能出现变量在前，常量在后的问题
* 修复 solon-docs-openapi2 类型如果为自定义类型无法正确识别到 OpenApi Json.
* 修复 solon-serialization-jackson TimeDeserializer 对空串解析异常的问题
* snack3 升为 3.2.126
* liquor 1.3.10
* smart-http 升为 2.5.2 修复 sse 流不能自动结束的问题
* jetty 升为 9.4.57.v20241219

### v3.0.7
* 添加 solon BindProps 绑定属性注解，用于简化集合属性绑定及配置元信息APT生成
* 添加 solon-flow Chain:meta 配置
* 添加 solon-flow FlowEngine:unload 接口
* 添加 solon-flow execute when 属性，方便做规则引擎应用
* 添加 solon-cloud Cloud:Event 模型添加 meta（需要适配插件支持）
* 添加 solon AppContext:beanPublish 用于替换 wrapPublish（旧名标为弃用）
* 调整 solon SolonApp:classLoader() 返回类型为 AppClassLoader 方便 e-spi 开发
* 调整 solon-flow start、end 节点不再支持 task 配置，只允许 execute 节点带任务（职责清晰些）
* 调整 solon-flow execute 节点任务为空时，也触发驱动器的任务处理事件（可适用审批型场景）
* 调整 solon-flow NodeType 缺省解析改为 execute（之前为 start）
* 调整 Props::loadAddIfAbsent(String name) 为 loadAddIfAbsent(String uri)，保持与 loadAdd(uri) 相同逻辑
* 修复 solon-proxy 当 ASM 的代理方法超过 128 个时会超界的问题
* 修复 solon-net-httputils 在空返回时 OkHttpResponseImpl:contentEncoding 会 nep 的问题
* snack3 升为 3.2.125

### v3.0.6
* 新增 solon-flow 插件
* 添加 solon ScanUtil 对本地文件目录的扫描支持
* 调整 solon-proxy ProxyUtil 增强工具实用性
* 调整 solon `Props:loadAdd(name)` 改为 `Props:loadAdd(uri)`，支持表达式
* 调整 solon `solon.config.load`、`solon.config.add`、`Props:loadAdd(uri)` 统一规范格式与处理逻辑（同时支持内部与外部）
* 优化 solon 注入失败时的日志定位（支持类级定位）
* 优化 IoUtil.transferTo 添加 out.flush 自动处理
* 优化 solon bean 集合注入处理
* 优化 solon-data ConnectionWrapper 添加 getNetworkTimeout 异常过滤（有些驱动不支持此接口）
* 优化 solon-mvc Action 返回为 void 的情况，当二次加工后仍为 null 时，不作渲染处理
* 优化 solon-cloud-gateway 路由排序，增加路径深度优先处理
* 优化 solon-cloud-gateway Path 断言，增加多路径支持
* 优化 mybatis-solon-plugin 用 MybatisSessionTemplate 替换 MybatisMapperInterceptor
* 优化 mybatis-solon-plugin SolonManagedTransaction getTimeout 添加异常过滤（有些驱动不支持此接口）
* 修复 solon 启动时使用接口排除插件无效的问题
* snack3 升为 3.2.124
* fastjson2 升为 2.0.54
* snakeyaml 升为 2.3
* mybatis 升为 3.5.17
* mybatis-plus 升为 3.5.9
* mybatis-flex 升为 1.10.5
* sqltoy 升为 5.6.37.jre8
* guava 升为 33.4.0-jre
* hutool 升为 5.8.35
* smarthttp 升为 2.5.1，修复 ws idle 超时问题
* freemarker 升为 2.3.34
* thymeleaf 升为 3.1.3.RELEASE
* beetl 升为 3.19.0.RELEASE
* logback 升为 1.3.15
* junit5 升为 5.11.4
* solonx 升为 1.1.3

### v3.0.5
* 新增 solon-data-rx-sqlutils 插件（基于 r2dbc 构建）
  * 可配合 solon-web-rx 或者 solon-cloud-gateway 使用
* 添加 solon ClassUtil.scanClasses 方法
* 添加 solon 非单例类使用生命周期时 warn 日志提醒
* 添加 solon-cloud-gateway ExContext:toContext 方法，可用于支持经典接口接入（比如，sa-token 签权）
* 添加 solon ContextHolder 替代 ContextUtil ，后者标为弃用
* 添加 solon Context::isHeadersSent 方法，用于响应式转经典式后识别数据发送情况
* 添加 solon SolonApp::isMain 方法，用于在单元测试时识别是否可同步到 System 属性集
* 优化 solon ClassUtils.newInstance 异常类名显示
* 优化 solon InjectGather:isMethod 条件（仅方法，之前构造也算），让`@Bean`方法的检测先于组件的构造器
* 优化 solon-mvc Action 返回为 void，不作渲染处理
* 优化 solon-data DsUtils 构建时支持 "@type" 属性申明（统一未来的配置类型申明风格）
* 优化 solon-data DataSources 的配置获取时机
* 优化 solon-data-sqlutils SqlUtilsFactory 接口设计
* 优化 solon-scheduling Async 异常提示
* 优化 solon-scheduling Retry 拦截优先级到最里层
* 优化 solon-scheduling-simple 调用异常提示
* 修复 solon-mvc 以实体接收时 `UploadedFile[]` 字段不能注入的问题
* 修复 solon-boot-smarthttp 会把默认时区设为 GMT 的问题
* snakc3 升为 3.2.122
* redisx 升为 1.6.9
* socketd 升为 2.5.14
* folkmq 升为 1.7.10
* redisson 升为 3.39.0
* smarthttp 升为 2.5
* mybatis-flex 升为 1.10.3
* sqltoy 升为 5.6.34.jre8
* slf4j 升为 2.0.16
* log4j 升为 2.24.3
* jansi 升为 2.4.1
* fury 升为 0.9.0

### v3.0.4.1
* 添加 solon Router:matchMainAndStatus 接口，并将 attr 处理移到外部（避免调用 Router:matchMain 地影响已有的 attr）
* 添加 solon Props:addAll 方法
* 添加 solon-security-validation 值类型支持检测，通过检测异常替代之前的类型校验失败
* 优化 solon ServerEndpoint 注解支持属性模板
* 优化 solon BeanWrap 增加重复代理的检测，避免特殊情况出现重复代理
* 优化 solon RunUtils 线程池支持重复关闭与恢复，并与 SolonApp 停止事件绑定
* 修复 solon-boot-smarthttp 在某些环境下会出 arraycopy 异常的问题
* smarthttp 升为 2.4

### v3.0.4
* 新增 nami-coder-abc 插件
* 新增 solon-serialization-abc 插件
* 新增 mybatis-solon 插件
* 添加 solon SolonApp:pluginExclude 方法，排除插件加载
* 添加 solon ResourceUtil.findResource(uri,defAsFile) 方法
* 添加 solon HandlerSlots.add(expr,method,index,handler) 方法，支持路由优先级排序
* 添加 solon Context::sessionState(create) 方法
* 添加 solon SessionState.creationTime, lastAccessTime 方法
* 添加 solon-data-sqlutils SqlUtils:ofName(dsName) 方法
* 添加 solon-net-httputils HttpRespose.cookie 解析处理
* 添加 solon-cloud-gateway CloudRouteRegister:routeRemove 方法
* 添加 nami Encoder:bodyRequired() 方法，支持必须 body 请求的场景
* 优化 solon `@Produces`、`@Consumes` 改为可继承，方便通过基类
* 优化 solon `@Bean` initMethodName,destroyMethodName 改为自有或公有方法（之前只能自有）
* 优化 solon 外部配置加载处理，没有时，尝试在内部找资源文件
* 优化 solon Props.bindTo 性能
* 优化 solon SessionState:sessionId 的保持处理（从 attr 转到 cookieMap；减少一道）
* 优化 solon 带'.'的环境变量自动加载到应用属性里（之前只加载 'solon.'）
* 优化 solon AppContext 支持对 app 为 null 的过滤处理，强化无 app 的测试场景
* 优化 solon-boot-websocket 对 "//" resourceDescriptor 的兼容性
* 优化 solon-data-sqlutils 接口设计，优化极限性能
* 优化 solon-cloud-gateway 改为流响应模式，节省内存驻留、并支持 sse 代理
* 优化 solon 静态字段注入，取消有默认值则不注入的限制条件
* 优化 kafka-solon-cloud-plugin 配置处理
* 修复 solon-docs-openapi2 部分泛型响应体无法识别的问题
* snack3 升为 3.2.121
* wood 升为 1.3.15
* redisx 升为 1.6.8
* lombok 升为 1.18.36
* smart-http 升为 2.3
* vertx 升为 4.5.11
* netty 升为 4.1.115.Final

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
* 优化 solon-boot-smarthttp 支持 get+body 空流提交
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