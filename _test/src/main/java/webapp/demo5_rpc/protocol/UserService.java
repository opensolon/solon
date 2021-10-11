package webapp.demo5_rpc.protocol;

public interface UserService {
    UserModel getUser(Integer userId);
    UserModel addUser(UserModel user);
}
