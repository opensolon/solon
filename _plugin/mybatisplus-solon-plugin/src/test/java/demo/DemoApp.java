package demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import demo.mappers.UserMapper;
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

        new Thread(() -> {
            while (true) {
                UserMapper userMapper = Aop.get(UserMapper.class);
                if (userMapper != null) {
                    System.out.println(userMapper.selectList(new QueryWrapper<>()));
                    break;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {}
                }
            }
        });
    }
}
