
# Fairy

Solon 伴生项目，为 http rpc 或 rest api 提供 client 支持。

## Demo

#### 接口申明

```java
@FairyClient("test:/ComplexModelService/")
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
    @FairyClient
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
```

#### 接口使用示例2

```java
@XBean
public class Demo2{
    IComplexModelService service = Fairy.builder()
                                        .encoder(SnackTypeEncoder.instance)
                                        .decoder(HessionDecoder.instance)
                                        .upstream(()->{
                                            return "http://localhost:8080";
                                        }).create(IComplexModelService.class);
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}
```