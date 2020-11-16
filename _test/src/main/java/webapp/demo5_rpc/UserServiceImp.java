package webapp.demo5_rpc;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Mapping;

import webapp.demo5_rpc.protocol.UserModel;
import webapp.demo5_rpc.protocol.UserService;

//开启bean的远程服务
@Bean(remoting = true)
@Mapping("/demo5/user/")
public class UserServiceImp implements UserService {
    @Override
    public UserModel getUser(Integer userId) {
        UserModel model = new UserModel();
        model.setId(userId);
        model.setName("user-" + userId);

        return model;
    }
}
