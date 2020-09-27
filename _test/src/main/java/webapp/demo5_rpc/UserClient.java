package webapp.demo5_rpc;

import org.noear.fairy.annotation.FairyClient;
import org.noear.solon.annotation.XBean;
import webapp.demo5_rpc.protocol.UserModel;
import webapp.demo5_rpc.protocol.UserService;

@XBean
public class UserClient {
    //直接指定服务端地址
    @FairyClient("http://localhost:8080/demo5/user/")
    public UserService userService;

    //使用负载
    @FairyClient("local:/demo5/user/")
    public UserService userService2;

    public void test() {
        UserModel user = userService.getUser(12);
        System.out.println(user);

        user = userService2.getUser(23);
        System.out.println(user);
    }
}
