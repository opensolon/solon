package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.TimeZone;

/**
 * @author noear 2024/12/22 created
 */
@SolonTest(App.class)
public class TimeZoneTest extends HttpTester {
    //检查其它插件对时区的影响（避免被 TimeZone.setDefault(xx)）
    @Test
    public void case1() throws Exception {
        System.out.println(TimeZone.getDefault());

        assert path("/debug.htm").execAsCode("GET") == 200;

        System.out.println(TimeZone.getDefault());

        //作者的机器属于 Asia/Shanghai
        assert TimeZone.getDefault().getID().equals("Asia/Shanghai");
    }
}
