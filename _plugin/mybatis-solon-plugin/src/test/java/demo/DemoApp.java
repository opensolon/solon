package demo;

import org.apache.ibatis.session.Configuration;
import org.noear.solon.SolonBuilder;

/**
 * @author noear 2021/7/12 created
 */
public class DemoApp {
    public static void main(String[] args) {
        new SolonBuilder()
                .onEvent(Configuration.class, c -> {
                    //添加插件
                    //c.addInterceptor();
                }).start(DemoApp.class, args);
    }
}
