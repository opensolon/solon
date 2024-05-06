package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/12/3 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class HttpPropertiesTest extends HttpTester {


    @Test
    public void json_bean() throws Exception {
        String tmp = path("/demo2/props/bean?user.id=1&user.name=noear&user.aaa[]=1&user.aaa[]=2").get();
        assert tmp.contains("noear");
        assert tmp.contains("[");
        assert tmp.contains("1,2");
    }

    @Test
    public void json_bean_encode() throws Exception {
        String tmp = path("/demo2/props/bean?user.id=1&user.name=noear&user.aaa%5B%5D=1&user.aaa%5B%5D=2").get();
        assert tmp.contains("noear");
        assert tmp.contains("[");
        assert tmp.contains("1,2");
    }

    @Test
    public void json_bean_header() throws Exception {
        String tmp = path("/demo2/props/bean?user.id=1&user.name=noear&user.aaa[]=1&user.aaa[]=2")
                .header("X-Serialization", "@properties")
                .get();
        assert tmp.contains("name=noear");
        assert tmp.contains("aaa[0]=1");
        assert tmp.contains("aaa[1]=2");
    }

    @Test
    public void json_bean_post() throws Exception {
        String tmp = path("/demo2/props/bean")
                .data("user.id", "1")
                .data("user.name", "noear")
                .data("user.aaa[]", "1")
                .data("user.aaa[]", "2")
                .post();
        assert tmp.contains("noear");
        assert tmp.contains("1,2");
    }

    @Test
    public void json_bean2() throws Exception {
        String tmp = path("/demo2/props/bean?id=1&name=noear&aaa[]=1&aaa[]=2").get();
        assert tmp.contains("noear");
        assert tmp.contains("[");
        assert tmp.contains("1,2");
    }

    @Test
    public void json_bean_map_post() throws Exception {
        String tmp = path("/demo2/props/bean_map")
                .data("user.id", "1")
                .data("user.name", "noear")
                .data("user.aaa[]", "1")
                .data("user.aaa[]", "2")
                .post();
        assert tmp.contains("noear");
        assert tmp.contains("\"1\",\"2\"");
    }

    @Test
    public void json_bean_map_post2() throws Exception {
        String tmp = path("/demo2/props/bean_map")
                .data("user[id]", "1")
                .data("user[\"name\"]", "noear")
                .post();
        String json = "{\"user\":{\"name\":\"noear\",\"id\":\"1\"}}";

        assert tmp.contains("noear");
        assert json.equals(tmp);
    }

    @Test
    public void json_bean_map_post3() throws Exception {
        String tmp = path("/demo2/props/bean_map")
                .data("user[id]", "1")
                .data("user[\"id_EQ\"]", "noear")
                .post();
        String json = "{\"user\":{\"id_EQ\":\"noear\",\"id\":\"1\"}}";

        assert tmp.contains("noear");
        assert json.equals(tmp);
    }

    @Test
    public void json_bean_map_post4() throws Exception {
        String tmp = path("/demo2/props/bean_map")
                .data("user[id]", "1")
                .data("user[\"id_EQ\"]", "noear")
                .multipart(true)
                .post();
        String json = "{\"user\":{\"id_EQ\":\"noear\",\"id\":\"1\"}}";

        assert tmp.contains("noear");
        assert json.equals(tmp);
    }
}
