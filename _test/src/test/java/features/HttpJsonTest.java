package features;

import model.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2021/12/3 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpJsonTest extends HttpTestBase {
    @Test
    public void json_map() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        path("/demo2/json/map").bodyJson(oNode.toJson()).post();
    }

    @Test
    public void json_map2() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        ONode oNode1 = new ONode();
        oNode1.set("userMap", oNode);

        path("/demo2/json/map").bodyJson(oNode1.toJson()).post();
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

        path("/demo2/json/list").bodyJson(ONode.stringify(list)).post();
    }
}
