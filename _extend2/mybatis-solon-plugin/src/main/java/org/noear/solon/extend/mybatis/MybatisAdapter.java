package org.noear.solon.extend.mybatis;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMonitor;
import org.noear.solon.core.XScaner;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 适配器
 * */
public class MybatisAdapter {
    protected Configuration config;
    protected SqlSessionFactory factory;
    protected List<String> mappers = new ArrayList<>();

    protected static int environmentIndex = 0;

    /**
     * 使用默认的 typeAliases 和 mappers 配置
     */
    public MybatisAdapter(DataSource dataSource) {
        this(dataSource, XApp.cfg().getProp("mybatis"));
    }

    public MybatisAdapter(DataSource dataSource, Properties props) {
        String environment_id = props.getProperty("environment");
        if (XUtil.isEmpty(environment_id)) {
            environment_id = "solon-" + (environmentIndex++);
        }

        TransactionFactory tf = new JdbcTransactionFactory();
        Environment environment = new Environment(environment_id, tf, dataSource);
        config = new Configuration(environment);

        if (props != null) {
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String key = (String) k;
                    String val = (String) v;

                    if (key.startsWith("typeAliases[")) {
                        if (key.endsWith(".class")) {
                            //type class
                            Class<?> clz = XUtil.loadClass(val.substring(0, val.length() - 6));
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
                            Class<?> clz = XUtil.loadClass(val.substring(0, val.length() - 6));
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
            XMonitor.sendError(null, ex);
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

    public MybatisAdapter mapperScan() {
        MybatisProxy proxy = MybatisProxy.get(getFactory());

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
    public MybatisAdapter mapperScan(String basePackages) {
        mapperScan0(MybatisProxy.get(getFactory()), basePackages);
        return this;
    }

    private void mapperScan0(MybatisProxy proxy, String val) {
        if (val.endsWith(".xml")) {

        } else if (val.endsWith(".class")) {
            Class<?> clz = XUtil.loadClass(val.substring(0, val.length() - 6));
            mapperBindDo(proxy, clz);
        } else {
            String dir = val.replace('.', '/');
            mapperScanDo(proxy, dir);
        }
    }

    private void mapperScanDo(MybatisProxy proxy, String dir) {
        XScaner.scan(dir, n -> n.endsWith(".class"))
                .stream()
                .map(name -> {
                    String className = name.substring(0, name.length() - 6);
                    return XUtil.loadClass(className.replace("/", "."));
                })
                .forEach((clz) -> {
                    mapperBindDo(proxy, clz);
                });
    }

    private void mapperBindDo(MybatisProxy proxy, Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = proxy.getMapper(clz);

            Aop.put(clz, mapper);
        }
    }
}
