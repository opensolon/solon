```xml

<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon-data-sqlink</artifactId>
</dependency>
```

### 1、描述

基于Lambda表达式树的orm框架，v3.0.3版本开始支持。

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

配置数据源后，可按数据源名直接注入：

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

### 3、查询操作

* 查询并获取值（只查一列）

```java
public void getValue() throws SQLException {
    //获取值
    Long val = sqlUtils.sql("select count(*) from appx")
            .queryValue();

    //获取值列表
    List<Integer> valList = sqlUtils.sql("select app_id from appx limit 5")
            .queryValueList();
}
```

* 查询并获取行

```java
public void getRow() throws SQLException {
    //获取行列表
    RowList rowList = sqlUtils.sql("select * from appx limit 2")
            .queryRowList();
    //获取行
    Row row = sqlUtils.sql("select * from appx where app_id=?", 11)
            .queryRow();

    //行的接口
    row.getObject(1);         //columIdx
    row.getObject("app_id");  //columLabel
    Map map = row.toMap();
    Appx app = row.toBean(Appx.calss);
}
```

* 查询并获取行迭代器（流式输出）

```java
public void getRowIterator() throws SQLException {
    String sql = "select * from appx";

    //（流读取）用完要 close //比较省内存
    try (RowIterator rowIterator = sqlUtils.sql(sql).queryRowIterator(100)) {
        while (rowIterator.hasNext()) {
            Appx app = rowIterator.next().toBean(Appx.class);
           ...
        }
    }
}
```

### 4、查询构建器操作

以上几种查询方式，都是一行代码就解决的。复杂的查询怎么办？比如管理后台的条件统计，可以先使用构建器：

```java
public List<Appx> findDataStat(int group_id, String channel, int scale) throws SQLException {
    SqlBuilder sqlSpec = new SqlBuilder();
    sqlSpec.append("select group_id, sum(amount) amount from appx ")
            .append("where group_id = ? ", group_id)
            .appendIf(channel != null, "and channel like ? ", channel + "%");

    //可以分离控制
    if (scale > 10) {
        sqlSpec.append("and scale = ? ", scale);
    }

    sqlSpec.append("group by group_id ");

    return sqlUtils.sql(sqlSpec).queryRowList().toBeanList(Appx.class);
}
```

管理后台常见的分页查询：

```java
public Page<Appx> findDataPage(int group_id, String channel) throws SQLException {
    SqlBuilder sqlSpec = new SqlBuilder()
            .append("from appx  where group_id = ? ", group_id)
            .appendIf(channel != null, "and channel like ? ", channel + "%");

    //备份
    sqlSpec.backup();
    sqlSpec.insert("select * ");
    sqlSpec.append("limit ?,? ", 10, 10); //分页获取列表

    //查询列表
    List<Appx> list = sqlUtils.sql(sqlSpec).queryRowList().toBeanList(Appx.class);

    //回滚（可以复用备份前的代码构建）
    sqlSpec.restore();
    sqlSpec.insert("select count(*) ");

    //查询总数
    Long total = sqlUtils.sql(sqlSpec).queryValue();

    return new Page(list, total);
}
```

构建器支持 `?...` 集合占位符查询：

```java
public List<Appx> findDataList() throws SQLException {
    SqlBuilder sqlSpec = new SqlBuilder()
            .append("select * from appx  where app_id in (?...) ", Arrays.asList(1, 2, 3, 4));

    //查询列表
    List<Appx> list = sqlUtils.sql(sqlSpec).queryRowList().toBeanList(Appx.class);
}
```

### 5、更新操作

* 插入

```java
public void add() throws SQLException {
    sqlUtils.sq("insert test(id,v1,v2) values(?,?,?)", 2, 2, 2).update();

    //返回自增主键
    long key = sqlUtils.sql("insert test(id,v1,v2) values(?,?,?)", 2, 2, 2)
            .updateReturnKey();
}
```

* 更新

```java
public void exe() throws SQLException {
    sqlUtils.sql("delete from test where id=?", 2).update();
}
```

* 批量执行（插入、或更新、或删除）

```java
public void exeBatch() throws SQLException {
    List<Object[]> argsList = new ArrayList<>();
    argsList.add(new Object[]{1, 1, 1});
    argsList.add(new Object[]{2, 2, 2});
    argsList.add(new Object[]{3, 3, 3});
    argsList.add(new Object[]{4, 4, 4});
    argsList.add(new Object[]{5, 5, 5});

    int[] rows = sqlUtils.sql("insert test(id,v1,v2) values(?,?,?)")
            .updateBatch(argsList);
}
```

### 6、接口说明

SqlUtils（Sql 工具类）

```java
public interface SqlUtils {
    static SqlUtils of(DataSource dataSource) {
        return new SimpleSqlUtils(dataSource);
    }

    /**
     * 执行代码
     *
     * @param sql  代码
     * @param args 参数
     */
    SqlExecutor sql(String sql, Object... args);

    /**
     * 执行代码
     *
     * @param sqlSpec 代码申明
     */
    default SqlExecutor sql(SqlSpec sqlSpec) {
        return sql(sqlSpec.getSql(), sqlSpec.getArgs());
    }
}
```

SqlExecutor （Sql 执行器）

```java
public interface SqlExecutor {
    /**
     * 查询并获取值
     *
     * @return 值
     */
    @Nullable
    <T> T queryValue() throws SQLException;

    /**
     * 查询并获取值列表
     *
     * @return 值列表
     */
    @Nullable
    <T> List<T> queryValueList() throws SQLException;

    /**
     * 查询并获取行
     *
     * @return 行
     */
    @Nullable
    Row queryRow() throws SQLException;

    /**
     * 查询并获取行列表
     *
     * @return 行列表
     */
    @Nullable
    RowList queryRowList() throws SQLException;

    /**
     * 查询并获取行遍历器（流式读取）
     *
     * @return 行遍历器
     */
    RowIterator queryRowIterator(int fetchSize) throws SQLException;


    /**
     * 更新（插入、或更新、或删除）
     *
     * @return 受影响行数
     */
    int update() throws SQLException;

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @return 受影响行数组
     */
    int[] updateBatch(Collection<Object[]> argsList) throws SQLException;


    /**
     * 更新并返回主键
     *
     * @return 主键
     */
    long updateReturnKey() throws SQLException;
}
```