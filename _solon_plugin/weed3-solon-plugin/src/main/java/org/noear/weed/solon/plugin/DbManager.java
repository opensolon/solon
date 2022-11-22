package org.noear.weed.solon.plugin;

import org.noear.solon.core.BeanWrap;
import org.noear.weed.DbContext;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.3
 */
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
