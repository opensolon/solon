package demo;

import org.noear.solon.aspect.annotation.Service;

/**
 * @author noear 2023/2/23 created
 */
@Service
public class UserService2 {
    private String userName;
    public UserService2(){
        this.userName = "demo";
    }

    public String getUserName(){
        return userName;
    }

    protected void setUserName(String name) throws RuntimeException{
        userName = name;
    }
}
