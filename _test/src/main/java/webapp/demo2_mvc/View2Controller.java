package webapp.demo2_mvc;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@XMapping("/demo2/rpc/")
@XBean(remoting = true)
public class View2Controller {

    public Object json(){
        Map<String,Object> model = new HashMap<>();
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }
}
