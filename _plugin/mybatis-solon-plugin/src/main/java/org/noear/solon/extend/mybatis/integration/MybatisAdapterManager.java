package org.noear.solon.extend.mybatis.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.extend.mybatis.MybatisAdapter;
import org.noear.solon.extend.mybatis.MybatisAdapterFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器
 *
 * @author noear
 * @since 1.1
 * */
public class MybatisAdapterManager {
    private static MybatisAdapterManager _global = new MybatisAdapterManager();

    /**
     * 全局对象
     */
    public static MybatisAdapterManager global() {
        return _global;
    }

    private MybatisAdapterFactory adapterFactory = new MybatisAdapterFactoryDefault();

    /**
     * 设置适配器工厂
     * */
    public void setAdapterFactory(MybatisAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    /**
     * 缓存会话代理
     */
    private Map<String, MybatisAdapter> dbMap = new ConcurrentHashMap<>();

    /**
     * 获取会话代理
     */
    public MybatisAdapter get(BeanWrap bw) {
        MybatisAdapter db = dbMap.get(bw.name());

        if (db == null) {
            synchronized (bw.name().intern()) {
                db = dbMap.get(bw.name());
                if (db == null) {
                    db = buildAdapter(bw);

                    dbMap.putIfAbsent(bw.name(), db);

                    if (bw.typed()) {
                        dbMap.putIfAbsent("", db);
                    }
                }
            }

        }

        return db;
    }

    /**
     * 注册数据源，并生成会话代理
     *
     * @param bw 数据源的BW
     */
    public void reg(BeanWrap bw) {
        get(bw);
    }

    /**
     * 构建会话代理
     */
    private MybatisAdapter buildAdapter(BeanWrap bw) {
        MybatisAdapter adapter;

        if (Utils.isEmpty(bw.name())) {
            adapter = adapterFactory.create(bw);
        } else {
            adapter = adapterFactory.create(bw, Solon.cfg().getProp("mybatis." + bw.name()));
        }

        adapter.mapperScan();

        return adapter;
    }
}
