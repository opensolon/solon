package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.nami.Nami;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.coder.hessian.HessianDecoder;
import org.noear.nami.coder.hessian.HessianEncoder;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demo5_rpc.protocol.UserModel;
import webapp.demo5_rpc.protocol.UserService;
import webapp.demo5_rpc.protocol.UserService4;
import webapp.demo5_rpc.protocol.UserService5;
import webapp.utils.Datetime;

import java.util.Date;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class RpcAndNamiTest {
    //直接指定服务端地址
    @NamiClient(url = "http://localhost:8080/demo5/user/")
    UserService userService;

    //使用负载
    @NamiClient(name = "local", path = "/demo5/user/", headers = {"Content-Type:application/json"}, timeout = 20)
    UserService userService2;


    //使用负载
    @NamiClient(name = "local", path = "/demo5/user/", headers = {"Content-Type:application/hessian"})
    UserService userService3;

    @Test
    public void test() {
        UserModel user = userService.getUser(12);
        System.out.println(user);
        assert user.getId() == 12;
    }

    @Test
    public void test_2() {
        UserModel user = userService.getUserDef(12);
        System.out.println(user);
        assert user.getId() == 12;
    }

    @Test
    public void test2() {
        UserModel user = userService2.getUser(23);
        System.out.println(user);
        assert user.getId() == 23;
    }

    @Test
    public void test2_2() throws Exception {
        UserModel userModel = new UserModel();
        userModel.setId(101);
        userModel.setName("noear");
        userModel.setLabel("k");
        userModel.setDate(Datetime.parse("2020-01-01", "yyyy-MM-dd").getFulltime());

        UserModel user = userService2.addUser(userModel);
        System.out.println(user);
        assert user.getId() == 101;
        assert new Datetime(user.getDate()).getDate() == 20200101;
    }

    @Test
    public void test2_3() throws Exception {
        UserModel userModel = new UserModel();
        userModel.setId(101);
        userModel.setName("noear");
        userModel.setLabel("k");
        userModel.setDate(Datetime.parse("2020-01-01", "yyyy-MM-dd").getFulltime());

        UserModel user = userService3.addUser(userModel);
        System.out.println(user);
        assert user.getId() == 101;
        assert new Datetime(user.getDate()).getDate() == 20200101;
    }

    @Test
    public void test3() {
        UserService userService3 = Nami.builder()
                .url("http://localhost:8080/demo5/user/")
                .create(UserService.class);

        UserModel user = userService3.getUser(23);
        System.out.println(user);
        assert user.getId() == 23;
    }

    @Test
    public void test3_2() {
        UserService userService3 = Nami.builder()
                .name("local")
                .path("/demo5/user/")
                .upstream(() -> "http://localhost:8080")
                .create(UserService.class);

        UserModel user = userService3.getUser(23);
        System.out.println(user);
        assert user.getId() == 23;
    }

    @Test
    public void test4() {
        UserService4 userService4 = Nami.builder()
                .url("http://localhost:8080/demo5/user/")
                .create(UserService4.class);

        UserModel user = userService4.xxx(23);
        System.out.println(user);
        assert user.getId() == 23;
    }

    @Test
    public void test5() {
        UserService5 userService5 = Nami.builder()
                .url("http://localhost:8080/demo5/")
                .create(UserService5.class);

        UserModel user = userService5.xxx(23);
        System.out.println(user);
        assert user.getId() == 23;
    }

    @Test
    public void test5_hessian() {
        UserService5 userService5 = Nami.builder()
                .url("http://localhost:8080/demo5/")
                .decoder(HessianDecoder.instance)
                .encoder(HessianEncoder.instance)
                .create(UserService5.class);

        UserModel user = userService5.xxx(23);
        System.out.println(user);
        assert user.getId() == 23;
    }

    @Test
    public void test6_throw() {
        UserService userService5 = Nami.builder()
                .url("http://localhost:8080/demo5/user/")
                .decoder(SnackDecoder.instance)
                .encoder(SnackEncoder.instance)
                .create(UserService.class);

        try {
            userService5.showError();

            System.out.println("error: 这里应该进不来");
        } catch (RuntimeException e) {
            e.printStackTrace();
            assert true;
            return;
        }

        assert false;
    }

    @Test
    public void test6_void() {
        try {
            userService.showVoid();
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
}
