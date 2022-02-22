package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.result.JapResponse;
import org.noear.solon.core.handle.Context;

/**
 * @author 颖
 * @since 1.6
 */
public abstract class JapController {

    public Object simpleResponse(JapResponse japResponse) {
        String next = Context.current().param("next");
        boolean isSeparate = next == null;

        if (isSeparate) {
            return japResponse;
        } else {
            if (japResponse.isSuccess()) {
                if (japResponse.isRedirectUrl()) {
                    Context.current().redirect((String) japResponse.getData());
                } else {
                    Context.current().redirect(next);
                }
            } else {
                // Todo: 异常处理
                Context.current().redirect(next);
            }
            return null;
        }
    }

    protected boolean validNext(String next) {
        return true;
    }

}
