package org.noear.solon.data.dynamicds;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.util.InvKeys;

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

        if(anno == null){
            anno = inv.method().getDeclaringClzAnnotation(DynamicDs.class);
        }

        if (anno == null) {
            return inv.invoke();
        } else {
            //备份
            String backup = DynamicDsHolder.get();

            try {
                String dsName = InvKeys.buildByTmlAndInv(anno.value(), inv);

                DynamicDsHolder.set(dsName);
                return inv.invoke();
            } finally {
                //还原
                DynamicDsHolder.set(backup);
            }
        }
    }
}
