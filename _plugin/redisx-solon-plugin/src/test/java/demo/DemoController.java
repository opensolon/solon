package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.redisx.RedisClient;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class DemoController {

    @Inject
    RedisClient redisX;

    @Mapping("/demo")
    public void demo() {
        //::redisX 基础接口使用

        redisX.open0(session -> {
            session.key("item:1").expire(10).valSet("noear");
        });

        String item_1 = redisX.open1(session -> {
            return session.key("item:1").expire(10).val();
        });

        redisX.open0(session -> {
            session.key("user:1").expire(10)
                    .hashSet("name", "noear")
                    .hashSet("sex", "1");
        });
    }

    @Mapping("/demo2")
    public void demo2() {
        //::redisX 快捷功能使用

        //--- id 使用
        long user_id = 10000 + redisX.getId("id:user").generate();
        long order_id = 1000000 + redisX.getId("id:order").generate();

        //--- lock 锁使用
        if (redisX.getLock("user:121212").tryLock()) {
            //业务处理
        } else {
            //提示：请不要频繁提交
        }
    }
}
