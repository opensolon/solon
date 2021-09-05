package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/9/5 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpValidTest3 extends HttpTestBase {
    @Test
    public void test0() throws IOException {
        assert path("/demo2/valid/bean3").get().contains("field10");

        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field10");
    }

    @Test
    public void test1() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","2020-12-12T12:12:12");
        assert path("/demo2/valid/bean3").data(data).post().contains("field10");

        data.put("field1","2020-12-12 12:12:12");
        assert path("/demo2/valid/bean3").data(data).post().contains("field1");
    }

    @Test
    public void test10() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","");
        data.put("field10","2020-12-12T12:12:12");
        assert path("/demo2/valid/bean3").data(data).post().contains("field2");

        data.put("field10","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field10");
    }

    @Test
    public void test2() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","");
        data.put("field10","2020-12-12T12:12:12");
        data.put("field2","noear@live.cn");
        assert path("/demo2/valid/bean3").data(data).post().contains("field20");

        data.put("field2","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field20");


        data.put("field20","noear@live.cn");
        assert path("/demo2/valid/bean3").data(data).post().contains("field30");
    }

    @Test
    public void test3() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field10","2020-12-12T12:12:12");
        data.put("field20","noear@live.cn");
        data.put("field3","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field30");

        data.put("field30","333-23333");
        assert path("/demo2/valid/bean3").data(data).post().contains("OK");
    }
}
