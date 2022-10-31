package demo2.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.auth.AuthAdapter;
import org.noear.solon.auth.AuthAdapterSupplier;

/**
 * @author noear 2022/8/15 created
 */
@Component
public class AdminAuthAdapterSupplierImpl implements AuthAdapterSupplier {
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
