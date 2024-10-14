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


### v3.0.2
* 添加 solon SerializerManager 及序列化可选注册机制
* 添加 solon Serializer::contentType,type 可选属性
* 添加 添加 AppContext::app() 属性
* 调整 solon-boot 的 junit-jupiter 依赖标为 scope=test
* 优化 TmplUtil 处理，当模型参数为 null 用空替代（避免出现"null"）
* 优化 solon-data-sqlutils 设计（进一步提搞性能和适用性）
* 优化 solon-net-httputils 支持快捷序列化
* cxf 升为 3.5.9
* beetlsql 升为 3.30.14-RELEASE
* hutool 升为 5.8.32
* liteflow 升为 2.12.4
* snack3 升为 3.2.114

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