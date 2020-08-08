package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        Aop.factory().beanCreatorAdd(MapperScan.class, (clz0, wrap, anno) -> {
            String dir = anno.basePackages().replace('.', '/');
            String sessionFactoryRef = anno.sqlSessionFactoryRef();

            Aop.getAsyn(sessionFactoryRef, (bw -> {
                if (bw.raw() instanceof SqlSessionFactory) {
                    try {
                        scanMapper(dir, MybatisProxy.get(bw.raw()));
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }));
        });

        Aop.factory().beanInjectorAdd(Df.class, (varH, anno) -> {
            Aop.getAsyn(anno.value(), (bw) -> {
                if (bw.raw() instanceof SqlSessionFactory) {
                    SqlSessionFactory factory = bw.raw();

                    if (varH.getType().isInterface()) {
                        Object mapper = MybatisProxy.get(factory).getMapper(varH.getType());

                        varH.setValue(mapper);
                        return;
                    }

                    if (SqlSession.class.isAssignableFrom(varH.getType())) {
                        varH.setValue(MybatisProxy.get(factory));
                        return;
                    }

                    if (SqlSessionFactory.class.isAssignableFrom(varH.getType())) {
                        varH.setValue(factory);
                        return;
                    }

                }
            });
        });
    }

    private static void scanMapper(String dir, MybatisProxy proxy) {
        XScaner.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return XUtil.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    if (clz != null && clz.isInterface()) {
                        Object mapper = proxy.getMapper(clz);

                        Aop.put(clz, mapper);
                    }
                });
    }
}
