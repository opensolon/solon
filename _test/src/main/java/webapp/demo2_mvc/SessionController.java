package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Mapping(path = "/demo2/session")
@Controller
public class SessionController {
    @Mapping("id")
    public String id(String val){
       return Context.current().sessionId();
    }

    @Mapping("set")
    public void set(String val){
        Context.current().sessionSet("val", val);
    }

    @Mapping("get")
    public Object get(){
        return Context.current().session("val");
    }
}
