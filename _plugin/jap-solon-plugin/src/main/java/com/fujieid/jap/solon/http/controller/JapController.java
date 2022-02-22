package com.fujieid.jap.solon.http.controller;

import com.fujieid.jap.core.result.JapResponse;
import com.fujieid.jap.solon.JapProps;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 颖
 * @since 1.6
 */
public abstract class JapController {

    @Inject
    JapProps japProprieties;
    private final Pattern urlPattern = Pattern.compile(
            "^((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)"
    );

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

    /**
     * 校验下一跳地址是否合法
     * @param next 下一跳地址
     * @return 是否合法
     */
    protected boolean validNext(String next) {
        Matcher matcher = this.urlPattern.matcher(next);
        next = matcher.find() ?  matcher.group() : next;
        return this.japProprieties.getNexts().contains(next);
    }

}
