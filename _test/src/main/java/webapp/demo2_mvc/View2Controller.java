package webapp.demo2_mvc;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@XMapping("/demo2/rpc/")
@XBean(remoting = true)
public class View2Controller {

    public Object json(){
        Map<String,Object> model = new HashMap<>();
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        model.put("bool",true);
        model.put("int",12);
        model.put("long",12l);
        model.put("double",12.12d);
        model.put("date",new Date());
        model.put("local_date", LocalDate.now());

        return model;
    }
}
