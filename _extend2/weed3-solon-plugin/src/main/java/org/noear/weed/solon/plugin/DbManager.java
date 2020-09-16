package org.noear.weed.solon.plugin;

import org.noear.solon.core.BeanWrap;
import org.noear.weed.DbContext;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

 class DbManager {
    private static DbManager _global = new DbManager();

    public static DbManager global() {
        return _global;
    }


    private Map<String, DbContext> dbMap = new ConcurrentHashMap<>();

    public DbContext get(BeanWrap bw) {
        DbContext db = dbMap.get(bw.name());

        if (db == null) {
            synchronized (bw.name().intern()) {
                db = dbMap.get(bw.name());
                if (db == null) {
                    DataSource ds = bw.raw();
                    db = new DbContext(ds);

                    dbMap.putIfAbsent(bw.name(), db);

                    if(bw.typed()){
                        dbMap.putIfAbsent("", db);
                    }
                }
            }

        }

        return db;
    }

    public void reg(BeanWrap bw){
        get(bw);
    }
}
