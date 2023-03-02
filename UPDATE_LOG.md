### 纪年

* v0: 2018 ~ 2019 (2y)
* v1: 2020 ~ 2022 (3y)
* v2: 2023 ~

### v1.x 升到 v2.x 提醒

* 提醒1：之前没有使用弃用接口的，可以直接升级 <br>
* 提醒2：有使用弃用接口的。建议先升级到 1.12.4；替换弃用代码后，再升级到 2.0.0

### v2.2.1

* mybatis-solon-plugin，取消 mappers 检测异常，改为警告日志
* ContextPathFilter 增加与 cfg().serverContextPath 同步配置
* LifecycleBean 增加对 InitializingBean 的继承，使用时简便些
* 模板添加 templates 目录支持
* 移除 RenderUtil 类
* 降低内部手动添加 lifecycle 的执行排序，调为 -99 和 -98
* 延迟 captcha-solon-plugin 运行时机


### v2.2.0

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

### v2.1.0
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


### v2.0.0

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
