package webapp.demo5_rpc.protocol;


import org.noear.nami.annotation.NamiMapping;

public interface UserService5 {
    @NamiMapping("PUT user/getUser")
    UserModel xxx(Integer userId);
}
