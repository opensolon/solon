```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-data-sqlink</artifactId>
</dependency>
```

### 1、描述

solon官方支持，基于Lambda表达式树的orm框架，v3.0.3版本开始支持。

### 2、配置示例

配置数据源（具体参考：[《数据源的配置与构建》](/article/794)）

```yaml
solon.dataSources:
  db1!:
    class: "com.zaxxer.hikari.HikariDataSource"
    jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
  db2:
    class: "com.zaxxer.hikari.HikariDataSource"
    jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
    driverClassName: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
```

配置maven

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven-compiler.version}</version>
            <configuration>
                <!--必须要配置，否则不生效-->
                <compilerArgs>
                    <arg>-Xplugin:ExpressionTree</arg>
                </compilerArgs>
                <annotationProcessorPaths>
                    <!--必须要配置，否则会有意外情况-->
                    <path>
                        <groupId>org.noear</groupId>
                        <artifactId>solon-data-sqlink</artifactId>
                    </path>
                    <!-- lombok -->
                    <!--<path>-->
                    <!--    <groupId>org.projectlombok</groupId>-->
                    <!--    <artifactId>lombok</artifactId>-->
                    <!--    <version>${lombok.version}</version>-->
                    <!--</path>-->
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

配置sqlink

```yaml
solon.data.sqlink:
  db1:
  # 打印sql
  printSql: true
  # 打印批量sql
  printBatch: false
  # 是否允许无where更新
  ignoreUpdateNoWhere: false
  # 是否允许无where删除
  ignoreDeleteNoWhere: false
  db2:
  # 无配置时以上为默认选项
```

配置数据源后，可按数据源名直接注入

```java
import org.noear.solon.data.sqlink.SqLink;

@Component
public class DemoService {
    @Inject //默认数据源
    SqLink sqLink1;

    @Inject("db2") //db2 数据源
    SqLink sqLink2;
}
```

### 2.1 注解说明

`@Table`:数据库表注解

| 特性     | 参数  | 参数类型   | 默认值 | 功能    |
|--------|-----|--------|-----|-------|
| value  | 表名  | String | 空   | 数据库表名 |
| schema | 模式名 | String | 空   | 选择的模式 |

`@IgnoreColumn`：用于表示字段与表无关的注解（当你想要忽略某个字段）

`@Column`:列注解

| 特性         | 参数      | 参数类型   | 默认值   | 功能        |
|------------|---------|--------|-------|-----------|
| value      | 列名      | String | 空     | 代表列名      |
| primaryKey | 是否为主键   | bool   | false | 是否是主键     |
| notNull    | 是否非null | bool   | false | 是否NOTNULL |                 

`@Navigate`:用于表示关联关系的注解

| 字段            | 参数               | 类型                             | 默认值                 | 说明                                      |
|---------------|------------------|--------------------------------|---------------------|-----------------------------------------|
| value         | 关联关系枚举           | RelationType                   | 无                   | 用于表示当前类与目标类的关联关系，有四种关系（一对一，一对多，多对一，多对多） |
| self          | 选择的自身字段的名称       | String                         | 无                   | 自身类的关联关系的java字段名                        |
| target        | 选择的目标字段的名称       | String                         | 无                   | 目标类的关联关系的java字段名                        |
| mappingTable  | 中间表的类对象          | Class<? extends IMappingTable> | IMappingTable.class | 多对多下必填,中间表，需要继承IMappingTable            |
| selfMapping   | 选择的自身对应中间表的字段的名称 | String                         | 无                   | 多对多下必填,自身类对应的mappingTable表java字段名       |
| targetMapping | 选择的目标对应中间表的字段的名称 | String                         | 无                   | 多对多下必填,目标类对应的mappingTable表java字段名       |

`@UseTypeHandler`:用于表示对字段使用指定的类型处理器

| 特性    | 参数                  | 参数类型                          | 默认值 | 功能             |
|-------|---------------------|-------------------------------|-----|----------------|
| value | 实现了ITypeHandler的类对象 | Class<? extends ITypeHandler> | 无   | 指定对该字段使用的类型处理器 |

`@InsertDefaultValue`:插入数据库时字段的默认值

| 特性       | 参数       | 参数类型                              | 默认值 | 功能                          |
|----------|----------|-----------------------------------|-----|-----------------------------|
| strategy | 生成默认值的策略 | GenerateStrategy                  | 无   | 指定生成默认值的来源，可以是来着数据库或静态值或动态值 |
| value    | 静态值      | String                            | 无   | 策略指定为静态值时使用                 |                    |
| dynamic  | 动态值      | Class<? extends DynamicGenerator> | 无   | 策略指定为动态值时使用                 |

`@OnPut`:列字段相关的拦截注解

| 特性    | 参数     | 参数类型                         | 默认值 | 功能                    |
|-------|--------|------------------------------|-----|-----------------------|
| value | 拦截器类对象 | Class<? extends Interceptor> | 无   | 列字段相关的值进入数据库前触发拦截器的调用 |

`@OnGet`:列字段相关的拦截注解

| 特性    | 参数     | 参数类型                         | 默认值 | 功能                     |
|-------|--------|------------------------------|-----|------------------------|
| value | 拦截器类对象 | Class<? extends Interceptor> | 无   | 列字段相关的值从数据库取出后触发拦截器的调用 |

### 2.2 自定义类型处理器

需要继承`ITypeHandler<T>`并且完全实现泛型

定义一个用来处理List<String>的类型处理器

```java
import org.noear.solon.annotation.Component;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// 加上@Component注解可以注册到容器里，这意味着全局的同类型字段都会调用这个类型处理器
// 如果是用来给UseTypeHandler的话就不需要注册
@Component
public class ListStringTypeHandler implements ITypeHandler<List<String>> {

    // 实现从ResultSet取出数据的逻辑
    @Override
    public List<String> getValue(ResultSet resultSet, int index, Type type) throws SQLException {
        String string = resultSet.getString(index);
        return Arrays.stream(string.split(",")).collect(Collectors.toList());
    }

    // 实现将数据填充到PreparedStatement的逻辑
    @Override
    public void setValue(PreparedStatement preparedStatement, int index, List<String> strings) throws SQLException {
        preparedStatement.setString(index, String.join(",", strings));
    }

    // 实现将@InsertDefaultValue注解下的字符串值转换到实际的值的逻辑
    @Override
    public List<String> castStringToTarget(String value) {
        return Arrays.stream(value.split(",")).collect(Collectors.toList());
    }
}
```

注意：支持替换框架内默认的类型处理(int,String...)

### 2.3 自定义拦截器

需要继承`Interceptor<T>`并且完全实现泛型

定义一个需要给密码字段加密的拦截器

```java
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.intercept.Interceptor;

public class Encryption extends Interceptor<String> {
    @Override
    public String doIntercept(String value, SqLinkConfig config) {
        return encrypt(value);
    }

    private String encrypt(String password) {
        // 加密逻辑
    }
}
```

再定义一个解密的拦截器

```java
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.intercept.Interceptor;

public class Decryption extends Interceptor<String> {
    @Override
    public String doIntercept(String value, SqLinkConfig config) {
        return decrypt(value);
    }

    private String decrypt(String value) {
        // 解密逻辑
    }
}
```

使用实例

```java
import demo.sqlink.interceptor.Encryption;
import demo.sqlink.interceptor.Decryption;

@Table("user")
public class User {
    private long id;
    private String username;
    @OnPut(Encryption.class)
    @OnGet(Decryption.class)
    private String password;
}
```

### 2.4 默认值注解的使用

`@InsertDefaultValue`注解可以在插入数据库时为没有值的字段提供默认值，有三种策列

+ 数据库提供
+ 字符串值
+ 动态生成器获取值

选择数据库提供时会直接忽略这个字段（常用于自增主键）

选择动态生成器需要继承`DynamicGenerator<T>`并且完全实现泛型

定义一个uuid生成器
```java
package org.noear.solon.data.sqlink.base.generate;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;

import java.util.UUID;

public class UUIDGenerator extends DynamicGenerator<String> {
    @Override
    public String generate(SqLinkConfig config, FieldMetaData fieldMetaData) {
        return UUID.randomUUID().toString();
    }
}
```

使用实例

```java
import org.noear.solon.data.sqlink.annotation.GenerateStrategy;
import org.noear.solon.data.sqlink.annotation.InsertDefaultValue;import org.noear.solon.data.sqlink.base.generate.UUIDGenerator;

@Table("user")
public class User {
    // 数据库,假设这里是自增
    @InsertDefaultValue(strategy = GenerateStrategy.DataBase)
    private long id;
    // 静态值
    @InsertDefaultValue(strategy = GenerateStrategy.Static,value = "新用户")
    private String username;
    // 动态值
    @InsertDefaultValue(strategy = GenerateStrategy.Dynamic,dynamic = UUIDGenerator.class)
    private String uuid;
}
```

### 3、增删查改操作

实体类

```java
import org.noear.solon.data.sqlink.annotation.*;

@Table("user")
public class User {
    // 主键
    @Column(primaryKey = true, notNull = true)
    // 数据库提供，这里是自增
    @InsertDefaultValue(strategy = GenerateStrategy.DataBase)
    private long id;
    private String username;
    private String password;
    private String email;
}
```

* 插入数据

```java
import demo.sqlink.model.User;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;

import java.util.Arrays;

@Component
public class InsertDemoService {
    @Inject 
    SqLink sqLink;

    // 插入一条
    public long insert(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return sqLink.insert(user).executeRows();
    }

    // 插入多条，自动启用批量
    public long batchInsert() {
        User user1 = new User();
        user1.setUsername("solon");
        user1.setPassword("aaa");
        User user2 = new User();
        user2.setUsername("noear");
        user2.setPassword("bbb");
        User user3 = new User();
        user3.setUsername("没有耳朵");
        user3.setPassword("ccc");
        return sqLink.insert(Arrays.asList(user1, user2, user3)).executeRows();
        // or sqLink.insert(user1).insert(user2).insert(user3).executeRows();
    }
}
```

* 更新数据

```java
import demo.sqlink.model.User;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;

@Component
public class UpdateDemoService {
    @Inject
    SqLink sqLink;

    // 根据id更新email
    public void updateEmailById(int id, String newEmail) {
        // UPDATE user SET email = {newEmail} WHERE id = {id}
        sqLink.update(User.class)
                .set(u -> u.setEmail(newEmail))
                .where(u -> u.getId() == id)
                .executeRows();
    }

    // 根据id更新name和email
    public void updateNameAndEmailById(int id, String newName, String newEmail) {
        // UPDATE user SET email = {newEmail}, username = {newName} WHERE id = {id}
        sqLink.update(User.class)
                .set(u -> {
                    u.setEmail(newEmail);
                    u.setUsername(newName);
                })
                .where(u -> u.getId() == id)
                .executeRows();
    }
}
```

* 删除数据

```java
import demo.sqlink.model.User;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;

@Component
public class DeleteDemoService {
    @Inject
    SqLink sqLink;

    // 根据id删除
    public long deleteById(int id) {
        return sqLink.delete(User.class)
                .where(u -> u.getId() == id)
                .executeRows();
    }

    // 根据id和name删除
    public long deleteByName(int id, String name) {
        return sqLink.delete(User.class)
                .where(u -> u.getId() == id && u.getUsername() == name)
                .executeRows();
    }

    // 删除所有错误的邮箱:(
    public long deleteByBadEmail() {
        return sqLink.delete(User.class)
                // NOT (email LIKE CONCAT('%','@','%'))
                .where(u -> !u.getEmail().contains("@"))
                .executeRows();
    }
}
```

* 查询数据

```java
import demo.sqlink.model.User;
import demo.sqlink.vo.UserVo;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;
import org.noear.solon.data.sqlink.api.Result;
import org.noear.solon.data.sqlink.core.sqlExt.SqlFunctions;

import java.util.List;

@Component
public class SelectDemoService {
    @Inject
    SqLink sqLink;

    // 根据id查询一位用户
    public User findById(long id) {
        return sqLink.query(User.class)
                .where(user -> user.getId() == id)
                .first();
    }

    // 根据名称和email查询一位用户
    public User findByNameAndEmail(String name, String email) {
        return sqLink.query(User.class)
                .where(u -> u.getUsername() == name && u.getEmail() == email)
                .first();
    }

    // 根据名称查询模糊匹配用户
    public List<User> findByName(String name) {
        return sqLink.query(User.class)
                // username LIKE '{name}%'
                .where(u -> u.getUsername().startsWith(name))
                .toList();
    }

    // 根据名称查询模糊匹配用户, 并且以匿名对象形式返回我们感兴趣的数据
    public List<? extends Result> findResultByName(String name) {
        return sqLink.query(User.class)
                // username LIKE '{name}%'
                .where(u -> u.getUsername().startsWith(name))
                .select(u -> new Result() {
                    long id = u.getId();
                    String email = u.getEmail();
                }).toList();
    }

    // 或者使用Vo返回
    public List<UserVo> findUserVoByName(String name) {
        return sqLink.query(User.class)
                .where(u -> u.getUsername().startsWith(name))
                .select(UserVo.class).toList();
    }
}
```



