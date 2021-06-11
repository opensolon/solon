package org.noear.solon.data;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.data.cache.CacheService;

class CacheEventListener implements EventListener<BeanWrap> {
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
