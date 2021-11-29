package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/6/15 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpValidTest2 extends HttpTestBase {

    @Test
    public void test0() throws IOException {
        assert path("/demo2/valid/bean2").get().contains("field1");
    }

    @Test
    public void test1() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field2");

        data.put("field1", "2020-12-12 12:12:12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field1");
    }

    @Test
    public void test2() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");

        assert path("/demo2/valid/bean2").data(data).post().contains("field3");

        data.put("field2", "2020-12-12 12:12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field2");
    }

    @Test
    public void test3() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");

        assert path("/demo2/valid/bean2").data(data).post().contains("field4");

        data.put("field3", "12.0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field3");
    }


    @Test
    public void test4() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");

        assert path("/demo2/valid/bean2").data(data).post().contains("field5");

        data.put("field4", "9.0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field4");
    }


    @Test
    public void test5() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");

        assert path("/demo2/valid/bean2").data(data).post().contains("field6");

        data.put("field5", "noear.live.cn");
        assert path("/demo2/valid/bean2").data(data).post().contains("field5");
    }

    @Test
    public void test6() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");

        assert path("/demo2/valid/bean2").data(data).post().contains("field7");

        data.put("field6", "noear@cc.cn");
        assert path("/demo2/valid/bean2").data(data).post().contains("field6");
    }

    @Test
    public void test7() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");

        assert path("/demo2/valid/bean2").data(data).post().contains("field8");

        data.put("field7", "12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field7");
    }

    @Test
    public void test8() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");

        assert path("/demo2/valid/bean2").data(data).post().contains("field9");

        data.put("field8", "9");
        assert path("/demo2/valid/bean2").data(data).post().contains("field8");
    }

    @Test
    public void test9() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");

        assert path("/demo2/valid/bean2").data(data).post().contains("field10");

        data.put("field9", " ");
        assert path("/demo2/valid/bean2").data(data).post().contains("field9");
    }

    @Test
    public void test10() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");

        assert path("/demo2/valid/bean2").data(data).post().contains("field11");

        data.put("field10", "");
        assert path("/demo2/valid/bean2").data(data).post().contains("field10");
    }

    @Test
    public void test11() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");

        assert path("/demo2/valid/bean2").data(data).post().contains("field12");

        data.remove("field11");
        assert path("/demo2/valid/bean2").data(data).post().contains("field11");
    }

    @Test
    public void test12() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");

        assert path("/demo2/valid/bean2").data(data).post().contains("field15");

        data.remove("field12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field12");
    }

    @Test
    public void test15() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");

        assert path("/demo2/valid/bean2").data(data).post().contains("field16");

        data.put("field15", "111a-12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field15");
    }

    @Test
    public void test16() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");
        data.put("field16", "1");

        assert path("/demo2/valid/bean2").data(data).post().contains("OK");

        data.put("field16", "0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field16");
    }

    @Test
    public void test17() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");
        data.put("field16", "1");
        data.put("field17", "1xxxx");

        assert path("/demo2/valid/bean2").data(data).post().contains("OK");

        data.put("field17", "0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field17");
    }

    @Test
    public void test18() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");
        data.put("field16", "1");
        data.put("field17", "1xxxx");

        ONode node = ONode.load(data);
        node.getOrNew("field18").add("1").add("2");

        assert path("/demo2/valid/bean2").bodyJson(node.toJson()).post().contains("OK");

        node.get("field18").clear();
        node.get("node").add("1");

        assert path("/demo2/valid/bean2").bodyJson(node.toJson()).post().contains("field18");
    }
}
