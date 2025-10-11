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

import java.io.IOException;

/**
 * @author noear 2021/6/15 created
 */
@SolonTest(App.class)
public class HttpValidTest extends HttpTester {

    @Test
    public void test2v_date() throws IOException {
        assert path("/demo2/valid/date?val1=2020-12-12T12:12:12").get().equals("OK");
        assert path("/demo2/valid/date?val1=").get().equals("OK");
        assert path("/demo2/valid/date").get().equals("OK");
        assert path("/demo2/valid/date?val1=2020-12-12 12:12:12").get().equals("OK") == false;
    }

    @Test
    public void test2v_date2() throws IOException {
        assert path("/demo2/valid/date2?val1=2020-12-12").get().equals("OK");
        assert path("/demo2/valid/date2?val1=2020-12-12T12:12:12").get().equals("OK") == false;
        assert path("/demo2/valid/date2?val1=2020-12-12 12:12").get().equals("OK") == false;
    }

    @Test
    public void test2v_date3() throws IOException {
        assert path("/demo2/valid/date3?val1=2020-12-12T12:12:12").get().equals("OK");
        assert path("/demo2/valid/date3?val1=").get().equals("OK") == false;
        assert path("/demo2/valid/date3").get().equals("OK") == false;
        assert path("/demo2/valid/date3?val1=2020-12-12 12:12:12").get().equals("OK") == false;
    }

    @Test
    public void test2v_dmax() throws IOException {
        assert path("/demo2/valid/dmax?val1=9.0&val2=9.0").get().equals("OK");
        assert path("/demo2/valid/dmax?val1=11.0&val2=11.0").get().equals("OK") == false;
    }


    @Test
    public void test2v_dmin() throws IOException {
        assert path("/demo2/valid/dmin?val1=9.0&val2=9.0").get().equals("OK") == false;
        assert path("/demo2/valid/dmin?val1=11.0&val2=11.0").get().equals("OK");
    }

    @Test
    public void test2v_email() throws IOException {
        assert path("/demo2/valid/email?val1=noear@live.cn").get().equals("OK");
        assert path("/demo2/valid/email?val1=ja.yan@163.com").get().equals("OK");
        assert path("/demo2/valid/email?val1=noe0ar@li-ve.com.cn").get().equals("OK");
        assert path("/demo2/valid/email?val1=noearlive.cn").get().equals("OK") == false;
        assert path("/demo2/valid/email?val1=").get().equals("OK");
    }

    @Test
    public void test2v_email2() throws IOException {
        assert path("/demo2/valid/email2?val1=noear@live.cn").get().equals("OK");
        assert path("/demo2/valid/email2?val1=noe0ar@li-ve.com.cn").get().equals("OK") == false;
        assert path("/demo2/valid/email2?val1=noearlive.cn").get().equals("OK") == false;
    }

    @Test
    public void test2v_email3() throws IOException {
        assert path("/demo2/valid/email3?val1=noear@live.cn").get().equals("OK");
        assert path("/demo2/valid/email3?val1=noe0ar@li-ve.com.cn").get().equals("OK");
        assert path("/demo2/valid/email3?val1=9979331@qq.com").get().equals("OK");
        assert path("/demo2/valid/email3?val1=noearlive.cn").get().equals("OK") == false;
        assert path("/demo2/valid/email3?val1=").get().equals("OK") == false;
    }


    @Test
    public void test2v_numeric() throws IOException {
        assert path("/demo2/valid/numeric?val1=").get().equals("OK");
        assert path("/demo2/valid/numeric?val1=1212").get().equals("OK");
        assert path("/demo2/valid/numeric?val1=aaa").get().equals("OK") == false;
    }

    @Test
    public void test2v_numeric2() throws IOException {
        assert path("/demo2/valid/numeric2?val1=").get().equals("OK") == false;
        assert path("/demo2/valid/numeric2?val1=1212").get().equals("OK");
        assert path("/demo2/valid/numeric2?val1=aaa").get().equals("OK") == false;
    }



    @Test
    public void test2v_max() throws IOException {
        assert path("/demo2/valid/max?val1=9&val2=9").get().equals("OK");
        assert path("/demo2/valid/max?val1=9&val2=9.0").get().equals("OK") == false;
        assert path("/demo2/valid/max?val1=11&val2=11").get().equals("OK") == false;
    }

    @Test
    public void test2v_min() throws IOException {
        assert path("/demo2/valid/min?val1=9&val2=9").get().equals("OK") == false;
        assert path("/demo2/valid/min?val1=11&val2=11").get().equals("OK");
        assert path("/demo2/valid/min?val1=11.0&val2=11").get().equals("OK") == false;
    }

    @Test
    public void test2v_nrs() throws IOException {
        assert path("/demo2/valid/nrs").get().equals("OK");
        assert path("/demo2/valid/nrs").get().equals("OK") == false;
    }

    @Test
    public void test2v_nblank() throws IOException {
        assert path("/demo2/valid/nblank?val1= &val2=2").get().equals("OK") == false;
        assert path("/demo2/valid/nblank?val1=11").get().equals("OK") == false;
        assert path("/demo2/valid/nblank?val1=11&val2=11").get().equals("OK");
    }

    @Test
    public void test2v_nempty() throws IOException {
        assert path("/demo2/valid/nempty?val1= &val2=2").get().equals("OK");
        assert path("/demo2/valid/nempty?val1=11&val2=").get().equals("OK") == false;
        assert path("/demo2/valid/nempty?val1=11").get().equals("OK") == false;
    }

    @Test
    public void test2v_nnull() throws IOException {
        assert path("/demo2/valid/nnull?val1=&val2=2").get().equals("OK");
        assert path("/demo2/valid/nnull?val1=&val2=").get().equals("OK");
        assert path("/demo2/valid/nnull?val1=11").get().equals("OK") == false;
    }

    @Test
    public void test2v_nnull2() throws IOException {
        assert path("/demo2/valid/nnull2?val1=1").get().equals("OK");
        assert path("/demo2/valid/nnull2").get().equals("OK") == false;
    }

    @Test
    public void test2v_nzero() throws IOException {
        assert path("/demo2/valid/nzero?val1=1&val2=2").get().equals("OK");
        assert path("/demo2/valid/nzero?val1=0&val2=0").get().equals("OK") == false;
        assert path("/demo2/valid/nzero?val1=1&val2=2.0").get().equals("OK") == false;
        assert path("/demo2/valid/nzero?val1=1&val2=").get().equals("OK") == false;
        assert path("/demo2/valid/nzero?val1=11").get().equals("OK") == false;
    }

    @Test
    public void test2v_nzero2() throws IOException {
        assert path("/demo2/valid/nzero2?val1=1&val2=2").get().equals("OK");
        assert path("/demo2/valid/nzero2?val1=0&val2=0").get().equals("OK") == false;
        assert path("/demo2/valid/nzero2?val1=1&val2=2.0").get().equals("OK") == false;
        assert path("/demo2/valid/nzero2?val1=1&val2=").get().equals("OK") == false;
        assert path("/demo2/valid/nzero2?val1=11").get().equals("OK") == false;
    }

    @Test
    public void test2v_null() throws IOException {
        assert path("/demo2/valid/null?val1=1&val2=2").get().equals("OK") == false;
        assert path("/demo2/valid/null?val1=1&val2=").get().equals("OK") == false;
        assert path("/demo2/valid/null").get().equals("OK");
    }

    @Test
    public void test2v_null2() throws IOException {
        assert path("/demo2/valid/null2?val1=1&val2=2").get().equals("OK") == false;
        assert path("/demo2/valid/null2?val1=1&val2=").get().equals("OK") == false;
        assert path("/demo2/valid/null2").get().equals("OK");
    }

    @Test
    public void test2v_null_2() throws IOException {
        assert path("/demo2/valid/null").get().equals("OK");
    }

    @Test
    public void test2v_patt() throws IOException {
        assert path("/demo2/valid/patt?val1=111-12&val2=222-12").get().equals("OK");
        assert path("/demo2/valid/patt?val1=111-12&val2=").get().equals("OK");
        assert path("/demo2/valid/patt?val1=111-12&val2=1").get().equals("OK") == false;
        assert path("/demo2/valid/patt").get().equals("OK");
    }

    @Test
    public void test2v_patt_json() throws IOException {
        assert path("/demo2/valid/patt").bodyOfJson("{val1:'111-12',val2:'222-12'}").post().equals("OK");
        assert path("/demo2/valid/patt").bodyOfJson("{val1:'111-12',val2:''}").post().equals("OK");
        assert path("/demo2/valid/patt").bodyOfJson("{val1:'111-12',val2:'1'}").post().equals("OK") == false;
        assert path("/demo2/valid/patt").bodyOfJson("{val1:'111-12',val2:'1'}").post().contains("demo");
        assert path("/demo2/valid/patt").bodyOfJson("{}").post().equals("OK");
    }

    @Test
    public void test2v_patt2() throws IOException {
        assert path("/demo2/valid/patt2?val1=111-12&val2=222-12").get().equals("OK");
        assert path("/demo2/valid/patt2?val1=111-12&val2=").get().equals("OK") == false;
        assert path("/demo2/valid/patt2").get().equals("OK")== false;
    }

    @Test
    public void test2v_patt2_json() throws IOException {
        assert path("/demo2/valid/patt2").bodyOfJson("{val1:'111-12',val2:'222-12'}").post().equals("OK");
        assert path("/demo2/valid/patt2").bodyOfJson("{val1:'111-12',val2:'222-12-6'}").post().equals("OK") == false;
        assert path("/demo2/valid/patt2").bodyOfJson("{val1:'111-12',val2:''}").post().equals("OK") == false;
    }

    @Test
    public void test2v_length() throws IOException {
        assert path("/demo2/valid/length?val1=111-2&val2=222-2").get().equals("OK");
        assert path("/demo2/valid/length?val1=11-12&val2=").get().equals("OK") == false;
        assert path("/demo2/valid/length?val1=11-12&val2=12121212").get().equals("OK") == false;
        assert path("/demo2/valid/length").get().equals("OK") == false;
    }

    @Test
    public void test2v_length2() throws IOException {
        assert path("/demo2/valid/length2?val1=111-2&val2=222-2").get().equals("OK");
        assert path("/demo2/valid/length2?val1=11-12&val2=").get().equals("OK") == true;
        assert path("/demo2/valid/length2?val1=11-12&val2=1").get().equals("OK") == false;
        assert path("/demo2/valid/length2?val1=11-12&val2=12121212").get().equals("OK") == false;
        assert path("/demo2/valid/length2").get().equals("OK");
    }

    @Test
    public void test2v_bean() throws IOException {
        assert path("/demo2/valid/bean?mobile=x&password=x").get().equals("OK");
        assert path("/demo2/valid/bean?password=x").get().equals("OK") == false;
    }


    @Test
    public void test2v_beanlist() throws IOException {
        ONode node = ONode.loadStr("{list:[{mobile:'x',password:'x'},{mobile:'y',password:'y'}]}");

        ONode node2 = ONode.loadStr("{list:[{mobile:'x'},{mobile:'y',password:'y'}]}");


        assert path("/demo2/valid/beanlist").bodyOfJson(node.toJson()).post().equals("OK");
        assert path("/demo2/valid/beanlist").bodyOfJson(node2.toJson()).post().equals("OK") == false;
    }

    @Test
    public void test2v_beanlist2() throws IOException {
        ONode node = ONode.loadStr("{list:[{mobile:'x',password:'x'},{mobile:'y',password:'y'}]}");

        ONode node2 = ONode.loadStr("{list:[{mobile:'x'},{mobile:'y',password:'y'}]}");


        assert path("/demo2/valid/beanlist2").bodyOfJson(node.toJson()).post().equals("OK");
        assert path("/demo2/valid/beanlist2").bodyOfJson(node2.toJson()).post().equals("OK") == false;
    }

    @Test
    public void test2v_beanlist2_b() throws IOException {
        assert path("/demo2/valid/beanlist2").bodyOfJson("[]").post().equals("OK") == false;
        assert path("/demo2/valid/beanlist2").bodyOfJson("[{}]").post().equals("OK") == false;
    }


    @Test
    public void test2v_map() throws IOException {
        ONode node = ONode.loadStr("{a:{mobile:'x',password:'x'},b:{mobile:'y',password:'y'}}");

        ONode node2 = ONode.loadStr("{a:{mobile:'x'},b:{mobile:'y'}}");


        assert path("/demo2/valid/map").bodyOfJson(node.toJson()).post().equals("OK");
        assert path("/demo2/valid/map").bodyOfJson(node2.toJson()).post().equals("OK") == false;
    }

}
