package features.auth.case1.dso;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.auth.AuthAdapter;

/**
 * @author noear 2024/10/16 created
 */
@Configuration
public class AuthConfig {
    @Managed
    public AuthAdapter admin() {
        return new AuthAdapter()
                .pathPrefix("/admin/")
                .loginUrl("/admin/login") //设定登录地址，未登录时自动跳转
                .addRule(r -> r.include("/admin/**").verifyIp().failure((c, t) -> c.output("你的IP不在白名单"))) //添加规则
                .addRule(b -> b.include("/admin/**").exclude("/admin/login**").verifyPath()) //添加规则
                .processor(new AdminAuthProcessorImpl());//设定认证处理器

    }

    @Managed
    public AuthAdapter user() {
        return new AuthAdapter()
                .pathPrefix("/user/")
                .loginUrl("/user/login") //设定登录地址，未登录时自动跳转
                .addRule(b -> b.include("/user/**").exclude("/user/login**").verifyPath()) //添加规则
                .processor(new UserAuthProcessorImpl()); //设定认证处理器

    }
}