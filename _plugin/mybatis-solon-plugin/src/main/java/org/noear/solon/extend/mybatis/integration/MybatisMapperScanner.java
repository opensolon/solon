package org.noear.solon.extend.mybatis.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
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
    protected static void mapperScan(MybatisAdapter adapter) {
        for (String val : adapter.getMappers()) {
            mapperScan0(adapter, val);
        }
    }

    private static void mapperScan0(MybatisAdapter adapter, String val) {
        if (val.endsWith(".xml")) {
            //跳过
        } else if (val.endsWith(".class")) {
            Class<?> clz = Utils.loadClass(val.substring(0, val.length() - 6));
            mapperBindDo(adapter, clz);
        } else {
            String dir = val.replace('.', '/');
            mapperScanDo(adapter, dir);
        }
    }

    private static void mapperScanDo(MybatisAdapter adapter, String dir) {
        ScanUtil.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return Utils.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    mapperBindDo(adapter, clz);
                });
    }

    private static void mapperBindDo(MybatisAdapter adapter, Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = adapter.getMapperProxy(clz);

            Aop.context().putWrap(clz, Aop.wrap(clz, mapper));
        }
    }
}