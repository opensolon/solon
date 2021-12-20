#### 1.6.9
* 增加 log4j2-solon-plugin,logback-solon-plugin 对记录器等级的应用配置支持
* 增加 JsonRenderFactory 的事件扩展支持
* 增加 Mvc 数组参数注入时，自动以,号分离为数组 **
* 取消 @Param::format 属性（自动处理增加了10多种格式）**

#### 1.6.8
* 增加 @Init 私有函数支持
* 增加 @Bean 私有函数支持
* 增加 @Inject("${xxx:}")，默认值为空的支持
* 增加 StringSerializerRender 对 renderAndReturn 的支持
* 增加 Context::renderAndReturn 支持非视图数据
* 增加 模板引擎配置 扩展机制
* 调整 EventListener 充许 onEvent 抛出异常
* 调整 初始化失败时，自动停掉所有插件并结束进程
* 增加 上下文特性，自动做为模板变量 **
* 增加 Context::sessionRemove 接口 **
* 新增 log4j2-solon-plugin 插件 **
* 新增 logback-solon-plugin 插件 **

#### 1.6.7
* 增加 Context::sessionAsInt, Context::sessionAsLong, Context::sessionAsDouble 接口
* 修复 solon.extend.stop 用户ip获取错误
* 优化 配置注入"${xxx:def}"的兼容性，def有":"符也没关系了
* 增加 mybatisplus-solon-plugin 为 globalConfig 注入内容的入口
* 集成包 solon-api 默认添加 solon.extend.cors 插件
* 增加 主体流注入支持（@Body InputStream body） 
* 取消 solon.cache 插件，由 solon.data 插件集成相关功能，并提供工厂扩展机制

#### 1.6.6
* 增加 @Body 注解，注入 body string 支持
* 增加 @Validated List<?> 验证模式支持
* 修复 solon.boot.socketd.websocket，去掉 session.path() 多余内容
* 修复 sockted sessionBase::paramMap()，当 query=null 时会出错的问题
* 插件 solon.boot.smarthttp，升级 smart-http 为 1.1.10
* 插件 weed3-solon-plugin，升级 weed 为 3.4.10
* 依赖 snack3 升级为 3.2.2

#### 1.6.5
* 修复 water-solon-plugin ，不能处理缓存更新通知的问题（之前的版改出了问题）

#### 1.6.4
* 关闭 water-solon-plugin 的默认日志打印

#### 1.6.3
* 插件 solon.serialization.fastjson 增加泛型参数支持
* 插件 solon.serialization.snack3 增加泛型参数支持
* 插件 beetlsql-solon-plugin，升级 beetlsql 到 3.12.2-RELEASE
* water 升级为 2.5.1，原 /run/,/msg/ 升级为 /_run/

#### 1.6.2
* Mvc 注入，支持 1 转为 true 的支持
* AuthProcessorBase 增加 list = null 的预检
* 去掉 Scan completed 打印
* 修复 Nami 构造器设定的 Headers，没有下传的问题
* Nami 增加 interface 默认函数的支持
* 允许 Size，Length 注解的数据为Null。交由 NotNull 负责

#### 1.6.1
* 增加 @Inject("ds1") BeanWrap bw; 模式
* 优化 mybatis-solon-plugin 的适配方案
* 优化 water-solon-plugin 任务调试的安全机制
* 升级 sa-token-solon-plugin ，sa-token 到 1.28
* 升级 beetlsql-solon-plugin ，beetlsql 到 3.12

#### 1.5.71
* 插件 solon.extend.aspect ，优化 AsmProxy 关于 class bin 的加载

#### 1.5.70
* 插件 water-solon-plugin 升级 water 2.4.1
* 插件 solon.cache 添加 CacheServiceSupplier

#### 1.5.67
* Bean 增加 index；Component 增加 index
* 优化 solon.locale 配置
* 优化 Locale 字符串解析

#### 1.5.66
* 插件 water-solon-plugin 升级 water 2.3.2

#### 1.5.65
* 缓存注解的 tags 值，支持返回数据做为模板参数
* 国际化，支持 key 级别的 默认配置（之前基于文件）
* 增加 solon.extend.hotdev 插件 
* 增加 国际化由过滤器自动为上下文解析地区

#### 1.5.63
* 添加 solon.extend.graalvm 插件，用于适配 graalvm native image 模式
* 添加 detector-solon-plugin 插件，用于为健康检测，增加一批预设的探测器（可选：cpu,disk,jvm,memory,os,qps）

#### 1.5.62
* 发布 sqltoy-solon-plugin 插件
* 优化 序列化插件关于 JsonActionExecutor 对数组数据的泛型处理

#### 1.5.61
* 调整 water-solon-plugin 内部的白名单机制

#### 1.5.60
* 内核 loadEnv 将同步到 System.setProperty 和 Solon.cfg().setProperty
* 增加新环境变量：solon.start.ping

#### 1.5.58
* 插件 solon.data 增加 CacheServiceProxy 类
* 内核 ResourceScaner 增加 resource 类型的资源扫描
* 增加 cloudevent-plus-solon-plugin 插件
* 增加 sqltoy-solon-plugin 插件

#### 1.5.57
* 插件 water-solon-plugin 增加基于服务名的消息订阅

#### 1.5.56
* 插件 water-solon-plugin 升级 water 2.3.0
* 简化接口 Utils::getResourceAsString(name)
* 插件 weed3-solon-puglin 升级 weed 3.4.8

#### 1.5.55
* 插件 solon.boot.smarthttp 升级 smart-http 为 1.1.9
* 插件 weed3-solon-puglin 升级 weed 3.4.7
* 修复 solon.extend.staticfiles 增加本地绝对位置时无效的问题
* 增加 app.before(index, handler) 接口
* 增加 app.cfg().isAloneMode() 接口（独立运行模式）

#### 1.5.54
* 插件 solon coud 事件总线，增加支持本地同主题多订阅模式（以支持同服务内，领域隔离的需求）
* 插件 solon.view.beetl，升级 beetl 到 3.8

#### 1.5.53
* 重新调整内核的异常处理链，进行让 filter 可以统一获取异常处理
* 设整 CrossHandler 接口，并增加 exposedHeaders(..)
* 插件 sa-token-solon-plugin，升级 sa-token 到 1.27.0
* 插件 beetlsql-solon-plugin，升级 beetlsql 到 3.11.0-RELEASE
* 增加 enableErrorAutoprint ，用于控制事件总线里的异常是否自动打印
* 调整 solon.logging 没有 solon 实例也可以使用

#### 1.5.50
* 添加 DownloadedFile 类，用于下载文件输出时用
* 将不确定的插件移到_hatch下

#### 1.5.49
* 插件 mybatis-solon-plugin 增加 mappers 单行配置支持

#### 1.5.48
* 插件 mybatis-solon-plugin 增加 SqlSessionFactoryBuilder 定制支持

#### 1.5.47
* 简化 序列化转换器的体验

#### 1.5.46
* 插件 aws-s3-solon-plugin，改为基于 rest api 适配（极大缩小包）
* 插件 weed3-solon-plugin，weed3 升级为 3.4.0

#### 1.5.45
* 去掉 redisx-solon-plugin 插件（做为独立项目发展）
* 调整 CloudLockService，lock 更名为 tryLock

#### 1.5.43
* 重新调整 solon.extend.health 输出格式
* 修复 solon.serialization.jackson 新增转换器未生效的问题

#### 1.5.42
* 增加 solon.serialization，做为序列化的基础插件
* 优化 所有Json序列化插件，使之可方便定制类型序列化

#### 1.5.41
* 添加 minio-solon-plugin 插件
* 添加 solon.extend.health 插件
* 增加 CloudLoadBalanceFactory::register 接口（用于本地注册）
* 将本地服务发现，改为与云端发现并存，并优于云端发现
* 修复 water HandlerCheck 无法获取网关数据的问题
* 调整 org.noear.solon.cloud.tool 更名为 org.noear.solon.cloud.utils.http
* 重新设计 CloudFileService 接口:get,put,delete+Media

#### 1.5.40
* 优化不启用缓存的控制（基于代码控制，可根据启动参数变化）
* 优化不启用事务的控制
* 调整缓存标签参数使用策略，缺时出异常方便用者发现
* 增加@Inject("${xx.xx.ary}") List<String> ary 的支持
* 修复 Props 在 forEach 时，可能出现重复key的问题

#### 1.5.39
* 修复 solon.extend.staticfiles 会出现 .htm 的mine 匹配 .xhtm 的情况

#### 1.5.38
* 接口 CloudFileService 增加 delete 方法

#### 1.5.37
* 调整 Aop.get(type) 改为 return bean || null
* 取消 Aop.getOrNull(type) 接口，由 Aop.get(type)
* 新增 Aop.getOrNew(type) return bean; 替代旧的 Aop.get(type)

#### 1.5.36
* 调整 验证器 Numeric ，空为通过（是否充许为空由@NotEmpty处理）

#### 1.5.34
* CacheServiceDefault 更名为 LocalCacheService
* 插件 solon.serialization.hession 更名为：插件 solon.serialization.hessian
* 调整序列化渲染方案

#### 1.5.33
* 路由规则调整：带 * 号的排到后面
* 端口被占用时，抛出异常
* 国际化 增加 Content-Language 头信息支持
* 国际化 增加 上下文的 Locale 注入支持
* 国际化 增加 I18nService 类

#### 1.5.32
* 插件 solon.validation，注解 Whitelist、NotBlacklist、Logined 增加继承能力

#### 1.5.30
* 增加 mybatisplus-solon-plugin 插件
* 插件 beetlsql-solon-plugin 升级 beetlsql 为 3.9.0

#### 1.5.29
* 调整 验证器 Date ，空为通过（是否充许为空由@NotEmpty处理）
* 调整 验证器 Email ，空为通过（是否充许为空由@NotEmpty处理）
* 调整 验证器 Pattern ，空为通过（是否充许为空由@NotEmpty处理）
* 插件 water-solon-plugin，升级 water.client 为 2.2.8

#### 1.5.28
* 插件 beetlsql-solon-plugin 升级 beetlsql 为 3.8.0
* 插件 sa-token-solon-plugin 升级 sa-token 为 1.26.0

#### 1.5.27
* 增强 配置转实体的枚举支持不计大小写
* 修复 跨域组件某些情况下会404的问题
* 增加 captcha-solon-plugin 插件

#### 1.5.26
* 插件 weed3-solon-plugin，升级 weed3 为 3.3.22
* 修复 Auth 注解在控制器上无效的问题
* 修复 Servelt 的 session 不能清空的问题
* 会话状态接口增加重置能力

#### 1.5.25
* 插件 sa-token-solon-plugin，升级 sa-token 为 1.25.0
* 插件 water-solon-plugin，升级 water.client 为 2.2.7
* 修复 当profile为空内容时会出错的问题

#### 1.5.24
* 插件 solon.boot.jlhttp，升级 jlhttp 为 2.6，解决大文件下载问题
* 插件 solon.boot.jlhttp，增加跨域支持
* 插件 solon.boot.smarthttp，升级 smart-http 为 1.1.8
* 插件 solon.socketd.client.smartsocket，升级 smart-socket 为 1.5.11
* Context 增加 forward(pathNew) 转发接口

#### 1.5.23
* 修复 solon.extend.sessionstate.jwt 在特定场景下会无限次解析的问题
* 优化 solon.extend.cors 对头信息的处理

#### 1.5.22
* 调整 solon.validation 插件的 NoRepeatSubmitChecker 接口
* 升级 solon.extend.staticfiles 插件 增加扩展仓库支持
* 升级 rocketmq-solon-plugin 插件，事件总线增加马甲包式的分组多租支持（即支持 group ）
* 升级 rabbitmq-solon-plugin 插件，事件总线增加马甲包式的分组多租支持（即支持 group ）

#### 1.5.20
* 升级 beetl 到 3.5.0
* 升级 beetlsql 到 3.6.4

#### 1.5.18
* 升级 sa-token 到：1.24.0
* 升级 beetlsq 到 3.6.3
* 升级 water-solon-plugin 插件，事件总线增加马甲包式的分组多租支持（即支持 group ）
* 修复 solon.auth 插件规则验证失败时会null异常的问题
* 文件下载输出时对 filename 进行 urlencode 处理

#### 1.5.17
* 修复 solon.extend.staticfiles 在体外加载模式下，不能获取插件包的静态文件

#### 1.5.16
* 修复 solon.extend.staticfiles 在 jar + debug=1 模式下会出错的问题
* 修复 solon.view 在 jar + debug=1 模式下会出错的问题
* 修复 有默认值的环境变量转换失效的问题
* 解决 water-solon-plugin 在k8s下，运行时检测的安全限制问题
* 增加 solon.extend.aspect:: @Repository 语议组件注解
* 增加 solon.extend.aspect:: BeanProxy 类，以支持自定义代理扩展

#### 1.5.15
* 升级 beetlsql 到 3.6.2
* 升级 jetty 到 9.4.40.v20210413 
* 缓存增加序列化接口，可切定制和切换

#### 1.5.14
* 调整 quartz-solon-plugin、cron4j-solon-plugin 执行顺序；在 AppLoadEndEvent 之后执行

#### 1.5.13
* 优化 beetlsql-solon-plugin
* 将 SQLManagerBuilder 的 name 设为：ds-${ds bean name}，例 ds-db1
* 主从库的数据源Bean收集改为订阅模式(免得有些源未生成好)
* 取消自动添加debug插件的机制,交由用户控制

#### 1.5.12
* 增加 mybatis-pagehelper-solon-plugin 插件（适配 pagehelper）
* 优化 mybatis-solon-plugin
* 增加 Configuration::Environment::id 设为：ds-${ds bean name}，例 ds-db1

#### 1.5.11
* 插件 solon.i18n，的本地实现改为基于 Properties 实现。解决中文乱码问题 

#### 1.5.10
* 增加 solon.i18n 插件
* 增加 Context 地区特性
* 增加 Appender 生命周期控制

#### 1.5.9
* 限制 solon.logging::ConsoleAppender 打印，仅 debug 或 files model 才打印。

#### 1.5.8
* 增加 Alias 别名注解
* 部分注解的 value() 增加别名支持

#### 1.5.7
* solon.validation::ValidatorFailureHandlerImpl 更名为：ValidatorFailureHandlerDefault，并设为内部类
* solon.validation::BeanValidatorImpl 更名为：BeanValidatorDefault，并设为内部类
* solon.validation 增加更多注释
* solon.cloud CloudJob 增加 cron7x 可选属性
* 增加 qiniu-kodo-solon-plugin 对象存储适配插件
* 增加 SolonApp::signalGet 接口
* 插件 mybatis-solon-plugin 升级 mybatis 为 3.5.7
* 插件 solon.serialization.protostuff 升级 protostuff 为 1.7.4
* 插件 solon.socketd.client.smartsocket 升级 smartsocket 1.5.10
* 插件 solon.serialization.hession 升级 hessian 4.0.65
* 插件 solon.auth AuthUtil 验证权限与角色前增加登录验证
* 插件 solon.extend.sessionstate.jwt，增加 prefix、allowAutoIssue 配置项

#### 1.5.6
* 添加 SolonProps::loadEnv 加载环境变量（利于弹性容器设置环境信息）
* 插件 httputils-solon-plugin，添加 PreheatUtils 预热小工具

#### 1.5.5
* 插件 beetlsql-solon-plugin，升级 beetlsql 为 3.4.3（SQLManagerBuilder 增强了构建能力）

#### 1.5.4
* 插件 solon.extend.sessionstate.jwt，增加 allowOutput、allowUseHeader 配置项

#### 1.5.2
* Solon Validation 增加实体验证（不再需要jsr303扩展了）
* Validator 接口的原函数 validate 更名为 validateOfContext；并增加 validateOfEntity 函数定义（实现实体验证）。
* solon.extend.jsr303 插件不再自动注入到容器（Solon Validation，已支持实体验证）
* 增加 httputils-solon-plugin 插件

#### 1.5.1
* 修复加载配置时，值为null会出错的问题
* 升级snack3，增加更多的时间处理格式
* 增加更多的时间处理格式（与snack3同）
* enjoy 模式，分离 debug 引擎的实例

#### 1.5.0

* 部分插件名调整

| 原插件 | 升级为新插件 | 原因说明 |
| -------- | -------- | -------- |
| solon.extend.auth     | solon.auth     | 地位升级     |
| solon.extend.data     | solon.data     | 地位升级    |
| solon.extend.validation     | solon.validation     | 地位升级    |
|      |      |      |
| solon.extend.jetty.jsp     | solon.boot.jetty.add.jsp     | 增加与 solon.boot.jetty 关联性     |
| solon.extend.jetty.websocket     | solon.boot.jetty.add.websocket     | 增加与 solon.boot.jetty 关联性     |
| solon.extend.undertow.jsp     | solon.boot.undertow.add.jsp     | 增加与 solon.boot.undertow 关联性     | 


* 部分包名调整  

| 原包名 | 升级为新包名 | 说明 |
| -------- | -------- | -------- |
| solon.extend.auth.*     | solon.auth.*     | 地位升级     |
| solon.extend.validation.*     | solon.validation.*     | 地位升级     | 
| solon.extend.data.*     | solon.data.*     | 地位升级     |
| solon.core.tran.*     | solon.data.tran.*     | 转到 solon.data 统一维护     |
| solon.core.cache.*     | solon.data.cache.*     | 转到 solon.data 统一维护     |
  

* 改动 ValidatorManager，由单例模式改为静态模式；并对接口做了优化
* 调整 ValidatorManager::onFailure 更名为 ValidatorManager::setFailureHandler
* 移动 org.noear.solon.core.cache.CacheService 到 org.noear.solon.data.cache.CacheService
* 移动 org.noear.solon.core.tran.TranExecutor  到 org.noear.solon.data.tran.TranExecutor
* 移动 org.noear.solon.core.tran.TranUtils     到 org.noear.solon.data.tran.TranUtils
* 调整 NoRepeatLock 更名为 NoRepeatSubmitChecker（与其它验证器的检测器一同，统一为Checker的概念）
* 调整 各模板引擎内部接口名称，显得更统一些
* 模板 beetl 增加权限认证标签支持
* 模板 enjoy 增加权限认证标签支持
* 模板 freemarker 增加权限认证标签支持
* 模板 jsp 增加权限认证标签支持
* 模板 thymeleaf 增加权限认证标签支持
* 模板 velocity 增加权限认证标签支持

#### 1.4.14
* Mapping 的信号类型，由 HTTP 改为 ALL（减少对MethodType的设定）
* 添加 AuthProcessorBase ，支持权限数组的配置方式
* 调整主体处理成功后，则立即设为ctx.setHandled(true)；方便after识别404状态
* 修复 main action setHandled(true)，after action 不执行的问题

#### 1.4.13
* socketd 协议 headers 最长由 1k 增加为 4k
* 增加简化的配置文件名支持，例：app.yml, app-env.yml
* opentracing-solon-plugin 增加 socketd 支持
* nami debug 日志改由 slf4j 控制（不再依赖 isFileModel() 和 isDebugModel()）
* socket debug 日志改由 slf4j 控制（不再依赖 isFileModel() 和 isDebugModel()）

#### 1.4.12
* 添加 opentracing-solon-plugin 插件
* 重构 nami 拦截系统
* 重构 NamiHandler 的初始化时机，改为调用时初始化（原：构建时初始化，可能一些依赖Bean未完成构建）
* 重构 Nami Filter；改为链式过滤；并添加 Invocation，做为配套
* 拆分 Nami.Builder 为独立的 NamiBuilder
* 取消 Decoder, Encoder, Channel 对 Filter 的继承，改为添加 pretreatment 接口  
* 移动 Nami Result 到 nami 包下 
* 添加 solon filter 序号位支持
* 添加 Utils 新能力

#### 1.4.11
* 简化 Naimi 的附件模式，改由 NamiContext 直接操作 //建用 Filter 进行清除
* 添加 CloudJobHandler 接口（直接使用Handler，让人想不起来）
* 添加 Props::getByParse 接口
* 标注 Utils::throwableWrap 函数为弃用，并调整内部异常包装处理

#### 1.4.10
* 取消 WarnThrowable
* 限制 DataThrowable 被最终渲染
* 添加 Log4j 的适配
* 添加 Solon Auth 新注解：`@AuthIp`, `@AuthPath` 支持

#### 1.4.9
* CloudConfig 支持 ${xx}配置
* CloudEvent 支持 ${xx}配置
* CloudJob 支持 ${xx}配置
* CloudBreaker 支持 ${xx}配置
* 升级 weed3；支持 jar in jar 打包模式
* 增加 env 启动参数切换配置文件；例：java -jar xxx.jar -env=test
* 调整 优先使用 system prop，并盖掉 solon prop；之后同时更新 system prop 和 solon prop（才能让 java -Dxxx.xxx=xxx 有效果）
* 调整 solon auth 设计

#### 1.4.8
* 增加 sa-token-solon-plugin 插件，适配 sa-token 认证框架
* 增加 solon.extend.auth 插件，Solon 自带的认证框架

#### 1.4.7
* 增加 solon-enjoy-web 快速开发套件（支持 enjob + activerecord 体验）
* 增加 water-solon-plugin 插件对 CloudJob 的支持  
* 简化 CloudMetricService 接口定义

#### 1.4.6
* 增加 CloudMetricService 接口定义
* 增加 water-solon-plugin 插件件对 CloudMetricService 接口的适配
* 升级 water.client
* 升级 weed3

#### 1.4.5
* 增加 activerecord-solon-plugin 多数据源支持与事务支持
* 增加 solon-enjoy-web 组件

#### 1.4.4
* 完成 CloudJob 第一个适配方案
* 增加 xxl-job-solon-plugin 适配插件
* 增加 activerecord-solon-plugin 适配插件

#### 1.4.3
* 增加 @Bean 形态注册支持
* 增加 Aop 提取器功能
* 增加 Utils 部分新能力

#### 1.4.2
* 升级 Snack3 版本
* 升级 Jetty 版本
* 升级 Smart-Http 和 Smart-Socket
* 升级 Nami coder: FastJson 版本，调整某些场景下反序列化策略
* 调整 Nami coder: hessian,protostuff，异常处理策略
* 增加 CloudListService 快捷接口
* 增加 Nami coder 一批序列化与反序列化的单测

#### 1.4.1
* 增加 @ClientEndpoint autoReconnect 属性
* 取消 @Component remoting 属性

##### 1.4.0
* 增加 @Remoting 注解，替代 @Component(remoting=true) 
* 增强 solon-springboot-starter，可以将 solon 容器的内容注入到 springboot bean
  * 取消 @EnabelNamiClients 注解
  * 取消 @EnableSolonCloud 注解
  * 取消 @EnableSolon 注解
* 取消 nami-springboot-starter，有 solon-springboot-starter 就可以实现需要的注入

##### 1.3.39
* 增加 AopContext::beanAroundAdd() 函数，用于手动创建拦截器
* 修复 mybatis-solon-plugin 插入数据时，全是0的问题

##### 1.3.37
* 增加 jsr303 标准实现组件 solon.extend.jsr303
* 优化 solon.logging 内部结构,扩展更自由
* 增加 solon.cache.spymemcached, solon.cache.jedis 缓存实组件


##### 1.3.35
* 增加 sureness-solon-plugin 组件
* 增加 新的 http method 设定方式
* 注解 @Inject 增加 required 属性
* Solon cloud 增加 kafka-solon-plugin 组件

##### 1.3.33
* Solon logging 增加记录器级别控制
* Solon cloud 增加 zookeeper-solon-plugin 组件
* Solon cloud 增加 snowflake-id-solon-plugin 组件（起始时间可自己定义）
* 增加 普通停止方案
* UploadFile 原字段访问方式改为只读访问（以免被中途改掉）
* 增加 @Inject 配置注入默认值，@Inject("${server.port:8080}") //好像以前加的


##### 1.3.30
* 断路器增加动态配置支持
* 日志打印增加未格式化的异常
* 增加路由组件切换支持

##### 1.3.26
* 增加 Solon cloud event 渠道概念，使不同的业务可以用不同的框架（即多框架并存）
* @Init 增加延时处理，并默认为 true
* 优化session.jwt组件内部机制

##### 1.3.21
* 优化 CrossOrigin 注解，支持${}注入配置值

##### 1.3.20
* 增加 安装模式启动参数支持
* 增加 aliyun-oss-solon-plugin 组件
* 增加 aws-s3-solon-plugin 组件
* 增加 mqtt-solon-plugin 组件
* 增加启动事件：AppInitEndEvent  
* 取消 HandlerLink 类，增加 HandlerPipeline 类


##### 1.3.19
* Utils.loadClass()，在没指定ClassLoader时，默认用Class.forName();
* 修复 @Tran(readOnly=true) 未起效的问题
* solon.extend.validation 增加 NotBlacklist、Logined 注解

##### 1.3.18
* 添加 luffy-solon-plugin 组件，实现脚本运行能力
* 添加 CloudListService 接口（统一黑名单白名单的检测）
* 增加 HttpServletRequest、HttpServletResponse 控制器注入支持
* 增强 solon.logging.impl 异常格式化能力
* 增加 solon.extend.sessionstate.jwt 通过Header的传输兼容性

##### 1.3.17
* 增加 solon.cloud.xxx.config.load 增加配置
* 增加 配置注入默认值支持@Inject("${xxx:def}")

##### 1.3.16
* 增加 ctx.bodyNew() 用于构建新的body内容（可应用于手动解码重设场景）
* jar包内的资源,增加debug模式读取


##### 1.3.15
* 增加 Solon Cloud Breaker 接口定义

##### 1.3.14
* 静态文件组件，增加max-age配置；调试模式下自动不缓存
* 数据组件，增加基于key的缓存控制（之前基于tag控制）
* 验证组件，增加状态码控制（之前只能400）
* 安全停止，升级为二段式暂停
* 增加本地发现服务支持，用于本地调试用

##### 1.3.10
* 组件：org.noear.solon:beetl-web，更名为：org.noear:solon-beetl-web
* 增加过滤器，支持：SolonApp（全局）, Gateway（网关）
* 将 solon.extend.servlet/ServletFilterSolon 转移到：solon-springboot-starter

##### 1.3.9
* 增加 solon.logging 和 solon.logging.impl 日志组件（基于slf4j实现）
  为Solon cloud log serivce 提供输入界面
* 增加 solon.extend.cors AJAX跨域注解组件

##### 1.3.5
* 增加solon.cloud分布式锁接口
* 增加文件快速输出 ctx.outputAsFile(file)
* 增加jwt分布式session state组件：solon.extend.sessionstate.jwt  
* Result.SUCCEED_CODE 定为：200
* Result.FAILURE_CODE 定为：400（可通过静态变量修改）
* 优化Session state 结构，增加 SessionStateFactory
* 增加应用属性 Solon.cfg().appTitle()
* 验证组件 增加 Logined 注解验证

##### 1.3.3
* 增加 tpc 注册支持；
* 增加 自定义信号注册支持；  
* 增加安全停止功能（自动从注册服务摘除，并保持10秒存活）

##### 1.3.1
* 完成 rabbitmq-solon-plugin，rocketmq-solon-plugin 适配

##### 1.3.0
* 修复solon.cloud，多个配置关注不能并发分发的问题
* 增强solon.cloud的健壮性
* *.正式发布solon.cloud组件

##### 1.2.27
* 增加 solon-lib 框架
* 增加 rabbitmq-solon-plugin
* 增加 rocketmq-solon-plugin

##### 1.2.25
* 增加 nlog 组件，正试推出自己的元信息 log 框架
* 增加 nami.NamiAttachment，为Nami添加动态添加http的能力
* 完善 solon.cloud 的日志框架
* 完善 water-solon-plugin 适配
* 完善 consul-solon-plugin 适配

##### 1.2.23
* 完善 solon.cloud 相关接口
* 增加 water 对 solon.cloud 的适配
* 更新 springboot-solon-plugin 改为 solon-springboot-starter
* 取消 NamiClient ，增加 name,path,url,group

##### 1.2.21
* 取消 EnableNamiClient
* 增加 solon.cloud 接口
* 增加 nacos 对 solon.cloud 的适配

##### 1.2.20
* 简化 网关开发
* 增加 配置宏引用
* 优化 /** 匹配性能

##### 1.2.19
* 增加 nami.channel.socketd.jdksocket
* 增加 nami.channel.socketd.netty
* 增加 nami.channel.socketd.rsocket
* 增加 nami.channel.socketd.smartsocket
* 增加 nami.channel.socketd.websocket

##### 1.2.17
* 增加springboot 对 nami 的适配
* 增加nami-springboot-starter 组件
* SolonApp::enableSocket 更名为： SolonApp::enableSocketD

##### 1.2.15
* 增加hutool.http 对 nami的适配
* 增加solon-rpc快速开发集成包

##### 1.2.14
* 拆分nami项目为三大件：内核 nami；编码 nami.coder.* ；通道 nami.channel.*

##### 1.2.13
* 增加consul-solon-plugin组件
* 增加springboot-solon-plugin组件（用于链接springboot与solon）

##### 1.2.12 更新记录
* 强化@Param能力，支持默认值，增加输入控制
* 增加 SocketProps/connectTimeout,socketTimeout 配置（有需要的用起来）
* BeanWrap.attrs 改为 String[]类型
* BeanWrap 增加 attrGet(name) 方法
* 缩减SokectD包大小
* 修复一个VarGather问题

##### 1.2.10 更新记录
* 强化SocketD能力，充许外部定义实体
  
##### 1.2.7 更新记录
* 完成Swagger适配

##### 1.2.6 更新记录
* 优化SocketD的双向RPC模式；
* 增加Websocket的SocketD支持，支持双向RPC模式

##### 1.2.4 更新记录
* 增加扩展包隔离加载机制
* 增加双向RPC支持
* 增加Socket异步回调支持
* 增加SocketD，提供双向RPC快捷方式（D：double）

##### 1.2 更新记录
* 所有类去掉X前缀
* org.noear.solon.core 下的目录类重新组织结构
* 新增：
    *  Component 注解，用于拆分原XBean的双重功能（之后：Component 加在类上；Bean 加在函数上；与Spring 一至；方便迁移）
    * Solon 类，负责启动入口；（原XApp 拆为：Solon  和 SolonApp ）
* 更名（即名字变得较大）
    * XApp                      改为 SolonApp（实际上拆为：Solon 和 SolonApp）
    * XAppProperties            改为 SolonProps
    * XUtil                     改为 Utils
    * core/XMap                 改为 NvMap
    * core/XFile                改为 UploadedFile
    * core/XClassLoader         改为 JarClassLoader
    * core/XProperties          改为 Props
    * core/XPropertiesLoader    改为 PropsLoader

##### 1.1.12 更新记录
* 添加泻染返回支持：XRender::renderAndReturn，只支持 ModelAndView
* 添加XSessionFactory类，为双向RCP提供更友好支持

##### 1.1.7 更新记录
* 添加事件：PluginLoadEndEvent, BeanLoadEndEvent, AppLoadEndEvent

##### 1.1.6 更新记录
* 添加事件：org.eclipse.jetty.server.Server
* 添加事件：io.undertow.Undertow.Builder
* 改进 cron4j-solon-plugin 适配
* 新增 quartz-solon-plugin 适配（支持持久化）
* @EnableFeignClients 更名为：@EnableFeignClient
* @EnableFairyClients 更名为：@EnableFairyClient


##### 1.1.4 更新记录
* 完善注释；在注释中添加大量的代码示例
* 增加XAround的类级支持，及类继续支持
* 简化ClassWrap;去除其MethodWrap的初始化
* 重新定位拦截器为Method级的，与XContext无关；由XAround承载
* 取消@@全控制器过滤功能；太多符号让人混乱（统一走基类形式）
* 添加enableSessionState()，控制SessionState的开关
* 调整目录结构，减少core下的文件;合并或转移

##### 1.1.3 更新记录
* 结构微调，全并部分类
* 去掉变量下划线
* 调整部分命名

##### 1.1.2 更新记录
* 添加@XImport，用于启动时导入bean
* Servlet 增加：ServletContainerInitializer 配置支持
* Servlet 增加：@WebServlet,@WebFilter,@WebListener 自带注解支持(对Servlet友好了很多)

##### 1.0.43 更新记录
* method bean，取消之前的线程模式；改为回调模式；不然，beanOnload时，有些bean未完成
* 添加：org.noear.solon:beetl-web 项目

##### 1.0.42 更新记录
* @XAround 改为动态调用链形式
* 进而将 @XCache, @XTran 相关全部迁移到 solon.extend.data
* Aop 的实现，内部结构重新调整与命名

##### 1.0.40 更新记录
* 完成jetty websocket 适配
* 完成undertow websocket 适配
* 冠成smarthttp websocket 适配
* 添加XMessage + XListener架构，用于支持socket 和 websocket

##### 1.0.39 更新记录
* app.loadBean 更名为：app.beanScan
* app.makeBean 更名为：app.beanMake
* 取消 Aop 的上述两个方法
* 将bean加载完成事件，独立为：beanLoaded()，安排在app 加载完成时执行


##### 1.0.38 更新记录
* dubbo-solon-plugin, dubbo降为2.7.5（2.7.6有问题）
* smart-http，升为1.0.17

##### 1.0.37 更新记录
* Bean的加载增加新模式
* beanLoad（基于源加载）
* beanMake模式（基于类加载::新增）
* 增加控制器自己实现 XRender；从而定制渲染
* @Cache 添加 get, put 属性（可以控制只读，或只写）

##### 1.0.34 更新记录
* 增加父类注入支持

##### 1.0.30 更新记录
* XConfiguration类产生的 Bean 添加事件通知（可以及时一些Bean，进行后续加工；如：数据源）
* 取消XEvent注解，改由XBean 或 XConfiguration 替代 

##### 1.0.28 更新记录
* 添加 XUtil::throwableUnwrap，解决异常拆包
* 添加 XBean::attrs() 用于附助与置
* 添加 XBean::typed() 用于同时注册名字与类型
* BeanWrap 对应添加 attrs()
* 添加 SolonProperties::loadAdd(url)

##### 1.0.24 更新记录
* 添加 solon.extend.data 项目
* 重写注解事务支持（简化替代开发）
* 添加缓存注解支持
* 添加 solon.extend.validation 项目
* 提供验证注解支持


##### 1.0.18 更新记录
* 添加 XBridge，用于统一内部扩展
* 添加 XUpstream 接口，用于统一服务发现的接口；方便一些对接

##### 1.0.14 更新记录
* 完善事务，添加四个新的册略（与spring差不多）
* XTran 的 multisource() 更名为 group()

##### 1.0.13 更新记录
* 添加@XTran及事务管理支持
* 优化拦截器，将@XInterceptor的前后控制，移到@XMapping。一个类可以同时定义前后拦截
* 增加action信息的透传，之前已透传controller
* XBefore和XAfter改为可继承
* 添加bean包装库的遍历

##### 1.0.10 更新记录
* 优化bean的注册机制（带name的bean，不做类型映射）
* 由函数构建的bean，用新beanWrap进行包装
* 新增 XEventBus 系统（取消旧的 XMonitor）
* 重写 Aop 内部实现
* 静态文件代理，增加 solon.mime.xxx = "xxx/xxx" 配置支持
* 注入配置，增加@XInject("${classpath:user.yml}")支持

##### 1.0.9 更新记录
* 增加构建函数注入能力 仅在 @XConfiguration 类里有效
* 增加VarHolder新接口，用于接收数据注入
* 为stop增加延时支持
* 添加solon-web包，取消solon-api, solon-mvc, solon-mvc-beetl（之前太散了）
* 完善undertow jsp的支持，增加支持jstl
* 完成mybatis适配

##### 1.0.8 更新记录
* 整合 XNav 和 UapiGateway 为 XGateway
* 将Bean默认定为非单例
* 添加jsr330扩展插件

##### 1.0.7 更新记录
* 重写XAction执行机制
* 增加MethodExecutor接口（函数执行器）
* 增加XAction对[ct=application/json]的支持
* 增加XAction对[ct=application/hessian]的支持
* HttpUpstream 列名为：XUpstream
* XUpstream 增加备用节点概念
* 添加XContext.result + XContext.getRendered（组合使用控制渲染处理）

##### 1.0.6 更新记录
* 控制器添加 BigDecimal, BigInteger 支持
* 配置加载器，增加 text 的加载（支持yaml,properties,json）
* 允许XBean函数为空（只运行，不进工厂）
* 上下文的header统一到 headerMap() 后再获取；确保所有适配器的逻辑统一
* 初始完善UAPI扩展插件
* 增加isDriftMode()，ip漂移模式
* 增加@XInit注解
* 增加以$name方式从ctx.attr()获取并注入控制器
* 增加支持 short,int,long,float,double,boolean
* 增加XBean.tag()，为完全不同的bean增加tag归类

##### 1.0.5 更新记录
* 调整 Context.attrMap() 为 public
* 优化 ActionUtil.exeMethod() 内部处理逻辑
* 优化 org.noear.solon.extend.schedule ；添加并发线程数的控制
* 优化 SolonProperties 与 System.getProperties() 的交互
* solon.extend.staticfiles 增加开关控制；可动态关掉
* Context添加headerAdd(k,v)

##### 1.0.4 更新记录::
* 添加XConfiguration,BeanBuilder，用于动态构建XBean
* 完成所有模板引擎的调试模式

##### 1.0.3 更新记录::
* 增加渲染管理器，通过它来实现多模板引擎共存效果
* 添加XSessionState接口，以实现session 可切换效果（如切换为分布式Session）
* 优化路径路由器
* XMethod 改为 enum 类型
* 拦截器，添加多路拦截和排序支持支持
* 原视图渲染器取消对json的支持（改由专门的序列化插件；以后灵活切换）
* 取消 XContext.output() 部分显示异常，改由内部RuntimeEx..
* 取消 rpc 的概念，用 remoting 和 solon.reader.mode=serialize 代替。同时改则 @XBean(remoting=true) 来注解
* XMapping 的 XMethod 改为多选模式（原为单选）
* 将 Solon.render(obj,ctx) 转移到  XContext.render(obj)（使用更自然）
* 模板引擎 添加 XApp.share() 同步支持（可以通过共享对象接口，为所有引擎动态添加变量）
* 增加扩展文件夹加载支持（运行时,如要加载额外的配置或jar包，可通过机制此实现）
* 增加XMonitor统一收集异常，并转发异常事件
* 通过XBean("view:xxx")做为统一的自定义视图标签收集器,通过"share:"统一收集共享变量
* 添加XContext.contentTypeNew(),close(),commit()

