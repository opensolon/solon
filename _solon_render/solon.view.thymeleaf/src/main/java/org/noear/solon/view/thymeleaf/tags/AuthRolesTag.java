package org.noear.solon.view.thymeleaf.tags;

import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.auth.tags.AuthConstants;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.*;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author noear
 * @since 1.4
 */
public class AuthRolesTag extends AbstractElementTagProcessor {

    public AuthRolesTag(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, AuthConstants.TAG_roles, true, null, false, 100);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        String nameStr = tag.getAttributeValue(AuthConstants.ATTR_name);
        String logicalStr = tag.getAttributeValue(AuthConstants.ATTR_logical);

        if (Utils.isEmpty(nameStr)) {
            return;
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return;
        }

        if (AuthUtil.verifyRoles(names, Logical.of(logicalStr)) == false) {
            structureHandler.setBody("", false);
        }
    }
}
