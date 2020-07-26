package feature.controller.service;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XMapping;

@XBean(tag = "api")
public class API_A_0_3 {
    @XMapping("A.0.3")
    public Object addOrder(){
        throw new RuntimeException("没饭了");
    }
}
