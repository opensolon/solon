package demo;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.solon.dao.SaTokenDaoOfRedis;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/3/30 created
 */
@Configuration
public class Config {
    @Bean
    public SaTokenDao saTokenDaoInit(@Inject("${sa-token-dao.redis}") SaTokenDaoOfRedis saTokenDao) {
        return saTokenDao;
    }

    @Bean
    public void saTokenConfigInit(){
        SaTokenConfig saTokenConfig = new SaTokenConfig();
        //..

        SaManager.setConfig(saTokenConfig);
    }

}
