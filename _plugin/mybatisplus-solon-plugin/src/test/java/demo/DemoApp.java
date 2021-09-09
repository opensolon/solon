package demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import demo.dso.mapper.UserMapper;
import demo.dso.service.UserService;
import org.apache.ibatis.session.Configuration;
import org.noear.solon.SolonBuilder;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.Aop;

/**
 * @author noear 2021/7/12 created
 */
@Import(scanPackages = { "demo" })
public class DemoApp {
    public static void main(String[] args) {
        new SolonBuilder()
                .onEvent(Configuration.class, c -> {
                    //添加插件
                    //c.addInterceptor();
                })
                .start(DemoApp.class, args);


        //test
        UserService userService = Aop.get(UserService.class);
        assert userService.getUserList() != null;
    }
}
