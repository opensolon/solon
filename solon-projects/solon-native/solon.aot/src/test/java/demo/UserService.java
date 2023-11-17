package demo;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2023/2/23 created
 */
@Component
public class UserService extends UserServiceBase<String>{
    private String userName;
    public UserService(){
        this.userName = "demo";
    }

    @Override
    public String getUserName(){
        return userName;
    }

    protected void setUserName(String name) throws RuntimeException{
        userName = name;
    }
}
