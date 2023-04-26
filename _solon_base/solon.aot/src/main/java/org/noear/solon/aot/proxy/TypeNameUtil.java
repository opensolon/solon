package org.noear.solon.aot.proxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    public static TypeName getTypeName(Class<?> clz, Type type){
        if (type instanceof ParameterizedType) {
            ParameterizedType type2 = (ParameterizedType) type;
            if(type2.getActualTypeArguments().length > 0) {
                //支持泛型
                ClassName petClassName = ClassName.get(clz);
                List<TypeName> petTypeVars = new ArrayList<>();
                for(Type e : type2.getActualTypeArguments()){
                    petTypeVars.add(TypeName.get(e));
                }

                return ParameterizedTypeName.get(petClassName, petTypeVars.toArray(new TypeName[]{}));
            }else{
                return TypeName.get(type);
            }
        } else {
            return TypeName.get(type);
        }
    }
}
