package demo3;

import org.noear.solon.annotation.Bean;
import org.noear.solon.auth.AuthAdapter;

/**
 * @author noear 2022/7/14 created
 */
public class AuthConfig {
    /**
     * 构建认证适配器
     *
     * @author 多仔ヾ
     */
    @Bean
    public AuthAdapter initAuthAdapter(){
        AuthProcessorImpl authProcessor = new AuthProcessorImpl();

        return new AuthAdapter()
                .addRule(r -> r.include("/manager/**").exclude("/manager/passport/login").verifyLogined().failure((ctx, rst)-> {
                    ctx.redirect("/manager/passport/login");
                }))
                .addRule(r -> r.include("/user/**").exclude("/user/passport/login/**").verifyLogined().failure((ctx, rst)-> {
                    ctx.redirect("/user/passport/login/weixinQuickLogin");
                }))
                .processor(authProcessor)
                .failure(authProcessor);
    }
}
