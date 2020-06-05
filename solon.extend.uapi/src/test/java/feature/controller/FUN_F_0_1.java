package feature.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UApi;

@XMapping("/fun/f.0.1")
@XController
public class FUN_F_0_1 extends UApi {
    public Object call(String mobile){
        return mobile;
    }
}
