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
                })
                .onAppPluginLoadEnd(e->{
                    //重新定义 SqlSessionFactoryBuilder（没有需要，最好别动它...）
                    //Aop.wrapAndPut(SqlSessionFactoryBuilder.class, new SqlSessionFactoryBuilderImpl());
                })
                .start(DemoApp.class, args);
    }
}
