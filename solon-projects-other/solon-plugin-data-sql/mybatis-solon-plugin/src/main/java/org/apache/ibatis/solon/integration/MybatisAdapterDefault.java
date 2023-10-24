package org.apache.ibatis.solon.integration;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ResourceUtil;
import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.tran.SolonManagedTransactionFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Mybatis 适配器默认实现
 *
 * @author noear
 * @since 1.1
 */
public class MybatisAdapterDefault implements MybatisAdapter {
    protected final BeanWrap dsWrap;
    protected final Props dsProps;

    //mapper 注解验证启用？
    protected final boolean mapperVerifyEnabled;

    protected Configuration config;
    protected SqlSessionFactory factory;
    protected List<String> mappers = new ArrayList<>();
    protected SqlSessionFactoryBuilder factoryBuilder;

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    protected MybatisAdapterDefault(BeanWrap dsWrap) {
        this(dsWrap, Solon.cfg().getProp("mybatis"));
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    protected MybatisAdapterDefault(BeanWrap dsWrap, Props dsProps) {
        this.dsWrap = dsWrap;
        if (dsProps == null) {
            this.dsProps = new Props();
        } else {
            this.dsProps = dsProps;
        }

        this.mapperVerifyEnabled = dsProps.getBool("configuration.mapperVerifyEnabled", false);
        this.factoryBuilder = new SqlSessionFactoryBuilder();

        DataSource dataSource = getDataSource();
        String dataSourceId = dsWrap.name();
        if (Utils.isEmpty(dataSourceId)) {
            dataSourceId = "_main";
        }

        TransactionFactory tf = new SolonManagedTransactionFactory();
        Environment environment = new Environment(dataSourceId, tf, dataSource);

        initConfiguration(environment);

        //加载插件（通过配置）
        for (Interceptor i : MybatisPluginManager.getInterceptors()) {
            config.addInterceptor(i);
        }

        //加载插件（通过Bean）
        dsWrap.context().lifecycle(-99, () -> {
            dsWrap.context().beanForeach(bw -> {
                if (bw.raw() instanceof Interceptor) {
                    config.addInterceptor(bw.raw());
                }
            });
        });

        //1.分发事件，推给扩展处理
        EventBus.publish(config);

        //2.初始化（顺序不能乱）
        initDo();

        dsWrap.context().getBeanAsync(SqlSessionFactoryBuilder.class, bean -> {
            factoryBuilder = bean;
        });
    }

    public List<String> getMappers() {
        return mappers;
    }

    protected DataSource getDataSource() {
        return dsWrap.raw();
    }

    protected void initConfiguration(Environment environment) {
        config = new Configuration(environment);

        //for configuration section
        Props cfgProps = dsProps.getProp("configuration");
        if (cfgProps.size() > 0) {
            Utils.injectProperties(config, cfgProps);
        }
    }

    protected void initDo() {
        //for typeAliases & typeHandlers section
        dsProps.forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String key = (String) k;
                String valStr = (String) v;

                if (key.startsWith("typeAliases[") || key.equals("typeAliases")) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }

                        //package || type class，转为类表达式
                        String valNew = getClassExpr(val);

                        for (Class<?> clz : ResourceUtil.scanClasses(dsWrap.context().getClassLoader(),valNew)) {
                            if (clz.isInterface() == false) {
                                getConfiguration().getTypeAliasRegistry().registerAlias(clz);
                            }
                        }
                    }
                }

                if (key.startsWith("typeHandlers[") || key.equals("typeHandlers")) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }

                        //package || type class，转为类表达式
                        String valNew = getClassExpr(val);

                        for (Class<?> clz : ResourceUtil.scanClasses(dsWrap.context().getClassLoader(),valNew)) {
                            if (TypeHandler.class.isAssignableFrom(clz)) {
                                getConfiguration().getTypeHandlerRegistry().register(clz);
                            }
                        }
                    }
                }
            }
        });

        //todo: 上面的完成后，才能做下面这个

        //for mappers section
        dsProps.forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String key = (String) k;
                String valStr = (String) v;

                if (key.startsWith("mappers[") || key.equals("mappers")) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }

                        mappers.add(val);

                        if (val.endsWith(".xml")) {
                            //mapper xml， 新方法，替代旧的 *.xml （基于表达式；更自由，更语义化）
                            for (String uri : ResourceUtil.scanResources(val)) {
                                addMapperByXml(uri);
                            }

                            //todo: 兼容提醒:
                            compatibilityTipsOfXml(val);
                        } else {
                            //package || type class，转为类表达式
                            String valNew = getClassExpr(val);

                            for (Class<?> clz : ResourceUtil.scanClasses(dsWrap.context().getClassLoader(),valNew)) {
                                if (clz.isInterface()) {
                                    if (mapperVerifyEnabled) {
                                        if (isMapper(clz)) {
                                            getConfiguration().addMapper(clz);
                                        }
                                    } else {
                                        getConfiguration().addMapper(clz);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        if (mappers.size() == 0) {
            LogUtil.global().warn("Missing mappers configuration!");
            //throw new IllegalStateException("Please add the mappers configuration!");
        }

        if (config.getMapperRegistry().getMappers().size() == 0) {
            LogUtil.global().warn("Missing mapper registration, please check the mappers configuration!");
            //throw new IllegalStateException("Please check the mappers configuration!");
        }

        //for plugins section
        List<Interceptor> interceptors = MybatisPluginUtils.resolve(dsProps, "plugins");
        for (Interceptor itp : interceptors) {
            getConfiguration().addInterceptor(itp);
        }
    }

    protected boolean isMapper(Class<?> clz) {
        return clz.isAnnotationPresent(Mapper.class);
    }

    /**
     * 获取配置器
     */
    @Override
    public Configuration getConfiguration() {
        return config;
    }

    /**
     * 获取会话工厂
     */
    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = factoryBuilder.build(getConfiguration());//new SqlSessionFactoryProxy(factoryBuilder.build(config));
        }

        return factory;
    }

    Map<Class<?> , Object> mapperCached = new HashMap<>();

    @Override
    public  <T> T getMapper(Class<T> mapperClz) {
        Object mapper = mapperCached.get(mapperClz);

        if (mapper == null) {
            synchronized (mapperClz) {
                mapper = mapperCached.get(mapperClz);
                if (mapper == null) {
                    MybatisMapperInterceptor handler = new MybatisMapperInterceptor(getFactory(), mapperClz);

                    mapper = Proxy.newProxyInstance(
                            mapperClz.getClassLoader(),
                            new Class[]{mapperClz},
                            handler);
                    mapperCached.put(mapperClz, mapper);
                }
            }
        }

        return (T) mapper;
    }

    @Override
    public void injectTo(VarHolder varH) {
        //@Db("db1") MybatisAdapter adapter;
        if (MybatisAdapter.class.isAssignableFrom(varH.getType())) {
            varH.setValue(this);
            return;
        }

        //@Db("db1") SqlSessionFactory factory;
        if (SqlSessionFactory.class.isAssignableFrom(varH.getType())) {
            varH.setValue(this.getFactory());
            return;
        }

        //@Db("db1") Configuration cfg;
        if (Configuration.class.isAssignableFrom(varH.getType())) {
            varH.setValue(this.getConfiguration());
            return;
        }

        //@Db("db1") UserMapper userMapper;
        if (varH.getType().isInterface()) {
            Object mapper = this.getMapper(varH.getType());

            varH.setValue(mapper);
            return;
        }
    }

    protected void addMapperByXml(String uri) {
        try {
            // resource 配置方式
            ErrorContext.instance().resource(uri);

            //读取mapper文件
            InputStream stream = Resources.getResourceAsStream(uri);

            //mapper映射文件都是通过XMLMapperBuilder解析
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(stream, getConfiguration(), uri, getConfiguration().getSqlFragments());
            mapperParser.parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compatibilityTipsOfXml(String val) {
        //todo: 兼容提醒:
        if (val.endsWith("*.xml") && val.indexOf("*") == val.indexOf("*.xml")) {
            //@Deprecated //弃用提示
            LogUtil.global().warn("Mybatis-新文件表达式提示：'" + val + "' 不包括深度子目录；如有需要可增加'/**/'段");
        }
    }

    private String getClassExpr(String val) {
        //兼容旧代码: 把包名转为类表达式，但类名保持原态

        if (val.endsWith(".class") == false && val.endsWith(".*") == false) {
            int idx = val.lastIndexOf('.');
            char acr = val.charAt(idx + 1);

            if (acr > 96) { //44=$ 97=a
                //开头为小写（说明是包）
                return val + ".*";
            } else {
                //开头为大写或$（说明是类）
                return val;
            }
        }

        return val;
    }
}