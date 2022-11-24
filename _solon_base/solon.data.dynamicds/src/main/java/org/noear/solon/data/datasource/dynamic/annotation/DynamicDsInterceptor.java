package org.noear.solon.data.datasource.dynamic.annotation;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.dynamicds.DynamicDsUtils;

/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.11
 */
public class DynamicDsInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        DynamicDs anno = inv.method().getAnnotation(DynamicDs.class);

        if (anno == null) {
            return inv.invoke();
        } else {
            //备份
            String backup = DynamicDsUtils.getCurrent();

            try {
                DynamicDsUtils.setCurrent(anno.value());
                return inv.invoke();
            } finally {
                //还原
                DynamicDsUtils.setCurrent(backup);
            }
        }
    }
}
