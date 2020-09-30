package webapp.demo5_rpc.protocol;

import org.noear.fairy.annotation.Mapping;

public interface UserService4 {
    @Mapping("GET getUser")
    UserModel xxx(Integer userId);
}
