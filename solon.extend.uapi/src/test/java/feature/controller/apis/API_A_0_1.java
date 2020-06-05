package feature.controller.apis;

import feature.controller.ApiResult;
import org.noear.solon.annotation.XMapping;

import java.util.HashMap;
import java.util.Map;

//@XController              //如果想直接被运行，加这两个注解
//@XMapping("/api/")
public class API_A_0_1 {
    @XMapping("A.0.1")
    public Object call(Integer cid, Integer user) {
        Map<String, Object> data = new HashMap<>();

        data.put("user_id", 12);

        data.put("nickname", "noear");

        return new ApiResult(1, "ok", data);
    }
}
