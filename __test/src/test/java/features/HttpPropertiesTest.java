package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.net.URL;
import java.net.URLEncoder;

/**
 * @author noear 2021/12/3 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class HttpPropertiesTest extends HttpTester {


    @Test
    public void json_bean() throws Exception {
        String tmp = path("/demo2/json/bean?user.id=1&user.name=noear&user.aaa[]=1&user.aaa[]=2").get();
        assert tmp.contains("noear");
        assert tmp.contains("[");
        assert tmp.contains("1,2");
    }

    @Test
    public void json_bean_encode() throws Exception {
        String url_params_encoded = "user.id=1&user.name=noear&user.aaa%5B%5D=1&user.aaa%5B%5D=2";

        String tmp = path("/demo2/json/bean?" + url_params_encoded).get();
        assert tmp.contains("noear");
        assert tmp.contains("[");
        assert tmp.contains("1,2");
    }

    @Test
    public void json_bean_b() throws Exception {
        String tmp = path("/demo2/json/bean?user.id=1&user.name=noear&user.aaa[]=1&user.aaa[]=2")
                .header("X-Serialization","@properties")
                .get();
        assert tmp.contains("name=noear");
        assert tmp.contains("aaa[0]=1");
        assert tmp.contains("aaa[1]=2");
    }

//    @Test
//    public void json_bean_2() throws Exception {
//        String tmp = path("/demo2/json/bean")
//                .data("user.id","1")
//                .data("user.name","noear")
//                .post();
//        assert tmp.contains("noear");
//    }

    @Test
    public void json_bean2() throws Exception {
        String tmp = path("/demo2/json/bean?id=1&name=noear&aaa[]=1&aaa[]=2").get();
        assert tmp.contains("noear");
        assert tmp.contains("[");
        assert tmp.contains("1,2");
    }
}
