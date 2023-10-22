package org.apache.ibatis.solon.aot;

import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.DefaultVFS;
import org.apache.ibatis.io.JBoss6VFS;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.RuntimeSupport;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.integration.MybatisAdapterDefault;
import org.apache.ibatis.solon.integration.MybatisAdapterManager;
import org.noear.solon.Utils;
import org.noear.solon.aot.NativeMetadataUtils;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * mybatis aot 注册 native 元数据
 *
 * @author songyinyin
 * @since 2.3
 * @link <a href="https://github.com/kazuki43zoo/mybatis-native-demo/blob/main/src/main/java/com/example/nativedemo/MyBatisNativeConfiguration.java">MyBatisNativeConfiguration</a>
 */
public class MybatisRuntimeNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {
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

        metadata.registerJdkProxy(Executor.class);
        metadata.registerReflection(Executor.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerAllDeclaredMethod(Executor.class, ExecutableMode.INVOKE);

        metadata.registerJdkProxy(StatementHandler.class);
        metadata.registerReflection(StatementHandler.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerAllDeclaredMethod(StatementHandler.class, ExecutableMode.INVOKE);

        metadata.registerReflection(BoundSql.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_DECLARED_METHODS, MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS);
        metadata.registerAllDeclaredMethod(BoundSql.class, ExecutableMode.INVOKE);

        metadata.registerReflection(Configuration.class, MemberCategory.DECLARED_FIELDS);
        metadata.registerAllDeclaredMethod(Configuration.class, ExecutableMode.INVOKE);

        metadata.registerReflection(JBoss6VFS.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(DefaultVFS.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        for (String name : MybatisAdapterManager.getAll().keySet()) {
            //用 name 找，避免出现重复的（默认的name=null）
            if (Utils.isNotEmpty(name)) {
                MybatisAdapter adapter = MybatisAdapterManager.getOnly(name);
                if (adapter instanceof MybatisAdapterDefault) {
                    registerMybatisAdapter(context, metadata, (MybatisAdapterDefault) adapter);
                }
            }
        }
    }

    protected void registerMybatisAdapter(AppContext context, RuntimeNativeMetadata metadata, MybatisAdapterDefault bean) {
        //注册 xml 资源
        for (String res : bean.getMappers()) {
            if (res.startsWith(Utils.TAG_classpath)) {
                res = res.substring(Utils.TAG_classpath.length());
                res = res.replace("**", "*");
                res = res.replace("*", ".*");
                metadata.registerResourceInclude(res);
            }
        }

        //注册 mapper 代理
        for (Class<?> clz : bean.getConfiguration().getMapperRegistry().getMappers()) {
            metadata.registerJdkProxy(clz);
            metadata.registerReflection(clz, MemberCategory.INTROSPECT_PUBLIC_METHODS);
            Method[] declaredMethods = clz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                MethodWrap methodWrap = context.methodGet(method);
                NativeMetadataUtils.registerMethodAndParamAndReturnType(metadata, methodWrap);
            }
        }

        // 注册 entity
        for (Class<?> clz : bean.getConfiguration().getTypeAliasRegistry().getTypeAliases().values()) {
            metadata.registerReflection(clz, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            metadata.registerDefaultConstructor(clz);
        }
    }
}
