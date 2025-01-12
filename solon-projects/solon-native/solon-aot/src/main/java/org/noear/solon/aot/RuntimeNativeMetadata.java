/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.aot;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.aot.hint.ExecutableHint;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.JdkProxyHint;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.aot.hint.ReflectionHints;
import org.noear.solon.aot.hint.ResourceHint;
import org.noear.solon.aot.hint.SerializationHint;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ReflectUtil;
import org.noear.solon.core.util.ScanUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * graalvm native 可达性元数据，用于生成 {@code META-INF/native-image/xxx/xxx-config.json} 文件，请注意，这个类是对 graalvm native 的不完整实现，需要时再补充
 *
 * @author songyinyin
 * @link <a href="https://www.graalvm.org/latest/reference-manual/native-image/metadata">Reachability Metadata</a>
 * @since 2023/4/5 20:50
 */
public class RuntimeNativeMetadata {

    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);

    private final Map<String, ReflectionHints> reflection = new LinkedHashMap<>();
    private final Set<String> args = new TreeSet<>();
    private final List<ResourceHint> includes = new ArrayList<>();
    private final List<ResourceHint> excludes = new ArrayList<>();
    private final Map<String, SerializationHint> serializations = new LinkedHashMap<>();

    private final Map<String, SerializationHint> lambdaSerializations = new LinkedHashMap<>();
    private final Map<String, JdkProxyHint> jdkProxys = new LinkedHashMap<>();

    private String applicationClassName;

    /**
     * 注册参数
     *
     * @param args 参数
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerArg(String... args) {
        for (String arg : args) {
            this.args.add(arg);
        }

        return this;
    }

    /**
     * 注册 jdk 代理接口
     */
    public RuntimeNativeMetadata registerJdkProxy(Class<?> type) {
        return registerJdkProxy(type, null);
    }

    /**
     * 注册 jdk 代理接口
     */
    public RuntimeNativeMetadata registerJdkProxy(Class<?> type, String reachableType) {
        if (type.isInterface() && type.isAnnotation() == false) {
            registerJdkProxyDo(type.getName(), reachableType);
        }

        return this;
    }

    private void registerJdkProxyDo(String typeName, String reachableType) {
        if (jdkProxys.containsKey(typeName) == false) {
            JdkProxyHint proxyHint = new JdkProxyHint();
            proxyHint.setReachableType(reachableType);
            proxyHint.setInterfaces(Arrays.asList(typeName));

            jdkProxys.put(typeName, proxyHint);
        }
    }

    /**
     * 注册反射相关
     *
     * @param className 全类名
     * @param typeHint  为该类型添加更多的反射信息
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerReflection(String className, Consumer<ReflectionHints> typeHint) {
        if (Utils.isEmpty(className)) {
            return this;
        }
        ReflectionHints reflectionHints = getReflectionHints(className);
        typeHint.accept(reflectionHints);

        Class<?> clazz = ClassUtil.loadClass(className);
        if (!className.startsWith("java.") && clazz != null) {
            // 如果类存在，且不是jdk中的类，则注册字段相关元数据
            try {
                if (reflectionHints.getMemberCategories().contains(MemberCategory.DECLARED_FIELDS)) {
                    Field[] declaredFields = clazz.getDeclaredFields();
                    if (Arrays.stream(declaredFields).allMatch(e -> ClassUtil.hasClass(e::getType))) {
                        for (Field declaredField : declaredFields) {
                            registerField(declaredField);
                        }
                    }
                } else if (reflectionHints.getMemberCategories().contains(MemberCategory.PUBLIC_FIELDS)) {
                    Field[] fields = clazz.getFields();
                    if (Arrays.stream(fields).allMatch(e -> ClassUtil.hasClass(e::getType))) {
                        for (Field field : fields) {
                            registerField(field);
                        }
                    }
                }
            } catch (NoClassDefFoundError ignore) {
                // 该类中使用了不存在的类，不注册字段相关元数据
            }
        }
        return this;
    }

    /**
     * 注册反射相关
     *
     * @param type     类型
     * @param typeHint 为该类型添加更多的反射信息
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerReflection(Class<?> type, Consumer<ReflectionHints> typeHint) {
        return registerReflection(type.getName(), typeHint);
    }

    /**
     * 注册反射相关
     *
     * @param type             类型
     * @param memberCategories 成员类别
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerReflection(Class<?> type, MemberCategory... memberCategories) {
        return registerReflection(type, hints -> hints.getMemberCategories().addAll((Arrays.asList(memberCategories))));
    }

    /**
     * 注册反射相关
     *
     * @param className        全类名
     * @param memberCategories 成员类别
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerReflection(String className, MemberCategory... memberCategories) {
        return registerReflection(className, hints -> hints.getMemberCategories().addAll((Arrays.asList(memberCategories))));
    }

    public RuntimeNativeMetadata registerField(Field field) {
        getReflectionHints(field.getDeclaringClass().getName()).getFields().add(field.getName());
        return this;
    }

    public RuntimeNativeMetadata registerConstructor(Constructor<?> constructor, ExecutableMode mode) {
        getReflectionHints(constructor.getDeclaringClass().getName())
                .getConstructors().add(new ExecutableHint("<init>", constructor.getParameterTypes(), mode));
        return this;
    }

    public RuntimeNativeMetadata registerMethod(Method method, ExecutableMode mode) {
        getReflectionHints(method.getDeclaringClass().getName())
                .getMethods().add(new ExecutableHint(method.getName(), method.getParameterTypes(), mode));
        return this;
    }

    /**
     * 注册类上所有的方法
     */
    public RuntimeNativeMetadata registerAllDeclaredMethod(Class<?> clazz, ExecutableMode mode) {
        Method[] declaredMethods = ReflectUtil.getDeclaredMethods(clazz);
        for (Method declaredMethod : declaredMethods) {
            registerMethod(declaredMethod, mode);
        }
        return this;
    }

    /**
     * 注册默认构造方法，如果不存在则不注册
     */
    public RuntimeNativeMetadata registerDefaultConstructor(Class<?> clazz) {
        if (ClassUtil.loadClass(clazz.getName()) != null && hasDefaultConstructor(clazz)) {
            getReflectionHints(clazz.getName()).getConstructors().add(new ExecutableHint("<init>", null, ExecutableMode.INVOKE));
        }
        return this;
    }

    /**
     * 注册默认构造方法，如果类不存在或不存在默认构造 则不注册
     */
    public RuntimeNativeMetadata registerDefaultConstructor(String className) {
        Class<?> clazz = ClassUtil.loadClass(className);
        if (clazz != null && hasDefaultConstructor(clazz)) {
            return registerDefaultConstructor(clazz);
        }
        return this;
    }

    /**
     * 注册包含的资源
     *
     * @param pattern 正则表达式
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerResourceInclude(String pattern) {
        return registerResourceInclude(pattern, null);
    }

    /**
     * 注册包含的资源
     *
     * @param pattern       正则表达式
     * @param reachableType condition class
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerResourceInclude(String pattern, String reachableType) {
        ResourceHint resourceHint = new ResourceHint();
        resourceHint.setReachableType(reachableType);
        resourceHint.setPattern(pattern);
        includes.add(resourceHint);
        return this;
    }

    /**
     * 注册排除的资源
     *
     * @param pattern 正则表达式
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerResourceExclude(String pattern) {
        return registerResourceExclude(pattern, null);
    }

    /**
     * 注册排除的资源
     *
     * @param pattern       正则表达式
     * @param reachableType condition class
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerResourceExclude(String pattern, String reachableType) {
        ResourceHint resourceHint = new ResourceHint();
        resourceHint.setReachableType(reachableType);
        resourceHint.setPattern(pattern);
        excludes.add(resourceHint);
        return this;
    }

    /**
     * 注册Java序列化
     *
     * @param basePackage 类型基础包
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerSerialization(Package basePackage) {
        String dir = basePackage.getName().replace('.', '/');

        //扫描类文件并处理（采用两段式加载，可以部分bean先处理；剩下的为第二段处理）
        ScanUtil.scan(AppClassLoader.global(), dir, n -> n.endsWith(".class"))
                .stream()
                .forEach(name -> {
                    String className = name.substring(0, name.length() - 6);
                    className = className.replace("/", ".");

                    Class<?> clz = ClassUtil.loadClass(AppClassLoader.global(), className);
                    if (clz != null) {
                        registerSerializationDo(clz.getName(), null);
                    }
                });

        return this;
    }

    /**
     * 注册Java序列化
     *
     * @param type 类型
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerSerialization(Class<?> type) {
        return registerSerialization(type, null);
    }


    /**
     * 注册Java序列化
     *
     * @param name 全类名
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerSerialization(String name) {
        registerSerializationDo(name, null);
        return this;
    }

    /**
     * 注册Java序列化
     *
     * @param type          类型
     * @param reachableType condition class
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerSerialization(Class<?> type, String reachableType) {
        registerSerializationDo(type.getName(), reachableType);
        return this;
    }

    /**
     * 注册Lambda序列化
     */
    public RuntimeNativeMetadata registerLambdaSerialization(Class<?> type) {
        registerLambdaSerializationDo(ReflectUtil.getClassName(type), null, null);
        return this;
    }

    /**
     * 生成 reflect-config.json
     *
     * @see <a href="https://www.graalvm.org/latest/reference-manual/native-image/metadata/#specifying-reflection-metadata-in-json">Specifying Reflection Metadata in JSON</a>
     */
    public String toReflectionJson() {
        if (reflection.isEmpty()) {
            return "";
        }
        ONode oNode = new ONode(jsonOptions).asArray();
        for (ReflectionHints hint : reflection.values()) {
            ONode item = oNode.addNew();

            // name
            item.set("name", hint.getName());
            // condition
            if (Utils.isNotEmpty(hint.getReachableType())) {
                item.getOrNew("condition").set("typeReachable", hint.getReachableType());
            }

            List<ExecutableHint> collect = Stream.concat(hint.getConstructors().stream(), hint.getMethods().stream()).collect(Collectors.toList());
            // methods
            List<ExecutableHint> invoke = collect.stream().filter(h -> h.getMode().equals(ExecutableMode.INVOKE)).collect(Collectors.toList());
            if (!invoke.isEmpty()) {
                ONode methods = item.getOrNew("methods").asArray();
                for (ExecutableHint executableHint : invoke) {
                    methods.addNew()
                            .set("name", executableHint.getName())
                            .getOrNew("parameterTypes").asArray().addAll(executableHint.getParameterTypes());
                }
            }
            // queriedMethods
            List<ExecutableHint> introspect = collect.stream().filter(h -> h.getMode().equals(ExecutableMode.INTROSPECT)).collect(Collectors.toList());
            if (!introspect.isEmpty()) {
                ONode queriedMethods = item.getOrNew("queriedMethods").asArray();
                for (ExecutableHint executableHint : introspect) {
                    queriedMethods.addNew()
                            .set("name", executableHint.getName())
                            .getOrNew("parameterTypes").asArray().addAll(executableHint.getParameterTypes());
                }
            }
            // fields
            if (!hint.getFields().isEmpty()) {
                ONode on = item.getOrNew("fields").asArray();
                if (!hint.getFields().isEmpty()) {
                    for (String field : hint.getFields()) {
                        on.addNew().set("name", field);
                    }
                }

            }

            handleCategories(item, hint.getMemberCategories());

        }
        return oNode.toJson();
    }

    /**
     * 生成 resource-config.json
     *
     * @see <a href="https://www.graalvm.org/latest/reference-manual/native-image/metadata/#resource-metadata-in-json">Resource Metadata in JSON</a>
     */
    public String toResourcesJson() {
        if (includes.isEmpty() && excludes.isEmpty()) {
            return "";
        }
        ONode oNode = new ONode(jsonOptions);

        ONode resources = oNode.getOrNew("resources");

        if (!includes.isEmpty()) {
            ONode includesNode = resources.getOrNew("includes").asArray();
            for (ResourceHint hint : includes) {
                ONode item = includesNode.addNew()
                        .set("pattern", hint.getPattern());
                if (Utils.isNotEmpty(hint.getReachableType())) {
                    item.getOrNew("condition").set("typeReachable", hint.getReachableType());
                }
            }
        }

        if (!excludes.isEmpty()) {
            ONode excludesNode = resources.getOrNew("excludes").asArray();
            for (ResourceHint hint : excludes) {
                ONode item = excludesNode.addNew()
                        .set("pattern", hint.getPattern());
                if (Utils.isNotEmpty(hint.getReachableType())) {
                    item.getOrNew("condition").set("typeReachable", hint.getReachableType());
                }
            }
        }
        return oNode.toJson();
    }


    /**
     * 生成 serialization-config.json
     *
     * @see <a href="https://www.graalvm.org/latest/reference-manual/native-image/metadata/#serialization-metadata-in-json">Serialization Metadata in JSON</a>
     */
    public String toSerializationJson() {
        if (serializations.isEmpty() && lambdaSerializations.isEmpty()) {
            return "";
        }
        ONode oNode = new ONode(jsonOptions);

        ONode types = oNode.getOrNew("types").asArray();
        for (SerializationHint hint : serializations.values()) {
            ONode item = types.addNew().set("name", hint.getName());
            if (Utils.isNotEmpty(hint.getReachableType())) {
                item.getOrNew("condition").set("typeReachable", hint.getReachableType());
            }
        }

        ONode lambdaCapturingTypes = oNode.getOrNew("lambdaCapturingTypes").asArray();
        for (SerializationHint hint : lambdaSerializations.values()) {
            ONode item = lambdaCapturingTypes.addNew().set("name", hint.getName());
            if (Utils.isNotEmpty(hint.getReachableType())) {
                item.getOrNew("condition").set("typeReachable", hint.getReachableType());
            }
            if (Utils.isNotEmpty(hint.getCustomTargetConstructorClass())) {
                item.set("customTargetConstructorClass", hint.getCustomTargetConstructorClass());
            }
        }
        return oNode.toJson();
    }


    public String toJdkProxyJson() {
        if (jdkProxys.isEmpty()) {
            return "";
        }
        ONode oNode = new ONode(jsonOptions).asArray();
        for (JdkProxyHint hint : jdkProxys.values()) {
            if (Utils.isEmpty(hint.getInterfaces())) {
                continue;
            }

            ONode item = oNode.addNew();
            item.set("interfaces", hint.getInterfaces());

            if (Utils.isNotEmpty(hint.getReachableType())) {
                item.getOrNew("condition").set("typeReachable", hint.getReachableType());
            }
        }

        return oNode.toJson();
    }

    public Map<String, ReflectionHints> getReflection() {
        return reflection;
    }

    public Set<String> getArgs() {
        return args;
    }

    public List<ResourceHint> getIncludes() {
        return includes;
    }

    public List<ResourceHint> getExcludes() {
        return excludes;
    }

    public Map<String, SerializationHint> getSerializations() {
        return serializations;
    }

    public Map<String, SerializationHint> getLambdaSerializations() {
        return lambdaSerializations;
    }

    public Map<String, JdkProxyHint> getJdkProxys() {
        return jdkProxys;
    }

    public String getApplicationClassName() {
        return applicationClassName;
    }

    public void setApplicationClassName(String applicationClassName) {
        this.applicationClassName = applicationClassName;
    }


    private void handleCategories(ONode attributes, Set<MemberCategory> categories) {
        if (categories.isEmpty()) {
            return;
        }
        for (MemberCategory category : categories) {
            switch (category) {
                case PUBLIC_FIELDS:
                    attributes.set("allPublicFields", true);
                    break;
                case DECLARED_FIELDS:
                    attributes.set("allDeclaredFields", true);
                    break;
                case INTROSPECT_PUBLIC_CONSTRUCTORS:
                    attributes.set("queryAllPublicConstructors", true);
                    break;
                case INTROSPECT_DECLARED_CONSTRUCTORS:
                    attributes.set("queryAllDeclaredConstructors", true);
                    break;
                case INVOKE_PUBLIC_CONSTRUCTORS:
                    attributes.set("allPublicConstructors", true);
                    break;
                case INVOKE_DECLARED_CONSTRUCTORS:
                    attributes.set("allDeclaredConstructors", true);
                    break;
                case INTROSPECT_PUBLIC_METHODS:
                    attributes.set("queryAllPublicMethods", true);
                    break;
                case INTROSPECT_DECLARED_METHODS:
                    attributes.set("queryAllDeclaredMethods", true);
                    break;
                case INVOKE_PUBLIC_METHODS:
                    attributes.set("allPublicMethods", true);
                    break;
                case INVOKE_DECLARED_METHODS:
                    attributes.set("allDeclaredMethods", true);
                    break;
                case PUBLIC_CLASSES:
                    attributes.set("allPublicClasses", true);
                    break;
                case DECLARED_CLASSES:
                    attributes.set("allDeclaredClasses", true);
                    break;
            }
        }
    }

    private void registerSerializationDo(String typeName, String reachableType) {
        if (!serializations.containsKey(typeName)) {
            SerializationHint serializationHint = new SerializationHint();
            serializationHint.setName(typeName);
            serializationHint.setReachableType(reachableType);
            serializations.put(typeName, serializationHint);
        }
    }

    private void registerLambdaSerializationDo(String name, String customTargetConstructorClass, String reachableType) {
        if (!lambdaSerializations.containsKey(name)) {
            SerializationHint serializationHint = new SerializationHint();
            serializationHint.setName(name);
            serializationHint.setCustomTargetConstructorClass(customTargetConstructorClass);
            serializationHint.setReachableType(reachableType);
            lambdaSerializations.put(name, serializationHint);
        }
    }

    private boolean hasDefaultConstructor(Class<?> clazz) {
        if (clazz.isInterface() || clazz.isEnum()) {
            return false;
        }

        return Arrays.stream(clazz.getDeclaredConstructors()).anyMatch(e -> e.getParameterCount() == 0);
    }

    private ReflectionHints getReflectionHints(String className) {
        return reflection.computeIfAbsent(className, k -> {
            ReflectionHints hints = new ReflectionHints();
            hints.setName(className);
            return hints;
        });
    }

}