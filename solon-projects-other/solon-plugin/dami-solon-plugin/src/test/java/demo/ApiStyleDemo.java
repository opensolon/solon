package demo;

import demo.mod2.UserEventSender;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

@Component
public class ApiStyleDemo {
    @Inject
    UserEventSender userEventSender;

    @Init
    public void test(){
        //发送测试
        long rst = userEventSender.created(1, "noear");
        System.out.println("收到返回：" + rst);
        userEventSender.updated(2, "dami");
    }

    public static void main(String[] args) {
        Solon.start(ApiStyleDemo.class, args);
    }
}
