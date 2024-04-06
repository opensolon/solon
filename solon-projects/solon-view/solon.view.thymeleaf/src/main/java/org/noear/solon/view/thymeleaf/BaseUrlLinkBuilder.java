package org.noear.solon.view.thymeleaf;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;

import java.util.Map;

public class BaseUrlLinkBuilder extends StandardLinkBuilder {
    private String baseUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
        if (baseUrl == null) {
            throw new TemplateProcessingException("baseUrl is null");
        }

        return baseUrl;
    }
}
