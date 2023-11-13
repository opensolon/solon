package org.noear.wood.solon.integration;

import org.noear.solon.core.BeanWrap;
import org.noear.wood.DbContext;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.10
 */
class DbManager {
    private static DbManager _global = new DbManager();

    public static DbManager global() {
        return _global;
    }


    private final Map<String, DbContext> dbMap = new HashMap<>();

    public DbContext get(BeanWrap bw) {
        DbContext db = dbMap.get(bw.name());

        if (db == null) {
            synchronized (dbMap) {
                db = dbMap.get(bw.name());
                if (db == null) {
                    DataSource ds = bw.raw();
                    db = new DbContext(ds).nameSet(bw.name());

                    dbMap.put(bw.name(), db);

                    if (bw.typed()) {
                        dbMap.put("", db);
                    }

                    //初始化元信息（可起到热链接的作用）
                    db.initMetaData();
                }
            }

        }

        return db;
    }

    public void reg(BeanWrap bw) {
        get(bw);
    }
}
