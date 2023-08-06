```xml
<dependency>
    <groupId>org.noear</groupId>
    <artifactId>solon.web.stop</artifactId>
</dependency>
```

#### 1、描述

基础扩展插件，为 solon 提供远程关掉服务的能力



#### 2、配置参考


```yml
solon.stop:
  enable: false           #是否启用。默认为关闭
  path: "/_run/stop/"     #命令路径。默认为'/_run/stop/'
  whitelist: "127.0.0.1"  #白名单，`*` 表示不限主机。默认为`127.0.0.1`
```


#### 3、代码应用

```shell
#通过命令关掉服务，主要是运维提供帮助
curl http://127.0.0.1/_run/stop/
```
