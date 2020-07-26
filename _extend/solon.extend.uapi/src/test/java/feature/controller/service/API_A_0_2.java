package feature.controller.service;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UapiCode;

@XBean(tag = "api")
public class API_A_0_2 {
    @XMapping("A.0.2")
    public Object getOrder(){
        throw new UapiCode(12,"There are no appropriate parameters");
    }
}
