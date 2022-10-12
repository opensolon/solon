package demo2.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.auth.AuthAdapter;
import org.noear.solon.auth.AuthAdapterSupplier;

/**
 * @author noear 2022/8/15 created
 */
@Component
public class UserAuthAdapterSupplierImpl implements AuthAdapterSupplier {
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