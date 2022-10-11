package org.noear.solon.extend.activerecord.impl;

import com.jfinal.plugin.activerecord.Model;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.extend.activerecord.annotation.Db;
import org.noear.solon.extend.activerecord.proxy.MapperInvocationHandler;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

/**
 * @author noear
 * @since 1.10
 */
public class DbBeanInjector implements BeanInjector<Db> {
    @Override
    public void doInject(VarHolder varH, Db anno) {
        if (Utils.isEmpty(anno.value())) {
            varH.context().getWrapAsyn(DataSource.class, (dsBw) -> {
                this.injectDo(varH, anno.value());
            });
        } else {
            varH.context().getWrapAsyn(anno.value(), (dsBw) -> {
                this.injectDo(varH, anno.value());
            });
        }
    }

    /**
     * 字段注入
     */
    private void injectDo(VarHolder varH, String annoValue) {
        MapperInvocationHandler handler = new MapperInvocationHandler(varH.getType(), annoValue);

        Object obj = Proxy.newProxyInstance(varH.context().getClassLoader(),
                new Class[]{varH.getType()},
                handler);

        varH.setValue(obj);
    }
}
