package org.noear.solon.extend.mybatis.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.extend.mybatis.MybatisAdapter;

/**
 * Mybatis Mapper Scanner
 *
 * @author noear
 * @since 1.6
 */
class MybatisMapperScanner {
    /**
     * 扫描 Mapper
     */
    protected static void mapperScan(BeanWrap dsBw, MybatisAdapter adapter) {
        for (String val : adapter.getMappers()) {
            mapperScan0(dsBw, adapter, val);
        }
    }

    private static void mapperScan0(BeanWrap dsBw, MybatisAdapter adapter, String val) {
        if (val.endsWith(".xml")) {
            //跳过
        } else if (val.endsWith(".class")) {
            Class<?> clz = Utils.loadClass(val.substring(0, val.length() - 6));
            mapperBindDo(dsBw, adapter, clz);
        } else {
            String dir = val.replace('.', '/');
            mapperScanDo(dsBw, adapter, dir);
        }
    }

    private static void mapperScanDo(BeanWrap dsBw, MybatisAdapter adapter, String dir) {
        ScanUtil.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return Utils.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    mapperBindDo(dsBw, adapter, clz);
                });
    }

    private static void mapperBindDo(BeanWrap dsBw, MybatisAdapter adapter, Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = adapter.getMapperProxy(clz);

            dsBw.context().putWrap(clz, Aop.wrap(clz, mapper));
        }
    }
}