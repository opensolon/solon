

这个插件的适配分为两部分：


### 1、原生 jpa 接口

* 主要由添加个 SolonPersistenceProvider 实现。从而避开需要 xml 文件（也还可以有 hibernate 提供者支持）

```java
PersistenceProviderResolverHolder
                .getPersistenceProviderResolver()
                .getPersistenceProviders()
                .add(new SolonPersistenceProvider());
```

* 要完成与 @Tran 的事务对接（最好是不重写事务注解，能与其它 orm 同时存在）

### 2、solon.data.jpa

* jpa 接口，尽量保持与 spring.data.jpa 兼容（有好几个 jpa 需求的用户，提过这个）
  * 要从 spring 那边的接口定义重新 copy 一下。。。目前是从网上copy的，可能旧了
* 后面 jpa 接口可以独立为  solon.data.jpa