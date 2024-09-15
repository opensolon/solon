### 纪年

* v0: 2018 ~ 2019 (2y)
* v1: 2020 ~ 2022 (3y)
* v2: 2023 ~

### v2.x 升到 v3.x 提醒

* 提醒1：之前没有使用弃用接口的，可以直接升级 <br>
* 提醒2：有使用弃用接口的。建议先升级到 2.9.2；替换弃用代码后，再升级到 3.0.0


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

### v3.0.0

* 新增 solon-web-vertx 插件
* 新增 solon 分类注入支持体系
* 新增 solon 分类构建支持体系
* 移除 `Before`、`After` 体系，（统一为 `Filter` 体系） // ok（化敏为简）
* 移除 `@ProxyComponent` (统一为 `@Component`) //ok（化敏为简）
* 移除 无关配置的弃用代码（具体参考对应表）
* 移除 部分事件扩展（具体参考对应表）
* 调整 solon-net WebSocket:paramMap 改为 MultiMap 类型
* 添加 solon Duration 类型配置注入
* 添加 solon-boot-vertx websocket 支持
* 添加 solon AppContext::beanExclude 方法
* snack3 升为 3.2.110
* smart-http 升为 1.6.0
* smartsocket 升为 1.5.52
* beetlsql 升为 3.30.13-RELEASE
* easy-trans 升为 1.3.1