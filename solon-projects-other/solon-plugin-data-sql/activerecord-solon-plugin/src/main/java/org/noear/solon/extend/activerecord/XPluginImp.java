package org.noear.solon.extend.activerecord;

import javax.sql.DataSource;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.activerecord.annotation.Db;
import org.noear.solon.extend.activerecord.annotation.Table;
import org.noear.solon.extend.activerecord.impl.DbBeanInjectorImpl;
import org.noear.solon.extend.activerecord.impl.TableBeanBuilderImpl;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        // 构建Bean时的Table标签
        context.beanBuilderAdd(Table.class, new TableBeanBuilderImpl());

        // 注入Bean时的Db标签
        context.beanInjectorAdd(Db.class,new DbBeanInjectorImpl());

        context.subWrapsOfType(DataSource.class, bw->{
            ArpManager.add(bw);
        });

        // 通过DataSource类型获取Bean实例
        context.lifecycle(-99, () -> {
            ArpManager.start();
        });
    }


    @Override
    public void prestop() throws Throwable {
        // 循环停止ActiveRecordPlugin
        ArpManager.stop();
    }
}