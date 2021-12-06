package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2021/6/15 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpValidTest extends _TestBase{

    @Test
    public void test2v_date() throws IOException {
        assert get("/demo2/valid/date?val1=2020-12-12T12:12:12").equals("OK");
        assert get("/demo2/valid/date?val1=").equals("OK");
        assert get("/demo2/valid/date").equals("OK");
        assert get("/demo2/valid/date?val1=2020-12-12 12:12:12").equals("OK") == false;
    }

    @Test
    public void test2v_date2() throws IOException {
        assert get("/demo2/valid/date2?val1=2020-12-12").equals("OK");
        assert get("/demo2/valid/date2?val1=2020-12-12T12:12:12").equals("OK") == false;
        assert get("/demo2/valid/date2?val1=2020-12-12 12:12").equals("OK") == false;
    }

    @Test
    public void test2v_date3() throws IOException {
        assert get("/demo2/valid/date3?val1=2020-12-12T12:12:12").equals("OK");
        assert get("/demo2/valid/date3?val1=").equals("OK") == false;
        assert get("/demo2/valid/date3").equals("OK") == false;
        assert get("/demo2/valid/date3?val1=2020-12-12 12:12:12").equals("OK") == false;
    }

    @Test
    public void test2v_dmax() throws IOException {
        assert get("/demo2/valid/dmax?val1=9.0&val2=9.0").equals("OK");
        assert get("/demo2/valid/dmax?val1=11.0&val2=11.0").equals("OK") == false;
    }


    @Test
    public void test2v_dmin() throws IOException {
        assert get("/demo2/valid/dmin?val1=9.0&val2=9.0").equals("OK") == false;
        assert get("/demo2/valid/dmin?val1=11.0&val2=11.0").equals("OK");
    }

    @Test
    public void test2v_email() throws IOException {
        assert get("/demo2/valid/email?val1=noear@live.cn").equals("OK");
        assert get("/demo2/valid/email?val1=noe0ar@li-ve.com.cn").equals("OK");
        assert get("/demo2/valid/email?val1=noearlive.cn").equals("OK") == false;
        assert get("/demo2/valid/email?val1=").equals("OK");
    }

    @Test
    public void test2v_email2() throws IOException {
        assert get("/demo2/valid/email2?val1=noear@live.cn").equals("OK");
        assert get("/demo2/valid/email2?val1=noe0ar@li-ve.com.cn").equals("OK") == false;
        assert get("/demo2/valid/email2?val1=noearlive.cn").equals("OK") == false;
    }

    @Test
    public void test2v_email3() throws IOException {
        assert get("/demo2/valid/email3?val1=noear@live.cn").equals("OK");
        assert get("/demo2/valid/email3?val1=noe0ar@li-ve.com.cn").equals("OK");
        assert get("/demo2/valid/email3?val1=9979331@qq.com").equals("OK");
        assert get("/demo2/valid/email3?val1=noearlive.cn").equals("OK") == false;
        assert get("/demo2/valid/email3?val1=").equals("OK") == false;
    }


    @Test
    public void test2v_numeric() throws IOException {
        assert get("/demo2/valid/numeric?val1=").equals("OK");
        assert get("/demo2/valid/numeric?val1=1212").equals("OK");
        assert get("/demo2/valid/numeric?val1=aaa").equals("OK") == false;
    }

    @Test
    public void test2v_numeric2() throws IOException {
        assert get("/demo2/valid/numeric2?val1=").equals("OK") == false;
        assert get("/demo2/valid/numeric2?val1=1212").equals("OK");
        assert get("/demo2/valid/numeric2?val1=aaa").equals("OK") == false;
    }



    @Test
    public void test2v_max() throws IOException {
        assert get("/demo2/valid/max?val1=9&val2=9").equals("OK");
        assert get("/demo2/valid/max?val1=9&val2=9.0").equals("OK") == false;
        assert get("/demo2/valid/max?val1=11&val2=11").equals("OK") == false;
    }

    @Test
    public void test2v_min() throws IOException {
        assert get("/demo2/valid/min?val1=9&val2=9").equals("OK") == false;
        assert get("/demo2/valid/min?val1=11&val2=11").equals("OK");
        assert get("/demo2/valid/min?val1=11.0&val2=11").equals("OK") == false;
    }

    @Test
    public void test2v_nrs() throws IOException {
        assert get("/demo2/valid/nrs").equals("OK");
        assert get("/demo2/valid/nrs").equals("OK") == false;
    }

    @Test
    public void test2v_nblank() throws IOException {
        assert get("/demo2/valid/nblank?val1= &val2=2").equals("OK") == false;
        assert get("/demo2/valid/nblank?val1=11").equals("OK") == false;
        assert get("/demo2/valid/nblank?val1=11&val2=11").equals("OK");
    }

    @Test
    public void test2v_nempty() throws IOException {
        assert get("/demo2/valid/nempty?val1= &val2=2").equals("OK");
        assert get("/demo2/valid/nempty?val1=11&val2=").equals("OK") == false;
        assert get("/demo2/valid/nempty?val1=11").equals("OK") == false;
    }

    @Test
    public void test2v_nnull() throws IOException {
        assert get("/demo2/valid/nnull?val1=&val2=2").equals("OK");
        assert get("/demo2/valid/nnull?val1=&val2=").equals("OK");
        assert get("/demo2/valid/nnull?val1=11").equals("OK") == false;
    }

    @Test
    public void test2v_nzero() throws IOException {
        assert get("/demo2/valid/nzero?val1=1&val2=2").equals("OK");
        assert get("/demo2/valid/nzero?val1=1&val2=2.0").equals("OK") == false;
        assert get("/demo2/valid/nzero?val1=1&val2=").equals("OK") == false;
        assert get("/demo2/valid/nzero?val1=11").equals("OK") == false;
    }

    @Test
    public void test2v_null() throws IOException {
        assert get("/demo2/valid/null?val1=1&val2=2").equals("OK") == false;
        assert get("/demo2/valid/null?val1=1&val2=").equals("OK") == false;
        assert get("/demo2/valid/null").equals("OK");
    }

    @Test
    public void test2v_null2() throws IOException {
        assert get("/demo2/valid/null").equals("OK");
    }

    @Test
    public void test2v_patt() throws IOException {
        assert get("/demo2/valid/patt?val1=111-12&val2=222-12").equals("OK");
        assert get("/demo2/valid/patt?val1=111-12&val2=").equals("OK");
        assert get("/demo2/valid/patt").equals("OK");
    }

    @Test
    public void test2v_patt2() throws IOException {
        assert get("/demo2/valid/patt2?val1=111-12&val2=222-12").equals("OK");
        assert get("/demo2/valid/patt2?val1=111-12&val2=").equals("OK") == false;
        assert get("/demo2/valid/patt2").equals("OK")== false;
    }

    @Test
    public void test2v_length() throws IOException {
        assert get("/demo2/valid/length?val1=111-2&val2=222-2").equals("OK");
        assert get("/demo2/valid/length?val1=11-12&val2=").equals("OK") == false;
        assert get("/demo2/valid/length?val1=11-12&val2=12121212").equals("OK") == false;
        assert get("/demo2/valid/length").equals("OK");
    }

    @Test
    public void test2v_bean() throws IOException {
        assert get("/demo2/valid/bean?mobile=x&password=x").equals("OK");
        assert get("/demo2/valid/bean?password=x").equals("OK") == false;
    }


    @Test
    public void test2v_beanlist() throws IOException {
        ONode node = ONode.loadStr("{list:[{mobile:'x',password:'x'},{mobile:'y',password:'y'}]}");

        ONode node2 = ONode.loadStr("{list:[{mobile:'x'},{mobile:'y',password:'y'}]}");


        assert path("/demo2/valid/beanlist").bodyJson(node.toJson()).post().equals("OK");
        assert path("/demo2/valid/beanlist").bodyJson(node2.toJson()).post().equals("OK") == false;
    }

    @Test
    public void test2v_beanlist2() throws IOException {
        ONode node = ONode.loadStr("{list:[{mobile:'x',password:'x'},{mobile:'y',password:'y'}]}");

        ONode node2 = ONode.loadStr("{list:[{mobile:'x'},{mobile:'y',password:'y'}]}");


        assert path("/demo2/valid/beanlist2").bodyJson(node.toJson()).post().equals("OK");
        assert path("/demo2/valid/beanlist2").bodyJson(node2.toJson()).post().equals("OK") == false;
    }

}
