package org.noear.solon.extend.data;

import org.noear.solon.XUtil;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.CacheService;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XEventListener;

class CacheEventListener implements XEventListener<BeanWrap> {
    @Override
    public void onEvent(BeanWrap bw) {
        if (bw.raw() instanceof CacheService) {
            if (XUtil.isEmpty(bw.name())) {
                XBridge.cacheServiceAdd("", bw.raw());
            } else {
                XBridge.cacheServiceAddIfAbsent(bw.name(), bw.raw());

                if (bw.typed()) {
                    XBridge.cacheServiceAdd("", bw.raw());
                }
            }
        }
    }
}
