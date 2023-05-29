package demo;

/**
 * @author noear 2023/2/24 created
 */
public abstract class UserServiceBase<T> {
    public String getInfo(){
        return "T";
    }

    public abstract String getUserName();
}
