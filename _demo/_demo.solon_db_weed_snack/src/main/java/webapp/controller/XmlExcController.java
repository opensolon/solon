package webapp.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.handler.ModelAndView;
import org.noear.weed.DbContext;
import webapp.dso.DbConfig;
import webapp.model.AppxModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapping("/xmlexc")
@Singleton(true)
@Controller
public class XmlExcController {
    DbContext db2 = DbConfig.db2;


    @Mapping("demo0/html")
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


    @Mapping("demo1/json")
    public Object demo1() throws Exception {
        Map<String, Object> map = new HashMap<>();

        Object tmp = db2.mapper("@webapp.dso.appx_get", map);
        return tmp;
    }

    @Mapping("demo2/json")
    public Object demo2() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", 48);
        AppxModel tmp = db2.mapper("@webapp.dso.appx_get2", map);
        return tmp;
    }

    @Mapping("demo3/json")
    public Object demo3() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", 48);
        map.put("tb","appx");
        Map tmp = db2.mapper("@webapp.dso.appx_get3", map);
        return tmp;
    }

    @Mapping("demo4/json")
    public Object demo4() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("app_id", 1);
        List<AppxModel> tmp = db2.mapper("@webapp.dso.appx_getlist", map);
        return tmp;
    }

    @Mapping("demo5/json")
    public Object demo5() throws Exception {
        List<Integer> tmp = db2.mapper("@webapp.dso.appx_getids", null);
        return tmp;
    }

}
