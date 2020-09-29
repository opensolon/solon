package webapp.demo5_rpc.protocol;

import org.noear.fairy.annotation.Mapping;

public interface UserService5 {
    @Mapping("PUT user/getUser")
    UserModel xxx(Integer userId);
}
