package demo;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.dao.SaTokenDaoOfRedis;
import cn.dev33.satoken.solon.integration.SaTokenRouteInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.noear.solon.Solon;
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
    public void saTokenConfigInit() {
        SaTokenConfig saTokenConfig = new SaTokenConfig();
        //..

        SaManager.setConfig(saTokenConfig);
    }

    @Bean
    public void saTokenRouteInterceptor() {
        Solon.app().before(SaTokenRouteInterceptor.newInstance((req, res, o) -> {
            // 根据路由划分模块，不同模块不同鉴权
            SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
            SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
            SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
            SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
        }));
    }
}
