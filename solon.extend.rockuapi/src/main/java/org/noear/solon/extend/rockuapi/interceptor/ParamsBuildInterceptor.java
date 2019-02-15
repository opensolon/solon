package org.noear.solon.extend.rockuapi.interceptor;

import noear.snacks.ONode;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.extend.rockuapi.RockApi;
import org.noear.solon.extend.rockuapi.RockApiParams;
import org.noear.solon.extend.rockuapi.RockApiResult;
import org.noear.solon.extend.rockuapi.RockCode;
import org.noear.solon.extend.rockuapi.decoder.RockDecoder;
import org.noear.solon.extend.rockuapi.decoder.RockDefDecoder;

/** 参数构建拦截器（将输出内容构建为统一的参数模型） */
public class ParamsBuildInterceptor implements XHandler {

    private RockDecoder _decoder;

    public ParamsBuildInterceptor(RockDecoder decoder) {
        if(decoder == null) {
            _decoder = new RockDefDecoder();
        }
        else {
            _decoder = decoder;
        }
    }

    @Override
    public void handle(XContext context) throws Exception {
        //检查接口是否存
        if(context.attr("noapi", false)){
            //如果接口不存在，就中止处理
            context.attrSet("result", new RockApiResult(RockCode.CODE_11));
            context.setHandled(true);
        }

        RockApiParams args = new RockApiParams();

        args.context = context;

        if (context.pathAsUpper().contains("/CMD")) {
            /** 处理CMD风格的参数 */

            args.org_param = context.param("p");
            args.org_token = context.param("k");

            if (!XUtil.isEmpty(args.org_token)) {
                String[] token = args.org_token.split("\\.");

                if (token.length >= 3) {
                    args.appID = Integer.parseInt(token[0]);
                    args.verID = Integer.parseInt(token[1]);
                    args.sgin = token[2];
                }
            }

            if (!XUtil.isEmpty(args.org_param)) {
                args.org_param = _decoder.tryDecode(context, args.getApp(), args.org_param);
                args.data = ONode.tryLoad(args.org_param);
            }
        } else {
            /** 处理API风格的参数 */
            context.paramMap().forEach((k, v) -> {
                args.data.set(k, v);
            });
        }

        RockApi api = context.attr("api", null);
        if (api != null) {
            api.params = args;
        }

        context.attrSet("params", args);
    }
}
