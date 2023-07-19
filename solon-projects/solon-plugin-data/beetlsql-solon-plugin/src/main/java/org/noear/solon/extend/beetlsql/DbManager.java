package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.noear.solon.Solon;
import org.noear.solon.core.BeanWrap;

/**
 * SQLManager 工具
 *
 * @author noear
 * @since 2020-09-01
 * @deprecated 2.4
 * */
@Deprecated
public class DbManager {
    /**
     * 获取动态管理器
     */
    public static ConditionalSQLManager dynamicGet() {
        return org.beetl.sql.solon.DbManager.dynamicGet();
    }

    public static void dynamicBuild(BeanWrap def) {
        org.beetl.sql.solon.DbManager.dynamicBuild(def);
    }

    public static SQLManager get(String dsName) {
        BeanWrap dsWrap = Solon.context().getWrap(dsName);
        return get(dsWrap);
    }

    /**
     * 获取管理器
     */
    public static SQLManager get(BeanWrap dsWrap) {
        return org.beetl.sql.solon.DbManager.get(dsWrap);
    }

    /**
     * 注册管理器
     */
    public static void reg(BeanWrap bw) {
        get(bw);
    }
}
