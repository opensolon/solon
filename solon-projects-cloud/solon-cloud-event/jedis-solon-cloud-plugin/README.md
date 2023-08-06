
集群订阅时，接收时需要加锁（不然每个节点，都会收到）；没有 ACK（不适合原子性业务）

#### 能力

* CloudEventServicePlus
* CloudLockService

#### 配置

```yaml
#完整配置示例
solon.cloud.jedis:
  server: "localhost:6379"
  password: "123456"
  lock:
    db: 0 #默认为 0，可不配置
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