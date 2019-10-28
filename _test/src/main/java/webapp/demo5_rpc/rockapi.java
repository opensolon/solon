package webapp.demo5_rpc;

import org.noear.solonclient.annotation.XClient;

@XClient("http://localhost:8080/demo5/test")
public interface rockapi {

    Object test1(Integer a);

    Object test2();

    Object test3();

    Object test4();

    Object test5();

    Object test6();

    Object test7();

    Object test8();
}