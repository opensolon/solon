package org.noear.solon.data.cache;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventListener;

/**
 * 缓存服务事件监控器。监听BeanWrap，获得CacheService bean
 *
 * @author noear
 * @since 1.0
 * */
public class CacheServiceEventListener implements EventListener<BeanWrap> {
    @Override
    public void onEvent(BeanWrap bw) {
        if (bw.raw() instanceof CacheService) {
            if (Utils.isEmpty(bw.name())) {
                CacheLib.cacheServiceAdd("", bw.raw());
            } else {
                CacheLib.cacheServiceAddIfAbsent(bw.name(), bw.raw());

                if (bw.typed()) {
                    CacheLib.cacheServiceAdd("", bw.raw());
                }
            }
        }
    }
}
