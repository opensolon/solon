package features;

import features._model.UserModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2021/12/3 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class HttpJsonTest extends HttpTester {
    @Test
    public void json_map() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        assert path("/demo2/json/map").bodyJson(oNode.toJson()).post().equals("12");
    }

    @Test
    public void json_map2() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        ONode oNode1 = new ONode();
        oNode1.set("userMap", oNode);

        assert path("/demo2/json/map").bodyJson(oNode1.toJson()).post().equals("12");
    }

    @Test
    public void json_list() throws Exception {
        List<UserModel> list = new ArrayList<>();

        UserModel userModel = new UserModel();
        userModel.id = 12;

        list.add(userModel);

        userModel = new UserModel();
        userModel.id = 13;

        list.add(userModel);

        assert path("/demo2/json/list").bodyJson(ONode.stringify(list)).post().equals("12");
    }

    @Test
    public void json_bean() throws Exception {
        String tmp = path("/demo2/json/bean?user.id=1&user.name=noear&user.aaa[]=1&user.aaa[]=2").get();
        assert tmp.contains("noear");
        assert tmp.contains("[");
        assert tmp.contains("1,2");
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
