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
        DynamicDs anno = inv.getMethodAnnotation(DynamicDs.class);

        if(anno == null){
            anno = inv.getTargetAnnotation(DynamicDs.class);
        }

        if (anno == null) {
            return inv.invoke();
        } else {
            //备份
            String backup = DynamicDsKey.getCurrent();

            try {
                String dsName = InvKeys.buildByTmlAndInv(anno.value(), inv);

                DynamicDsKey.setCurrent(dsName);
                return inv.invoke();
            } finally {
                //还原
                DynamicDsKey.setCurrent(backup);
            }
        }
    }
}
