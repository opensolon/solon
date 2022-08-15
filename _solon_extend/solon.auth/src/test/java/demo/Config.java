package demo;

import demo.dso.AdminAuthProcessorImpl;
import demo.dso.AuthProcessorImpl;
import demo.dso.UserAuthProcessorImpl;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.auth.AuthAdapter;
import org.noear.solon.auth.AuthAdapterSupplier;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2021/6/2 created
 */
@Configuration
public class Config {
    /**
     * 单验证体系
     * */
    @Bean
    public void authAdapter1() {
        AuthUtil.adapter()
                .loginUrl("/login") //设定登录地址，未登录时自动跳转
                .addRule(r -> r.include("**").verifyIp().failure((c, t) -> c.output("你的IP不在白名单"))) //添加规则
                .addRule(b -> b.exclude("/login**").exclude("/_run/**").verifyPath()) //添加规则
                .processor(new AuthProcessorImpl()) //设定认证处理器
                .failure((ctx, rst) -> { //设定默认的验证失败处理
                    ctx.render(rst);
                });
    }

@Component
public class UserAuthAdapterSupplierImpl implements AuthAdapterSupplier{
    final AuthAdapter userAuth;

    public UserAuthAdapterSupplierImpl(){
        userAuth = new AuthAdapter()
                .loginUrl("/login") //设定登录地址，未登录时自动跳转
                .addRule(b -> b.exclude("/login**").exclude("/_run/**").exclude("/admin/**").verifyPath()) //添加规则
                .processor(new UserAuthProcessorImpl()) //设定认证处理器
                .failure((ctx, rst) -> { //设定默认的验证失败处理
                    ctx.render(rst);
                });
    }

    @Override
    public String pathPrefix() {
        return "/";
    }

    public AuthAdapter adapter(){
        return userAuth;
    }
}

@Component
public class AdminAuthAdapterSupplierImpl implements AuthAdapterSupplier{
    final AuthAdapter adminAuth;

    public AdminAuthAdapterSupplierImpl(){
        adminAuth = new AuthAdapter()
                .loginUrl("/admin/login") //设定登录地址，未登录时自动跳转
                .addRule(r -> r.include("/admin/**").verifyIp().failure((c, t) -> c.output("你的IP不在白名单"))) //添加规则
                .addRule(b -> b.include("/admin/**").exclude("/admin/login**").verifyPath()) //添加规则
                .processor(new AdminAuthProcessorImpl()) //设定认证处理器
                .failure((ctx, rst) -> { //设定默认的验证失败处理
                    ctx.render(rst);
                });
    }

    @Override
    public String pathPrefix() {
        return "/admin/";
    }

    public AuthAdapter adapter(){
        return adminAuth;
    }
}
}
