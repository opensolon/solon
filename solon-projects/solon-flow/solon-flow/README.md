
solon-flow 提供一种低成本的流处理支持。支持 java 构建，json,yml,properties 配置

### 1、配置字典

Chain 配置属性

| 属性          | 数据类型         | 描述                       |
|-------------|--------------|--------------------------|
| id          | `String`          | 标识（必要）                       |
| title       | `String`       | 显示标题                     |
| driver      | `ChainDriver`  | 驱动（缺省为 SimpleFlowDriver） |



Node 配置属性

| 属性       | 数据类型                                          | 描述                |
|----------|-----------------------------------------------|-------------------|
| id       | `String`                                      | 标识（必要）            |
| type     | `NodeType`                                    | 节点类型（必要）          |
| title    | `String`                                      | 显示标题              |
| meta     | `Map<String,Object>`                          | 元信息               |
| link     | `String` or `Link` or `String[]`  or `Link[]` | 链接（支持单值、多值；简写、全写） |
| task     | `String`                                      | 任务描述              |

NodeLink 配置属性


| 属性        | 数据类型                 | 描述     |
|-----------|----------------------|--------|
| toId      | `String`                  | 标识（必要） |
| title     | `String`               | 显示标题   |
| meta      | `Map<String,Object>` | 元信息    |
| condition | `String`               | 条件描述   |

### 2、节点类型（NodeType 成员）

|            | 支持配置简写 | 描述        | 任务   | 链接数      | 链接条件 |
|------------|------|-----------|------|----------|------|
| start      |      | 开始        | 可有   | `1`      | 无效   |
| execute    | exec | 执行节点      | 可有   | `1`      | 无效   |
| inclusive  | inc  | 包容网关（多选）  |      | `1...n`  | 有效   |
| exclusive  | excl | 排它网关（单选）  |      | `1...n`  | 有效   |
| parallel   | par  | 并行网关（全选）  |      | `1...n`  | 无效   |
| end        |      | 结束        | 可有   | `0`      | 无效   |

### 3、任务与条件

任务与条件的描述，采用开放格式（即没有格式约定）。格式由 ChainDriver 处理（或约定），使用哪个 ChainDriver 就采用哪个格式约定。就像 jdbc 的 Driver, mysql 和 pgsql 的语法即不同。

格式参考1（SimpleFlowDriver 方案，框架内置）：

|        | 示例                                         | 
|--------|--------------------------------------------|
| 任务描述   | `@a`（任务组件引用） 或者 `context.result=1;` （脚本风格） | 
| 条件描述   | `user_id > 12`（脚本表达式风格）                    | 


格式参考2（RubberFlowDriver 方案）：

|            | 示例                                        |
|------------|-------------------------------------------|
| 任务描述       | `F,tag/fun1;R,tag/rule1`（dsl 风格）          |
| 条件描述       | `m.user_id,>,12,A;m,F,$ssss(m),E`（dsl 风格） | 

