
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/nami.svg)](https://mvnrepository.com/search?q=g:org.noear%20AND%20nami)

` QQ交流群：22200020


# Nami

Solon 伴生项目，为 http rpc 或 rest api 提供 client 支持；兼容 http、socket、web socket三种通道。

## 一、演示

##### 接口申明

```java
public interface IComplexModelService {
    //持久化
    void save(ComplexModel model);

    //读取
    ComplexModel read(Integer modelId);
}
```


##### 接口使用示例1（直接注入，需要 LoadBalance 适配）

```java
@Managed
public class Demo1{
    @NamiClient(name="test", path="/ComplexModelService/")
    IComplexModelService service;
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}

//构建一个test负载均衡组件
@Managed("test")
public class TestUpstream implements LoadBalance {
    @Override
    public String getServer() {
        return "http://localhost:8080";
    }
}

//更改默认配置器的代理，将编码器换掉
NamiConfigurationDefault.proxy = (c,b)->b.encoder(SnackTypeEncoder.instance);
```

##### 接口使用示例2

```java
public class Demo2{
    IComplexModelService service = Nami.builder()
                                        .encoder(SnackTypeEncoder.instance)
                                        .url("http://localhost:8080/ComplexModelService/")
                                        .create(IComplexModelService.class);
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}
```

##### 接口使用示例3

```java
public class Demo3{
    IComplexModelService service = Nami.builder()
                                        .encoder(SnackTypeEncoder.instance)
                                        .path("/ComplexModelService/")
                                        .upstream(()->"http://localhost:8080")
                                        .create(IComplexModelService.class);
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}
```

## 二、注解与属性说明

##### @NamiClient 注解说明

| 字段 | 说明 | 示例 |
| -------- | -------- | -------- |
| url     | 完整的url地址     | http://api.water.org/cfg/get/ |
| | | |
| group     | 服务组     | water |
| name     | 服务名或负载均衡组件名（配合发现服务使用）     | waterapi |
| path     | 路径     | /cfg/get/ |
| | | |
| headers     | 添加头信息     | {"head1=a","head2=b"} |
| configuration     | configuration 配置器     |  |

注：原uri，分拆为：url 和 group+name+path（1.3版本及之后）

##### @Mapping 注解说明（注在函数上；默认不需要注解）

| 字段 | 说明 | 
| -------- | -------- | 
| value     | 映射值（支持种三情况）     | 

映射值的三种情况（包括没有注解）：
* 例1：没有注解：没有参数时执行GET，有参数时执行POST；path为函数名（此为默认）
* 例2：注解值为：`GET`，执行GET请求，path为函数名
* 例3：注解值为：`PUT user/a.0.1` ，执行PUT请求，path为user/a.0.1

##### @Mapping 没有时的说明，即默认
* 函数名将做为path
* 函数没有参数时，执行GET请求
* 函数有参数时，执行POST请求

##### @Body 注解说明（注在参数上）

| 字段 | 说明 | 
| -------- | -------- | 
| contentType     | 内容类型     | 

注在参数上，表示以此参数做为内容进行提交


## 三、相关组件清单

| 组件 | 说明 | 
| -------- | -------- | 
| org.noear:nami     | 内核     | 
|      |      | 
| org.noear:nami.channel.http.hutool     |  http通道    | 
| org.noear:nami.channel.http.okhttp     |  http通道     | 
| org.noear:nami.channel.socketd.jdksocket     |  socketd通道-基于bio实现    | 
| org.noear:nami.channel.socketd.netty     | socketd通道-基于nio,netty实现     | 
| org.noear:nami.channel.socketd.rsocket     | socketd通道-基于rsocket实现     | 
| org.noear:nami.channel.socketd.smartsocket     | socketd通道-基于aio实现     | 
| org.noear:nami.channel.socketd.websocket     |  socketd通道-基于websocket实现    | 
|      |      | 
| org.noear:nami.coder.fastjson     |  json编码器    | 
| org.noear:nami.coder.hessian     |  hessian编码器    |
| org.noear:nami.coder.jackson     |  jackson编码器    |
| org.noear:nami.coder.protostuff     |  protostuff编码器    |
| org.noear:nami.coder.snack3     |  json编码器    |


使用合，选一个通道+一个编码器使用
