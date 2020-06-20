package feature.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;

@XMapping("/fun/")
@XController
public class FUN_F_0_1 {
    @XMapping("f.0.1")
    public Object call(String mobile){
        return mobile;
    }
}
