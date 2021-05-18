package webapp.demo5_rpc.rpc_provider;

import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import webapp.demo5_rpc.rockapi;
import webapp.models.UserModel;

import java.util.ArrayList;
import java.util.List;

@Before({SocketChannelAdapter.class})
@Mapping(value = "/demo5/test", method = {MethodType.HTTP, MethodType.SOCKET})
@Remoting
public class rockservice implements rockapi {

    @Override
    public Object test1(Integer a) {
        return Context.current().method() + "::test1=" + a;
    }

    @Override
    public Object test2(int b){
        return Context.current().path();
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
