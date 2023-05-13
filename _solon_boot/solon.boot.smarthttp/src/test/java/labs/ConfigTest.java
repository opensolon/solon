package labs;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;
import org.smartboot.http.server.HttpServerConfiguration;

/**
 * @author noear 2023/5/13 created
 */
@SolonMain
public class ConfigTest {
    public static void main(String[] args){
        Solon.start(ConfigTest.class, args, app->{
           app.onEvent(HttpServerConfiguration.class, e->{
               e.debug(true);
           });
        });
    }
}
