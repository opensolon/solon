package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventBus;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQLManager 工具
 *
 * @author noear
 * @since 2020-09-01
 * */
class DbManager {
    private static DbManager _global = new DbManager();

    public static DbManager global() {
        return _global;
    }


    private final Map<String, SQLManager> cached = new ConcurrentHashMap<>();
    private ConditionalSQLManager dynamic;

    /**
     * 构建
     */
    private SQLManager build(BeanWrap bw) {
        DbConnectionSource cs = null;
        DataSource master = bw.raw();

        if (Utils.isNotEmpty(bw.attrs())) {
            String[] slaveAry = bw.attrs().split(",");
            DataSource[] slaves = new DataSource[slaveAry.length];

            for (int i = 0, len = slaveAry.length; i < len; i++) {
                slaves[i] = Aop.get(slaveAry[i]);

                if (slaves[i] == null) {
                    throw new RuntimeException("SQLManagerUtils: This data source does not exist: " + slaveAry[i]);
                }
            }

            cs = new DbConnectionSource(master, slaves);
        } else {
            cs = new DbConnectionSource(master, null);
        }

        SQLManagerBuilder builder = SQLManager.newBuilder(cs);

        //推到事件中心，用于扩展
        EventBus.push(builder);

        return builder.build();
    }

    /**
     * 获取动态管理器
     */
    public ConditionalSQLManager dynamicGet() {
        return dynamic;
    }

    public void dynamicBuild(BeanWrap def) {
        SQLManager master = get(def);
        if (master == null) {
            for (Map.Entry<String, SQLManager> kv : cached.entrySet()) {
                master = kv.getValue();
                break;
            }
        }

        if (master != null) {
            dynamic = new ConditionalSQLManager(master, cached);
        }
    }

    /**
     * 获取管理器
     */
    public SQLManager get(BeanWrap bw) {
        if (bw == null) {
            return null;
        }

        SQLManager tmp = cached.get(bw.name());
        if (tmp == null) {
            synchronized (bw.name().intern()) {
                tmp = cached.get(bw.name());
                if (tmp == null) {
                    tmp = build(bw);

                    cached.put(bw.name(), tmp);
                }
            }
        }

        return tmp;
    }

    /**
     * 注册管理器
     * */
    public void reg(BeanWrap bw) {
        get(bw);
    }
}
