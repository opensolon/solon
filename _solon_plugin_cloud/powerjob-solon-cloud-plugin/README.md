## Powerjob Solon CLoud Plugin

> 为 Solon Cloud 提供 Powerjob 的支持
>
> _since 2.0.0_

```xml

<dependency>
    <groupId>org.noear</groupId>
    <artifactId>powerjob-solon-cloud-plugin</artifactId>
</dependency>
```

### 特性

1. 支持 Powerjob 的所有功能
2. 默认注入了 `PowerJobClient`，可以直接使用(需要配置 `password`)
3. 通过 `@Cloudjob` 注解，可以直接在方法上创建Job

### 配置

```yaml
solon.app:
  name: powerjob-solon
  group: demo

#::server, password 移到了上级；其它属性在 job 下
solon.cloud.powerjob:
  # Address of PowerJob-server node(s). Ip:port or domain. Multiple addresses should be separated with comma.
  server: 127.0.0.1:7700
  # If use PowerjobClient to submit a job, you need to set this property.
  password: powerjob123
  job:
    # Transport port, default is 27777
    port: 28888
    # transport protocol between server and worker
    protocol: akka
    # Store strategy of H2 database. disk or memory. Default value is disk.
    storeStrategy: disk
    # Max length of result. Results that are longer than the value will be truncated.
    maxResultLength: 4096
    # Max length of appended workflow context . Appended workflow context value that is longer than the value will be ignore.
    maxAppendedWfContextLength: 4096
```

### Quick Start

#### 1.准备环境:

```shell
git clone --depth=1 https://github.com/PowerJob/PowerJob.git
cd PowerJob
docker-compose up
```

#### 2. 创建Job

1. 打开 http://localhost:7700/
2. 注册应用
3. 创建Job

> 如果不清楚怎么创建Job, 请参考 [PowerJob文档](https://www.yuque.com/powerjob/guidence).

#### 代码示例

详情代码: [Powerjob solon cloud demo](https://github.com/noear/solon-examples/tree/main/9.Solon-Cloud/demo9064-job_powerjob)

```java
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

public class DemoApp {
    public void main(String[] args) {
        SolonApp app = Solon.start(DemoApp.class, args);
    }

    /**
     * 会转为 PowerJobProxy implements BasicProcessor 处理
     * */
    @CloudJob("job2")
    public void job2(TaskContext taskContext) {
        System.out.println("xxxxx - job2");
    }
}
```

### 原理

1. 启动 `PowerJobWorkerOfSolon` 与服务的通信
2. 当有任务调度时，会调用 `ProcessorFactory` 的 `build` 方法获取容器中的 `bean`，这里是 `ProcessorFactoryOfSolon`
3. `ProcessorFactoryOfSolon` 就是从 Solon Ioc 容器中获取对应的 bean，然后调用 `process` 方法
4. 关于 `@CloudJob` 注解标记的方法可以直接注入 `TaskContext`
    1. `PowerJobProxy`的 `process` 注入参数 
    2. 可以参考 `ActionExecutorDefault#buildArgs` 匹配产生然后 invoke

### Changelog

1. 2023/01/29 - MVP 版本 `solon:2.0.0` ~ `powerjob:4.3.0`