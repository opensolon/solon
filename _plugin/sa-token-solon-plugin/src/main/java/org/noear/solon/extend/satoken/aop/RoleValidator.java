package org.noear.solon.extend.satoken.aop;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.validator.AbstractValidator;

/**
 * @author noear 2021/5/30 created
 */
public class RoleValidator extends AbstractValidator<SaCheckLogin> {
    @Override
    public Class<SaCheckLogin> type() {
        return SaCheckLogin.class;
    }

    @Override
    public Result validate(SaCheckLogin anno) {
        return null;
    }
}
