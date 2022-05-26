```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.extend.stop</artifactId>
</dependency>
```

#### 1、描述

基础扩展插件，为 solon 提供远程关掉服务的能力



#### 2、配置参考


```yml
#是否启用，默认为关闭
solon.stop.enable: false
#停止命令路径
solon.stop.path: "/_run/stop/"
#有权停目的主机ip，`*` 表示不限主机
solon.stop.host: "127.0.0.1"
```


#### 3、代码应用

```shell
#通过命令关掉服务，主要是运维提供帮助
curl http://127.0.0.1/_run/stop/
```
