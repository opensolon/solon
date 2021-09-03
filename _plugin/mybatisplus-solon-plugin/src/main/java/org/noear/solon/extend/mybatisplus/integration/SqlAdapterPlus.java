package org.noear.solon.extend.mybatisplus.integration;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.mapping.Environment;
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
import org.noear.solon.extend.mybatis.integration.SqlSessionProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 适配器
 * <p>
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 *
 * @author noear, iYarnFog
 * @since 1.5
 */
class SqlAdapterPlus implements SqlAdapter {
    protected InputStream configStream;
    protected Environment environment;
    protected Configuration configuration;
    protected SqlSessionFactory factory;
    protected List<String> mappers = new ArrayList<>();
    protected BeanWrap dsWrap;

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    public SqlAdapterPlus(BeanWrap dsWrap) {
        this(dsWrap, Solon.cfg().getProp("mybatis"));
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    public SqlAdapterPlus(BeanWrap dsWrap, Properties props) {
        this.dsWrap = dsWrap;

        DataSource dataSource = dsWrap.raw();
        String dataSourceId = "ds-" + (dsWrap.name() == null ? "" : dsWrap.name());

        TransactionFactory tf = new JdbcTransactionFactory();
        this.environment = new Environment(dataSourceId, tf, dataSource);

        // 1.初始化（顺序不能乱）
        init0(props);

        // 2.初始化 Mybatis Plus
        this.configuration = this.getFactory().getConfiguration();
        this.configuration.setEnvironment(this.environment);

        // 3.分发事件，推给扩展处理
        EventBus.push(this.configuration);
    }

    private void init0(Properties props) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.newDocument();
//        document.set
            document.setXmlStandalone(true);
            Element configuration = document.createElement("configuration");
            // Solon Start
            if (props != null) {
                //支持包名和xml
                Element typeAliases = document.createElement("typeAliases");
                Element mappers = document.createElement("mappers");
                props.forEach((k, v) -> {
                    if (k instanceof String && v instanceof String) {
                        String key = (String) k;
                        String val = (String) v;

                        if (key.startsWith("typeAliases[")) {
                            if (key.endsWith(".xml")) {
                            } else if (key.endsWith(".class")) {
                            } else {
                                Element package0 = document.createElement("package");
                                package0.setAttribute("name", val);
                                typeAliases.appendChild(package0);
                            }
                        }
                        if (key.startsWith("mappers[")) {
                            Element package0 = document.createElement("package");
                            if (key.endsWith(".xml")) {
                                package0.setAttribute("resource", val);
                            } else if (key.endsWith(".class")) {
                                package0.setAttribute("class", val);
                            } else {
                                package0.setAttribute("name", val);
                            }
                            mappers.appendChild(package0);
                            this.mappers.add(val);
                        }
                    }
                });
                configuration.appendChild(typeAliases);
                configuration.appendChild(mappers);

                if (this.mappers.size() == 0) {
                    throw new RuntimeException("Please add the mappers configuration!");
                }
            }
            // Solon End

            document.appendChild(configuration);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // 输出内容是否使用换行
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//mybatis.org//DTD Config 3.0//EN");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-config.dtd");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            this.configStream = new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取配置器
     */
    public Configuration getConfig() {
        return this.configuration;
    }

    /**
     * 获取会话工厂
     */
    public SqlSessionFactory getFactory() {

        if (factory == null) {
            factory = new MybatisSqlSessionFactoryBuilder().build(this.configStream);
        }

        return factory;
    }

    public SqlAdapter mapperScan(SqlSessionProxy proxy) {
        for (String val : this.mappers) {
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

            Aop.context().putWrap(clz, Aop.wrap(clz, mapper));
        }
    }
}
