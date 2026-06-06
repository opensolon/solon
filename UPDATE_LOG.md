### 纪年

* v0: 2018 ~ 2019 (2y)
* v1: 2020 ~ 2022 (3y)
* v2: 2023 ~ 2024 (2y)
* v3: 2024 ~ 2026 (1.5y)
* v4: 2026 ~ 

### v3.x 升到 v4.x 提醒

* 提醒1：之前没有使用弃用接口的，可以直接升级 <br>
* 提醒2：有使用弃用接口的。建议先升级到 3.10.7；替换弃用代码后，再升级到 4.0.0


### 待议
* 增加 quartz jdbc 及时更新支持（数据库里变了，它马上变） ???
* 调整 取消启动参数简写模式，保持与应用参数相同的名字 ???
* 调整 solon-docs-openapi2 合并 solon-docs-openapi2-javadoc ???
* 
* 添加 序列化安全名单接口?

* 优化 solon-flow （有包含结构的）网关的流入流出架构，使不再需要记录栈和计数器???
* 添加 Plugin postStart 事件???
* 调整 Plugin 统一交给 AppContext 管理（之前由 SolonApp 或 PluginPackage 管理）？？




### v4.0.0  (2026-05-26)

* 新增 mcp-json-jackson2
* 新增 solon-view-aifei-enjoy 插件
* 新增 solon-cache-caffeine3 插件
* 新增 solon-ai-talent-gateway（由 solon-ai-skill-restapi 和 solon-ai-skill-toolgateway 合并而来）
* 新增 solon-ai-talent-mount 才能插件
* 添加 solon `ScopeLocal.getOr(Supplier)` 方法
* 添加 solon `AppContext.resolvePlaceholders` 方法
* 添加 solon-config-snack4 开放属性序列化的 Options 定制支持
* 优化 solon-server-xxx MultipartUtil 字段处理 RunUtil.runAndTry(part::delete) 尝试删除
* 调整 solon-cache-caffeine 增加 md5 控制、默认时间、缓存头控制支持
* 调整 solon-cache-xxx enableMd5key 默认为 false 
* 调整 solon Utils.annoAlias 标为弃用（由 Utils.valueOr 替代）
* 修复 solon-serialization-snack4 Snack4StringSerializer.name 为 “snack4-json”
* eggg 升为 1.1.3
* snack4 升为 4.0.51
* liquor 升为 1.6.8
* socketd 升为 2.6.0
* folkmq 升为 1.8.0-M1
* asm 升为 9.10
* lombok 升为 1.18.46
* slf4j 升为 2.0.18
* log4j 升为 2.26.0
* junit5 升为 5.14.4
* hutool 升为 5.8.44
* snakeyaml 升为 2.6
* fastjson2 升为 2.0.62
* jackson2 升为 2.21.4
* gson 升为 2.14.0
* redisson 升为 3.52.0
* lettuce 升为 6.8.2.RELEASE
* kafka 升为 3.9.2
* reactor-netty-http 升为 1.3.5
* netty 升为 4.1.134.Final
* smartsocket 升为 2.0.0
* smarthttp 升为 2.5.19
* vert.x 升为 4.5.27
* tomcat 升为 9.0.118
* undertow 升为 2.2.39.Final
* rocketmq5 升为 5.2.0
* nacos3 升为 3.2.1
* swagger 升为 1.6.16
* swagger2 升为 2.2.50
