package webapp.demo5_rpc;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2023/6/13 created
 */
@Component
public class ApiDemo {
    @Mapping("hello/{name}")
    public String hello(String name){
        return name;
    }
}
