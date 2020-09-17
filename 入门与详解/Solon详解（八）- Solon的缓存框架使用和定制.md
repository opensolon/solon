`solon.extend.data` 框加在完成 @XTran 注解的支持同时，还提供了 @XCache 注解的支持；可以为业务开发提供了良好的便利性



| 属性 | 说明 | 
| -------- | -------- | 
| service()     | 缓存服务     | 
| seconds()     | 缓存时间     | 
| tags()     | 缓存标签，多个以逗号隔开（为当前缓存块添加标签，用于清除）     | 
| clearTags()     | 清除缓存标签，多个以逗号隔开     | 

Solon 的 @XCache 注解只支持：XController 、XService 、XDao 类下的方法。且借签了Weed3的简洁设计方案。

### （一）示例

```java
@XController
public class CacheController {
    /**
     * 执行结果缓存10秒，并添加 test_${label} 和 test1 标签
     * */
    @XCache(tags = "test_${label},test1" , seconds = 10)
    @XMapping("/cache/")
    public Object test(int label) {
        return new Date();
    }

    /**
     * 执行后，清除 标签为 test  的缓存（不过，目前没有 test 的示签...）
     * */
    @XCache(clearTags = "test")
    @XMapping("/cache/clear")
    public String clear() {
        return "清除成功(其实无效)-" + new Date();
    }

    /**
     * 执行后，清除 标签为 test_${label}  的缓存
     * */
    @XCache(clearTags = "test_${label}")
    @XMapping("/cache/clear2")
    public String clear2(int label) {
        return "清除成功-" + new Date();
    }
}
```

### （二）定制分布式缓存

```java

```


