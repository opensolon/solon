
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/fairy.svg)](https://mvnrepository.com/search?q=g:org.noear%20AND%20fairy)

` QQ交流群：22200020 ` 

# Fairy demo

```java
//
// Rpc接口申明
//
@FairyClient("test:/ComplexModelService/")
public interface IComplexModelService {
    //持久化
    void save(ComplexModel model);

    //读取
    ComplexModel read(Integer modelId);
}

//
// 使用示例
//

@XBean
public class Demo1{
    //
    // 直接注入，需要框架适配
    //
    @XInject
    IComplexModelService service;
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}

@XBean
public class Demo2{
    IComplexModelService service = Fairy.builder()
                                        .encoder(SnackTypeEncoder.instance)
                                        .decoder(HessionDecoder.instance)
                                        .filterAdd((cfg,url,h,a)->{
                                            h.put("X-Serialization","@hession");
                                        })
                                        .upstream(()->{
                                            return "http://localhost:8080";
                                        }).create(IComplexModelService.class);
    
    public void test(){
        ComplexModel tmp = service.read(1);
        service.save(tmp);
    }
}

```