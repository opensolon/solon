package feature.controller.apis;

import feature.controller.ApiResult;
import org.noear.solon.annotation.XMapping;

public class API_0_0_0 {
    @XMapping()
    public Object call(){
        return new ApiResult(0, "接口不存在");
    }
}
