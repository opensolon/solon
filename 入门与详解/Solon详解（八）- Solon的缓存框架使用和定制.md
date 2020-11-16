`solon.extend.data` 框加在完成 @Tran 注解的支持同时，还提供了 @Cache、@CacheRemove 注解的支持；可以为业务开发提供良好的便利性


Solon 的缓存注解只支持：Controller 、Service 、Dao 类下的方法。且借签了Weed3的简洁设计方案。

### （一）示例

从Demo开始，先感受一把

```java
@Controller
public class CacheController {
    /**
     * 执行结果缓存10秒，并添加 test_${label} 和 test1 标签
     * */
    @Cache(tags = "test_${label},test1" , seconds = 10)
    @Mapping("/cache/")
    public Object test(int label) {
        return new Date();
    }

    /**
     * 执行后，清除 标签为 test  的缓存（不过，目前没有 test 的示签...）
     * */
    @CacheRemove(tags = "test")
    @Mapping("/cache/clear")
    public String clear() {
        return "清除成功(其实无效)-" + new Date();
    }

    /**
     * 执行后，清除 标签为 test_${label}  的缓存
     * */
    @CacheRemove(tags = "test_${label}")
    @Mapping("/cache/clear2")
    public String clear2(int label) {
        return "清除成功-" + new Date();
    }
}
```

### （二）定制分布式缓存

Solon 的缓存标签，是通过CacheService接口提供支持的。定制起来也相当的方便，比如：对接Memcached...

#### 1. 了解 CacheService 接口：

```java
public interface CacheService {
    //保存
    void store(String key, Object obj, int seconds);

    //获取
    Object get(String key);

    //移除
    void remove(String key);
}
```

#### 2. 定制基于 Memcached 缓存服务：

```java
public class MemCacheService implements CacheService{
    private MemcachedClient _cache = null;
    public MemCacheService(Properties props){
        //略...
    }
  
    @Override
    public void store(String key, Object obj, int seconds) {
        if (_cache != null) {
            _cache.set(key, seconds, obj);
        }
    }
    
    @Override
    public Object get(String key) {
        if (_cache != null) {
            return _cache.get(key);
        } else {
            return null;
        }
    }
    
    @Override
    public void remove(String key) {
        if (_cache != null) {
            _cache.delete(key);
        }
    }
}
```

#### 3. 通过配置换掉默认的缓存服务：

```java
@Configuration
public class Config {
    //此缓存，将替代默认的缓存服务
    @Bean
    public CacheService cache(@Inject("${cache}") Properties props) {
        return new MemCacheService(props);
    }
}
```

### （三）注解说明

**@Cache 注解：**

| 属性 | 说明 | 
| -------- | -------- | 
| service()     | 缓存服务     | 
| seconds()     | 缓存时间     | 
| tags()     | 缓存标签，多个以逗号隔开（为当前缓存块添加标签，用于清除）     | 


**@CacheRemove 注解：**

| 属性 | 说明 | 
| -------- | -------- | 
| service()     | 缓存服务     | 
| tags()     | 清除缓存标签，多个以逗号隔开     | 



