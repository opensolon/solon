package org.noear.solon.extend.activerecord;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ResourceScaner;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.getAsyn(DataSource.class, bw -> {
            initActiveRecord(bw.raw());
        });
    }

    private void initActiveRecord(DataSource ds) {
        IDataSourceProvider dsp = new DataSourceProviderWrap(ds);

        ActiveRecordPlugin arp = new ActiveRecordPlugin(dsp);

//        arp.getEngine().setBaseTemplatePath("/activerecord/");
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());

        ResourceScaner.scan("activerecord", n -> n.endsWith(".sql"))
                .forEach(url -> {
                    arp.addSqlTemplate(url);
                });

        EventBus.push(arp);

        // 启动 arp
        arp.start();
    }
}
