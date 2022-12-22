package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.core.db.*;
import org.beetl.sql.core.nosql.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.ValHolder;
import org.noear.solon.core.event.EventBus;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQLManager 工具
 *
 * @author noear
 * @since 2020-09-01
 * */
public class DbManager {
    private static final String TAG = "beetlsql";

    private static final String ATTR_dialect = "dialect";
    private static final String ATTR_slaves = "slaves";
    private static final String ATTR_dev = "dev";


    private static final Map<String, SQLManager> cached = new ConcurrentHashMap<>();
    private static ConditionalSQLManager dynamic;


    /**
     * 获取动态管理器
     */
    public static ConditionalSQLManager dynamicGet() {
        return dynamic;
    }

    public static void dynamicBuild(BeanWrap def) {
        SQLManager master = get(def);
        if (master == null) {
            for (Map.Entry<String, SQLManager> kv : cached.entrySet()) {
                master = kv.getValue();
                break;
            }
        }

        if (master != null) {
            dynamic = new ConditionalSQLManager(master, cached);
        }
    }

    public static SQLManager get(String dsName) {
        BeanWrap dsWrap = Solon.context().getWrap(dsName);
        return get(dsWrap);
    }

    /**
     * 获取管理器
     */
    public static SQLManager get(BeanWrap dsWrap) {
        if (dsWrap == null) {
            return null;
        }

        SQLManager tmp = cached.get(dsWrap.name());
        if (tmp == null) {
            synchronized (dsWrap.name().intern()) {
                tmp = cached.get(dsWrap.name());
                if (tmp == null) {
                    tmp = build(dsWrap);

                    cached.put(dsWrap.name(), tmp);
                }
            }
        }

        return tmp;
    }

    /**
     * 注册管理器
     */
    public static void reg(BeanWrap bw) {
        get(bw);
    }

    /**
     * 构建
     */
    private static SQLManager build(BeanWrap bw) {
        DbConnectionSource cs = null;
        DataSource master = bw.raw();
        Props dsProps;

        if (Utils.isNotEmpty(bw.name())) {
            dsProps = bw.context().cfg().getProp(TAG + "." + bw.name());
        } else {
            dsProps = new Props();
        }

        //从库
        String slaves_str = dsProps.get(ATTR_slaves);
        dsProps.remove(ATTR_slaves);
        if (Utils.isEmpty(slaves_str)) {
            slaves_str = bw.attrGet(ATTR_slaves);
        }

        if (Utils.isNotEmpty(slaves_str)) {
            String[] slaveAry = slaves_str.split(",");
            DataSource[] slaves = new DataSource[slaveAry.length];

            for (int i = 0, len = slaveAry.length; i < len; i++) {
                ValHolder<Integer> valHolder = new ValHolder<>(i);

                //todo::此处不能用同步，有些源可能还没构建好 //不过异常，没法检查了
                bw.context().getWrapAsync(slaveAry[i], dsBw -> {
                    slaves[valHolder.value] = dsBw.raw();
                });
            }

            cs = new DbConnectionSource(master, slaves);
        } else {
            cs = new DbConnectionSource(master, null);
        }

        //方言
        String dialect_str = dsProps.get(ATTR_dialect);
        dsProps.remove(ATTR_dialect);
        if (Utils.isEmpty(slaves_str)) {
            dialect_str = bw.attrGet(ATTR_dialect);
        }

        SQLManagerBuilder builder = SQLManager.newBuilder(cs);
        //as bean name
        String dataSourceId = "ds-" + (bw.name() == null ? "" : bw.name());
        builder.setName(dataSourceId);

        //支持特性加持
        buildStyle(builder, dialect_str);

        //支持配置注入
        if (dsProps.size() > 0) {
            //处理调试模式
            if (dsProps.getBool(ATTR_dev, false)) {
                builder.addInterDebug();
            }
            dsProps.remove(ATTR_dev);

            Utils.injectProperties(builder, dsProps);
        }

        //推到事件中心，用于扩展
        EventBus.push(builder);

        return builder.build();
    }

    private static void buildStyle(SQLManagerBuilder builder, String dialect) {
        if (Utils.isNotEmpty(dialect)) {
            DBStyle style = null;

            if (dialect.indexOf(".") > 0) {
                style = Utils.newInstance(dialect);

            } else {
                dialect = dialect.toLowerCase();

                switch (dialect) {
                    case "oracle":
                        style = new OracleStyle();
                        break;
                    case "mysql":
                        style = new MySqlStyle();
                        break;
                    case "sqlserver":
                        style = new SqlServerStyle();
                        break;
                    case "sqlserver2012":
                        style = new SqlServer2012Style();
                        break;
                    case "postgres":
                    case "postgresql":
                    case "pgsql":
                        style = new PostgresStyle();
                        break;
                    case "db2":
                        style = new DB2SqlStyle();
                        break;
                    case "h2":
                        style = new H2Style();
                        break;
                    case "sqlite":
                        style = new SQLiteStyle();
                        break;
                    case "polardb":
                        style = new PolarDBStyle();
                        break;


                    case "cassandra":
                    case "cassandrasql":
                        style = new CassandraSqlStyle();
                        break;
                    case "clickhouse":
                        style = new ClickHouseStyle();
                        break;
                    case "couchbase":
                        style = new CouchBaseStyle();
                        break;
                    case "drill":
                        style = new DrillStyle();
                        break;
                    case "druid":
                        style = new DruidStyle();
                        break;
                    case "hbase":
                        style = new HBaseStyle();
                        break;
                    case "hive":
                        style = new HiveStyle();
                        break;
                    case "ignite":
                        style = new IgniteStyle();
                        break;
                    case "impala":
                        style = new ImpalaStyle();
                        break;
                    case "machbase":
                        style = new MachbaseStyle();
                        break;
                    case "presto":
                        style = new PrestoStyle();
                        break;
                    case "taos":
                        style = new TaosStyle();
                        break;

                }
            }

            if (style != null) {
                builder.setDbStyle(style);
            }
        }
    }
}
