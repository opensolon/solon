package org.noear.solon.extend.activerecord;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.activerecord.annotation.Db;
import org.noear.solon.extend.activerecord.annotation.Table;
import org.noear.solon.extend.activerecord.impl.DbBeanInjector;
import org.noear.solon.extend.activerecord.impl.TableBeanBuilder;

import com.jfinal.plugin.activerecord.DbKit;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        // 构建Bean时的Table标签
        context.beanBuilderAdd(Table.class, new TableBeanBuilder());

        // 注入Bean时的Db标签
        context.beanInjectorAdd(Db.class,new DbBeanInjector());

        // 通过DataSource类型获取Bean实例
        context.beanOnloaded((ctx) -> {
            Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

            // 循环所有数据源
            ctx.beanForeach((name, bw) -> {
                if (bw.raw() instanceof DataSource) {
                    if (Utils.isBlank(bw.name())) {
                        // 无名称的数据源就是主数据源
                        dataSourceMap.put(DbKit.MAIN_CONFIG_NAME, bw.raw());
                    } else {
                        // 指定名称的数据源
                        dataSourceMap.put(name, bw.raw());

                        if(bw.typed()){
                            dataSourceMap.put(DbKit.MAIN_CONFIG_NAME, bw.raw());
                        }
                    }
                }
            });
            // 如果上面没有找到任何数据源，则未命名的DataSource就是主数据源
            if (dataSourceMap.size() == 0) {
                ctx.beanForeach(bw -> {
                    if (bw.raw() instanceof DataSource) {
                        dataSourceMap.put(DbKit.MAIN_CONFIG_NAME, bw.raw());
                    }
                });
            }

            // 初始化ActiveRecordPlugin
            ActiveRecordManager.start(dataSourceMap);
        });
    }


    @Override
    public void prestop() throws Throwable {
        // 循环停止ActiveRecordPlugin
        ActiveRecordManager.stop();
    }

}