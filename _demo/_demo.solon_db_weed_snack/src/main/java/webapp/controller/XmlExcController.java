package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.ModelAndView;
import org.noear.weed.DbContext;
import webapp.dso.DbConfig;
import webapp.model.AppxModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XMapping("/xmlexc")
@XSingleton(true)
@XController
public class XmlExcController {

    DbContext db1 = DbConfig.db1;
    DbContext db2 = DbConfig.db2;


    @XMapping("demo0/html")
    public ModelAndView demo0() throws Exception {
        ModelAndView mv = new ModelAndView("view.ftl");

        //
        // 直接通过 call @{namespace}.{id} 调用
        //

        Map<String,Object> map = new HashMap<>();
        map.put("app_id", 48);
        map.put("tb","appx");

        Object tmp = db2.mapper("@webapp.dso.appx_get3", map);
        mv.put("map", tmp);

        return mv;
    }


    @XMapping("demo1/json")
    public Object demo1() throws Exception {
        Map<String, Object> map = new HashMap<>();

        Object tmp = db2.mapper("@webapp.dso.appx_get", map);
        return tmp;
    }

    @XMapping("demo2/json")
    public Object demo2() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", 48);
        AppxModel tmp = db2.mapper("@webapp.dso.appx_get2", map);
        return tmp;
    }

    @XMapping("demo3/json")
    public Object demo3() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", 48);
        map.put("tb","appx");
        Map tmp = db2.mapper("@webapp.dso.appx_get3", map);
        return tmp;
    }

    @XMapping("demo4/json")
    public Object demo4() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", 1);
        List<AppxModel> tmp = db2.mapper("@webapp.dso.appx_getlist", map);
        return tmp;
    }

    @XMapping("demo5/json")
    public Object demo5() throws Exception {
        List<Integer> tmp = db2.mapper("@webapp.dso.appx_getids", null);
        return tmp;
    }

}
