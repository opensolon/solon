package org.noear.solon.view.freemarker.tags;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.noear.solon.Utils;
import org.noear.solon.core.NdMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.i18n.tags.I18nConstants;

import java.io.IOException;
import java.util.Map;

/**
 * @author noear 2021/7/7 created
 */
public class I18nTag implements TemplateDirectiveModel {
    @Override
    public void execute(Environment env, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        NdMap mapExt = new NdMap(map);

        String codeStr = (String) mapExt.get(I18nConstants.ATTR_code);
        Object[] argsAry = (Object[]) mapExt.get(I18nConstants.ATTR_args);

        if (Utils.isEmpty(codeStr)) {
            return;
        }

        env.getOut().write(I18nUtil.getMessage(Context.current(), codeStr, argsAry));
    }
}
