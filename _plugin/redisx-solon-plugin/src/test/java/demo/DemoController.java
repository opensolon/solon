package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.redisx.RedisX;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class DemoController {

    @Inject
    RedisX redisX;

    @Mapping("/demo")
    public void demo() {
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
    }
}
