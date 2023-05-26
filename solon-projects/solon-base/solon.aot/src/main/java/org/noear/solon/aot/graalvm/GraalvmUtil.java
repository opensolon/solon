package org.noear.solon.aot.graalvm;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.aot.hint.ExecutableHint;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ReflectUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author 馒头虫/瓢虫
 * @since 1.5
 */
public class GraalvmUtil {

    public static final String NATIVE_IMAGE_DIR = "META-INF/native-image";
    public static final String SOLON_RESOURCE_NAME = "solon-resource.json";

    private static final Set<String> resources = new HashSet<>();
    private static final Map<String, Set<String>> classFieldNames = new HashMap<>();

    private static final Map<Class<?>, Set<Field>> classFields = new HashMap<>();
    private static final Map<String, Set<ExecutableHint>> classExecutable = new HashMap<>();
    private static final Map<Class<?>, Set<Method>> classMethods = new HashMap<>();

    static {
        readNativeResourceConfig();
        readNativeReflectConfig();
    }

    /**
     * META-INF/native-image + 启动类包名
     */
    public static String getNativeImageDir() {
        String packageName = Solon.cfg().source().getPackage().getName();
        return NATIVE_IMAGE_DIR + "/" + packageName.replace('.', '/');
    }

    /**
     * solon-resource.json 全路径
     */
    public static String getSolonResourcePath() {
        return getNativeImageDir() + "/" + SOLON_RESOURCE_NAME;
    }

    /**
     * 获取类上定义的字段，优先从reflect-config.json中获取
     */
    public static Field[] getDeclaredFields(Class<?> clz) {
        Set<Field> fieldSet = classFields.get(clz);
        if (fieldSet != null) {
            return fieldSet.toArray(new Field[0]);
        }
        Set<String> fieldNames = classFieldNames.get(ReflectUtil.getClassName(clz));
        if (fieldNames == null) {
            return clz.getDeclaredFields();
        }
        Set<Field> fields = fieldNames.stream().map(e -> {
            try {
                return clz.getDeclaredField(e);
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
        }).collect(Collectors.toSet());
        classFields.put(clz, fields);
        return fields.toArray(new Field[0]);
    }

    /**
     * 获取类上的方法，优先从reflect-config.json中获取
     */
    public static Method[] getDeclaredMethods(Class<?> clz) {
        Set<Method> methods = classMethods.get(clz);
        if (methods != null) {
            return methods.toArray(new Method[0]);
        }
        Set<ExecutableHint> executableHints = classExecutable.get(ReflectUtil.getClassName(clz));
        if (executableHints == null) {
            return clz.getDeclaredMethods();
        }
        Set<Method> methodSet = executableHints.stream()
                .filter(e -> !ReflectUtil.CONSTRUCTOR_NAME.equals(e.getName()))
                .map(e -> {
                    try {
                        List<String> types = e.getParameterTypes();
                        if (types == null || types.isEmpty()) {
                            return clz.getDeclaredMethod(e.getName());
                        }
                        Class<?>[] classes = types.stream().map(type -> {
                            try {
                                return TypeUtil.forName(type, JarClassLoader.global());
                            } catch (Exception ex) {
                                return new RuntimeException(ex);
                            }
                        }).toArray(Class[]::new);
                        return clz.getDeclaredMethod(e.getName(), classes);
                    } catch (NoSuchMethodException ex) {
                        throw new RuntimeException(ex);
                    }
                }).collect(Collectors.toSet());
        classMethods.put(clz, methodSet);
        return methodSet.toArray(new Method[0]);
    }

    /**
     * graalvm 里的 scan 通过预处理，存放到配置文件，key= solon.scan (@since 1.6)
     *
     * @param path   路径
     * @param filter 过滤条件
     * @param urls   扫描到的路径 作为返回
     */
    public static void scanResource(String path, Predicate<String> filter, Set<String> urls) {

        for (String f : resources) {
            if (f.startsWith(path) && filter.test(f)) {
                urls.add(f);
            }
        }
    }

    /**
     * 读取reflect-config 获取需要扫描的 class列表
     */
    private static void readNativeReflectConfig() {
        try {
            List<ClassLoader> loaderList = ExtendLoader.load(Solon.cfg().extend(), false);

            for (ClassLoader loader : loaderList) {
                Enumeration<URL> rs = ResourceUtil.getResources(loader, getNativeImageDir() + "/reflect-config.json");

                while (rs.hasMoreElements()) {
                    String s = readFileByLines(rs.nextElement());
                    ONode o = ONode.loadStr(s);
                    o.forEach(on -> {
                        String className = on.get("name").getString();
                        String name = className.replaceAll("\\.", "/") + ".class";
                        resources.add(name);

                        // fields
                        Set<String> fields = new LinkedHashSet<>();
                        on.get("fields").forEach(f -> {
                            String fieldName = f.get("name").getString();
                            fields.add(fieldName);
                        });
                        classFieldNames.put(className, fields);

                        // methods
                        Set<ExecutableHint> executableHints = new LinkedHashSet<>();
                        on.get("methods").forEach(f -> {
                            String methodName = f.get("name").getString();
                            List<String> parameterTypes = new ArrayList<>();
                            f.get("parameterTypes").asArray().forEach(p -> {
                                String parameterType = p.getString();
                                parameterTypes.add(parameterType);
                            });
                            ExecutableHint executableHint = new ExecutableHint(methodName, parameterTypes);
                            executableHints.add(executableHint);
                        });
                        classExecutable.put(className, executableHints);
                    });
                }
            }

            if (Solon.cfg().isDebugMode()) {
                LogUtil.global().info("reflect-config: load completed: " + resources);
            }
        } catch (Exception e) {
            LogUtil.global().warn("reflect-config: read error: " + e.getLocalizedMessage());
            EventBus.pushTry(e);
        }
    }

    /**
     * 读取resource-config.json 获取需要扫描的资源文件列表
     */
    private static void readNativeResourceConfig() {
        try {
            List<ClassLoader> loaderList = ExtendLoader.load(Solon.cfg().extend(), false);

            String solonResource = getSolonResourcePath();
            for (ClassLoader loader : loaderList) {
                Enumeration<URL> rs = ResourceUtil.getResources(loader, solonResource);

                while (rs.hasMoreElements()) {
                    String s = readFileByLines(rs.nextElement());
                    ONode o = ONode.loadStr(s);
                    o.forEach(on -> {
                        String name = on.getString();
                        resources.add(name);
                    });
                }
            }

            if (Solon.cfg().isDebugMode()) {
                LogUtil.global().info(solonResource + ": load completed: " + resources);
            }
        } catch (Exception e) {
            LogUtil.global().warn("resource-config: read error: " + e.getLocalizedMessage());
            EventBus.pushTry(e);
        }
    }

    /**
     * 以行为单位读取文件
     *
     * @param url 资源地址
     */
    public static String readFileByLines(URL url) {
        StringBuilder buf = new StringBuilder();//比多次字符串加性能好些

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                buf.append(tempString).append("\r\n");
            }
        } catch (IOException e) {

        }

        return buf.toString();
    }
}