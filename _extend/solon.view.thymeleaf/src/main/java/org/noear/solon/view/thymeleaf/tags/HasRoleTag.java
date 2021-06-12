package org.noear.solon.view.thymeleaf.tags;

import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.auth.tags.Constants;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.*;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author noear
 * @since 1.4
 */
public class HasRoleTag extends AbstractElementTagProcessor {

    public HasRoleTag(TemplateMode templateMode, String dialectPrefix, String elementName, boolean prefixElementName, String attributeName, boolean prefixAttributeName, int precedence) {
        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        String nameStr = tag.getAttributeValue(Constants.ATTR_name);
        String logicalStr = tag.getAttributeValue(Constants.ATTR_logical);

        if (Utils.isEmpty(nameStr)) {
            return;
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return;
        }

        if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
            structureHandler.setInliner(context.getInliner());
        }
    }
}
