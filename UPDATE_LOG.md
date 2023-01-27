### 记事

* v0: 2018 ~ 2019 (2y)
* v1: 2020 ~ 2022 (3y)
* v2: 2013 ~ 

### v1.x 升到 v2.x 提醒

* 提醒1：之前没有使用弃用接口的，可以直接升级 <br>
* 提醒2：有使用弃用接口的。建议先升级到 1.12.4；替换弃用代码后，再升级到 2.0.0

### v2.0.0

* 说明：第一个版只删除弃用代码，不加新功能
* 
* 调整 solon//
  * 删降 Aop；由 Solon.context() 替代
  * 删除 Bean:attr，Component:attr
  * 删除 BeanLoadEndEvent，PluginLoadEndEvent；由 AppBeanLoadEndEvent，AppPluginLoadEndEvent 替代
  * 删除 Utils.parallel()...等几个弃用接口；由 RunUtil 替代
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
  * 删除 "solon.profiles.active" 应用配置（只在某版临时出现过）；由 "solon.env" 替代
  * 删除 "solon.extend.config" 应用配置（只在某版临时出现过）；由 "solon.config" 替代
  * 删除 "solon.encoding.request" 应用配置（只在某版临时出现过）；由 "server.request.encoding" 替代
  * 删除 "solon.encoding.response" 应用配置（只在某版临时出现过）；由 "server.request.response" 替代
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
* 调整 solon.scheduling/JobManger 添加更多注册时检测
