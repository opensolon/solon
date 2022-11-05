package org.noear.solon.extend.activerecord;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Model;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.extend.activerecord.annotation.Db;
import org.noear.solon.extend.activerecord.annotation.Table;
import org.noear.solon.extend.activerecord.impl.ConfigImpl;
import org.noear.solon.extend.activerecord.impl.DataSourceProxyImpl;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * ActiveRecordPlugin 管理器
 *
 * @author noear
 * @since 1.10
 */
public class ActiveRecordManager {
    private static Map<String, ActiveRecordPlugin> arpMap = new HashMap<>();


    /**
     * 开始构建 ActiveRecordPlugin 服务
     * */
    public static void start(Map<String, DataSource> dsMap) {
        for (Map.Entry<String, DataSource> entry : dsMap.entrySet()) {
            // 构建配置
            DataSource dsp = new DataSourceProxyImpl(entry.getValue());
            Config cfg = new ConfigImpl(entry.getKey(), dsp, DbKit.DEFAULT_TRANSACTION_LEVEL);

            // 构建arp
            ActiveRecordPlugin arp = new ActiveRecordPlugin(cfg);

            // arp.getEngine().setSourceFactory(new ClassPathSourceFactory());

            // 添加表映射
            addTableMapping(arp);

            // 添加SQL模板映射
            ScanUtil.scan("sql", n -> n.endsWith(".sql")).forEach(url -> {
                arp.addSqlTemplate(url);
            });

            // 发布事件，以便扩展
            EventBus.push(arp);

            // 启动 arp
            arp.start();

            arpMap.put(cfg.getName(), arp);
        }
    }

    /**
     * 停止 ActiveRecordPlugin 实例
     * */
    public static void stop() throws Throwable {
        for (Map.Entry<String, ActiveRecordPlugin> entry : arpMap.entrySet()) {
            entry.getValue().stop();
        }
    }


    private static void addTableMapping(ActiveRecordPlugin arp) {
        // 取得config的名称，即数据源名称
        String dsName = arp.getConfig().getName();

        // 处理主数据源
        for (Map.Entry<Table, Class<? extends Model<?>>> entry : ModelManager.getModelClassMap().entrySet()) {
            // 根据数据源名称做不同处理
            Table table = entry.getKey();
            String dbSource = getDbSource(entry.getValue());
            if (dsName.equals(dbSource)) {
                //添加表印射
                arp.addMapping(table.name(), table.primaryKey(), entry.getValue());
            }
        }
    }


    private static String getDbSource(Class<? extends Model<?>> model) {
        Db db = model.getAnnotation(Db.class);
        if (null == db) {
            // 没有Db标签，默认为主数据库
            return DbKit.MAIN_CONFIG_NAME;
        }

        return db.value();
    }
}
