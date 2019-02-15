package webapp.demo6_aop.dao;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XService;
import org.noear.solon.core.XContext;

@XService
public class Rockservice2 implements Rockapi {

    public String test1(Integer a){
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
