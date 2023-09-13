package org.hibernate.solon.integration;

import org.hibernate.solon.Dao;
import org.hibernate.solon.annotation.Db;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.GenericUtil;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

public class DbBeanBuilder implements BeanBuilder<Db> {
    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Db anno) throws Throwable {
        if (clz.isInterface() == false) {
            throw new IllegalArgumentException("This is not an interface; " + clz.getName());
        }

        if (Utils.isEmpty(anno.value())) {
            bw.context().getWrapAsync(DataSource.class, (dsBw) -> {
                create0(clz, dsBw, bw);
            });
        } else {
            bw.context().getWrapAsync(anno.value(), (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    create0(clz, dsBw, bw);
                }
            });
        }
    }

    private void create0(Class<?> clz, BeanWrap dsBw, BeanWrap daoBw) {
        HibernateAdapter adapter = HibernateAdapterManager.get(dsBw);

        Class<?>[] tArgs = GenericUtil.resolveTypeArguments(clz, Dao.class);
        Class<?> entityClass = tArgs[0];

        Object proxyInstance = Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{clz}, new DaoInvocationHandler(adapter.getSessionFactory(), entityClass, clz)
        );

        dsBw.context().wrapAndPut(clz, proxyInstance);
    }
}
