package org.noear.solon.extend.beetlsql;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.core.db.*;
import org.beetl.sql.core.nosql.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
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
class DbManager {
    private static DbManager _global = new DbManager();

    public static DbManager global() {
        return _global;
    }


    private final Map<String, SQLManager> cached = new ConcurrentHashMap<>();
    private ConditionalSQLManager dynamic;

    /**
     * 构建
     */
    private SQLManager build(BeanWrap bw) {
        DbConnectionSource cs = null;
        DataSource master = bw.raw();

        String slaves_str = bw.attrGet("slaves");

        if (Utils.isNotEmpty(slaves_str)) {
            String[] slaveAry = slaves_str.split(",");
            DataSource[] slaves = new DataSource[slaveAry.length];

            for (int i = 0, len = slaveAry.length; i < len; i++) {
                slaves[i] = Aop.get(slaveAry[i]);

                if (slaves[i] == null) {
                    throw new RuntimeException("DbManager: This data source does not exist: " + slaveAry[i]);
                }
            }

            cs = new DbConnectionSource(master, slaves);
        } else {
            cs = new DbConnectionSource(master, null);
        }

        SQLManagerBuilder builder = SQLManager.newBuilder(cs);

        buildStyle(bw, builder);

        if(Solon.cfg().isDebugMode() || Solon.cfg().isFilesMode()){
            builder.addInterDebug();
        }

        //推到事件中心，用于扩展
        EventBus.push(builder);

        return builder.build();
    }

    /**
     * 获取动态管理器
     */
    public ConditionalSQLManager dynamicGet() {
        return dynamic;
    }

    public void dynamicBuild(BeanWrap def) {
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

    /**
     * 获取管理器
     */
    public SQLManager get(BeanWrap bw) {
        if (bw == null) {
            return null;
        }

        SQLManager tmp = cached.get(bw.name());
        if (tmp == null) {
            synchronized (bw.name().intern()) {
                tmp = cached.get(bw.name());
                if (tmp == null) {
                    tmp = build(bw);

                    cached.put(bw.name(), tmp);

                    EventBus.push(tmp);
                }
            }
        }

        return tmp;
    }

    /**
     * 注册管理器
     */
    public void reg(BeanWrap bw) {
        get(bw);
    }

    private void buildStyle(BeanWrap bw, SQLManagerBuilder builder) {
        String dialect = bw.attrGet("dialect");

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
