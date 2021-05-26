package org.noear.solon.extend.mybatis;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ResourceScaner;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 适配器
 *
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 * */
class SqlFactoryAdapter {
    protected Configuration config;
    protected SqlSessionFactory factory;
    protected List<String> mappers = new ArrayList<>();
    protected BeanWrap dsWrap;

    protected static int environmentIndex = 0;

    /**
     * 使用默认的 typeAliases 和 mappers 配置
     */
    public SqlFactoryAdapter(BeanWrap dsWrap) {
        this(dsWrap, Solon.cfg().getProp("mybatis"));
    }

    public SqlFactoryAdapter(BeanWrap dsWrap, Properties props) {
        this.dsWrap = dsWrap;

        DataSource dataSource = dsWrap.raw();

        String environment_id = props.getProperty("environment");
        if (Utils.isEmpty(environment_id)) {
            environment_id = "solon-" + (environmentIndex++);
        }

        TransactionFactory tf = new JdbcTransactionFactory();
        Environment environment = new Environment(environment_id, tf, dataSource);
        config = new Configuration(environment);

        if(XPluginImp.pluginList.size() > 0) {
            for (Interceptor i : XPluginImp.pluginList) {
                config.addInterceptor(i);
            }
        }

        //分发事件，推给扩展处理
        EventBus.push(config);

        init0(config, props);
    }

    private void init0(Configuration config,Properties props){
        if (props != null) {
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String key = (String) k;
                    String val = (String) v;

                    if (key.startsWith("typeAliases[")) {
                        if (key.endsWith(".class")) {
                            //type class
                            Class<?> clz = Utils.loadClass(val.substring(0, val.length() - 6));
                            if (clz != null) {
                                cfg().getTypeAliasRegistry().registerAlias(clz);
                            }
                        } else {
                            //package
                            cfg().getTypeAliasRegistry().registerAliases(val);
                        }
                    }
                }
            });

            //支持包名和xml
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String key = (String) k;
                    String val = (String) v;

                    if (key.startsWith("mappers[")) {
                        if (val.endsWith(".xml")) {
                            //mapper xml
                            addMappersByXml(val);
                            mappers.add(val);
                        } else if (val.endsWith(".class")) {
                            //mapper class
                            Class<?> clz = Utils.loadClass(val.substring(0, val.length() - 6));
                            if (clz != null) {
                                cfg().addMapper(clz);
                                mappers.add(val);
                            }
                        } else {
                            //package
                            cfg().addMappers(val);
                            mappers.add(val);
                        }
                    }
                }
            });

            if (mappers.size() == 0) {
                throw new RuntimeException("Please add the mappers configuration!");
            }
        }
    }

    private void addMappersByXml(String val) {
        try {
            // resource 配置方式
            ErrorContext.instance().resource(val);
            /**
             * 读取mapper文件
             */
            InputStream stream = Resources.getResourceAsStream(val);
            /**
             * mapper映射文件都是通过XMLMapperBuilder解析
             */
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(stream, cfg(), val, cfg().getSqlFragments());
            mapperParser.parse();
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    public Configuration cfg() {
        return config;
    }

    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = new SqlSessionFactoryBuilder().build(config);
        }

        return factory;
    }

    public SqlFactoryAdapter mapperScan(SqlSessionHolder proxy) {
        for (String val : mappers) {
            mapperScan0(proxy, val);
        }

        return this;
    }

    /**
     * 替代 @mapperScan
     * <p>
     * 扫描 basePackages 里的类，并生成 mapper 实例注册到bean中心
     */
    public SqlFactoryAdapter mapperScan(SqlSessionHolder proxy, String basePackages) {
        mapperScan0(proxy, basePackages);
        return this;
    }

    private void mapperScan0(SqlSessionHolder proxy, String val) {
        if (val.endsWith(".xml")) {

        } else if (val.endsWith(".class")) {
            Class<?> clz = Utils.loadClass(val.substring(0, val.length() - 6));
            mapperBindDo(proxy, clz);
        } else {
            String dir = val.replace('.', '/');
            mapperScanDo(proxy, dir);
        }
    }

    private void mapperScanDo(SqlSessionHolder proxy, String dir) {
        ResourceScaner.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return Utils.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    mapperBindDo(proxy, clz);
                });
    }

    private void mapperBindDo(SqlSessionHolder proxy, Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = proxy.getMapper(clz);

            Aop.context().putWrap(clz, Aop.wrap(clz,mapper));
        }
    }
}
