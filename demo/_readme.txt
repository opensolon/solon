
1.0.3::
1.增加渲染工厂，通过它来实现多模板引擎共存效果
2.添加XSessionState接口，以实现session 可切换效果
3.改写路由器，参考javain
4.XMethod 改为 enum 类型
5.拦截器，添加index支持
6.视图渲染器取消对json的支持
7.取消 XContext.output() 部分显示异常
8.取消 rpc 的概念，用 remoting 和 solon.reader.mode=serialize 代替
9.XMapping 的 XMethod 改为多选模式
a.将XApp.render 转移到  XContext.render
b.模板引擎 添加 XApp.share() 同步支持
c.增加扩展文件夹加载支持