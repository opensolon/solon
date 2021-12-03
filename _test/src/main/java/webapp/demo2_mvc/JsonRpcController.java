package webapp.demo2_mvc;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Mapping("/demo2/rpc/")
@Remoting
public class JsonRpcController {

    public Object json(Context ctx) {
//        ctx.headerMap().put("serialization","@avro");

        Map<String, Object> model = new HashMap<>();
        model.put("title", "dock");
        model.put("msg", "你好 world! in XController");

        model.put("bool", true);
        model.put("int", 12);
        model.put("long", 12l);
        model.put("double", 12.12d);
        model.put("date", new Date());
        model.put("local_date", LocalDate.now());

        return model;
    }

    public String header(Context ctx) {
        return ctx.header("name");
    }
}
