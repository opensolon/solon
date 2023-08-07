package org.noear.solon.extend.activerecord.impl;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.DbPro;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.extend.activerecord.ArpManager;
import org.noear.solon.extend.activerecord.annotation.Db;
import org.noear.solon.extend.activerecord.proxy.MapperInvocationHandler;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

/**
 * @author noear
 * @since 1.10
 */
public class DbBeanInjectorImpl implements BeanInjector<Db> {
    @Override
    public void doInject(VarHolder varH, Db anno) {
        if (Utils.isEmpty(anno.value())) {
            varH.context().getWrapAsync(DataSource.class, (dsBw) -> {
                this.injectDo(varH, anno.value(), dsBw.raw());
            });
        } else {
            varH.context().getWrapAsync(anno.value(), (dsBw) -> {
                this.injectDo(varH, anno.value(), dsBw.raw());
            });
        }
    }

    /**
     * 字段注入
     */
    private void injectDo(VarHolder varH, String name, DataSource ds) {
        //如果是 DbPro
        if (DbPro.class.isAssignableFrom(varH.getType())) {
            if (Utils.isEmpty(name)) {
                name = DbKit.MAIN_CONFIG_NAME;
            }

            Config config = DbKit.getConfig(name);
            if (config != null) {
                varH.setValue(com.jfinal.plugin.activerecord.Db.use(name));
            } else {
                String name2 = name;
                ArpManager.addStartEvent(() -> {
                    varH.setValue(com.jfinal.plugin.activerecord.Db.use(name2));
                });
            }
            return;
        }

        //如果是 ActiveRecordPlugin
        if (ActiveRecordPlugin.class.isAssignableFrom(varH.getType())) {
            ActiveRecordPlugin arp = ArpManager.getOrAdd(name, ds);
            varH.setValue(arp);
            return;
        }

        //如果是 interface，则为 Mapper 代理
        if (varH.getType().isInterface()) {
            MapperInvocationHandler handler = new MapperInvocationHandler(varH.getType(), name);

            Object obj = Proxy.newProxyInstance(varH.context().getClassLoader(),
                    new Class[]{varH.getType()},
                    handler);

            varH.setValue(obj);
            return;
        }
    }
}
