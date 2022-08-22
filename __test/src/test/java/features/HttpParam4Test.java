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

        String json2 = path("/demo2/param4/json").bodyJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
    }

    @Test
    public void json2() throws IOException {
        //走json通过，这个格式OK
        String json = "{id:1,name:'noear',date:'2021-12-12T12:12:12'}";

        String json2 = path("/demo2/param4/json").bodyJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
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
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
    }

    @Test
    public void param_2() throws IOException {
        //走param，@Param 的格式化会起效果
        String json2 = path("/demo2/param4/param2")
                .data("id", "1")
                .data("name", "noear")
                .post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
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
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
    }

    @Test
    public void param3() throws IOException {
        //走param，@Param 的格式化会起效果
        String json2 = path("/demo2/param4/param3")
                .data("id", "1")
                .data("name", "noear")
                .data("icon", "bbb")
                .data("date", "2021-12-12 12:12:12")
                .post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
        assert "bbb".equals(oNode2.get("icon").getString());
    }

    @Test
    public void param3_json() throws IOException {
        ONode oNode = new ONode();
        oNode.set("id", "1")
                .set("name", "noear")
                .set("icon", "bbb")
                .set("date", "2021-12-12 12:12:12");

        //走param，@Param 的格式化会起效果
        String json2 = path("/demo2/param4/param3").bodyJson(oNode.toJson()).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
        assert "bbb".equals(oNode2.get("icon").getString());
    }


    @Test
    public void body() throws IOException {
        String body = "{name:'noear'}";

        String body2 = path("/demo2/param4/body").bodyJson(body).post();
        assert body.equals(body2);


        body2 = path("/demo2/param4/body").bodyTxt(body).post();
        assert body.equals(body2);
    }

    @Test
    public void body2() throws IOException {
        String body = "{username:'noear',confirmPassword:'123456'}";

        System.out.println(body);
        String body2 = path("/demo2/param4/body2").bodyJson(body).post();
        ONode oNode = ONode.loadStr(body2);


        assert oNode.get("username").getString().equals("noear");
        assert oNode.get("confirmPassword").getString().equals("123456");
    }

    @Test
    public void test() throws IOException {
        String body = "hello";

        //paramMap()->body()
        System.out.println(body);
        String body2 = path("/demo2/param4/test").bodyJson(body).post();

        assert body.equals(body2);
    }

    @Test
    public void test2() throws IOException {
        String body2 = path("/demo2/param4/test2?id=3&aaa[0]=1&aaa[1]=2").get();

        ONode oNode = ONode.loadStr(body2);

        assert oNode.get("id").getInt() == 3;
        assert oNode.get("aaa").get(0).getInt() == 1;
        assert oNode.get("aaa").get(1).getInt() == 2;
    }
}
