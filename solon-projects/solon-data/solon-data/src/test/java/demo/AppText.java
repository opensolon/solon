package demo;

import org.noear.solon.Solon;

import javax.sql.DataSource;

/**
 *
 * @author noear 2025/8/27 created
 *
 */
public class AppText {
    public static void main(String[] args) {
        Solon.start(AppText.class, args, app -> {
            app.context().subWrapsOfType(DataSource.class, bw->{
                DataSource ds = bw.raw();
                String dsName = bw.name();

                //...
            },-999);

            app.context().<DataSource>getBeanAsync("xxx", ds->{
                //如果有具体名字
            });
        });
    }
}
