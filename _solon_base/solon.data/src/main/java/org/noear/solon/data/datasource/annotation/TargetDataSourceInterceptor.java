package org.noear.solon.data.datasource.annotation;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.datasource.DynamicDataSourceHolder;

/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.10
 */
public class TargetDataSourceInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        TargetDataSource anno = inv.method().getAnnotation(TargetDataSource.class);

        if (anno == null) {
            return inv.invoke();
        } else {
            //备份
            String backup = DynamicDataSourceHolder.get();

            try {
                DynamicDataSourceHolder.set(anno.value());
                return inv.invoke();
            } finally {
                //还原
                DynamicDataSourceHolder.set(backup);
            }
        }
    }
}
