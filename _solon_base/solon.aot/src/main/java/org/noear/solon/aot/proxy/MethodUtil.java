package org.noear.solon.aot.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 函数处理工具
 *
 * @author noear
 * @since 2.2
 */
public class MethodUtil {
    /**
     * 是否允许函数处理
     */
    public static boolean allowMethod(Method element) {

        int modifiers = element.getModifiers();
        if (Modifier.isStatic(modifiers) ||
                Modifier.isFinal(modifiers) ||
                Modifier.isPrivate(modifiers) ||
                Modifier.isProtected(modifiers)) {
            //静态 或 只读 或 私有；不需要重写
            return false;
        } else {
            return true;
        }
    }


    /**
     * 构建函数唯一识别键
     */
    public static String buildMethodKey(Method method) {
        StringBuilder buf = new StringBuilder();
        buf.append(method.getName());
        buf.append("(");

        for (Parameter pe : method.getParameters()) {
            Class<?> pet = pe.getType();//todo: 可能要增加泛型支持

            buf.append(pet.toString());
            buf.append(",");
        }

        buf.append(")");

        return buf.toString();
    }

    /**
     * 查找类的所有函数（包括父类）
     *
     * @param type 类型
     */
    public static Map<String, Method> findMethodAll(Class<?> type) {
        Map<String, Method> methodAll = new TreeMap<>(); //new LinkedHashMap<>();

        //本级优先
        for (Method method : type.getDeclaredMethods()) {
            if (allowMethod(method)) {
                String methodKey = buildMethodKey(method);
                methodAll.put(methodKey, method);
            }
        }

        //再取超类的函数
        Class<?> origin = type.getSuperclass();
        while (true) {
            if (origin == Object.class) {
                break;
            }

            for (Method method : origin.getDeclaredMethods()) {
                if (allowMethod(method)) {
                    String methodKey = buildMethodKey(method);
                    methodAll.putIfAbsent(methodKey, method);
                }
            }

            origin = origin.getSuperclass();
        }

        return methodAll;
    }
}