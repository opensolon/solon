package org.noear.solon.proxy.apt.util;

import org.noear.solon.proxy.apt.holder.MethodElementHolder;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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
    public static boolean allowMethod(Element element) {
        if (element.getModifiers().contains(Modifier.STATIC) ||
                element.getModifiers().contains(Modifier.FINAL) ||
                element.getModifiers().contains(Modifier.PRIVATE) ||
                element.getModifiers().contains(Modifier.PROTECTED)) {
            //静态 或 只读 或 私有；不需要重写
            return false;
        } else {
            return true;
        }
    }


    /**
     * 构建函数唯一识别键
     */
    public static String buildMethodKey(ExecutableElement method) {
        StringBuilder buf = new StringBuilder();
        buf.append(method.getSimpleName().toString());
        buf.append("(");

        for (VariableElement pe : method.getParameters()) {
            TypeMirror pet = pe.asType();

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
    public static Map<String, ExecutableElement> findMethodAll(TypeElement type) {
        Map<String, ExecutableElement> methodAll = new TreeMap<>();//new LinkedHashMap<>();

        //本级优先
        for (Element e : type.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                if (allowMethod(e)) {
                    ExecutableElement method = (ExecutableElement) e;
                    String methodKey = buildMethodKey(method);
                    methodAll.put(methodKey, method);
                }
            }
        }

        //再取超类的函数
        TypeMirror origin = type.getSuperclass();
        while (true) {
            if (origin.getKind() != TypeKind.DECLARED) {
                break;
            }

            if ("java.lang.Object".equals(origin.toString())) {
                break;
            }

            DeclaredType originDt = (DeclaredType) origin;
            TypeElement originElement = (TypeElement) (originDt.asElement());

            Map<String, TypeMirror> gtArgMap = new LinkedHashMap<>();

            for (int i = 0, size = originDt.getTypeArguments().size(); i < size; i++) {
                TypeParameterElement gtKey = originElement.getTypeParameters().get(i);
                TypeMirror gtVal = originDt.getTypeArguments().get(i);

                gtArgMap.put(gtKey.toString(), gtVal);
            }

            for (Element e : originElement.getEnclosedElements()) {
                if (e.getKind() == ElementKind.METHOD) {
                    //需要处理泛型
                    if (allowMethod(e)) {
                        ExecutableElement method = new MethodElementHolder((ExecutableElement) e, gtArgMap);
                        String methodKey = buildMethodKey(method);
                        methodAll.put(methodKey, method);
                    }
                }
            }

            origin = originElement.getSuperclass();
        }

        return methodAll;
    }
}