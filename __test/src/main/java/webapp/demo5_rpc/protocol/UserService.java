package webapp.demo5_rpc.protocol;

public interface UserService {
    UserModel getUser(Integer userId);

    UserModel addUser(UserModel user);

    void showError();

    void showVoid();

    default UserModel getUserDef(Integer userId) {
        return getUser(userId);
    }
}
