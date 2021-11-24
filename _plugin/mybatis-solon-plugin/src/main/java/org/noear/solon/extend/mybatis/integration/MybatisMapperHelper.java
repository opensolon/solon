package org.noear.solon.extend.mybatis.integration;

import org.apache.ibatis.session.SqlSession;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.extend.mybatis.MybatisAdapter;

/**
 * Mybatis Mapper Scan Helper
 *
 * @author noear
 * @since 1.6
 */
class MybatisMapperHelper {
    /**
     * 扫描 Mapper
     * */
    protected static void mapperScan(MybatisAdapter adapter) {
        SqlSession session = adapter.getSession();

        for (String val : adapter.getMapperList()) {
            mapperScan0(session, val);
        }
    }

    private static void mapperScan0(SqlSession session, String val) {
        if (val.endsWith(".xml")) {

        } else if (val.endsWith(".class")) {
            Class<?> clz = Utils.loadClass(val.substring(0, val.length() - 6));
            mapperBindDo(session, clz);
        } else {
            String dir = val.replace('.', '/');
            mapperScanDo(session, dir);
        }
    }

    private static void mapperScanDo(SqlSession session, String dir) {
        ScanUtil.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return Utils.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    mapperBindDo(session, clz);
                });
    }

    private static void mapperBindDo(SqlSession session, Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = session.getMapper(clz);

            Aop.context().putWrap(clz, Aop.wrap(clz, mapper));
        }
    }
}
