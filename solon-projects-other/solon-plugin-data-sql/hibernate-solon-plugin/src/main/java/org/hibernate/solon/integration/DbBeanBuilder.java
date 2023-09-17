package org.hibernate.solon.integration;

import org.hibernate.solon.BaseMapperDefinition;
import org.hibernate.solon.annotation.Db;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.GenericUtil;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class DbBeanBuilder implements BeanBuilder<Db> {
    private Map<Class<?>, BaseMapperDefinition> mapperDefinitions = new HashMap<>();

    public void register(BaseMapperDefinition definition) {
        mapperDefinitions.put(definition.getMapperClz(), definition);
    }

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
        BaseMapperDefinition definition = null;
        for (BaseMapperDefinition md : mapperDefinitions.values()) {
            if (md.getMapperClz().isAssignableFrom(clz)) {
                definition = md;
                break;
            }
        }

        if (definition == null) {
            return;
        }

        HibernateAdapter adapter = HibernateAdapterManager.get(dsBw);

        Class<?>[] tArgs = GenericUtil.resolveTypeArguments(clz, definition.getMapperClz());
        Class<?> entityClass = tArgs[0];
        InvocationHandler invocationHandler = definition.getInvocationHandler(adapter.sessionFactory, entityClass);

        Object proxyInstance = Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{clz}, invocationHandler);

        dsBw.context().wrapAndPut(clz, proxyInstance);
    }
}
