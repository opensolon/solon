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
    public AuthAdapter adapter(){
        return new AuthAdapter()
                .loginUrl("/login") //设定登录地址，未登录时自动跳转
                .authRuleAdd(b->b.include("").exclude("").verifyIp())
                .authRuleAdd(b->b.include("").exclude("").verifyLogined())
                .authRuleAdd(b->b.include("").exclude("").verifyPath())
                .authRuleAdd(b->b.include("").exclude("").verifyPermissions(""))
                .authRuleAdd(b->b.include("").exclude("").verifyPermissionsAnd(""))
                .authRuleAdd(b->b.include("").exclude("").verifyRoles(""))
                .authRuleAdd(b->b.include("").exclude("").verifyRolesAnd(""))
                .authProcessor(null) //设定认证处理器
                .authOnFailure((ctx, rst) -> { //设定验证失败代理
                    ctx.render(rst);
                });
    }
}
