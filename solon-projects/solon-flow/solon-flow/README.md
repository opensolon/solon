
solon-flow 提供一种低成本的开放流处理支持。支持 java 构建，json,yml,properties 配置

使用示例:

```java
FlowEngine flowEngine =  FlowEngine.newInstance();

flowEngine.load("classpath:script_case1.json")
flowEngine.load("classpath:script_case2.json")
flowEngine.load("classpath:script_case3.yml")


flowEngine.eval("c1");
flowEngine.eval("c1", new ChainContext());
```

### 1、配置字典

Chain 配置属性

| 属性    | 数据类型     | 描述     |
|-------|----------|--------|
| id    | `String` | 标识（必要） |
| title | `String` | 显示标题   |
| nodes | `Node[]` | 节点集合   |



Node 配置属性

| 属性       | 数据类型                                          | 描述                |
|----------|-----------------------------------------------|-------------------|
| id       | `String`                                      | 标识（必要）            |
| type     | `NodeType`                                    | 节点类型（必要）          |
| title    | `String`                                      | 显示标题              |
| meta     | `Map[String,Object]`                          | 元信息               |
| link     | `String` or `NodeLink` or `String[]`  or `NodeLink[]` | 链接（支持单值、多值；简写、全写） |
| task     | `String`                                      | 任务描述              |

NodeLink 配置属性


| 属性         | 数据类型                 | 描述         |
|------------|----------------------|------------|
| nextId     | `String`             | 后面节点标识（必要） |
| title      | `String`             | 显示标题       |
| meta       | `Map[String,Object]` | 元信息        |
| condition  | `String`             | 条件描述       |

### 2、节点类型（NodeType 成员）

|             | 描述       | 任务 | 链接条件 | 可流入链接数  | 可流出链接数   |
|-------------|----------|----|------|---------|---------|
| start       | 开始       | 可有 | /    | `0`     | `1`     | 
| execute     | 执行节点     | 可有 | /    | `1...n` | `1`     | 
| inclusive   | 包容网关（多选） | /  | 支持   | `1...n` | `1...n` | 
| exclusive   | 排它网关（单选） | /  | 支持   | `1...n` | `1...n` | 
| parallel    | 并行网关（全选） | /  | /    | `1...n` | `1...n` | 
| end         | 结束       | 可有 | /    | `1...n` | `0`     | 


### 3、上下文接口（ChainContext）

|                                      | 数据类型                 |              |
|--------------------------------------|----------------------|--------------|
| `isInterrupted()`                    | `bool`               | 是否已中断        |
| `interrupt()`                        |                      | 中断流转         |
|                                      |                      |              |
| `params()`                           | `Map<String,Object>` | 参数集合         |
| `paramSet(String key, Object value)` | `self`               | 参数设置         |
| `param(String key)`                  | `T`                  | 参数获取         |
| `paramOrDefault(key, def)`           | `T`                  | 参数默认         |
|                                      |                      |              |
| `result`                             | `Object`             | 执行结果（执行中可赋值） |


### 4、任务与条件

任务与条件的描述，采用开放格式（即没有格式约定）。格式由 ChainDriver 处理（或约定），使用哪个 ChainDriver 就采用哪个格式约定。就像 jdbc 的 Driver, mysql 和 pgsql 的语法即不同。

格式参考1（SimpleChainDriver 方案，框架内置）：

|        | 示例                                                          | 
|--------|-------------------------------------------------------------|
| 任务描述   | `@a`（任务组件风格） 或者 `context.result=1;` （脚本风格） 或者 `#c12`（链调用风格） | 
| 条件描述   | `@a`（条件组件风格） 或者 `user_id > 12`（脚本表达式风格）                     | 


格式参考2（RubberChainDriver 方案）：

|            | 示例                                        |
|------------|-------------------------------------------|
| 任务描述       | `F,tag/fun1;R,tag/rule1`（dsl 风格）          |
| 条件描述       | `m.user_id,>,12,A;m,F,$ssss(m),E`（dsl 风格） | 

