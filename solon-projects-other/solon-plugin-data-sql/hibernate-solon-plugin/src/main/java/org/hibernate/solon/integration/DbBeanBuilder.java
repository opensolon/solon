package org.hibernate.solon.integration;

import org.hibernate.solon.jpa.RepositoryProxy;
import org.hibernate.solon.annotation.Db;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

import javax.sql.DataSource;

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

        Object proxy = RepositoryProxy.newProxyInstance(adapter.getSessionFactory(), clz);

        daoBw.rawSet(proxy);
    }
}
