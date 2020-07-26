package feature.controller.service;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XMapping;

@XBean(tag = "api")
public class API_A_0_1 {
    @XMapping("A.0.1")
    public Object getName(String name){
        return  name;
    }
}
