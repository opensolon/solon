package demo;

import demo.dso.PaginationConfig;
import demo.dso.service.UserService;
import org.apache.ibatis.session.Configuration;
import org.noear.solon.SolonBuilder;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.Aop;

/**
 * @author noear 2021/7/12 created
 */
@Import(scanPackages = { "demo" })
public class DemoApp2 {
    public static void main(String[] args) {
        Class mpClass = Utils.loadClass("com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor");

        new SolonBuilder()
                .onEvent(Configuration.class, e -> {
                    //动态添加 mybatis-plug 分页支持
                    if (mpClass != null) {
                        PaginationConfig.init(e);
                    }
                })
                .start(DemoApp2.class, args);

        //test
        UserService userService = Aop.get(UserService.class);
        assert userService.getUserList() != null;
    }
}
