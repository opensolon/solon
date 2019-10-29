package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;

import org.noear.solon.core.ModelAndView;
import webapp.Config;
import webapp.dso.*;
import webapp.model.AppxModel;

@XSingleton(true)
@XController
public class DemoController {
    @XMapping("/demo1/json")
    public Object demo1() throws Exception{
        return Config.db1.table("appx").limit(1).select("*").getMap();
    }

    @XMapping("/demo2/json")
    public Object demo2() throws Exception{
        return Config.db2.table("appx").limit(1).select("*").getMap();
    }

    @XMapping("/demo3/html")
    public ModelAndView demo3() throws Exception{
        ModelAndView mv = new ModelAndView("view.ftl");

        mv.put("map",Config.db2.call("@webapp.dso.appx_get").getMap());

        return mv;
    }

    @XMapping("/demo4/html")
    public Object demo4() throws Exception{
        SqlMapper tmp = Config.db2.mapper(SqlMapper.class);

        ModelAndView mv = new ModelAndView("view.ftl");

        mv.put("map", tmp.appx_get());

        return mv;
    }

    @XMapping("/demo5/json")
    public Object demo5() throws Exception{
        SqlMapper2 tmp = Config.db2.mapper(SqlMapper2.class);

        return tmp.appx_get();
    }

    @XMapping("/demo6/json")
    public AppxModel demo6() throws Exception{
        SqlMapper2 tmp = Config.db2.mapper(SqlMapper2.class);

        return tmp.appx_get2(1);
    }

    @XMapping("/demo6_1/json")
    public Object demo6_1() throws Exception{
        return Config.db2.call("select * from appx where app_id = @app_id limit 1")
                .set("app_id",1)
                .getItem(AppxModel.class);
    }

    @XMapping("/demo7/json")
    public Object demo7() throws Exception{
        SqlMapper2 tmp = Config.db2.mapper(SqlMapper2.class);

        return tmp.appx_get3("appx",1);
    }

    @XMapping("/demo8/json")
    public Object demo8() throws Exception{
        SqlMapper2 tmp = Config.db2.mapper(SqlMapper2.class);

        return tmp.appx_getlist(1);
    }

}
