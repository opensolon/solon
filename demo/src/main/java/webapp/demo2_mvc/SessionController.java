package webapp.demo2_mvc;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;

@XMapping("/demo2/session")
@XController
public class SessionController {
    @XMapping("id")
    public String id(String val){
       return XContext.current().sessionId();
    }

    @XMapping("set")
    public void set(String val){
        XContext.current().sessionSet("val", val);
    }

    @XMapping("get")
    public Object get(){
        return XContext.current().session("val");
    }
}
