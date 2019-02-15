package org.noear.solon.extend.rockuapi.interceptor;

import lib.sponge.rock.models.AppModel;
import org.noear.solon.extend.rockuapi.encoder.RockDefEncoder;
import org.noear.solon.extend.rockuapi.encoder.RockEncoder;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.extend.rockuapi.UApi;
import org.noear.solon.extend.rockuapi.RockApiParams;
import org.noear.solon.extend.rockuapi.RockApiResult;
import org.noear.solon.extend.rockuapi.RockCode;

/** 参数签权拦截器 */
public class ParamsAuthInterceptor implements XHandler {
    private RockEncoder _encoder;

    public ParamsAuthInterceptor(RockEncoder encoder) {
        if(encoder == null)
            _encoder = new RockDefEncoder();
        else
            _encoder = encoder;
    }

    @Override
    public void handle(XContext context) throws Exception {
        /** 如果已处理，不再执行 */
        if (context.getHandled()) {
            return;
        }

        /** 获取参数 */
        RockApiParams params = context.attr("params", null);
        boolean isOk = (params != null);

        if (context.pathAsUpper().contains("/CMD/")) {
            /** 如果是CMD方案，则进行签名对比（签权） */
            if (params != null && params.appID > 0 && params.org_param != null) {
                UApi api = context.attr("api", null);
                isOk = checkSign(context, params.getApp(), api.name(), params.org_param, params.sgin);
            } else {
                isOk = false;
            }
        }

        if (isOk == false) {
            RockApiResult result = new RockApiResult();
            result.code = RockCode.CODE_12;
            context.attrSet("result", result);

            context.setHandled(true);
        }
    }

    /**
     * 签权算法
     */
    private boolean checkSign(XContext context, AppModel app, String cmd, String params, String sign) throws Exception {
        String key = app.app_key;

        StringBuilder sb = new StringBuilder();
        sb.append(cmd).append("#").append(params).append("#").append(key);

        String _sign_ = _encoder.tryEncode(context, app, sb.toString());

        return (_sign_.equalsIgnoreCase(sign));
    }
}
