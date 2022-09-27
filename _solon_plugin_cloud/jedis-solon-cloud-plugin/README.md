
#### 配置

```yaml
#完整配置示例
solon.cloud.jedis:
  lock:
    server: "localhost:6379"
    db: 0 #默认为 0，可不配置
    password: ""
    maxTotal: 200 #默认为 200，可不配
```

#### 代码

```java
//应用
@Controller
public class DemoController {
    @Mapping("/hello")
    public void hello(String name) {
        if(CloudClient.lock().tryLock("user_"+user_id, 3)){
            //对一个用户尝试3秒的锁；3秒内不充行重复提交
        }else{
            //请求太频繁了...
        }
    }
}
```