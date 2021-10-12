package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.redisx.RedisX;
import org.noear.solon.extend.redisx.utils.RedisId;
import org.noear.solon.extend.redisx.utils.RedisLock;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class DemoController {

    @Inject
    RedisX redisX;

    @Inject
    RedisId redisId;

    @Inject
    RedisLock redisLock;

    @Mapping("/demo")
    public void demo() {
        //--- redisX 使用
        redisX.open0(session -> {
            session.key("item_1").expire(10).valSet("noear");
        });

        String item_1 = redisX.open1(session -> {
            return session.key("item_1").expire(10).val();
        });

        redisX.open0(session -> {
            session.key("user_1").expire(10)
                    .hashSet("name", "noear")
                    .hashSet("sex", "1");
        });

        //--- id 使用
        long user_id = 10000 + redisId.newID(Solon.cfg().appName(), "user_id");
        long order_id = 1000000 + redisId.newID(Solon.cfg().appName(), "order_id");

        //--- lock 锁使用
        if (redisLock.tryLock(Solon.cfg().appName(), "user_121212")) {
            //业务处理
        } else {
            //提示：请不要频繁提交
        }
    }
}
