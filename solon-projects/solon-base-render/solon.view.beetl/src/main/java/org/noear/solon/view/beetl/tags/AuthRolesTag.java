package org.noear.solon.view.beetl.tags;

import org.beetl.core.tag.Tag;
import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.auth.tags.AuthConstants;

/**
 * @author noear
 * @since 1.4
 */
public class AuthRolesTag extends Tag {
    @Override
    public void render() {
        String nameStr = (String) getHtmlAttribute(AuthConstants.ATTR_name);
        String logicalStr = (String) getHtmlAttribute(AuthConstants.ATTR_logical);

        if (Utils.isEmpty(nameStr)) {
            return;
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return;
        }

        if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
            this.doBodyRender();
        }

    }
}
