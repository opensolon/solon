package webapp.demo5_rpc;

import org.noear.nami.annotation.NamiClient;
import webapp.models.UserModel;

import java.util.List;

@NamiClient(name = "demo",path = "/demo5/test/", timeout = 20)
public interface rockapi {
    Object test1(Integer a);
    Object test2(int b);
    Object test3();
    UserModel test4();
    List<UserModel> test5();

    Object testerror();

    default Object textdef(){
        return test3();
    }
}