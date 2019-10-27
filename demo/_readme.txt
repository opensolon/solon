
##### 1.0.3 主要更新::
1.支持多视图共存（即一个项目里可同时使有用jsp,freemarker...多个模板）
2.支持Session可切换（如切换为分布式Session）
3.支持拦截器排序
4.支持序列化插件可切换（之前与视图插件合在一起）
5.添加应用共享变量概念，供不同插件使用
6.添加扩展文件夹概念（即可加载外置的jar和配置文件）

##### 1.0.3 更新记录::
* 1.增加渲染管理器，通过它来实现多模板引擎共存效果（就是支持多视图共存）
* 2.添加XSessionState接口，以实现session 可切换效果（如切换为分布式Session）
* 3.优化路径路由器
* 4.XMethod 改为 enum 类型
* 5.拦截器，添加多路拦截和排序支持
* 6.原视图渲染器取消对json的支持（改由专门的序列化插件；以后灵活切换）
* 7.取消 XContext.output() 部分显示异常，改由内部RuntimeEx..
* 8.取消 rpc 的概念，用 remoting 和 solon.reader.mode=serialize 代替。同时改则 @XBean(remoting=true) 来注解
* 9.XMapping 的 XMethod 改为多选模式（原为单选）
* a.将XApp.render(obj,ctx) 转移到  XContext.render(obj)（使用更自然）
* b.模板引擎 添加 XApp.share() 同步支持（可以通过共享对象接口，为所有引擎动态添加变量）
* c.增加扩展文件夹加载支持（运行时,如要加载额外的配置或jar包，可通过机制此实现）
