package feature.controller.service;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XMapping;

import java.util.HashMap;
import java.util.Map;

@XBean(tag = "api")
public class API_A_0_4 {
    @XMapping("A.0.4")
    public Object getUser(){
        Map<String,Object>  map = new HashMap<>();

        map.put("user_id",12);
        map.put("name","noear");

        return map;
    }
}
