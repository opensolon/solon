package org.apache.ibatis.solon.aot;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.RuntimeSupport;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Condition;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Props;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Mybatis aot 注册 native 元数据
 *
 * @author songyinyin
 * @since 2.3.1
 * @link <a href="https://github.com/kazuki43zoo/mybatis-native-demo/blob/main/src/main/java/com/example/nativedemo/MyBatisNativeConfiguration.java">MyBatisNativeConfiguration</a>
 */
@Component
@Condition(onClass = RuntimeNativeRegistrar.class)
public class MybatisRuntimeNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AopContext context, RuntimeNativeMetadata metadata) {
        Stream.of(RawLanguageDriver.class,
                XMLLanguageDriver.class,
                RuntimeSupport.class,
                ProxyFactory.class,
                Slf4jImpl.class,
                Log.class,
                JakartaCommonsLoggingImpl.class,
                Log4j2Impl.class,
                Jdk14LoggingImpl.class,
                StdOutImpl.class,
                NoLoggingImpl.class,
                SqlSessionFactory.class,
                PerpetualCache.class,
                FifoCache.class,
                LruCache.class,
                SoftCache.class,
                WeakCache.class,
                ArrayList.class,
                HashMap.class,
                TreeSet.class,
                HashSet.class
        ).forEach(x -> metadata.registerReflection(x, MemberCategory.values()));

        Stream.of(
                "org/apache/ibatis/builder/xml/.*.dtd",
                "org/apache/ibatis/builder/xml/.*.xsd"
        ).forEach(metadata::registerResourceInclude);

        context.beanForeach(beanWrap -> {
            if (Arrays.stream(beanWrap.annotations()).anyMatch(x -> x instanceof Mapper)) {

                metadata.registerJdkProxy(beanWrap.clz());
                metadata.registerReflection(beanWrap.clz(), MemberCategory.INTROSPECT_PUBLIC_METHODS);
                Method[] declaredMethods = beanWrap.clz().getDeclaredMethods();
                for (Method method : declaredMethods) {
                    metadata.registerMethod(method, ExecutableMode.INVOKE);
                }
            }
        });

        // TODO 待优化
        //  1. 这里与 MybatisAdapterDefault 中获取 mapper 的逻辑重复了
        //  2. 有可能用户配置的不是正则表达式，需要处理为正则表达式
        context.subWrapsOfType(DataSource.class, bw -> {
            Props prop = Solon.cfg().getProp("mybatis." + bw.name());
            prop.forEach((k, v) -> {
                String key = (String) k;
                String valStr = (String) v;
                valStr = valStr.replace(".", "/");

                if (key.startsWith("mappers[") || key.equals("mappers")) {
                    if (valStr.endsWith(".xml")) {
                        metadata.registerResourceInclude(valStr);
                    } else {
                        metadata.registerResourceInclude(valStr + "/.*.xml");
                    }
                }
            });
        });
    }
}
