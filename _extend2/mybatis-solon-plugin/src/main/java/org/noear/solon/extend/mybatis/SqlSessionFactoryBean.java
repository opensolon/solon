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
import org.noear.solon.core.XMonitor;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class SqlSessionFactoryBean {
    protected Configuration config;
    protected SqlSessionFactory factory;
    private static int count = 0;

    public SqlSessionFactoryBean(DataSource dataSource) {
        this(dataSource,null);
    }

    public SqlSessionFactoryBean(DataSource dataSource, Properties props) {
        TransactionFactory tf = new JdbcTransactionFactory();
        Environment envi = new Environment("solon-" + (count++), tf, dataSource);
        config = new Configuration(envi);

        if (props != null) {
            props.forEach((k, v) -> {
                if (k instanceof String && v instanceof String) {
                    String key = (String) k;
                    String val = (String) v;

                    if (key.startsWith("typeAliases[")) {
                        cfg().getTypeAliasRegistry().registerAliases(val);
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
                            addMappersByXml(val);
                        } else if (val.endsWith(".class")) {
                            addMappersByClz(val.substring(0, val.length() - 6));
                        } else {
                            cfg().addMappers(val);
                        }
                    }
                }
            });
        }
    }

    private void addMappersByClz(String val) {
        try {
            Class<?> mapperInterface = Resources.classForName(val);
            cfg().addMapper(mapperInterface);
        } catch (Throwable ex) {
            XMonitor.sendError(null, ex);
        }
    }

    private void addMappersByXml(String val) {
        try {
            // resource 配置方式
            ErrorContext.instance().resource(val);
            /**
             * 读取mapper文件
             */
            InputStream inputStream = Resources.getResourceAsStream(val);
            /**
             * mapper映射文件都是通过XMLMapperBuilder解析
             */
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, cfg(), val, cfg().getSqlFragments());
            mapperParser.parse();
        } catch (Throwable ex) {
            XMonitor.sendError(null, ex);
        }
    }

    public Configuration cfg() {
        return config;
    }

    public SqlSessionFactory getObject() {
        if (factory == null) {
            factory = new SqlSessionFactoryBuilder().build(config);
        }

        return factory;
    }
}
