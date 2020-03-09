s#### 1.0.4.4::
* 1. 添加XClassLoader，完成jar包加载的自由控制
* 2. 完成java8-java11的兼容测试
* 3. 添加XContext.paramsMap()

#### 1.0.4.3::
* 1. 字段注入支持多注解（如有冲突，需要自己处理）
* 2. 类注解支持多注解（如有冲突，需要自己处理）
* 3. 优化序列化管理器，支持多种序列化能力同时存在(xml,json,type_json,protobuf)

#### 1.0.4.1::
* 1. BeanBuilderAdd 更名为：beanInjectorAdd 
* 2. BeanLoaderAdd  更名为：beanCreatorAdd

#### 1.0.3.40::
* 1.修复添加BeanBuilderAdd名称错误(BeanBuilderadd)
* 2.修复 thymeleaf, velocity 调试模式支持
* 3.优化 AopFactory.wrap() 的代码结构

#### 1.0.3.39::
* 1.BeanWrap添加remoting()

#### 1.0.3.38::
* 1.优化AopFactory和BeanWrap
* 2.添加BeanBuilder（协助注入时处理）

#### 1.0.3.37::
* 1.添加XConfiguration（实现XBean动态构建能力）
* 2.添加ClassWrap,MethodWrap优化反射相关性能

#### 1.0.3.32::
* 1.添加定时任务支持
* 2.Xbean注册时，默认用className做为name

##### 1.0.3.24::
* 1.XPlugin 添加 defautl stop()接口
* 2.将插件stop 从Closeable 接口 改为 stop()

##### 1.0.3.22::
* 1.snack3升为：3.1.3
* 2.meven改为parent继承模式，所有版本与parent统一

#### 1.0.3.18::
* 1.XSessionState 添加 sessionClear()