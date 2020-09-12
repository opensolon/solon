package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQLManager 工具
 *
 * @author noear
 * @since 2020-09-01
 * */
public class SQLManagerUtils {

    private final static Map<String, SQLManager> cached = new ConcurrentHashMap<>();
    private static ConditionalSQLManager dynamic;

    /**
     * 构建
     */
    private static SQLManager build(BeanWrap bw) {
        SQLConnectionSource cs = null;
        DataSource master = bw.raw();

        if (XUtil.isNotEmpty(bw.meta())) {
            String[] slaveAry = bw.meta().split(",");
            DataSource[] slaves = new DataSource[slaveAry.length];
            for (int i = 0, len = slaveAry.length; i < len; i++) {
                slaves[i] = Aop.get(slaveAry[i]);

                if (slaves[i] == null) {
                    throw new RuntimeException("SQLManagerHolder: This data source does not exist: " + slaveAry[i]);
                }
            }

            cs = new SQLConnectionSource(master, slaves);
        } else {
            cs = new SQLConnectionSource(master, null);
        }

        return SQLManager.newBuilder(cs).build();
    }

    /**
     * 获取动态管理器
     */
    public static ConditionalSQLManager dynamicGet() {
        return dynamic;
    }

    public static void dynamicBuild(BeanWrap def) {
        SQLManager master = get("", def);
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
    public static SQLManager get(String name, BeanWrap bw) {
        if (bw == null || name == null) {
            return null;
        }

        SQLManager tmp = cached.get(name);
        if (tmp == null) {
            synchronized (name.intern()) {
                tmp = cached.get(name);
                if (tmp == null) {
                    tmp = build(bw);

                    cached.put(name, tmp);
                }
            }
        }

        return tmp;
    }
}
