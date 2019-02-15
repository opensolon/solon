package webapp.demo5_rpc.rpc_provider;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XService;
import org.noear.solon.core.XContext;
import webapp.demo5_rpc.rockapi;

@XMapping("/demo5/test")
@XService(remoting = true)
public class rockservice implements rockapi {

    public Object test1(Integer a){
        return "test1="+a;
    }

    public Object test2(){
        return XContext.current().path();
    }

    public Object test3(){
        throw new RuntimeException("xxxx");
    }

    public Object test4(){
        return XContext.current().path();
    }

    public Object test5(){
        return XContext.current().path();
    }

    public Object test6(){
        return XContext.current().path();
    }

    public Object test7(){
        return XContext.current().path();
    }

    public Object test8(){
        return XContext.current().path();
    }
}
