### 一、Solon

最近号称小而美的的Solon框架，终于得空，搞了一把，发觉Solon确实好用，那Solon到底是什么，又是怎么好用呢？

#### 什么是Solon?

Solon参考过Spring boot 和 Javalin 的设计。吸取了两者的的优点，避开了很多繁重的设计，使其支持http, websocket, socket 三种通讯信号接入。Solon 2M即可支撑起普通的mvc开发了，0.1秒就可以完成启动；相对于言，Spring boot 的一个普通mvc项目，基本上20M起步，5秒左右完成启动。

总体来说，Solon 有着类似于 Spring boot的开发体验。

#### Solon的优缺点

**优点**

1. 小巧美丽
2. 快速构建项目
3. 项目可独立运行，不需要外部的web容器（jar直接部署）
4. 部署效率高

**缺点**

1. 文档少
2. 第三方项目的适配少（与Spring生态没法比）

### 二、快速入门

#### 1、Solon的Java bean配置方式

使用Solon，可以零配置就让你的项目快速运行起来，完全使用代码和注解取代配置。使用java代码方式可以更好的理解你配置的Bean，下面就先来看看两个最基本的注释：

##### 1）@XConfiguration 和 @XBean

Solon的java配置方式是通过@XConfiguration 和 @XBean这两个注释实现的：

* @XConfiguration 作用于类上，对Bean进行配置
* @XBean 用在类上，也可以作用在 @XConfiguration 类的方法上

##### 2）示例
> 该示例将通过java配置方式配置Bean，实现Solon IOC功能。

下面是一个简单的模拟从数据库获取User数据的Dao类（使用了@XBean注解，说明它将交给Solon容器管理）。

```java
@XBean 
public class UserDao {
    public List<String> queryUserList() {
        //为省事儿，此处不操作数据库
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("User " + i);
        }
        return list;
    }
}

//也可以通过配置器构建Bean
//
//@XConfiguration
//public class SolonConfig {
//    @XBean
//    public UserDao getUserDao() {
//        return new UserDao();
//    }
//}
```

然后是一个最最常见的Service，通过注入UserDao，使用UserDao的方法获取用户数据。

```java
@XBean
public class UserService {

    @XInject
    UserDao userDao;

    public void getUserList() {
        List<String> list = userDao.queryUserList();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

}
```

接下来就是启动Solon的容器服务, 然后从容器中拿到UserService,并调用其获取用户数据的方法,代码如下:

```java
public class Test {
    public static void main(String[] args) {
        //启动容器服务
        XApp.start(Test.class, args);
        
        //或通过Aop对象获取托管的Bean（或者注解方式）
        //
        UserService userService = Aop.get(UserService.class);
        userService.getUserList();
    }
}
```

像普通的java程序一样，直接运行Test类中的main方法即可在控制台看到用户数据输出了。

> 应该可以发现了,和以往的Spring boot 很像，又很不一样。

#### 2、第一个Web应用

通过上面的示例,我们已经知道了Solon的java配置方式是怎么回事了,那接下来便正式开始使用Solon来开发我们的第一个web应用了.

##### 1）pom.xml配置

设置solon的parent

```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.0.40</version>
</parent>
```

> 设置solon的parent配置不是必须的，但包含了大量默认的配置，可简化我们的开发。

导入solon的web支持

```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-web</artifactId>
    <type>pom</type>
</dependency>
```

通过上面简单的2步配置,Solon就配置完毕了,是相当简洁的呢？

##### 2）小示例
```java
@XController    //这标明是一个solon的控制器
public class HelloApp {
    public static void main(String[] args) {    //这是程序入口
        XApp.start(HelloApp.class, args);
    }

    @XMapping("/hello")
    public String hello(String name){
        return "Hello world!";
    }
}
```

> Solon 程序的重点是要：在main函数的入口处，通过XApp.start(...) 启动Solon的容器服务，进而启动它的所有机能。

运行HelloApp中的main()方法，启动该web应用后，在地址栏输入"http://localhost:8080/hello"，就可以看到输出结果了。

```xml
Hello world!
```

### 三、疑问

到这里是不是已经大概感觉到了Solon的高效和简洁了？配置就是如此简单，功能就是如此强大，但通过上面一系列的讲解，是不是也会产生一些疑惑呢，比如：

1. Solon启动的过程，都干了啥？
2. WEB应用的默认端口就是8080，那这个端口要怎么修改呢？
3. 我们自定义的配置要如何读出来？
4. 页面重定向用什么接口？
5. 等等...

淡定，后续文章将会对一些常见的问题展开说明。
