package org.noear.solon.extend.rockuapi.interceptor;

import noear.water.utils.TextUtil;
import org.noear.solon.extend.rockuapi.encoder.RockDefEncoder;
import org.noear.solon.extend.rockuapi.encoder.RockEncoder;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.extend.rockuapi.RockApiParams;
import org.noear.solon.extend.rockuapi.RockApiResult;
import org.noear.solon.extend.rockuapi.RockCode;

/** 输出拦截器（用于内容格式化并输出） */
public class OutputBuildInterceptor implements XHandler {

    RockEncoder _encoder;
    int _agroup_id;


    public OutputBuildInterceptor(RockEncoder encoder, int agroup_id){
        _agroup_id = agroup_id;

        if(encoder == null) {
            _encoder = new RockDefEncoder();
        }
        else {
            _encoder = encoder;
        }
    }

    @Override
    public void handle(XContext context) throws Exception {
        RockApiParams params = context.attr("params", null);
        RockApiResult result = context.attr("result", null);
        Exception error = context.attr("error", null);

        /** 如果没有结果，实例化一下 */
        if (result == null) {
            result = new RockApiResult();
        }

        /** 如果结果里有异常，则转存为通用异常 */
        if (result.error != null) {
            error = result.error;
            context.attrSet("error", result.error);
        }

        /** 如果有异常，则将成可能成功的码改为异常码 */
        if (error != null && result.code == 1) {
            result.code = 0;
        }

        /** 格式化消息 */
        if(TextUtil.isEmpty(result.message)) {
            result.message = RockCode.CODE_txt(_agroup_id, result);
        }

        /** 开始输出 */
        String output = result.toJson();

        if (params != null) {
            output = _encoder.tryEncode(context, params.getApp(), output);
        }

        context.attrSet("output", output);
    }
}
