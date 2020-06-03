package test2;

import org.junit.BeforeClass;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.solon.XApp;
import org.noear.water.utils.HttpUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {
    @BeforeClass
    public static void test0() throws Exception{
        XApp.start(webapp.TestApp.class, new String[]{});
        Thread.sleep(1000);
    }


    @Test
    public void test11() throws IOException{
       assert  get("/demo1/run0/?str=").equals("不是null(ok)");
    }

    @Test
    public void test12() throws IOException{
        assert  get("/demo1/run1/*").equals("http://localhost:8080/demo1/run1/*");
    }

    @Test
    public void test13() throws IOException{
        assert  getStatus("/demo1/run2/*") != 200;
    }

    @Test
    public void test14() throws IOException{
        assert  get("/demo1/run2/send").equals("http://localhost:8080/demo1/run2/send");
    }

    @Test
    public void test15() throws IOException{
        assert  get("/demo1/run2/test").equals("http://localhost:8080/demo1/run2/test");
    }

    @Test
    public void test16() throws IOException{
        String rst =  get("/demo1/run2/ip");
        assert  rst.equals("0:0:0:0:0:0:0:1") || rst.equals("127.0.0.1");
    }

    @Test
    public void test17() throws IOException{
        assert  get("/demo1/view/*").indexOf("ftl::") > 0;
    }

    @Test
    public void test18() throws IOException{
        assert  get("/demo1/view/xx").indexOf("你好 world! in XController") > 0;
    }

    @Test
    public void test21() throws IOException{
        assert  getStatus("/demo2/CMD/{cmd_name}") == 404;
    }

    @Test
    public void test22() throws IOException{
        assert  get("/demo2/CMD/A.0.1").equals("A.0.1");
    }

    @Test
    public void test23() throws IOException{
        assert  get("/demo2/CMD/A.0.2").equals("A.0.2");
    }

    @Test
    public void test24() throws IOException{
        assert  get("/demo2/mapping/a").equals("/demo2/mapping/a");
    }

    @Test
    public void test25() throws IOException{
        assert  getStatus("/demo2/mapping/post") == 404;
    }

    @Test
    public void test25_2() throws IOException{
        Map<String,String> map = new HashMap<>();
        map.put("name","中文");

        assert  post("/demo2/mapping/post",map).equals("中文");
    }

    @Test
    public void test26() throws IOException{
        assert  get("/demo2/mapping/post_get").equals("/demo2/mapping/post_get");
    }

    @Test
    public void test27() throws IOException{
        assert  get("/demo2/mapping/b/*").equals("/demo2/mapping/b/*");
    }

    @Test
    public void test28() throws IOException{
        assert  get("/demo2/mapping/c/**").equals("/demo2/mapping/c/**");
    }

    @Test
    public void test29() throws IOException{
        assert  get("/demo2/mapping/c/**/xx").equals("/demo2/mapping/c/**/xx");
    }

    @Test
    public void test2a() throws IOException{
        assert  get("/demo2/mapping/d1/**/$*").equals("/demo2/mapping/d1/**/$*");
    }

    @Test
    public void test2b() throws IOException{
        assert  get("/demo2/mapping/d1/**/@*").equals("/demo2/mapping/d1/**/@*");
    }

    @Test
    public void test2c() throws IOException{
        assert  get("/demo2/mapping/e/{p_q}/{obj}/{id}").equals("/demo2/mapping/e/{p_q}/{obj}/{id}::{p_q}-{obj}-{id}");
    }

    @Test
    public void test2d() throws IOException{
        assert  get("/demo2/param/body?name=xxx").equals("");
    }

    @Test
    public void test2d_2() throws IOException{
        assert  post("/demo2/param/body","name=xxx").equals("name=xxx");
    }


    @Test
    public void test2e() throws IOException{
        assert  get("/demo2/param/d/*?name=中文").equals("中文");
    }

    @Test
    public void test2f() throws IOException{
        assert  get("/demo2/param/e/p_q/obj/id").equals("/demo2/param/e/p_q/obj/id::p_q-obj-id");
    }

    @Test
    public void test2g() throws IOException{
        assert  get("/demo2/param/array_str?aaa=1&aaa=2&aaa=中文").equals("[\"1\",\"2\",\"\\u4E2D\\u6587\"]");
    }

    @Test
    public void test2h() throws IOException{
        assert  get("/demo2/param/array_Int?aaa=1&aaa=2&ccc=3").equals("[1,2]");
    }

    @Test
    public void test2i() throws IOException{
        assert  get("/demo2/param/array_int?aaa=1&aaa=2&ccc=3").equals("[1,2]");
    }

    @Test
    public void test2j() throws IOException{
        assert  get("/demo2/param/model?id=1&name=xxx&sex=2&date=2019-12-1&aaa=1&aaa=2").equals("{\"aaa\":[1,2],\"date\":1575129600000,\"id\":1,\"name\":\"xxx\",\"sex\":2}");
    }

    @Test
    public void test2k() throws IOException{
        assert  get("/demo2/param/date?date=2018-11-11&date2=2019-11-11T11:11:11").equals("Sun Nov 11 00:00:00 CST 2018 # Mon Nov 11 11:11:11 CST 2019");
    }

    @Test
    public void test2o() throws IOException{
        assert  get("/demo2/view").indexOf("你好 world! in XController") > 0;
    }
    @Test
    public void test2p() throws IOException{
        ONode n = ONode.loadStr(get("/demo2/json"));
        assert  n.contains("@type") == false;
        assert  n.get("msg").getString().indexOf("你好")>=0;
    }
    @Test
    public void test2u() throws IOException{
        assert get("/demo2/rpc/json").indexOf("@type") > 0;
    }
    @Test
    public void test41() throws IOException{
        assert  get("/demo4/*").equals("是插件生出了我...");
    }
    @Test
    public void test61() throws IOException{
        assert  get("/demo6/aop").equals("{\"rockapi12\":\"\\u6211\\u662F\\uFF1ARockservice1\",\"rockapi11\":\"\\u6211\\u662F\\uFF1ARockservice1\",\"rockapi2\":\"\\u6211\\u662F\\uFF1ARockservice2\"}");
    }
    @Test
    public void test71() throws IOException{
        assert  get("/demo7/test").equals("/demo7/test");
    }
    @Test
    public void test81() throws IOException{
        assert  get("/demo8/config_inject").equals("{\"dbcfg\":{\"url\":\"jdbc:mysql:\\/\\/127.0.0.1\\/user\",\"paasword\":\"12\",\"username\":\"root\"},\"nameuser_2\":\"noear\",\"paasword\":1234,\"test\":{\"url\":\"jdbc:mysql:\\/\\/127.0.0.1\\/user\",\"paasword\":\"12\",\"username\":\"root\"},\"username\":\"noear\"}");
    }
    @Test
    public void test82() throws IOException{
        assert  ONode.loadStr(get("/demo8/config_all")).get("username").getString().equals("noear");
    }

    @Test
    public void test83() throws IOException{
        assert ONode.loadStr(get("/demo8/config_system")).get("file.separator").getString().equals("/");
    }
    @Test
    public void test91() throws IOException{
        assert  get("/demo9/view/beetl").indexOf("beetl::") > 0;
    }
    @Test
    public void test92() throws IOException{
        assert  get("/demo9/view/ftl").indexOf("ftl::") > 0;
    }
    @Test
    public void testa1() throws IOException{
        assert  get("/demoa/trigger").equals(
                "XInterceptor1::你被我拦截了(/demoa/**)!!!\n" +
                "XInterceptor2::你被我拦截了(/demoa/**)!!!\n" +
                "XInterceptor3::你被我拦截了(/demoa/**)!!!\n" +
                "/demoa/triggerXInterceptor_aft::你被我拦截了(/demoa/**)!!!\n");
    }

    private String get(String path) throws IOException {
        String url = "http://localhost:8080" + path;
        String rst = HttpUtils.http(url).get();

        System.out.println(path + " :: " + rst);

        return rst;
    }

    private int getStatus(String path) throws IOException {
        String url = "http://localhost:8080" + path;
        return HttpUtils.http(url).exec("GET").code();
    }

    private String post(String path, String body) throws IOException {
        String url = "http://localhost:8080" + path;
        return HttpUtils.http(url).bodyTxt(body).post();
    }

    private String post(String path, Map<String,String> data) throws IOException {
        String url = "http://localhost:8080" + path;
        return HttpUtils.http(url).data(data).post();
    }
}
