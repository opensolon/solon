package demo;

import org.apache.shiro.realm.AuthorizingRealm;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2021/5/13 created
 */
@Configuration
public class Config {
    //
    //不知道，是此处加；；；还是在shiro.ini 配置??
    //
    @Bean
    public AuthorizingRealm realm(){
        return new AuthorizingRealmImpl();
    }
}
