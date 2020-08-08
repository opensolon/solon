package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;
import org.noear.solon.core.XScaner;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        Aop.factory().beanCreatorAdd(MapperScan.class, (clz0, wrap, anno) -> {
            String dir = anno.basePackages().replace('.', '/');
            String sessionFactoryRef = anno.sqlSessionFactoryRef();

            Aop.getAsyn(sessionFactoryRef, (bw -> {
                if (bw.raw() instanceof SqlSessionFactory) {
                    scanMapper(dir, (SqlSessionFactory) bw.raw());
                }
            }));
        });

//        try {
//            //通过配置文件获取数据库连接信息
//            Reader reader = Resources.getResourceAsReader("mybatis.xml");
//
//            //通过配置信息构建一个SqlSessionFactory
//            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
//
//            //通过SqlSessionFactory打开一个数据库会话
//            SqlSession sqlsession = sqlSessionFactory.openSession();
//
//            Aop.put(SqlSessionFactory.class, sqlSessionFactory);
//            Aop.put("sqlsession", sqlsession);
//        }catch (Exception ex){
//            throw new RuntimeException(ex);
//        }
    }

    private void scanMapper(String dir, SqlSessionFactory factory) {
        XScaner.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return XUtil.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    if (clz != null && clz.isInterface()) {
                        Object mapper = factory.openSession().getMapper(clz);
                        Aop.put(clz, mapper);
                    }
                });
    }
}
