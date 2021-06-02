package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.extend.auth.AuthAdapter;

/**
 * @author noear 2021/6/2 created
 */
@Configuration
public class Config {
    @Bean
    public AuthAdapter adapter() {
        return new AuthAdapter()
                .loginUrl("/login") //设定登录地址，未登录时自动跳转
                .addRule(b -> b.include("").exclude("").verifyIp().failure((c, r) -> c.output("你的ip不是白名单")))
                .addRule(b -> b.include("").exclude("").verifyLogined())
                .addRule(b -> b.include("").exclude("").verifyPath())
                .addRule(b -> b.include("").exclude("").verifyPermissions(""))
                .addRule(b -> b.include("").exclude("").verifyPermissionsAnd(""))
                .addRule(b -> b.include("").exclude("").verifyRoles(""))
                .addRule(b -> b.include("").exclude("").verifyRolesAnd(""))
                .processor(null) //设定认证处理器
                .failure((ctx, rst) -> { //设定验证失败代理
                    ctx.render(rst);
                });
    }
}
