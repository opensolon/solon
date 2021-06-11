package org.noear.solon.view.thymeleaf.tags;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.processor.element.MatchingAttributeName;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * @author noear 2021/6/11 created
 */
public class HasRolesTag implements IElementTagProcessor {
    @Override
    public void process(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, IElementTagStructureHandler iElementTagStructureHandler) {

    }

    @Override
    public MatchingElementName getMatchingElementName() {
        return null;
    }

    @Override
    public MatchingAttributeName getMatchingAttributeName() {
        return null;
    }

    @Override
    public TemplateMode getTemplateMode() {
        return null;
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
