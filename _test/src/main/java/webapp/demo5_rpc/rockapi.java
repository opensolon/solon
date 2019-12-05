package webapp.demo5_rpc;

import org.noear.solonclient.annotation.XClient;
import webapp.models.UserModel;

import java.util.List;

@XClient("demo:/demo5/test/")
public interface rockapi {
    Object test1(Integer a);
    Object test2();
    Object test3();
    UserModel test4();
    List<UserModel> test5();
}