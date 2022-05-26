package webapp.demo5_rpc;

import org.noear.solon.annotation.Mapping;

import org.noear.solon.annotation.Remoting;
import webapp.demo5_rpc.protocol.UserModel;
import webapp.demo5_rpc.protocol.UserService;

//开启bean的远程服务
@Mapping("/demo5/user/")
@Remoting
public class UserServiceImp implements UserService {
    @Override
    public UserModel getUser(Integer userId) {
        UserModel model = new UserModel();
        model.setId(userId);
        model.setName("user-" + userId);

        return model;
    }

    @Override
    public UserModel addUser(UserModel user) {
        return user;
    }

    @Override
    public void showError() {
        throw new RuntimeException("我要出错");
    }

    @Override
    public void showVoid() {

    }
}
