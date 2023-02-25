package org.noear.solon.proxy.apt.impl;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author noear 2023/2/25 created
 */
public class Helper {
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

    public static TypeName getTypeName(TypeMirror type){
        if (type instanceof DeclaredType) {
            DeclaredType pet2 = (DeclaredType) type;
            if(pet2.getTypeArguments().size() > 0) {
                //支持泛型
                ClassName petClassName = ClassName.get((TypeElement) pet2.asElement());
                List<TypeName> petTypeVars = pet2.getTypeArguments().stream().map(e -> TypeName.get(e)).collect(Collectors.toList());

                return ParameterizedTypeName.get(petClassName, petTypeVars.toArray(new TypeName[]{}));
            }else{
                return TypeName.get(type);
            }
        } else {
            return TypeName.get(type);
        }
    }

    public static TypeMirror getRealType(TypeMirror type){
        if(type instanceof TypeVariable){
            return ((TypeVariable) type).getUpperBound();
        }else{
            return type;
        }
    }

    public static String getMethodKey(ExecutableElement method) {
        StringBuilder buf = new StringBuilder();
        buf.append(method.getSimpleName().toString());
        buf.append("(");

        for (VariableElement pe : method.getParameters()) {
            TypeMirror pet = getRealType(pe.asType());

            buf.append(pet.toString());
            buf.append(",");
        }

        buf.append(")");

        return buf.toString();
    }

    /**
     * 根据类型和包名获取类名
     *
     * @param type        类型
     * @param packageName 包名
     */
    public static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}
