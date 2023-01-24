#### 后续（可以提 Issue 增加需求）
* 新增 kubernetes-solon-cloud-plugin 插件
* 新增 jmdns-solon-cloud-plugin 插件
* 新增 shiro-solon-plugin 插件
* 新增 elastic-job-solon-plugin 分布式定时任务 ???
* 新增 thrift-solon-cloud-plugin 插件 ???
* 新增 swagger-solon-plugin 插件 ???
* 新增 knife4j-solon-plugin 插件 ???
* 调整 运行完成后，提供未注入的对象警告???
* 增加 更多友好的异常分类???
* 增加 便利的原生编译机制

#### 2.0.0
* 说明：删除的是已弃用多时的代码（建议先升级到 1.12.4，替换所有弃用代码；再升级到 2.0.0）
* 
* 调整 solon//
  * 删降 Aop；由 Solon.context() 替代
  * 删除 Bean:attr，Component:attr
  * 删除 BeanLoadEndEvent，PluginLoadEndEvent；由 AppBeanLoadEndEvent，AppPluginLoadEndEvent 替代
  * 删除 Utils.async()...等几个弃用接口；由 RunUtil 替代
  * 删除 Solon.global()；由 Solon.app() 替代
  * 删除 SolonApp::port()；由 Solon.cfg().serverPort() 替代
  * 删除 SolonApp::enableSafeStop()；由 Solon.cfg().enableSafeStop() 替代
  * 删作 AopContext::getProps()；由 ::cfg() 替代
  * 删除 AopContext::getWrapAsyn()；由 ::getWrapAsync() 替代
  * 删除 AopContext::subWrap()；由 ::subWrapsOfType() 替代
  * 删除 AopContext::subBean()；由 ::subBeansOfType() 替代
  * 删除 AopContext::getBeanAsyn()；由::getBeanAsync() 替代
  * 删除 Solon.cfg().version()；由 Solon.version() 替代
  * 删除 EventBus::pushAsyn()；由 pushAsync() 替代
  * 删除 PrintUtil::debug()，::info() 等...；由 LogUtil 替代
  * 删除 "application.xxx" 配置文件支持（只支持 app.xxx） ???
  * 删除 "solon.profiles.active" 配置支持；由 "solon.env" 替代
  * 删除 "solon.extend.config" 配置支持；由 "solon.config" 替代
  * 删除 "solon.encoding.request" 配置；由 "server.request.encoding" 替代
  * 删除 "solon.encoding.response" 配置；由 "server.request.response" 替代
  * 
  * 调整 DownloadedFile，UploadedFile 字段改为私有，由属性替代
* 调整 solon.i18n//
  * 删除 I18nBundle::toMap()；由 ::toProp() 替代
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
* 调整 solon.web.cors//
  * 删除 ..extend.cores 包；由 ..web.cors 包替代 
* 删除插件 httputils-solon-cloud-plugin；由 solon.cloud.httputils 替代
* 删除插件 solon.extend.stop；由 solon.web.stop 替代
* 删除插件 solon.extend.async；由 solon.scheduling 替代
* 删除插件 solon.schedule；由 solon.scheduling.simple 替代
* 删除插件 solon.extend.retry
* 删除插件 solon.extend.jsr330
* 删除插件 solon.extend.jsr303
* 
* 新增 solon.mvc 插件 ???
* 调整 solon.scheduling/JobManger 添加更多注册时检测
