package graphql.solon.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.noear.solon.core.util.GenericUtil;

/**
 * @author fuzi1996
 * @since 2.3
 */
public abstract class ClazzUtil {

    public static Class<?> getGenericReturnClass(Type type) {
        // 获取返回泛型类型
        ParameterizedType parameterizedType = GenericUtil
                .toParameterizedType(type);
        if (parameterizedType == null) {
            return null;
        }

        // 返回泛型的的第一个Class类型对象
        if (parameterizedType.getActualTypeArguments().length > 0) {
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        } else {
            return null;
        }
    }

}
