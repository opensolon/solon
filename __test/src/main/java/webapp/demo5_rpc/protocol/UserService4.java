package webapp.demo5_rpc.protocol;

import org.noear.nami.annotation.NamiMapping;

public interface UserService4 {
    @NamiMapping("GET getUser")
    UserModel xxx(Integer userId);
}
