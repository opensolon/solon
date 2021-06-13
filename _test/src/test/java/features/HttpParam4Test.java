package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.utils.Datetime;

import java.io.IOException;

/**
 * @author noear 2021/6/13 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpParam4Test extends HttpTestBase {
    @Test
    public void json() throws IOException {
        //走json通过，这个格式会有问题
        String json = "{id:1,name:'noear',date:'2021-12-12'}";

        String json2 =  path("/demo2/param4/json").bodyJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate() ).getYear() > 2000;
    }

    @Test
    public void json2() throws IOException {
        //走json通过，这个格式OK
        String json = "{id:1,name:'noear',date:'2021-12-12T12:12:12'}";

        String json2 =  path("/demo2/param4/json").bodyJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate() ).getYear() > 2000;
    }

    @Test
    public void param() throws IOException {
        //走param，@Param 的格式化会起效果
        String json2 = path("/demo2/param4/param")
                .data("id", "1")
                .data("name", "noear")
                .data("date", "2021-12-12")
                .post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate() ).getYear() > 2000;
    }

    @Test
    public void param2() throws IOException {
        //走param，@Param 的格式化会起效果
        String json2 = path("/demo2/param4/param")
                .data("id", "1")
                .data("name", "noear")
                .data("date", "2021-12-12 12:12:12")
                .post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate() ).getYear() > 2000;
    }
}
