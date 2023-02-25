package org.noear.solon.proxy.apt.util;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类名称工具
 *
 * @author noear
 * @since 2.2
 */
public class TypeNameUtil {
    /**
     * 获取类名称（支持泛型）
     * */
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
}
