> 在前面的篇章里我们已经见识了 Springboot mini - Solon 对事务的控制，及其优雅曼妙的身姿。该篇将对事务及其处理策略进行详解。出于对用户的学习成本考虑，Solon 借签了Spring 的事务传播策略；所以体验上几乎一样。

### 一、为什么要有传播机制？

Solon 对事务的控制，是使用 aop 切面实现的，所以不用关心事务的开始，提交 ，回滚，只需要在方法上加 `@Tran` 注解即可。
因为这些都是暗的，看不见的，所以也容易产生一些疑惑：

* 场景一：classA 方法调用了 classB 方法，但两个方法都有事务
```
如果 classB 方法异常，是让 classB 方法提交，还是两个一起回滚？
```

* 场景二：classA 方法调用了 classB 方法，但是只有 classA 方法加了事务
```
是否把 classB 也加入 classA 的事务，如果 classB 异常，是否回滚 classA？
```

* 场景三：classA 方法调用了 classB 方法，两者都有事务，classB 已经正常执行完，但 classA 异常
```
是否需要回滚 classB 的数据？
```

这个时候，传说中的事务传播机制和策略就派上用场了

### 二、传播机制生效条件

所有用 aop 实现的事务控制方案 ，都是针对于接口或类的。所以在同一个类中两个方法的调用，传播机制是不生效的。


### 三、传播机制的策略

下面的类型都是针对于被调用方法来说的，理解起来要想象成两个 class 方法的调用才可以。


|  传番策略 | 说明 | 
| -------- | -------- | 
| TranPolicy.required     | 支持当前事务，如果没有则创建一个新的。这是最常见的选择。也是默认。     |
| TranPolicy.requires_new     | 新建事务，如果当前存在事务，把当前事务挂起。     |
| TranPolicy.nested     | 如果当前有事务，则在当前事务内部嵌套一个事务；否则新建事务。     |
| TranPolicy.mandatory     | 支持当前事务，如果没有事务则报错。     |
| TranPolicy.supports     | 支持当前事务，如果没有则不使用事务。     |
| TranPolicy.not_supported    | 以无事务的方式执行，如果当前有事务则将其挂起。     |
| TranPolicy.never    | 以无事务的方式执行，如果当前有事务则报错。     |

### 四、事务的隔离级别

| 属性 | 说明 | 
| -------- | -------- | 
| unspecified     | 默认（JDBC默认）    | 
| read_uncommitted     | 脏读：其它事务，可读取未提交数据     | 
| read_committed     | 只读取提交数据：其它事务，只能读取已提交数据  | 
| repeatable_read     | 可重复读：保证在同一个事务中多次读取同样数据的结果是一样的  | 
| serializable     | 可串行化读：要求事务串行化执行，事务只能一个接着一个执行，不能并发执行  | 

### 五、@Tran 属性说明


| 属性 | 说明 | 
| -------- | -------- | 
| policy     | 事务传导策略     | 
| isolation     | 事务隔离等级     | 
| readOnly     | 是否为只读事务  | 



### 六、示例

* 父回滚，子回滚

```java
@Service
public class UserService{
    @Tran
    public void addUser(UserModel user){
        //....
    }
}

@Controller
public class DemoController{
    @Inject
    UserService userService; 
    
    //父回滚，子回滚
    //
    @Tran
    @Mapping("/user/add2")
    pubblic void addUser2(UserModel user){
        userService.addUser(user); 
        throw new RuntimeException("不让你加");
    }
}
```

* 父回滚，子不回滚

```java
@Service
public class UserService{
    @Tran(policy = TranPolicy.requires_new)
    public void addUser(UserModel user){
        //....
    }
}

@Controller
public class DemoController{
    @Inject
    UserService userService; 
    
    //父回滚，子不回滚
    //
    @Tran
    @Mapping("/user/add2")
    pubblic void addUser2(UserModel user){
        userService.addUser(user); 
        throw new RuntimeException("不让你加；但还是加了:(");
    }
}
```

* 子回滚父不回滚

```java
@Service
public class UserService{
    @Tran(policy = TranPolicy.nested)
    public void addUser(UserModel user){
        //....
        throw new RuntimeException("不让你加");
    }
}

@Controller
public class DemoController{
    @Inject
    UserService userService; 
    
    //子回滚父不回滚
    //
    @Tran
    @Mapping("/user/add2")
    pubblic void addUser2(UserModel user){
        try{
            userService.addUser(user); 
        }catch(ex){ }
    }
}
```

* 多数据源事务示例

```java
@Service
public class UserService{
    @Db("db1")
    UserMapper userDao;
    
    @Tran
    public void addUser(UserModel user){
        userDao.insert(user);
    }
}

@Service
public class AccountService{
    @Db("db2")
    AccountMappeer accountDao;

    @Tran
    public void addAccount(UserModel user){
        accountDao.insert(user);
    }
}

@Controller
public class DemoController{
    @Inject
    AccountService accountService; 
    
    @Inject
    UserService userService; 
    
    @Tran
    @Mapping("/user/add")
    public void addUser(UserModel user){
        userService.addUser(user);     //会执行db1事务
        
        accountService.addAccount(user);    //会执行db2事务
    }
}
```


### 附：Solon项目地址

* gitee:  [https://gitee.com/noear/solon](https://gitee.com/noear/solon)
* github:  [https://github.com/noear/solon](https://github.com/noear/solon)





