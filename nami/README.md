
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
@Component
public class Demo1{
    @NamiClient("test:/ComplexModelService/")
    IComplexModelService service;
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}

//适配test upstream
@Component("test")
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
                                        .uri("http://localhost:8080/ComplexModelService/")
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
                                        .uri("test:/ComplexModelService/")
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

| 字段 | 说明 | 
| -------- | -------- | 
| value     | Uri 申明（支持三种格式）     | 
| headers     | 添加头信息     | 
| configuration     | configuration 配置器     | 

Uri 申明的三种格式：
* url（ 例：`http://x.x.x/x/x/` ），此格式不支持upstream
* upstream:path（ 例：`local:/x/x/` ），此格式必须配合upstream
* upstream（ 例：`local` ），此格式必须配合upstream

headers 格式说明：
* `{"head1=a","head2=b"}`

##### @Mapping 注解说明

| 字段 | 说明 | 
| -------- | -------- | 
| value     | 映射值（支持两种格式）     | 

映射值的两种格式：
* method（ 例：`GET` ）
* method path（ 例：`PUT user/a.0.1` ）

##### @Mapping 没有时的说明，即默认
* 函数名将做为path
* 函数没有参数时，执行GET请求
* 函数有参数时，执行POST请求
