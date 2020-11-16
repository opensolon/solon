package webapp.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.handler.ModelAndView;
import org.noear.weed.DbContext;
import webapp.dso.DbConfig;
import webapp.dso.SqlAnnotation;


@Mapping("/annot")
@Singleton(true)
@Controller
public class AnnotController {
    DbContext db2 = DbConfig.db2;

    @Mapping("demo0/html")
    public ModelAndView demo0() throws Exception {
        ModelAndView mv = new ModelAndView("view.ftl");

        Object _map = demo3();
        mv.put("map", _map);

        return mv;
    }

    @Mapping("demo1/json")
    public Object demo1() throws Exception {
        return db2.mapper(SqlAnnotation.class).appx_get();
    }

    @Mapping("demo2/json")
    public Object demo2() throws Exception {
        return db2.mapper(SqlAnnotation.class).appx_get2(48);
    }

    @Mapping("demo3/json")
    public Object demo3() throws Exception {
        return db2.mapper(SqlAnnotation.class).appx_get3("appx",48);
    }

    @Mapping("demo4/json")
    public Object demo4() throws Exception {
        return db2.mapper(SqlAnnotation.class).appx_getlist(1);
    }

    @Mapping("demo5/json")
    public Object demo5() throws Exception {
        return db2.mapper(SqlAnnotation.class).appx_getids();
    }

}
