package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.redisx.RedisX;
import org.noear.solon.extend.redisx.utils.RedisId;
import org.noear.solon.extend.redisx.utils.RedisLock;

import java.util.Properties;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class Config {
    @Bean
    public RedisX redisX(@Inject("${test.rd1}") RedisX redisX) {
        return redisX;
    }

    @Bean
    public RedisId redisId(@Inject("${test.rd1}") Properties prop) {
        return new RedisId(new RedisX(prop, 2));
    }

    @Bean
    public RedisLock redisLock(@Inject("${test.rd1}") Properties prop) {
        return new RedisLock(new RedisX(prop, 3));
    }
}
