package demo;

import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.core.db.MySqlStyle;
import org.noear.solon.SolonBuilder;

/**
 * @author noear 2021/7/12 created
 */
public class DemoApp {
    public static void main(String[] args) {
        new SolonBuilder()
                .onEvent(SQLManagerBuilder.class, c -> {
                    //添加插件
                    //c.addIdAutoGen()
                    //c.setDbStyle(MySqlStyle.class);
                    //c.addInterceptor();
                }).start(DemoApp.class, args, x -> {
                    x.cfg().loadAdd("demo.yml");
                });
    }
}
