package org.noear.solon.extend.activerecord;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ResourceScaner;
import org.noear.solon.extend.activerecord.annotation.Table;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    Map<Table, Class<? extends Model<?>>> tableMap = new LinkedHashMap<>();

    @Override
    public void start(SolonApp app) {
        Aop.context().beanBuilderAdd(Table.class, (clz, wrap, anno) -> {
            if (wrap.raw() instanceof Model) {
                tableMap.put(anno, (Class<? extends Model<?>>) clz);
            }
        });

        Aop.beanOnloaded(() -> {
            Aop.beanForeach(bw -> {
                if (bw.raw() instanceof DataSource) {
                    initActiveRecord(bw.raw(), bw.name());
                }
            });
        });
    }

    private void initActiveRecord(DataSource ds, String name) {
        if (ds == null) {
            return;
        }

        if (Utils.isEmpty(name)) {
            name = DbKit.MAIN_CONFIG_NAME;
        }

        //构建配置
        ConfigImpl cfg = new ConfigImpl(
                name,
                new DataSourceProxy(ds),
                DbKit.DEFAULT_TRANSACTION_LEVEL);

        //构建arp
        ActiveRecordPlugin arp = new ActiveRecordPlugin(cfg);

        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());

        ResourceScaner.scan("sql", n -> n.endsWith(".sql"))
                .forEach(url -> {
                    arp.addSqlTemplate(url);
                });

        //添加表印射
        tableMap.forEach((anno, clz) -> {
            arp.addMapping(anno.name(), anno.primaryKey(), clz);
        });

        //发布事件，以便扩展
        EventBus.push(arp);

        // 启动 arp
        arp.start();
    }
}