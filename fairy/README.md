
# Fairy

Solon 伴生项目，为 http rpc 或 rest api 提供 client 支持。

## 一、演示

#### 接口申明

```java
public interface IComplexModelService {
    //持久化
    void save(ComplexModel model);

    //读取
    ComplexModel read(Integer modelId);
}
```


#### 接口使用示例1（直接注入，需要XUpstream适配）

```java
@XBean
public class Demo1{
    @FairyClient("test:/ComplexModelService/")
    IComplexModelService service;
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}

//适配test upstream
@XBean("test")
public class TestUpstream implements XUpstream {
    @Override
    public String getServer() {
        return "http://localhost:8080";
    }
}

//切换更改默认配置器的代理，将编码器换掉
FairyConfigurationDefault.proxy = (c,b)->b.encoder(SnackTypeEncoder.instance);
```

#### 接口使用示例2

```java
public class Demo2{
    IComplexModelService service = Fairy.builder()
                                        .encoder(SnackTypeEncoder.instance)
                                        .uri("http://localhost:8080/ComplexModelService/")
                                        .create(IComplexModelService.class);
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}
```

#### 接口使用示例3

```java
public class Demo3{
    IComplexModelService service = Fairy.builder()
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

## 二、属性说明

| @FairyClient 字段 | 说明 | 
| -------- | -------- | 
| value     | uri 配置     | 
| headers     | 添加头信息     | 
| configuration     | configuration 配置器     | 

#### value(uri) 的三种格式：

* url（例：`http://x.x.x/x/x/`），此格式不支持upstream

* name:path（例：`local:/x/x/`），此格式必须配合upstream

* path（例：`/x/x`），此格式必须配合upstream
