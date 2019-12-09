package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.ModelAndView;
import org.noear.weed.DbContext;
import webapp.dso.DbConfig;
import webapp.model.AppxModel;


@XMapping("/java")
@XSingleton(true)
@XController
public class JavaController {
    DbContext db2 = DbConfig.db2;

    @XMapping("demo0/html")
    public ModelAndView demo0() throws Exception {
        ModelAndView mv = new ModelAndView("view.ftl");

        Object _map = demo3();
        mv.put("map", _map);

        return mv;
    }

    @XMapping("demo1/json")
    public Object demo1() throws Exception {
        //
        // select app_id from appx limit 1
        //
        return db2.table("appx")
                .limit(1)
                .select("app_id")
                .getValue();
    }

    @XMapping("demo2/json")
    public Object demo2() throws Exception {
        //
        // select * from appx where app_id = @{app_id} limit 1
        //
        int app_id = 48;

        return db2.table("appx")
                .whereEq("app_id", app_id)
                .limit(1)
                .select("*")
                .caching(DbConfig.cache)
                .cacheTag("app_" + app_id)
                .getItem(AppxModel.class);
    }

    @XMapping("demo3/json")
    public Object demo3() throws Exception {
        //
        // select * from ${tb} where app_id = @{app_id} limit 1
        //
        int app_id = 48;
        String tb = "appx";

        Object tmp = db2.table(tb)
                .whereEq("app_id", app_id)
                .limit(1)
                .select("*")
                .getMap();

        DbConfig.cache.clear("test");

        return tmp;
    }

    @XMapping("demo4/json")
    public Object demo4() throws Exception {
        int app_id = 48;

        //
        // select * from appx where app_id>@{app_id} order by app_id asc limit 4
        //
        return db2.table("appx")
                .where("app_id>?",48)
                .orderBy("app_id ASC")
                .limit(4)
                .select("*")
                .getList(AppxModel.class);
    }

    @XMapping("demo5/json")
    public Object demo5() throws Exception {
        //
        // select app_id from appx limit 4
        //
        return db2.table("appx")
                .limit(4)
                .select("app_id")
                .getArray(0);
    }
}
