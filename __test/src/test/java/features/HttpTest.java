/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package features;

import org.junit.jupiter.api.Test;
import org.noear.nami.Nami;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SolonTest(App.class)
public class HttpTest extends HttpTester {

    @Inject("${username}")
    public String username;

    @Test
    public void test0() throws IOException {
        assert path("/debug.htm").execAsCode("GET") == 200;
    }

    @Test
    public void test_404() throws IOException {
        assert path("/debug000.htm").execAsCode("GET") == 404;
        assert path("/debug0/c00").execAsCode("GET") == 404;
    }

    @Test
    public void test11() throws IOException {
        assert path("/demo1/run0/?str=").get().equals("不是null(ok)");

        //NumberUtils.isNumber()
    }

    @Test
    public void test11_2() throws IOException {
        assert path("/demo1//run0/?str=").head() == 200;
        assert path("/demo1//run2//ip").head() == 200;

        //NumberUtils.isNumber()
    }

    @Test
    public void test12_get() throws IOException {
        assert path("/demo1/run1/*").get().equals("http://localhost:8080/demo1/run1/*");
    }

    @Test
    public void test12_post() throws IOException {
        assert path("/demo1/run1/*").bodyOfTxt("").post().equals("http://localhost:8080/demo1/run1/*");
    }

    @Test
    public void test12_put() throws IOException {
        assert path("/demo1/run1/*").bodyOfTxt("").put().equals("http://localhost:8080/demo1/run1/*");
    }

    @Test
    public void test12_head() throws IOException {
        assert path("/demo1/run1/*").execAsCode("HEAD") == 200;
    }

    @Test
    public void test13() throws IOException {
        assert path("/demo1/run2/*").execAsCode("GET") != 200;
    }

    @Test
    public void test14() throws IOException {
        assert path("/demo1/run2/send").get().equals("http://localhost:8080/demo1/run2/send");
        assert path("/demo1/run2/send").get().equals("http://localhost:8080/demo1/run2/send");
        assert path("/demo1/run2/test").get().equals("http://localhost:8080/demo1/run2/test");
        assert path("/demo1/run2/test").get().equals("http://localhost:8080/demo1/run2/test");
    }

    @Test
    public void test15() throws IOException {
        assert path("/demo1/run2/test").get().equals("http://localhost:8080/demo1/run2/test");
    }

    @Test
    public void test15_run10() throws IOException {
        assert path("/demo1/run10/test?a=1").get().equals("/demo1/run11/a:a=1");
    }

    @Test
    public void test15_run12() throws IOException {
        assert path("/demo1/run12/test?a=1").get().equals("你好；你好2；");
    }

    @Test
    public void test16() throws IOException {
        String rst = path("/demo1/run2/ip").get();
        assert rst.equals("0:0:0:0:0:0:0:1") || rst.equals("127.0.0.1");
    }

    @Test
    public void test17() throws IOException {
        assert path("/demo1/view/*").get().indexOf("ftl::") > 0;
    }

    @Test
    public void test18() throws IOException {
        assert path("/demo1/view/xx").get().indexOf("你好 world! in XController") > 0;
    }

    @Test
    public void test21() throws IOException {
        assert path("/demo2/CMD/{cmd_name}").execAsCode("GET") == 404;
    }

    @Test
    public void test22() throws IOException {
        assert path("/demo2/CMD/A.0.1").get().equals("A.0.1");
    }

    @Test
    public void test23() throws IOException {
        assert path("/demo2/CMD/A.0.2").get().equals("A.0.2");
    }

    @Test
    public void test24() throws IOException {
        assert path("/demo2/mapping/a").get().equals("/demo2/mapping/a");
    }


    @Test
    public void test27() throws IOException {
        assert path("/demo2/mapping/b/*").get().equals("/demo2/mapping/b/*");
    }

    @Test
    public void test28() throws IOException {
        assert path("/demo2/mapping/c/**").get().equals("/demo2/mapping/c/**");
    }

    @Test
    public void test29() throws IOException {
        assert path("/demo2/mapping/c/**/xx").get().equals("/demo2/mapping/c/**/xx");
    }

    @Test
    public void test2a() throws IOException {
        assert path("/demo2/mapping/d1/**/$*").get().equals("/demo2/mapping/d1/**/$*");
    }

    @Test
    public void test2b() throws IOException {
        assert path("/demo2/mapping/d1/**/@*").get().equals("/demo2/mapping/d1/**/@*");
    }

    @Test
    public void test2c() throws IOException {
        assert path("/demo2/mapping/e/{p_q}/{obj}/{id}").get().equals("/demo2/mapping/e/{p_q}/{obj}/{id}:{p_q}-{obj}-{id}");
    }

    @Test
    public void test2d() throws IOException {
        assert path("/demo2/param/body?name=xxx").get().equals("");
    }

    @Test
    public void test2d_header() throws IOException {
        assert path("/demo2/param/header").header("Test-Token", "demo").get().equals("demo");
        assert path("/demo2/param/header").header("Test-Token", "demo").data("test", "test").post().equals("demo");
    }

    @Test
    public void test2d_cookie() throws IOException {
        Map<String, String> cookieMap = new HashMap<>();
        cookieMap.put("Test-Token", "demo");

        assert path("/demo2/param/cookie").cookies(cookieMap).get().equals("demo");
        assert path("/demo2/param/cookie").cookies(cookieMap).data("test", "test").post().equals("demo");
    }

    @Test
    public void test2d_2() throws IOException {
        assert path("/demo2/param/body").bodyOfTxt("name=xxx").post().equals("name=xxx");
        assert path("/demo2/param/body?name=xxx").get().equals("");
        assert path("/demo2/param/body").bodyOfTxt("name=xxx").post().equals("name=xxx");
    }

    @Test
    public void test2dd() throws IOException {
        assert path("/demo2/param/decimal").get().equals("");
        assert path("/demo2/param/decimal?num=").get().equals("");
        assert path("/demo2/param/decimal?num=1.12").get().equals("1.12");
    }

    @Test
    public void test2int() throws IOException {
        assert path("/demo2/param/int").get().equals("0");
        assert path("/demo2/param/int?num=").get().equals("0");
        assert path("/demo2/param/int?num=12").get().equals("12");
    }

    @Test
    public void test2int_2() throws IOException {
        assert path("/demo2/param/int?num=s").execAsCode("GET") == 400;
    }


    @Test
    public void test2e() throws IOException {
        assert path("/demo2/param/d/*?name=中文").get().equals("中文");
        assert path("/demo2/param/d/*?name=https://a.a.a/b/c/_xxx").get().equals("https://a.a.a/b/c/_xxx");

        assert path("/demo2/param/d/*").data("name", "中文").post().equals("中文");
        assert path("/demo2/param/d/*").data("name", "https://a.a.a/b/c/_xxx").post().equals("https://a.a.a/b/c/_xxx");
    }

    @Test
    public void test2f() throws IOException {
        assert path("/demo2/param/e/p_q/obj/id")
                .get().equals("/demo2/param/e/p_q/obj/id:p_q-obj-id");
    }

    @Test
    public void test2g() throws IOException {
        String json = path("/demo2/param/array_str?aaa=1&aaa=2&aaa=中文")
                .get();

        assert ONode.load(json).toJson().equals("[\"1\",\"2\",\"中文\"]");
    }

    @Test
    public void test2g_2() throws IOException {
        String json = path("/demo2/param/array_str?aaa=1,2,中文").get();
        assert ONode.load(json).toJson().equals("[\"1\",\"2\",\"中文\"]");
    }

    @Test
    public void test2g_3() throws IOException {
        String json = path("/demo2/param/array_str")
                .data("aaa", "1,2,中文")
                .post();

        assert ONode.load(json).toJson().equals("[\"1\",\"2\",\"中文\"]");
    }



    @Test
    public void test2g2() throws IOException {
        String json = path("/demo2/param/array_str2?aaa=1&aaa=2&aaa=中文")
                .get();

        assert ONode.load(json).toJson().equals("[\"1\",\"2\",\"中文\"]");
    }

    @Test
    public void test2g2_2() throws IOException {
        String json = path("/demo2/param/array_str2?aaa=1,2,中文").get();
        assert ONode.load(json).toJson().equals("[\"1\",\"2\",\"中文\"]");
    }

    @Test
    public void test2g2_3() throws IOException {
        String json = path("/demo2/param/array_str2")
                .data("aaa", "1,2,中文")
                .post();

        assert ONode.load(json).toJson().equals("[\"1\",\"2\",\"中文\"]");
    }





    @Test
    public void test2h() throws IOException {
        assert path("/demo2/param/array_Int?aaa=1&aaa=2&ccc=3")
                .get().equals("[1,2]");
    }

    @Test
    public void test2h_2() throws IOException {
        assert path("/demo2/param/array_Int?aaa=1,2&ccc=3")
                .get().equals("[1,2]");
    }

    @Test
    public void test2h_3() throws IOException {
        assert path("/demo2/param/array_Int")
                .data("aaa", "1,2")
                .data("ccc", "3")
                .post()
                .equals("[1,2]");
    }



    @Test
    public void test2h2() throws IOException {
        assert path("/demo2/param/array_Int2?aaa=1&aaa=2&ccc=3")
                .get().equals("[1,2]");
    }

    @Test
    public void test2h2_2() throws IOException {
        assert path("/demo2/param/array_Int2?aaa=1,2&ccc=3")
                .get().equals("[1,2]");
    }

    @Test
    public void test2h2_3() throws IOException {
        assert path("/demo2/param/array_Int2")
                .data("aaa", "1,2")
                .data("ccc", "3")
                .post()
                .equals("[1,2]");
    }




    @Test
    public void test2i() throws IOException {
        assert path("/demo2/param/array_int?aaa=1&aaa=2&ccc=3")
                .get().equals("[1,2]");
    }

    @Test
    public void test2j() throws IOException {
        String json = path("/demo2/param/model?id=1&name=xxx&sex=2&date=2019-12-1&aaa=1&aaa=2")
                .get();

        //assert .equals("{\"id\":1,\"name\":\"xxx\",\"sex\":2,\"date\":1575129600000,\"aaa\":[1,2]}");

        ONode oNode = ONode.load(json);

        assert oNode.get("id").getInt() == 1;
        assert oNode.get("aaa").count() == 2;
        assert oNode.get("sex").getInt() == 2;
    }

    @Test
    public void test2j_2() throws IOException {
        String json = "{\"id\":1,\"name\":\"xxx\",\"sex\":2,\"date\":\"2019-11-11T11:11:11\",\"aaa\":[1,2]}";

        String json2 = path("/demo2/param/model").body(json, "application/json")
                .post();

        ONode oNode = ONode.load(json2);

        assert oNode.get("id").getInt() == 1;
        assert oNode.get("aaa").count() == 2;
        assert oNode.get("sex").getInt() == 2;
    }

    @Test
    public void test2k() throws IOException {
        assert path("/demo2/param/date?date=2018-11-11&date2=2019-11-11T11:11:11")
                .get().equals("Sun Nov 11 00:00:00 CST 2018 # Mon Nov 11 11:11:11 CST 2019");
    }

    @Test
    public void test2k2() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("username", "noear");
        map.put("password", "zk1234");
        assert path("/demo2/param/login").data(map).post().equals("noear # zk1234");
    }

    @Test
    public void test2o() throws IOException {
        assert path("/demo2/view").get().indexOf("你好 world! in XController") > 0;
    }

    @Test
    public void test2p() throws IOException {
        ONode n = ONode.loadStr(path("/demo2/json").get());
        assert n.contains("@type") == false;
        assert n.get("msg").getString().indexOf("你好") >= 0;
    }

    @Test
    public void test2u() throws IOException {
        assert path("/demo2/rpc/json").get().indexOf("你好") > 0;
    }

    @Test
    public void test2u_nami() throws IOException {
        Demo2Rpc rpc = Nami.builder()
                .path("/demo2/rpc/")
                .upstream(() -> "http://localhost:" + Solon.cfg().serverPort())
                .headerSet("name", "noear")
                .create(Demo2Rpc.class);
        assert "noear".equals(rpc.header());
    }

    @Test
    public void test41() throws IOException {
        assert path("/demo4/*").get().equals("是插件生出了我...");
    }

    @Test
    public void test61() throws IOException {
        String json = path("/demo6/aop").get();

        assert ONode.load(json).toJson().equals("{\"rockapi12\":\"我是：Rockservice1\",\"rockapi11\":\"我是：Rockservice1\",\"rockapi2\":\"我是：Rockservice2\",\"rockapi132\":\"我是：Rockservice3\"}");
    }

    @Test
    public void test63() throws IOException {
        assert path("/demo6/aop3").get().equals("我是：Rockservice3");
    }

    @Test
    public void test71() throws IOException {
        assert path("/demo7/test").get().equals("/demo7/test");
    }

    @Test
    public void test72() throws IOException {
        assert path("/demo7/exception").get().contains("出错了");
    }

    @Test
    public void test81() throws IOException {
        String json0 = "{\"username\":\"noear\",\"paasword\":1234,\"test\":{\"url\":\"jdbc:mysql://127.0.0.1/user\",\"paasword\":\"12\",\"username\":\"root\"},\"nameuser_2\":\"noear\",\"dbcfg\":{\"url\":\"jdbc:mysql://127.0.0.1/user\",\"paasword\":\"12\",\"username\":\"root\"}}";
        String json1 = path("/demo8/config_inject").get();

        System.out.println(json1);

        System.out.println("json0:: " + ONode.loadStr(json0).toJson());
        System.out.println("json1:: " + ONode.loadStr(json1).toJson());
        assert ONode.loadStr(json0).toJson().length() == ONode.loadStr(json1).toJson().length();
    }

    @Test
    public void test82() throws IOException {
        assert ONode.loadStr(path("/demo8/config_all").get()).get("username").getString().equals("noear");
    }

    @Test
    public void test83() throws IOException {
        String json = path("/demo8/config_system").get();
        String val = ONode.loadStr(json).get("file.separator").getString();

        System.out.println(val);
        assert val.equals("/") || val.equals("\\/") || val.equals(File.separator);
    }

    @Test
    public void test84() throws IOException {
        assert ONode.loadStr(path("/demo8/user").get()).get("name").getString().equals("noear");
    }


    @Test
    public void testa1() throws IOException {
        assert path("/demoa/trigger")
                .get().equals(
                        "XInterceptor1::你被我拦截了(/demoa/**)!!!\n" +
                                "XInterceptor2::你被我拦截了(/demoa/**)!!!\n" +
                                "XInterceptor3::你被我拦截了(/demoa/**)!!!\n" +
                                "/demoa/triggerXInterceptor_aft::你被我拦截了(/demoa/**)!!!\n");
    }
}
