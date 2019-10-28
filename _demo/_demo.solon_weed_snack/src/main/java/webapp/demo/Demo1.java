package webapp.demo;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMap;
import org.noear.weed.DbContext;

@XController
public class Demo1 {
    private DbContext _db;
    public DbContext db(){
        if(_db == null) {
            XMap cfg = Aop.prop().getXmap("test.db");

            _db = new DbContext(cfg.get("name"),
                    cfg.get("url"),
                    cfg.get("username"),
                    cfg.get("password"))
                    .driverSet(cfg.get("driverClassName"));
        }

        return _db;
    }

    @XMapping("/demo1/test")
    public Object test() throws Exception{
        return db().table("appx").limit(1).select("*").getMap();
    }
}
