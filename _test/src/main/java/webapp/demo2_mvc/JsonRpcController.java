package webapp.demo2_mvc;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XService;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@XMapping("/demo2/rpc/")
@XService(remoting = true)
public class JsonRpcController {

    public Object json(XContext ctx){
//        ctx.headerMap().put("serialization","@avro");

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
