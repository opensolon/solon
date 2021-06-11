package org.noear.solon.view.beetl.tags;

import org.beetl.core.tag.Tag;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.auth.tags.TagAttrs;

/**
 * @author noear
 * @since 1.4
 */
@Component("view:hasPermission")
public class HasPermissionTag extends Tag {
    @Override
    public void render() {
        String nameStr = (String) getHtmlAttribute(TagAttrs.ATTR_name);
        String logicalStr = (String) getHtmlAttribute(TagAttrs.ATTR_logical);

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
