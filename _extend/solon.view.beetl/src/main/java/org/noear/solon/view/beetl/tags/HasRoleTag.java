package org.noear.solon.view.beetl.tags;

import org.beetl.core.tag.Tag;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.extend.auth.AuthUtil;
import org.noear.solon.extend.auth.annotation.Logical;
import org.noear.solon.extend.auth.tags.TagAttrs;

/**
 * @author noear
 * @since 1.4
 */
@Component("view:hasRole")
public class HasRoleTag extends Tag {
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

        if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
            doBodyRender();
        }

    }
}
