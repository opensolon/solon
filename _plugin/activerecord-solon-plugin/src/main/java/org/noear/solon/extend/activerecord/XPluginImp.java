package org.noear.solon.extend.activerecord;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
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
    Map<String, Class<? extends Model<?>>> tableMap = new LinkedHashMap<>();

    @Override
    public void start(SolonApp app) {
        Aop.beanOnloaded(() -> {
            DataSource ds = Aop.get(DataSource.class);
            initActiveRecord(ds);
        });

        Aop.context().beanBuilderAdd(Table.class, (clz, wrap, anno) -> {
            if (wrap.raw() instanceof Model) {
                if (Utils.isEmpty(anno.value())) {
                    tableMap.put(clz.getSimpleName(), (Class<? extends Model<?>>) clz);
                } else {
                    tableMap.put(anno.value(), (Class<? extends Model<?>>) clz);
                }
            }
        });
    }

    private void initActiveRecord(DataSource ds) {
        if (ds == null) {
            return;
        }

        IDataSourceProvider dsp = new DataSourceProviderWrap(ds);

        ActiveRecordPlugin arp = new ActiveRecordPlugin(dsp);

//        arp.getEngine().setBaseTemplatePath("/activerecord/");
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());

        ResourceScaner.scan("activerecord", n -> n.endsWith(".sql"))
                .forEach(url -> {
                    arp.addSqlTemplate(url);
                });

        EventBus.push(arp);

        tableMap.forEach((key, clz) -> {
            arp.addMapping(key, clz);
        });

        // 启动 arp
        arp.start();
    }
}