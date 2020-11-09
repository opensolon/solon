#### 相关的源码

[https://gitee.com/noear/solon_demo/tree/master/demo08.solon_mybatis_multisource](https://gitee.com/noear/solon_demo/tree/master/demo08.solon_mybatis_multisource)


#### 故事开讲

Mybatis 是个资深的前辈，多年来它基本上只和 Spring 的家族企业合作。今天他尝试和年轻选手Solon组团做业务；Solon 是Java世界里一个新的极易上手的Web框架（哎，如同十八线的演员，没人知道的啦。。。但业务活也是一流的）

本次组队需要完成如下挑战：

1. 简单的配置
2. 多数据源支持（分区模式 和 注解模式）
2. 事务支持
3. 支持分页组件（这个，其实破坏了SQL的透明性......但业内很流行）


Action...


#### 一、环境说明


| 环境 | 版本 | 
| -------- | -------- | 
| IDEA     | 2020.2     | 
| Maven     | 4.0     | 
| Solon     | 1.1.10    | 
| mybatis-solon-plugin | 1.1.10  |
| mybatis-sqlhelper-solon-plugin | 1.1.10  |
| Mybatis     | 5.3.3     | 
| JDK     | 1.8     | 

#### 二、现在代码走起

新建个空白的Maven项目：`solon_mybatis_multisource`，下面开始操作：

* （一）在 `pom.xml` 文件里添加依赖

```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.1.10</version>
    <relativePath />
</parent>

<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
        <type>pom</type>
    </dependency>

    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>mybatis-solon-plugin</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>mybatis-sqlhelper-solon-plugin</artifactId>
    </dependency>
    
    <!-- 其它依赖参考源码，不然占板面太多了  -->
</dependencies>
```

* （二）修改属性文件 `application.yml` （添加多数据源和分页组件的配置）

Solon 没有特定的数据源配置，所以随便自己起个头就可以；配置项与使用的数据源匹配即可。本例用的是`HikariCP`：

```yaml
#数据库1的配置
test.db1:
    schema: rock
    jdbcUrl: jdbc:mysql://localdb:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: demo
    password: UL0hHlg0Ybq60xyb

#数据库2的配置（其实我用的是同一个库）
test.db2:
    schema: rock
    jdbcUrl: jdbc:mysql://localdb:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: demo
    password: UL0hHlg0Ybq60xyb


#默认（与数据源名一一对应）
mybatis.db1:
    typeAliases:    #支持包名 或 类名（.class 结尾）
        - "webapp.model"
    mappers:        #支持包名 或 类名（.class 结尾）或 xml（.xml结尾）
        - "webapp.dso.mapper.AppxMapper.class"

#再定义个新配置（为了体现多数据源性 - 应该简单吧?）
mybatis.db2:
    typeAliases:
        - "webapp.model"
    mappers:
        - "webapp.dso.mapper.Appx2Mapper.class"


#分页组件的配置
sqlhelper:
    mybatis:
        instrumentor:
            dialect: "mysql"
            cache-instrumented-sql: true
            subquery-paging-start-flag: "[PAGING_StART]"
            subquery-paging-end-flag: "[PAGING_END]"
        pagination:
            count: true
            default-page-size: 10
            use-last-page-if-page-no-out: true
            count-suffix: _COUNT
```

* （三）添加配置器（完成数据源的构建即可；看上去，极简洁的。。）

在Solon的适配下，只需要完成数据源的配置就完事了。其它的，根据配置已自动扫描或处理。

```java
@XConfiguration
public class Config {
    //
    //数据源名字与mybatis的配置名要对应上
    //
    @XBean(value = "db1", typed = true)
    public DataSource db1(@XInject("${test.db1}") HikariDataSource ds) {
        return ds;
    }

    @XBean("db2")
    public DataSource db2(@XInject("${test.db2}") HikariDataSource ds) {
        return ds;
    }
}
```

* （四）添加控制器

关于多数据源的分包模式示例：

```java
/**
 * 分包模式，一开始就被会话工厂mapperScan()并关联好了
 * */
@XMapping("/demo/")
@XController
public class DemoController {
    @XInject
    AppxMapper appxMapper;      //已被db1 mapperScan 了(内部自动处理)，可直接注入

    @XInject
    Appx2Mapper appxMapper2;    //已被db2 mapperScan 了(内部自动处理)，可直接注入

    @XMapping("test")
    public AppxModel test(){
        return appxMapper.appx_get();
    }

    @XMapping("test2")
    public AppxModel test2(){
        return appxMapper2.appx_get2(48);
    }

}
```

关于多数据源的注解模式示例：

```java
/**
 * 注解模式，通过@Db注入，并指定具体的数据源
 *
 * @Db 可注入 Mapper, SqlSession, SqlSessionFactory 类型的字段
 * */
@XMapping("/demo2/")
@XController
public class Demo2Controller {
    @Db("db1")
    AppxMapper appxMapper;     //使用@Db 指定会话工厂并注入

    @Db("db2")
    Appx2Mapper appxMapper2;

    @XMapping("test")
    public AppxModel test(){
        return appxMapper.appx_get();
    }

    @XMapping("test2")
    public AppxModel test2(){
        return appxMapper2.appx_get2(48);
    }

}
```

关于事务的示例：（分布式环境下，尽量用消息代理JDBC事务）

```java
/**
 * 事务演示
 * */
@XMapping("/tran/")
@XController
public class TranController {
    @XInject
    AppxMapper appxMapper;

    /**
     * mybatis-solon-plugin 的事务，由 @XTran 注解发起（更详细的说明，参考其它文章）
     * */
    @XTran
    @XMapping("test")
    public Object test() throws Throwable{
        appxMapper.appx_get();
    }
}
```

关于多数据源事务的示例，需要用到Service层：（分布式环境下，尽量用消息代理JDBC事务）

```java
/**
 * 多数据源事务演示
 * */
@XMapping("/tran2/")
@XController
public class Tran2Controller {
    @XInject
    AppService appService;   //这是定义的Service类，里面的函数注解了@XTran("db1")

    @XInject
    App2Service app2Service;    //同上


    /**
     * 这是一个多数据源的事务组
     * */
    @XTran
    @XMapping("test")
    public void test() throws Throwable {
        //内部走的是db2的事务
        app2Service.add();

        //内部走的是db1的事务
        appService.add();
    }
}
```

关于分页的示例：（本案用的是sqlhelper）

```java
@XMapping("/page/")
@XController
public class PageController {
    @XInject
    AppxMapper appxMapper;

    @XMapping("test")
    public Object test() throws Throwable{
        SqlPaginations.preparePagination(2,2);

       return appxMapper.appx_get_page();
    }
}
```

* （五）略过的一些代码文件（直接看开头的相关源码）


#### 故事结尾

加了事务注解后，更加的简洁优雅了。。。所有组团挑战全部完成，OY...
