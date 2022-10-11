package org.noear.solon.extend.activerecord;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.extend.activerecord.annotation.Db;
import org.noear.solon.extend.activerecord.annotation.Table;
import org.noear.solon.extend.activerecord.proxy.MapperInvocationHandler;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Model;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    Map<String, ActiveRecordPlugin> arpMap = new HashMap<>();

    private void addMapping(ActiveRecordPlugin arp) {
        // 取得config的名称，即数据源名称
        String dsName = arp.getConfig().getName();

        // 处理主数据源
        for (Entry<Table, Class<? extends Model<?>>> entry : ModelSingtonManager.getModelClassMap().entrySet()) {
            // 根据数据源名称做不同处理
            Table table = entry.getKey();
            String dbSource = this.getDbSource(entry.getValue());
            if (dsName.equals(dbSource)) {
                arp.addMapping(table.name(), table.primaryKey(), entry.getValue());
            }
        }
    }

    private void builderAddDo(Class<?> clz, BeanWrap wrap, Table anno)
        throws InstantiationException, IllegalAccessException {
        if (clz.isInterface() == true) {
            return;
        }

        if (!(wrap.raw() instanceof Model)) {
            return;
        }

        ModelSingtonManager.addModel(anno, (Model<?>)clz.newInstance());
    }

    private String getDbSource(Class<? extends Model<?>> model) {
        Db db = model.getAnnotation(Db.class);
        if (null == db) {
            // 没有Db标签，默认为主数据库
            return DbKit.MAIN_CONFIG_NAME;
        }

        return db.value();
    }

    private void initActiveRecords(Map<String, DataSource> dsMap) {
        for (Entry<String, DataSource> entry : dsMap.entrySet()) {
            // 构建配置
            Config cfg =
                new ConfigImpl(entry.getKey(), new DataSourceProxy(entry.getValue()), DbKit.DEFAULT_TRANSACTION_LEVEL);

            // 构建arp
            ActiveRecordPlugin arp = new ActiveRecordPlugin(cfg);

            // arp.getEngine().setSourceFactory(new ClassPathSourceFactory());

            // 添加表映射
            this.addMapping(arp);

            // 添加SQL模板映射
            ScanUtil.scan("sql", n -> n.endsWith(".sql")).forEach(url -> {
                arp.addSqlTemplate(url);
            });

            // 发布事件，以便扩展
            EventBus.push(arp);

            // 启动 arp
            arp.start();

            this.arpMap.put(cfg.getName(), arp);
        }
    }

    private void injectorAddDo(ClassLoader classLoader, VarHolder varHolder, String annoValue) {
        varHolder.context().getWrapAsyn(DataSource.class, (dsBw) -> {
            this.injectProxy(classLoader, varHolder, dsBw, annoValue);
        });
    }

    /**
     * 字段注入
     */
    @SuppressWarnings("unchecked")
    private void injectProxy(ClassLoader classLoader, VarHolder varHolder, BeanWrap dsBeanWrap, String annoValue) {
        Object obj = Proxy.newProxyInstance(classLoader, new Class[] {varHolder.getType()},
            new MapperInvocationHandler((Class<? extends Model<?>>)varHolder.getType(), annoValue));
        varHolder.setValue(obj);
    }

    @Override
    public void prestop() throws Throwable {
        // 循环停止ActiveRecordPlugin
        for (Entry<String, ActiveRecordPlugin> entry : this.arpMap.entrySet()) {
            entry.getValue().stop();
        }
    }

    @Override
    public void start(AopContext context) {
        // 构建Bean时的Table标签
        context.beanBuilderAdd(Table.class, (clz, wrap, anno) -> {
            this.builderAddDo(clz, wrap, anno);
        });

        // 注入Bean时的Db标签
        context.beanInjectorAdd(Db.class, (varH, anno) -> {
            this.injectorAddDo(context.getClassLoader(), varH, anno.value());
        });

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
            this.initActiveRecords(dataSourceMap);
        });
    }
}