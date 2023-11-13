package org.apache.ibatis.solon.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.MybatisAdapterFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 适配管理器
 *
 * @author noear
 * @since 1.1
 * */
public class MybatisAdapterManager {
    private static MybatisAdapterFactory adapterFactory = new MybatisAdapterFactoryDefault();

    /**
     * 设置适配器工厂
     */
    public static void setAdapterFactory(MybatisAdapterFactory adapterFactory) {
        MybatisAdapterManager.adapterFactory = adapterFactory;
    }

    /**
     * 缓存适配器
     */
    private static final Map<String, MybatisAdapter> dbMap = new ConcurrentHashMap<>();

    public static MybatisAdapter getOnly(String name){
        return dbMap.get(name);
    }

    public static Map<String, MybatisAdapter> getAll(){
        return Collections.unmodifiableMap(dbMap);
    }

    /**
     * 获取适配器
     */
    public static MybatisAdapter get(BeanWrap bw) {
        MybatisAdapter db = dbMap.get(bw.name());

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
    private static MybatisAdapter buildAdapter(BeanWrap bw) {
        MybatisAdapter adapter;

        if (Utils.isEmpty(bw.name())) {
            adapter = adapterFactory.create(bw);
        } else {
            adapter = adapterFactory.create(bw, bw.context().cfg().getProp("mybatis." + bw.name()));
        }

        mapperBinding(bw, adapter);

        return adapter;
    }

    private static void mapperBinding(BeanWrap dsBw, MybatisAdapter adapter) {
        for (Class<?> clz : adapter.getConfiguration().getMapperRegistry().getMappers()) {
            mapperBindingDo(dsBw, adapter, clz);
        }
    }

    private static void mapperBindingDo(BeanWrap dsBw, MybatisAdapter adapter, Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = adapter.getMapper(clz);

            //进入容器，用于 @Inject 注入
            dsBw.context().wrapAndPut(clz, mapper);
        }
    }
}
