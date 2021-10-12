package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.redisx.RedisX;

/**
 * @author noear 2021/10/12 created
 */
@Controller
public class Config {
    @Bean
    public RedisX redisX(@Inject("${test.rd1}") RedisX redisX){
        return redisX;
    }
}
