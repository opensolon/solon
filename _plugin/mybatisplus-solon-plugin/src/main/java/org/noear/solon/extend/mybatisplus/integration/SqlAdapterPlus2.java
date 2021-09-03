package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ResourceScaner;
import org.noear.solon.extend.mybatis.SqlAdapter;
import org.noear.solon.extend.mybatis.SqlPlugins;
import org.noear.solon.extend.mybatis.integration.SqlSessionProxy;
import org.noear.solon.extend.mybatis.integration.XPluginImp;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author noear
 * @since 1.5
 */
class SqlAdapterPlus2 implements SqlAdapter {
    protected Configuration config;
    protected SqlSessionFactory factory;
    protected List<String> mappers = new ArrayList<>();
    protected BeanWrap dsWrap;

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    public SqlAdapterPlus2(BeanWrap dsWrap) {
        this(dsWrap, Solon.cfg().getProp("mybatis"));
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     * */
    public SqlAdapterPlus2(BeanWrap dsWrap, Properties props) {
        this.dsWrap = dsWrap;

        DataSource dataSource = dsWrap.raw();
        String dataSourceId = "ds-" + (dsWrap.name() == null ? "" : dsWrap.name());

        TransactionFactory tf = new JdbcTransactionFactory();
        Environment environment = new Environment(dataSourceId, tf, dataSource);
        config = new Configuration(environment);

        //加载插件
        //加载插件
        for (Interceptor i : SqlPlugins.getInterceptors()) {
            config.addInterceptor(i);
        }

        //1.分发事件，推给扩展处理
        EventBus.push(config);

        //2.初始化（顺序不能乱）
        init0(props);
    }

    private void init0(Properties props){
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
                                getConfig().getTypeAliasRegistry().registerAlias(clz);
                            }
                        } else {
                            //package
                            getConfig().getTypeAliasRegistry().registerAliases(val);
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
                                getConfig().addMapper(clz);
                                mappers.add(val);
                            }
                        } else {
                            //package
                            getConfig().addMappers(val);
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
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(stream, getConfig(), val, getConfig().getSqlFragments());
            mapperParser.parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * 获取配置器*/
    public Configuration getConfig() {
        return config;
    }

    /**
     * 获取会话工厂
     * */
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = new MybatisSqlSessionFactoryBuilder().build(config);
        }

        return factory;
    }

    public SqlAdapter mapperScan(SqlSessionProxy proxy) {
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
    public SqlAdapter mapperScan(SqlSessionProxy proxy, String basePackages) {
        mapperScan0(proxy, basePackages);
        return this;
    }

    private void mapperScan0(SqlSessionProxy proxy, String val) {
        if (val.endsWith(".xml")) {

        } else if (val.endsWith(".class")) {
            Class<?> clz = Utils.loadClass(val.substring(0, val.length() - 6));
            mapperBindDo(proxy, clz);
        } else {
            String dir = val.replace('.', '/');
            mapperScanDo(proxy, dir);
        }
    }

    private void mapperScanDo(SqlSessionProxy proxy, String dir) {
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

    private void mapperBindDo(SqlSessionProxy proxy, Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = proxy.getMapper(clz);

            Aop.context().putWrap(clz, Aop.wrap(clz,mapper));
        }
    }
}
