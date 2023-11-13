package org.hibernate.solon.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingkang
 * @since 2.5
 */
public class HibernateAdapterManager {
    /**
     * 缓存适配器
     */
    private static final Map<String, HibernateAdapter> dbMap = new ConcurrentHashMap<>();

    public static HibernateAdapter getOnly(String name) {
        return dbMap.get(name);
    }

    public static Map<String, HibernateAdapter> getAll() {
        return Collections.unmodifiableMap(dbMap);
    }

    /**
     * 获取适配器
     */
    public static HibernateAdapter get(BeanWrap bw) {
        HibernateAdapter db = dbMap.get(bw.name());

        if (db == null) {
            synchronized (dbMap) {
                db = dbMap.get(bw.name());
                if (db == null) {
                    db = buildAdapter(bw);

                    dbMap.put(bw.name(), db);

                    if (bw.typed()) {
                        dbMap.put("", db);
                    }
                }
            }

        }

        return db;
    }

    /**
     * 注册数据源，并生成适配器
     *
     * @param bw 数据源的BW
     */
    public static void register(BeanWrap bw) {
        get(bw);
    }

    /**
     * 构建适配器
     */
    private static HibernateAdapter buildAdapter(BeanWrap bw) {
        HibernateAdapter adapter;

        if (Utils.isEmpty(bw.name())) {
            adapter = new HibernateAdapter(bw);
        } else {
            adapter = new HibernateAdapter(bw, Solon.cfg().getProp("jpa." + bw.name()));
        }

        return adapter;
    }
}
