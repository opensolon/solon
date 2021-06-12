package org.noear.solon.view.beetl.tags;

import org.beetl.core.tag.Tag;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.Logical;
import org.noear.solon.extend.auth.tags.Constants;

/**
 * @author noear
 * @since 1.4
 */
public class HasPermissionTag extends Tag {
    @Override
    public void render() {
        String nameStr = (String) getHtmlAttribute(Constants.ATTR_name);
        String logicalStr = (String) getHtmlAttribute(Constants.ATTR_logical);

        if (Utils.isEmpty(nameStr)) {
            return;
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return;
        }

        if (AuthUtil.verifyPermissions(names, Logical.of(logicalStr))) {
            this.doBodyRender();
        }
    }
}
