package com.fujieid.jap.solon.http.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.solon.JapProps;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 颖
 * @since 1.6
 */
public abstract class JapController {

    public final static String JAP_LAST_RESPONSE_KEY = "_jap:lastResponse";
    public final static String JAP_MFA_VERIFIED_KEY = "_jap:mfaVerified";

    @Inject
    JapProps japProprieties;

    private final Pattern urlPattern = Pattern.compile(
            "^((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)"
    );

    public Object simpleResponse(Context ctx, JapResponse japResponse) {
        // 记录 Response
        String next = ctx.param("next");
        boolean isSeparate = next == null;

        if (isSeparate) {
            return japResponse;
        } else {
            ctx.sessionSet(JAP_LAST_RESPONSE_KEY, japResponse);
            // 替换二次跳转后的参数
            for (Map.Entry<String, Object> parameters : ((JSONObject) JSON.toJSON(japResponse)).entrySet()) {
                if (parameters.getValue() != null) {
                    next = next.replace(String.format("{%s}", parameters.getKey()), parameters.getValue().toString());
                }
            }
            if (japResponse.isSuccess()) {
                if (japResponse.isRedirectUrl()) {
                    ctx.redirect((String) japResponse.getData());
                } else {
                    ctx.redirect(next);
                }
            } else {
                // Todo: 异常处理
                ctx.redirect(next);
            }

            return null;
        }
    }

    /**
     * 校验下一跳地址是否合法
     *
     * @param next 下一跳地址
     * @return 是否合法
     */
    protected boolean validNext(String next) {
        Matcher matcher = this.urlPattern.matcher(next);
        next = matcher.find() ? matcher.group() : next;
        return this.japProprieties.getNexts().contains(next);
    }

}
