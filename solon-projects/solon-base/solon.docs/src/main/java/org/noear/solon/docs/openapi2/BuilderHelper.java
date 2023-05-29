package org.noear.solon.docs.openapi2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.docs.ApiEnum;
import org.noear.solon.docs.DocDocket;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author noear
 */
public class BuilderHelper {
    public static boolean isModel(Class<?> clz){
        if(clz.isAnnotationPresent(ApiModel.class)){
            return true;
        }

        if(clz.getName().startsWith("java")){
            return false;
        }

        if(clz.isPrimitive()){
            return false;
        }

        if(Map.class.isAssignableFrom(clz) || Collection.class.isAssignableFrom(clz)){
            return false;
        }

        return true;
    }

    public static String getModelName(Class<?> clazz, Type type) {
        String modelName = clazz.getSimpleName();

        if (type instanceof ParameterizedType) {
            //支持泛型
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();

            if (typeArguments != null && typeArguments.length > 0) {
                StringBuilder buf = new StringBuilder();
                for (Type v : typeArguments) {
                    if (v instanceof Class<?>) {
                        buf.append(((Class<?>) v).getSimpleName()).append(",");
                    }

                    if (v instanceof ParameterizedType) {
                        ParameterizedType v2 = (ParameterizedType) v;
                        Type v22 = v2.getRawType();

                        if (v22 instanceof Class<?>) {
                            String name2 = getModelName((Class<?>) v22, v2);
                            buf.append(name2).append(",");
                        }
                    }
                }

                if (buf.length() > 0) {
                    buf.setLength(buf.length() - 1);

                    modelName = modelName + "«" + buf + "»";
                }
            }
        }

        return modelName;
    }

    public static String getHttpMethod(ActionHolder actionHolder, ApiOperation apiAction) {
        if (Utils.isBlank(apiAction.httpMethod())) {
            MethodType methodType = actionHolder.routing().method();

            if (methodType == null) {
                return ApiEnum.METHOD_GET;
            } else {
                if (actionHolder.isGet()) {
                    return ApiEnum.METHOD_GET;
                }

                if (methodType.ordinal() < MethodType.UNKNOWN.ordinal()) {
                    return methodType.name.toLowerCase();
                } else {
                    return ApiEnum.METHOD_POST;
                }
            }
        } else {
            return apiAction.httpMethod();
        }
    }

    /**
     * 获取host配置
     */
    public static String getHost(DocDocket swaggerDock) {
        String host = swaggerDock.host();
        if (Utils.isBlank(host)) {
            host = "localhost";
            if (Solon.cfg().serverPort() != 80) {
                host += ":" + Solon.cfg().serverPort();
            }
        }

        return host;
    }

    /**
     * 避免ControllerKey 设置前缀后,与swagger basePath 设置导致前端生成2次
     */
    public static String getControllerKey(Class<?> controllerClz) {
        Mapping mapping = controllerClz.getAnnotation(Mapping.class);
        if (mapping == null) {
            return "";
        }

        String path = Utils.annoAlias(mapping.value(), mapping.path());
        if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }
}
