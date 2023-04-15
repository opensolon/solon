package org.noear.solon.aot;

import lombok.Getter;
import lombok.Setter;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.aot.hint.ExecutableHint;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.aot.hint.ReflectionHints;
import org.noear.solon.aot.hint.ResourceHint;
import org.noear.solon.aot.hint.SerializationHint;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Getter
    @Setter
    private String packageName;

    private final Map<String, ReflectionHints> reflection = new LinkedHashMap<>();

    @Getter
    private final List<ResourceHint> includes = new ArrayList<>();
    private final List<ResourceHint> excludes = new ArrayList<>();

    private final List<SerializationHint> serialization = new ArrayList<>();

    @Getter
    @Setter
    private String applicationClassName;

    public String getNativeImageDir() {
        return GraalvmUtil.NATIVE_IMAGE_DIR;
    }


    /**
     * 注册反射相关
     *
     * @param className 全类名
     * @param typeHint  为该类型添加更多的反射信息
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerReflection(String className, Consumer<ReflectionHints> typeHint) {
        ReflectionHints reflectionHints = reflection.computeIfAbsent(className, k -> {
            ReflectionHints hints = new ReflectionHints();
            hints.setName(className);
            return hints;
        });
        typeHint.accept(reflectionHints);
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
        return registerReflection(field.getDeclaringClass(), hints -> hints.getFields().add((field.getName())));
    }

    public RuntimeNativeMetadata registerConstructor(Constructor<?> constructor, ExecutableMode mode) {
        return registerReflection(constructor.getDeclaringClass(), hints -> hints.getConstructors().add(new ExecutableHint("<init>", constructor.getParameterTypes(), mode)));
    }

    public RuntimeNativeMetadata registerMethod(Method method, ExecutableMode mode) {
        return registerReflection(method.getDeclaringClass(), hints -> hints.getMethods().add(new ExecutableHint(method.getName(), method.getParameterTypes(), mode)));
    }

    public RuntimeNativeMetadata registerDefaultConstructor(Class<?> clazz) {
        return registerReflection(clazz, hint -> hint.getConstructors().add(new ExecutableHint("<init>", null, ExecutableMode.INVOKE)));
    }

    public RuntimeNativeMetadata registerDefaultConstructor(String className) {
        return registerReflection(className, hint -> hint.getConstructors().add(new ExecutableHint("<init>", null, ExecutableMode.INVOKE)));
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
     * @param type 类型
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerSerialization(Class<? extends Serializable> type) {
        return registerSerialization(type, null);
    }

    /**
     * 注册Java序列化
     *
     * @param type          类型
     * @param reachableType condition class
     * @return {@code this}
     */
    public RuntimeNativeMetadata registerSerialization(Class<? extends Serializable> type, String reachableType) {
        SerializationHint serializationHint = new SerializationHint();
        serializationHint.setName(type.getName());
        serializationHint.setReachableType(reachableType);
        serialization.add(serializationHint);
        return this;
    }

    /**
     * 生成 reflect-config.json
     *
     * @see <a href="https://www.graalvm.org/latest/reference-manual/native-image/metadata/#specifying-reflection-metadata-in-json">Specifying Reflection Metadata in JSON</a>
     */
    public String toReflectionJson() {
        if (reflection.isEmpty()) {
            return null;
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
            return null;
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
        if (serialization.isEmpty()) {
            return null;
        }
        ONode oNode = new ONode(jsonOptions).asArray();
        for (SerializationHint hint : serialization) {
            ONode item = oNode.addNew().set("name", hint.getName());
            if (Utils.isNotEmpty(hint.getReachableType())) {
                item.getOrNew("condition").set("typeReachable", hint.getReachableType());
            }
        }
        return oNode.toJson();
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


}
