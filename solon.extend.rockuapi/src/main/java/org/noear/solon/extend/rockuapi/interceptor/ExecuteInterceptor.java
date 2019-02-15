package org.noear.solon.extend.rockuapi.interceptor;

import noear.weed.cache.CacheUsing;
import noear.weed.cache.ICacheService;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.extend.rockuapi.UApi;
import org.noear.solon.extend.rockuapi.RockApiParams;

/** 执行拦截器（用于执行接口） */
public class ExecuteInterceptor implements XHandler {
    private ICacheService _cache;
    public ExecuteInterceptor(ICacheService cache) {
        _cache = cache;
    }

    @Override
    public void handle(XContext context) throws Exception {
        /** 如果已处理，不再执行 */
        if (context.getHandled()) {
            return;
        }

        Object result = null;

        /** 按代码逻辑执行 */
        RockApiParams params = context.attr("params", null);
        UApi api = context.attr("api", null);

        String cache_key = api.name() + params.toString();

        if (api.isUsingCache() && _cache != null) {
            CacheUsing cu = new CacheUsing(_cache);
            result = cu.usingCache(api.cacheSeconds()).get(cache_key, () -> api.call(context));
        } else {
            result = api.call(context);
        }


        context.attrSet("result", result);
    }
}
