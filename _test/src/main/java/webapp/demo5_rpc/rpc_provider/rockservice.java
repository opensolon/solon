package webapp.demo5_rpc.rpc_provider;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XBefore;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMethod;
import webapp.demo5_rpc.rockapi;
import webapp.models.UserModel;

import java.util.ArrayList;
import java.util.List;

@XBefore({SocketChannelAdapter.class})
@XMapping(value = "/demo5/test", method = {XMethod.HTTP, XMethod.SOCKET})
@XBean(remoting = true)
public class rockservice implements rockapi {

    @Override
    public Object test1(Integer a) {
        return XContext.current().method() + "::test1=" + a;
    }

    @Override
    public Object test2(int b){
        return XContext.current().path();
    }

    @Override
    public Object test3(){
        throw new RuntimeException("xxxx");
    }

    @Override
    public UserModel test4() {
        UserModel m = new UserModel();
        m.id = 1;
        m.name = "user 1";
        m.sex = 1;
        return m;
    }

    @Override
    public List<UserModel> test5(){
        List<UserModel> list =new ArrayList<>();

        UserModel m = new UserModel();
        m.id = 1;
        m.name = "user 1";
        m.sex = 1;

        list.add(m);

        m = new UserModel();
        m.id = 2;
        m.name = "user 2";
        m.sex = 0;

        list.add(m);


        return list;
    }
}
