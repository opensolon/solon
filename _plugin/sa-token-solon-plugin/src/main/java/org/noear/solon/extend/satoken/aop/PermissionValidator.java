package org.noear.solon.extend.satoken.aop;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.auth.validator.AbstractValidator;

/**
 * @author noear 2021/5/30 created
 */
public class PermissionValidator extends AbstractValidator<SaCheckPermission> {
    @Override
    public Class<SaCheckPermission> type() {
        return SaCheckPermission.class;
    }

    @Override
    public Result validate(SaCheckPermission anno) {
        return null;
    }
}
