package feature.controller.service;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.Result;

public class API_0 {
    @XMapping
    public Object call(){
        return Result.failure(2,"Interface does not exist");
    }
}
