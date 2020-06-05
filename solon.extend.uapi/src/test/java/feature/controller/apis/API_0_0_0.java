package feature.controller.apis;

import org.noear.solon.core.Result;
import org.noear.solon.extend.uapi.UApi;

public class API_0_0_0 extends UApi {
    @Override
    public String name() {
        return null;
    }

    Object call(){
        return new Result<>(0, "接口不存在");
    }
}
