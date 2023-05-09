package org.noear.solon.aot.proxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.noear.solon.core.util.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名称工具
 *
 * @author noear
 * @since 2.2
 */
public class TypeNameUtil {
    /**
     * 获取类名称（支持泛型）
     */
    public static TypeName getTypeName(Map<String, Type> typeGenericMap, Class<?> clz, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType type2 = (ParameterizedType) type;

            if (type2.getActualTypeArguments().length > 0) {
                //支持泛型
                ClassName petClassName = ClassName.get(clz);
                List<TypeName> petTypeVars = new ArrayList<>();
                for (Type e : type2.getActualTypeArguments()) {
                    if (e instanceof TypeVariable) {
                        Type e2 = typeGenericMap.get(e.getTypeName());
                        if(e2 != null){
                            e = e2;
                        }
                    }

                    petTypeVars.add(TypeName.get(e));
                }

                return ParameterizedTypeName.get(petClassName, petTypeVars.toArray(new TypeName[]{}));
            } else {
                return TypeName.get(type);
            }
        } else {
            if (type instanceof TypeVariable) {
                Type type2 = typeGenericMap.get(type.getTypeName());

                if(type2 != null){
                    type = type2;
                }
            }


            return TypeName.get(type);
        }
    }

    public static Type getType(Map<String, Type> typeGenericMap, Class<?> clz, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType type2 = (ParameterizedType) type;

            if (type2.getActualTypeArguments().length > 0) {

                List<Type> petTypes = new ArrayList<>();
                for (Type e : type2.getActualTypeArguments()) {
                    if (e instanceof TypeVariable) {
                        e = typeGenericMap.get(e.getTypeName());
                    }

                    petTypes.add(e);
                }

                return new ParameterizedTypeImpl(clz, petTypes.toArray(new Type[]{}), type2.getOwnerType());
            } else {
                return type;
            }
        } else {
            if (type instanceof TypeVariable) {
                return typeGenericMap.get(type.getTypeName());
            } else {
                return type;
            }
        }
    }
}
