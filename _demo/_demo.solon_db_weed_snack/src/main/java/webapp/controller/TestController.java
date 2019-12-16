package webapp.controller;

import org.noear.solon.annotation.*;
import org.noear.weed.DbContext;
import webapp.dso.service.ServiceTest;
import webapp.dso.service.ServiceTest2;

import java.util.HashMap;
import java.util.Map;

@XMapping("/test")
@XSingleton(true)
@XController
public class TestController {
    @XInject("db1")
    DbContext db2;

    @XInject
    ServiceTest serviceTest;

    @XInject
    ServiceTest2 serviceTest2;

    @XMapping("demo0")
    public Object test0(){
        Map<String,Object> map = new HashMap<>();
        map.put("serviceTest",serviceTest.test());
        map.put("serviceTest2",serviceTest2.test());

        return map;
    }

    @XMapping("demo1")
    public Object test(String sql) throws Exception {

        //
        // mysql 8.0 才支持
        //
        Object tmp = db2.table("ag").innerJoin("ax").on("ag.agroup_id = ax.agroup_id")
                .limit(10)
                .with("ax", db2.table("appx").selectQ("*"))
                .with("ag", db2.table("appx_agroup").where("agroup_id < 10").selectQ("*"))
                .with("ah", "select * from appx_agroup where agroup_id<?",10)
                .select("ax.*")
                .getMapList();

        if (sql == null) {
            return tmp;
        } else {
            return db2.lastCommand.text;
        }
    }

    @XMapping("demo2")
    public Object test2(String sql) throws Exception {
        //
        // mysql 8.0 才支持
        //
        Object tmp = db2.table("ax")
                .orderByDesc("app_id")
                .limit(2)
                .with("ax", db2.table("appx").selectQ("*"))
                .select("ax.*")
                .getMapList();

        if (sql == null) {
            return tmp;
        } else {
            return db2.lastCommand.text;
        }
    }
}
