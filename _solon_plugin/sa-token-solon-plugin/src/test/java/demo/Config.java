package demo;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.dao.SaTokenDaoOfRedis;
import cn.dev33.satoken.solon.integration.SaTokenInterceptor;
import cn.dev33.satoken.solon.json.SaJsonTemplateForSnack3;
import cn.dev33.satoken.stp.StpUtil;
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
    public SaJsonTemplate saTokenJson() {
        //把它换成需要的组件
        return new SaJsonTemplateForSnack3();
    }

    @Bean(index=-100) //优先级可以排后些
    public SaTokenInterceptor saTokenInterceptor() {
        return new SaTokenInterceptor()
                // 指定 [拦截路由] 与 [放行路由]
                .addInclude("/**").addExclude("/favicon.ico")

                // 认证函数: 每次请求执行
                .setAuth(s -> {
                    SaRouter.match("/**", StpUtil::checkLogin);

                    // 根据路由划分模块，不同模块不同鉴权
                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
                })

                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    System.out.println("---------- sa全局异常 ");
                    System.out.println(e.getMessage());
                    StpUtil.login(123);
                    return e.getMessage();
                });
    }
}
