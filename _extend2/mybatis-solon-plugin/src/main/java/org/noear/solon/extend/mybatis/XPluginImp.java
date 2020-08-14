package org.noear.solon.extend.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {

        Aop.factory().beanCreatorAdd(Df.class, (clz,  wrap,  anno)->{
            if(XUtil.isEmpty(anno.value()) || clz.isInterface() == false){
                return;
            }

            Aop.getAsyn(anno.value(),(bw)->{
                if (bw.raw() instanceof SqlSessionFactory) {
                    SqlSessionFactory factory = bw.raw();

                    Object raw = MybatisUtil.get(factory).getMapper(clz);
                    Aop.wrapAndPut(clz,raw);
                }
            });
        });

        Aop.factory().beanInjectorAdd(Df.class, (varH, anno) -> {

            if (XUtil.isEmpty(anno.value())) {
                if (varH.getType().isInterface()) {
                    Aop.getAsyn(varH.getType(), (bw) -> {
                        varH.setValue(bw.raw());
                    });
                }
            } else {
                Aop.getAsyn(anno.value(), (bw) -> {
                    if (bw.raw() instanceof SqlSessionFactory) {
                        SqlSessionFactory factory = bw.raw();

                        if (varH.getType().isInterface()) {
                            Object mapper = MybatisUtil.get(factory).getMapper(varH.getType());

                            varH.setValue(mapper);
                            return;
                        }

                        if (SqlSession.class.isAssignableFrom(varH.getType())) {
                            varH.setValue(MybatisUtil.get(factory));
                            return;
                        }

                        if (SqlSessionFactory.class.isAssignableFrom(varH.getType())) {
                            varH.setValue(factory);
                            return;
                        }

                    }
                });
            }
        });
    }
}
