package webapp.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.weed.DbContext;
import webapp.dso.DbConfig;
import webapp.dso.SqlMapper;

@Mapping("/xml")
@Singleton(true)
@Controller
public class XmlController {
    DbContext db2 = DbConfig.db2;


    @Mapping("demo0/html")
    public ModelAndView demo0() throws Exception {
        ModelAndView mv = new ModelAndView("view.ftl");

        //
        // 直接通过 call @{namespace}.{id} 调用
        //
        Object _map = db2.call("@webapp.dso.appx_get3")
                         .set("app_id", 48)
                         .set("tb","appx")
                         .getMap();
        mv.put("map", _map);

        return mv;
    }


    @Mapping("demo1/json")
    public Object demo1() throws Exception {
        return db2.mapper(SqlMapper.class).appx_get();
    }

    @Mapping("demo2/json")
    public Object demo2() throws Exception {
        return db2.mapper(SqlMapper.class).appx_get2(48);
    }

    @Mapping("demo3/json")
    public Object demo3() throws Exception {
        return db2.mapper(SqlMapper.class).appx_get3("appx",48);
    }

    @Mapping("demo4/json")
    public Object demo4() throws Exception {
        return db2.mapper(SqlMapper.class).appx_getlist(1);
    }

    @Mapping("demo5/json")
    public Object demo5() throws Exception {
        return db2.mapper(SqlMapper.class).appx_getids();
    }

}
