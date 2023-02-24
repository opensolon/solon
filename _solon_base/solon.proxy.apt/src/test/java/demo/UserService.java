package demo;


import org.noear.solon.proxy.annotation.ProxyComponent;

/**
 * @author noear 2023/2/23 created
 */
@ProxyComponent
public class UserService {
    private String userName;
    public UserService(){
        this.userName = "demo";
    }

    public String getUserName(){
        return userName;
    }

    protected void setUserName(String name) throws RuntimeException{
        userName = name;
    }
}
