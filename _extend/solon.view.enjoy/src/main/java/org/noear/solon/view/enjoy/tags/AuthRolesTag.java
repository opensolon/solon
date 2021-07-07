package org.noear.solon.view.enjoy.tags;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;

/**
 * @author noear
 * @since 1.4
 */
public class AuthRolesTag extends Directive {
    @Override
    public void exec(Env env, Scope scope, Writer writer) {
        String[] attrs = getAttrArray(scope);

        if(attrs.length == 0){
            return;
        }

        String nameStr = attrs[0];
        String logicalStr = null;
        if(attrs.length > 1){
            logicalStr = attrs[1];
        }

        if (Utils.isEmpty(nameStr)) {
            return;
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return;
        }

        if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
            stat.exec(env, scope, writer);
        }
    }


    @Override
    public boolean hasEnd() {
        return true;
    }

    /**
     * 从 #xxx 指令参数中获取角色名称数组
     */
    private String[] getAttrArray(Scope scope) {
        Object[] values = exprList.evalExprList(scope);
        String[] ret = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof String) {
                ret[i] = (String) values[i];
            } else {
                throw new IllegalArgumentException("Name can only be strings");
            }
        }
        return ret;
    }
}
