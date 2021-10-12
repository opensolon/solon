package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.redisx.RedisClient;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class Config {
    @Bean
    public RedisClient redisX(@Inject("${test.rd1}") RedisClient redisX) {
        return redisX;
    }
}
