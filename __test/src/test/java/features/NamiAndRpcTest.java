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
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.nami.Nami;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.coder.hessian.HessianDecoder;
import org.noear.nami.coder.hessian.HessianEncoder;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.nami.coder.snack3.SnackTypeEncoder;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo5_rpc.protocol.UserModel;
import webapp.demo5_rpc.protocol.UserService;
import webapp.demo5_rpc.protocol.UserService4;
import webapp.demo5_rpc.protocol.UserService5;
import webapp.utils.Datetime;

@SolonTest(App.class)
public class NamiAndRpcTest {
    //直接指定服务端地址
    @NamiClient(url = "http://localhost:8080/demo5/user/")
    UserService userService;

    //使用负载
    @NamiClient(name = "local", path = "/demo5/user/", headers = {"Content-Type=application/json"}, timeout = 20)
    UserService userService2;


    //使用负载
    @NamiClient(name = "local", path = "/demo5/user/", headers = {"Content-Type=application/hessian"})
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
                .encoder(SnackTypeEncoder.instance)
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

    @Test
    public void test7_405(){
        Exception ee = null;
        try {
            userService.getUserPut(1);
        }catch (Exception e){
            ee = e;
            e.printStackTrace();
        }

        assert ee != null;
        assert ee.getMessage().contains("405");
    }
}
