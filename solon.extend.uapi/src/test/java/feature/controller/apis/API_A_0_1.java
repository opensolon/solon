package feature.controller.apis;

import org.noear.solon.annotation.XNote;
import org.noear.solon.core.Result;
import org.noear.solon.extend.uapi.UApi;

import java.util.HashMap;
import java.util.Map;

//@XController              //如果想直接被运行，加这两个注解
//@XMapping("/api/a.0.1")
public class API_A_0_1 extends UApi {
    @Override
    public String name() {
        return "A.0.1";
    }

    public Object call(Integer cid, Integer user) {
        Map<String, Object> data = new HashMap<>();

        data.put("user_id", 12);

        data.put("nickname", "noear");

        return new Result(1, "ok", data);
    }
}
