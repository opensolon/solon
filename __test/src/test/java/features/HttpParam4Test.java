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
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.utils.Datetime;

import java.io.IOException;

/**
 * @author noear 2021/6/13 created
 */
@SolonTest(App.class)
public class HttpParam4Test extends HttpTester {
    @Test
    public void json() throws IOException {
        //走json通过，这个格式会有问题
        String json = "{id:1,name:'noear',date:'2021-12-12'}";

        String json2 = path("/demo2/param4/json").bodyOfJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
    }

    @Test
    public void json_2() throws IOException {
        //走json通过，这个格式会有问题
        String json = "{id:1,name:'noear',date:'2021-12-12'}";

        String json2 = path("/demo2/param4/json_2").bodyOfJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
    }

    @Test
    public void json_2_header() throws IOException {
        //走json通过，这个格式会有问题
        String json = "{id:1,name:'noear',date:'2021-12-12'}";

        String json2 = path("/demo2/param4/json_2/header/t").header("id", "1").bodyOfJson(json).post();

        assert json2 != null;
        assert json2.startsWith("1{");
    }

    @Test
    public void json_2_path() throws IOException {
        //走json通过，这个格式会有问题
        String json = "{id:1,name:'noear',date:'2021-12-12'}";

        String json2 = path("/demo2/param4/json_2/1").bodyOfJson(json).post();

        assert "1".equals(json2);
    }

    @Test
    public void json2() throws IOException {
        //走json通过，这个格式OK
        String json = "{id:1,name:'noear',date:'2021-12-12T12:12:12'}";

        String json2 = path("/demo2/param4/json").bodyOfJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
    }

    @Test
    public void json2_2() throws IOException {
        //走json通过，这个格式OK
        String json = "[{id:1,name:'noear',date:'2021-12-12T12:12:12'}]";

        String json2 = path("/demo2/param4/json2").bodyOfJson(json).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.isArray();
        assert oNode2.get(0).get("id").getInt() == 1;
        assert new Datetime(oNode2.get(0).get("date").getDate()).getYear() > 2000;
    }

    @Test
    public void json2_2_path() throws IOException {
        //走json通过，这个格式OK
        String json = "[{id:1,name:'noear',date:'2021-12-12T12:12:12'}]";

        String json2 = path("/demo2/param4/json2/1").bodyOfJson(json).post();

        assert "1".equals(json2);
    }

    @Test
    public void json3() throws IOException {
        //走json通过，这个格式OK
        String json = "[[1],[3,4],[5,6,9]]";

        String val = path("/demo2/param4/json3").bodyOfJson(json).post();

        assert "Long".equals(val);
    }

    @Test
    public void json3_2() throws IOException {
        //走json通过，这个格式OK
        String json = "{\"list\":[[1],[3,4],[5,6,9]]}";

        String val = path("/demo2/param4/json3").bodyOfJson(json).post();

        assert "Long".equals(val);
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
                .data("type", "vip")
                .post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
    }

    @Test
    public void param_2_2() throws IOException {
        //走param，@Param 的格式化会起效果
        String json2 = path("/demo2/param4/param2_2")
                .data("type", "vip")
                .post();

        ONode oNode2 = ONode.loadStr(json2);

        assert "vip".equals(oNode2.getString());
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
                .set("date", "2021-12-12");


        //走param，@Param 的格式化会起效果
        String json2 = path("/demo2/param4/param3").bodyOfJson(oNode.toJson()).post();

        ONode oNode2 = ONode.loadStr(json2);

        assert oNode2.get("id").getInt() == 1;
        assert new Datetime(oNode2.get("date").getDate()).getYear() > 2000;
        assert "bbb".equals(oNode2.get("icon").getString());
    }


    @Test
    public void body() throws IOException {
        String body = "{\"name\":\"noear\"}";

        String body2 = path("/demo2/param4/body").bodyOfJson(body).post();
        assert body.equals(body2);


        body2 = path("/demo2/param4/body").bodyOfTxt(body).post();
        assert body.equals(body2);
    }

    @Test
    public void body_map() throws IOException {
        String body = "{\"name\":\"noear\"}";
        String body2;

        body2 = path("/demo2/param4/body_map").data("name","noear").post();
        assert "1".equals(body2);

        body2 = path("/demo2/param4/body_map").bodyOfJson(body).post();
        assert "1".equals(body2);
    }

    @Test
    public void body_val() throws IOException {
        String body = "{name:'noear'}";
        String body2;

        body2 = path("/demo2/param4/body_val").data("name","noear").post();
        assert "1".equals(body2);

        body2 = path("/demo2/param4/body_val").bodyOfJson(body).post();
        assert "1".equals(body2);
    }

    @Test
    public void val() throws IOException {
        String body = "{name:'noear'}";
        String body2;

        body2 = path("/demo2/param4/val").data("name","noear").post();
        assert "noear".equals(body2);

        body2 = path("/demo2/param4/val").bodyOfJson(body).post();
        assert "noear".equals(body2);
    }

    @Test
    public void body2() throws IOException {
        String body = "{username:'noear',confirmPassword:'123456'}";

        System.out.println(body);
        String body2 = path("/demo2/param4/body2").bodyOfJson(body).post();
        ONode oNode = ONode.loadStr(body2);


        assert oNode.get("username").getString().equals("noear");
        assert oNode.get("confirmPassword").getString().equals("123456");
    }

    @Test
    public void body2_t() throws IOException {
        String body = "{page:1,pageSize:3,data:{id:5,name:'noear'}}";

        System.out.println(body);
        String body2 = path("/demo2/param4/body2_t").bodyOfJson(body).post();
        ONode oNode = ONode.loadStr(body2);


        assert oNode.get("page").getInt() == 1;
        assert oNode.get("data").get("id").getInt() == 5;
    }

    @Test
    public void test() throws IOException {
        String body = "'hello'";

        //paramMap()->body()
        System.out.println(body);
        String body2 = path("/demo2/param4/test").bodyOfJson(body).post();
        System.out.println(body2);

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
